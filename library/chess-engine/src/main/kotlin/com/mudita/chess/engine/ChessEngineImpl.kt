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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
internal class ChessEngineImpl(
    private val process: ChessEngineProcess,
    private val net: ChessEngineNet
) : ChessEngine {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override suspend fun start() {
        if (stateFlow.value !is State.Uninitialized) return

        process.start()
        process.monitor()
        awaitFirstLine()
        process.writeLine(UCI)
        awaitLine { it == UCI_OK_TOKEN }
        process.writeLine(UCICommands.setOption(HASH_SIZE, DEFAULT_HASH_MB_SIZE))
        net.load()?.let {
            process.writeLine(UCICommands.setOption(NET_PATH, it.absoluteFile))
        }

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
        return try {
            awaitReady()

            moveToState(State.CalculatingMove)

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
            withContext(NonCancellable) {
                launch {
                    process.writeLine(STOP)
                    // consume lines from process before moving to Ready state
                    awaitLine {
                        it.startsWith(BEST_MOVE_TOKEN)
                    }
                    moveToState(State.Ready)
                }
            }
            throw ex
        }
    }

    override suspend fun cancelMoveCalculation() {
        assertStateOrNull<State.CalculatingMove>()?.let {
            process.writeLine(STOP)
        }
    }

    override suspend fun stop() {
        process.writeLine(QUIT)
        process.stop()
        moveToState(State.Uninitialized)
    }

    @VisibleForTesting
    suspend fun awaitReady() {
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
    }
}
