package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.engine.ChessEngine
import com.mudita.chess.engine.EngineUnavailableException
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.Idle
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.MoveCalculated
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.MoveCalculating
import com.mudita.chess.gameplay.usecase.GetComputerMoveUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import logcat.logcat

internal class ComputerParticipant(
    override val side: Side,
    private val board: ChessBoard,
    private val moveResultNotifier: MoveResultNotifier,
    private val engine: ChessEngine,
    private val getComputerMoveUseCase: GetComputerMoveUseCase
) : Participant {

    private var difficultyLevel: DifficultyLevel = DifficultyLevel(1)
    private val _state = MutableStateFlow<MoveState>(Idle)

    val state: MoveState
        get() = _state.value

    override suspend fun setup(options: GameOptions) {
        difficultyLevel = options.difficultyLevel
        try {
            engine.start()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: EngineUnavailableException) {
            throw ex
        } catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
            throw EngineUnavailableException("The engine could not be started", ex)
        }
    }

    override suspend fun doMove() {
        val move = (state as? MoveCalculated)?.move ?: calculateMove()

        try {
            board.setHighlighted(move.from)
            delay(COMPUTER_SELECTION_DELAY_MILLIS)
        } catch (ex: CancellationException) {
            board.clearHighlighted()
            throw ex
        }

        try {
            board.update {
                doUnconfirmedMove(move)
                setHighlighted(move.to)
            }
            delay(COMPUTER_SELECTION_DELAY_MILLIS)
        } catch (ex: CancellationException) {
            board.update {
                undoUnconfirmedMove()
                clearHighlighted()
            }
            throw ex
        }

        val moveResult = board.update {
            clearHighlighted()
            confirmMove()
        }
        moveToState(Idle)
        moveResultNotifier.maybeNotifyCheck(moveResult)
    }

    private suspend fun calculateMove(): Move =
        try {
            moveToState(MoveCalculating)
            val move = getComputerMoveUseCase(board.moves, side, difficultyLevel)
            moveToState(MoveCalculated(move))
            move
        } catch (ex: CancellationException) {
            moveToState(Idle)
            throw ex
        } catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
            // Whatever the engine threw, this runs on a background thread where nothing is
            // waiting to catch it: say the engine is gone and let the game deal with that.
            moveToState(Idle)
            throw ex as? EngineUnavailableException
                ?: EngineUnavailableException("The engine failed while choosing a move", ex)
        }

    override suspend fun cleanup() {
        try {
            engine.stop()
        } catch (ex: CancellationException) {
            throw ex
        } catch (@Suppress("TooGenericExceptionCaught") ex: Exception) {
            logcat { "Engine did not stop cleanly: $ex" }
        }
    }

    private fun moveToState(state: MoveState) {
        _state.update { state }
    }

    sealed interface MoveState {
        data object Idle : MoveState
        data object MoveCalculating : MoveState
        data class MoveCalculated(val move: Move) : MoveState
    }

    private companion object {
        const val COMPUTER_SELECTION_DELAY_MILLIS = 300L
    }
}
