package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.game.GameStatus.BLACK_WON
import com.mudita.chess.gameplay.game.GameStatus.CREATED
import com.mudita.chess.gameplay.game.GameStatus.DESTROYED
import com.mudita.chess.gameplay.game.GameStatus.DRAW
import com.mudita.chess.gameplay.game.GameStatus.PAUSED
import com.mudita.chess.gameplay.game.GameStatus.RESIGNED
import com.mudita.chess.gameplay.game.GameStatus.STARTED
import com.mudita.chess.gameplay.game.GameStatus.STOPPED
import com.mudita.chess.gameplay.game.GameStatus.WHITE_WON
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import logcat.logcat
import kotlin.coroutines.coroutineContext

internal enum class GameStatus {
    CREATED,
    STARTED,
    PAUSED,
    STOPPED,
    WHITE_WON,
    BLACK_WON,
    DRAW,
    RESIGNED,
    DESTROYED
}

@Suppress("TooManyFunctions")
internal class Game(
    val board: ChessBoard,
    private val whiteParticipant: Participant,
    private val blackParticipant: Participant,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val _status = MutableStateFlow(CREATED)

    val status: GameStatus
        get() = _status.value

    private var gameJob: Job? = null

    suspend fun setup(options: GameOptions) = withContext(ioDispatcher) {
        whiteParticipant.setup(options)
        blackParticipant.setup(options)
    }

    suspend fun loadMoves(moves: List<String>) = withContext(ioDispatcher) {
        board.loadMoves(moves)
        board.endgameStatus?.let { setStatus(it) }
    }

    suspend fun start(): Boolean =
        if (status in setOf(CREATED, STOPPED)) {
            gameJob = startGame()
            setStatus(STARTED)
            true
        } else {
            false
        }

    suspend fun startIfPaused() {
        if (status == PAUSED) {
            gameJob = startGame()
            setStatus(STARTED)
        }
    }

    suspend fun stop(): Boolean =
        if (status in setOf(STARTED, PAUSED)) {
            stopGame()
            setStatus(STOPPED)
            true
        } else {
            false
        }

    suspend fun pause() {
        if (status == STARTED) {
            stopGame()
            setStatus(PAUSED)
        }
    }

    suspend fun resign() {
        if (status in setOf(CREATED, STARTED, PAUSED, STOPPED)) {
            stopGame()
            setStatus(RESIGNED)
        }
    }

    suspend fun cleanup() = withContext(ioDispatcher) {
        whiteParticipant.cleanup()
        blackParticipant.cleanup()
        stopGame()
        setStatus(DESTROYED)
    }

    fun statuses(): Flow<GameStatus> =
        _status.asSharedFlow()

    private fun setStatus(status: GameStatus) {
        logcat { "Change game status from ${_status.value} to $status " }
        _status.update { status }
    }

    private suspend fun startGame(): Job {
        val supervisorJob = SupervisorJob()
        (CoroutineScope(coroutineContext) + supervisorJob).launch(ioDispatcher) {
            while (!board.isEndgame) {
                val sideToMove = board.sideToMove
                val participant = when (sideToMove) {
                    WHITE -> whiteParticipant
                    BLACK -> blackParticipant
                }
                participant.doMove()
            }
            setStatus(requireNotNull(board.endgameStatus))
        }
        return supervisorJob
    }

    private val ChessBoard.endgameStatus: GameStatus?
        get() = when {
            isMate -> if (sideToMove == WHITE) WHITE_WON else BLACK_WON
            isDraw -> DRAW
            else -> null
        }

    private suspend fun stopGame() {
        if (gameJob?.isCancelled == false) {
            gameJob?.cancelAndJoin()
        }
    }
}
