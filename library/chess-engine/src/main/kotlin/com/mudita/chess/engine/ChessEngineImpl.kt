package com.mudita.chess.engine

import androidx.annotation.VisibleForTesting
import com.mudita.chess.engine.UCICommands.NEW_GAME
import com.mudita.chess.engine.UCICommands.QUIT
import com.mudita.chess.engine.UCICommands.STOP
import com.mudita.chess.engine.UCICommands.UCI
import com.mudita.chess.engine.UCIOptions.HASH_SIZE
import com.mudita.chess.engine.UCIOptions.NET_PATH
import com.mudita.chess.engine.net.ChessEngineNet
import com.mudita.chess.engine.process.ChessEngineProcess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

@Suppress("TooManyFunctions")
internal class ChessEngineImpl(
    private val process: ChessEngineProcess,
    private val net: ChessEngineNet
) : ChessEngine {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    /** Set when a stopped search left the engine ended; the next caller starts it again. */
    @Volatile
    private var restartNeeded = false

    override suspend fun start() {
        if (stateFlow.value !is State.Uninitialized) return

        try {
            process.start()
            process.monitor()
            awaitFirstLine()
            process.writeLine(UCI)
            awaitLine { it == UCI_OK_TOKEN }
            process.writeLine(UCICommands.setOption(HASH_SIZE, DEFAULT_HASH_MB_SIZE))
            net.load()?.let {
                process.writeLine(UCICommands.setOption(NET_PATH, it.absoluteFile))
            }
        } catch (ex: NoSuchElementException) {
            process.stop()
            throw EngineUnavailableException("The engine ended before it was ready", ex)
        }

        restartNeeded = false
        moveToState(State.Ready)
    }

    override suspend fun setOptions(options: Map<String, Any>) {
        awaitReady()
        options.forEach { (option, value) ->
            process.writeLine(UCICommands.setOption(option, value))
        }
    }

    override suspend fun newGame() {
        awaitReady()
        process.writeLine(NEW_GAME)
        awaitLine { it == READY_OK_TOKEN }
    }

    override suspend fun calculateBestMove(options: SearchOptions): String {
        // A stop is only answered once a search is under way; cancelled before then, there is
        // nothing to stop and no "bestmove" will ever come.
        var searching = false
        return try {
            awaitReady()

            moveToState(State.CalculatingMove)
            searching = true

            process.writeLine(UCICommands.position(options.fen, options.moves))
            process.writeLine(
                UCICommands.go(
                    wTime = options.whiteTimeLeft,
                    bTime = options.blackTimeLeft,
                    wInc = options.whiteTimeIncrement,
                    bInc = options.blackTimeIncrement,
                    movesToGo = options.movesToNextTimeControl,
                    depth = options.depth,
                    nodes = options.nodes,
                    mate = options.mate,
                    moveTime = options.moveTimeMillis,
                    infinite = options.infinite
                )
            )
            // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
            val move = awaitLine {
                it.startsWith(BEST_MOVE_TOKEN)
            }.split(" ")[1]

            moveToState(State.Ready)

            move
        } catch (ex: CancellationException) {
            if (!searching) throw ex
            withContext(NonCancellable) {
                // consume lines from process before moving to Ready state. The wait is bounded:
                // a "bestmove" handed over just as the search was cancelled is lost, an idle
                // engine does not answer a stop, and a wedged one answers nothing.
                val drained = try {
                    withTimeoutOrNull(STOP_ANSWER_TIMEOUT_MILLIS) {
                        process.writeLine(STOP)
                        awaitLine {
                            it.startsWith(BEST_MOVE_TOKEN)
                        }
                    } != null
                } catch (ignored: NoSuchElementException) {
                    // The engine went away while the search was being stopped.
                    false
                }
                if (drained) {
                    moveToState(State.Ready)
                } else {
                    // Its next answer could belong to the search just stopped: end this engine,
                    // and the next caller starts a fresh one.
                    stop()
                    restartNeeded = true
                }
            }
            throw ex
        } catch (ex: NoSuchElementException) {
            throw EngineUnavailableException("The engine stopped answering", ex)
        }
    }

    override suspend fun cancelMoveCalculation() {
        assertStateOrNull<State.CalculatingMove>()?.let {
            process.writeLine(STOP)
        }
    }

    override suspend fun stop() {
        restartNeeded = false
        process.writeLine(QUIT)
        process.stop()
        moveToState(State.Uninitialized)
    }

    @VisibleForTesting
    suspend fun awaitReady() {
        if (restartNeeded) start()
        awaitState<State.Ready>()
    }

    @VisibleForTesting
    suspend fun awaitUninitialized() {
        awaitState<State.Uninitialized>()
    }

    private inline fun <reified T : State> assertStateOrNull(): T? {
        return stateFlow.value as? T
    }

    private suspend inline fun <reified T : State> awaitState() {
        stateFlow.takeWhile { it !is T }.collect()
    }

    private fun moveToState(state: State) {
        stateFlow.value = state
    }

    private suspend fun awaitFirstLine(): String =
        process.receiveLines().first()

    private suspend fun awaitLine(predicate: suspend (String) -> Boolean) =
        process.receiveLines().dropWhile { !predicate(it) }.first()

    private sealed interface State {
        data object Uninitialized : State
        data object CalculatingMove : State
        data object Ready : State
    }

    private companion object {
        const val DEFAULT_HASH_MB_SIZE = 64

        const val UCI_OK_TOKEN = "uciok"
        const val READY_OK_TOKEN = "readyok"
        const val BEST_MOVE_TOKEN = "bestmove"

        /** Stockfish answers a stop within milliseconds; this is for one that never will. */
        const val STOP_ANSWER_TIMEOUT_MILLIS = 2_000L
    }
}
