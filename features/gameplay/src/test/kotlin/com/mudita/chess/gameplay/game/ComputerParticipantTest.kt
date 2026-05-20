package com.mudita.chess.gameplay.game

import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.engine.ChessEngine
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.fixtures.ChessBoardStateData.BLACK_PLAYER_BOARD
import com.mudita.chess.gameplay.fixtures.replace
import com.mudita.chess.gameplay.fixtures.withHighlight
import com.mudita.chess.gameplay.fixtures.withMoves
import com.mudita.chess.gameplay.fixtures.withSideToMove
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.Idle
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.MoveCalculated
import com.mudita.chess.gameplay.game.ComputerParticipant.MoveState.MoveCalculating
import com.mudita.chess.gameplay.usecase.GetComputerMoveUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ComputerParticipantTest {

    private val engine: ChessEngine = mockk(relaxed = true)
    private val getComputerMoveUseCase: GetComputerMoveUseCase = mockk()

    private val gameOptions = GameOptions(isMoveSuggestionsOn = false, isPlayerWhite = false, DifficultyLevel(1))
    private val moveResultNotifier: MoveResultNotifier = mockk(relaxed = true)
    private val board = ChessBoard(topParticipantSide = WHITE)

    private val tested = ComputerParticipant(WHITE, board, moveResultNotifier, engine, getComputerMoveUseCase)


    @Test
    fun `setup starts engine`() = runTest {
        tested.setup(gameOptions)

        coVerify(exactly = 1) {
            engine.start()
        }
    }

    @Test
    fun `computer calculates move and plays it on board`() = runTest {
        val computerMoveDelayMillis = 100L

        coEvery {
            getComputerMoveUseCase(moves = emptyList(), sideToMove = WHITE, difficultyLevel = DifficultyLevel(1))
        } coAnswers {
            delay(computerMoveDelayMillis)
            Move(E2, E4)
        }

        tested.setup(gameOptions)
        launch { tested.doMove() }

        assertThat(tested.state).isEqualTo(Idle)
        // calculating state
        runCurrent()
        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
        assertThat(tested.state).isEqualTo(MoveCalculating)

        // piece selected state
        advanceTimeBy(computerMoveDelayMillis + 1)
        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withHighlight(E2))
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))

        // unconfirmed move state
        advanceTimeBy(COMPUTER_SELECTION_DELAY_MILLIS)
        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, Piece.NONE)
                .replace(E4, WHITE_PAWN)
                .withHighlight(E4)
        )
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }

        // confirmed move state
        advanceTimeBy(COMPUTER_SELECTION_DELAY_MILLIS)
        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, Piece.NONE)
                .replace(E4, WHITE_PAWN)
                .withMoves(Move(E2, E4))
                .withSideToMove(BLACK)
        )
        assertThat(tested.state).isEqualTo(Idle)
        coVerify(exactly = 1) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `computer calculating move is canceled when move turn is cancelled`() = runTest {
        val computerMoveDelayMillis = 100L

        coEvery {
            getComputerMoveUseCase(moves = emptyList(), sideToMove = WHITE, difficultyLevel = DifficultyLevel(1))
        } coAnswers {
            delay(computerMoveDelayMillis)
            Move(E2, E4)
        }

        tested.setup(gameOptions)
        val moveJob = launch { tested.doMove() }

        assertThat(tested.state).isEqualTo(Idle)
        // calculating state
        runCurrent()
        assertThat(tested.state).isEqualTo(MoveCalculating)

        moveJob.cancelAndJoin()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
        assertThat(tested.state).isEqualTo(Idle)
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `computer calculated move is preserved when move turn is cancelled`() = runTest {
        val computerMoveDelayMillis = 100L

        coEvery {
            getComputerMoveUseCase(moves = emptyList(), sideToMove = WHITE, difficultyLevel = DifficultyLevel(1))
        } coAnswers {
            delay(computerMoveDelayMillis)
            Move(E2, E4)
        }
        tested.setup(gameOptions)

        val moveJobToInterrupt = launch { tested.doMove() }
        assertThat(tested.state).isEqualTo(Idle)
        // calculating state
        runCurrent()
        assertThat(tested.state).isEqualTo(MoveCalculating)
        // piece selected state
        advanceTimeBy(computerMoveDelayMillis + 1)
        moveJobToInterrupt.cancelAndJoin()

        launch { tested.doMove() }
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        // piece selected state
        runCurrent()
        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withHighlight(E2))
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        // unconfirmed move state
        advanceTimeBy(COMPUTER_SELECTION_DELAY_MILLIS + 1)
        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, Piece.NONE)
                .replace(E4, WHITE_PAWN)
                .withHighlight(E4)
        )
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
        // confirmed move state
        advanceTimeBy(COMPUTER_SELECTION_DELAY_MILLIS)
        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, Piece.NONE)
                .replace(E4, WHITE_PAWN)
                .withMoves(Move(E2, E4))
                .withSideToMove(BLACK)
        )
        assertThat(tested.state).isEqualTo(Idle)
        coVerify(exactly = 1) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `computer piece selection is undone when move turn is cancelled`() = runTest {
        coEvery { getComputerMoveUseCase(any(), any(), any()) } returns Move(E2, E4)
        tested.setup(gameOptions)

        val moveJob = launch { tested.doMove() }

        runCurrent()

        board.states().test {
            val pieceSelectedState = awaitItem()
            assertThat(pieceSelectedState).isEqualTo(BLACK_PLAYER_BOARD.withHighlight(E2))

            moveJob.cancelAndJoin()

            val pieceSelectionUndoneState = awaitItem()
            assertThat(pieceSelectionUndoneState).isEqualTo(BLACK_PLAYER_BOARD)
        }
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `computer unconfirmed move is undone when move turn is cancelled`() = runTest {
        coEvery { getComputerMoveUseCase(any(), any(), any()) } returns Move(E2, E4)
        tested.setup(gameOptions)

        val moveJob = launch { tested.doMove() }

        runCurrent()

        board.states().test {
            skipItems(1)

            advanceTimeBy(COMPUTER_SELECTION_DELAY_MILLIS)
            val unconfirmedMoveState = awaitItem()
            assertThat(unconfirmedMoveState).isEqualTo(
                BLACK_PLAYER_BOARD
                    .replace(E2, Piece.NONE)
                    .replace(E4, WHITE_PAWN)
                    .withHighlight(E4)
            )

            moveJob.cancelAndJoin()

            val unconfirmedMoveUndoneState = awaitItem()
            assertThat(unconfirmedMoveUndoneState).isEqualTo(BLACK_PLAYER_BOARD)
        }
        assertThat(tested.state).isEqualTo(MoveCalculated(Move(E2, E4)))
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `cleanup stop engine`() = runTest {
        tested.cleanup()

        coVerify(exactly = 1) {
            engine.stop()
        }
    }

    private companion object {
        const val COMPUTER_SELECTION_DELAY_MILLIS = 300L
    }
}
