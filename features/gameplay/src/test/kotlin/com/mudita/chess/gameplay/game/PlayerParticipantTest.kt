package com.mudita.chess.gameplay.game

import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK
import com.github.bhlangonijr.chesslib.Piece.NONE
import com.github.bhlangonijr.chesslib.Piece.WHITE_KING
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN
import com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.PieceType.KNIGHT
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.Square.A1
import com.github.bhlangonijr.chesslib.Square.C1
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.E1
import com.github.bhlangonijr.chesslib.Square.F1
import com.github.bhlangonijr.chesslib.Square.F3
import com.github.bhlangonijr.chesslib.Square.G1
import com.github.bhlangonijr.chesslib.Square.G7
import com.github.bhlangonijr.chesslib.Square.H1
import com.github.bhlangonijr.chesslib.Square.H2
import com.github.bhlangonijr.chesslib.Square.H3
import com.github.bhlangonijr.chesslib.Square.H4
import com.github.bhlangonijr.chesslib.Square.H8
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.GameplayMapper
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmMoveButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmPawnPromotionClicked
import com.mudita.chess.gameplay.GameplayUiEvent.DialogDismissRequested
import com.mudita.chess.gameplay.GameplayUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.gameplay.GameplayUiEvent.SquareClicked
import com.mudita.chess.gameplay.GameplayUiEvent.UndoMoveButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvents
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.WHITE_PLAYER_SQUARES
import com.mudita.chess.gameplay.fixtures.ChessBoardStateData.WHITE_PLAYER_BOARD
import com.mudita.chess.gameplay.fixtures.after1RoundPgn
import com.mudita.chess.gameplay.fixtures.indexIn
import com.mudita.chess.gameplay.fixtures.replace
import com.mudita.chess.gameplay.fixtures.toMovesLAN
import com.mudita.chess.gameplay.fixtures.withHighlight
import com.mudita.chess.gameplay.fixtures.withHighlights
import com.mudita.chess.gameplay.fixtures.withMoveManualConfirmationRequired
import com.mudita.chess.gameplay.fixtures.withMoves
import com.mudita.chess.gameplay.fixtures.withSideToMove
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.AbandonedMove
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.ConfirmedMove
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.Idle
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.PieceSelected
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.PromotionConfirmationRequired
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.UnconfirmedMove
import com.mudita.chess.gameplay.model.GameplayDialogType.PAWN_PROMOTION
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PlayerParticipantTest {

    private val moveResultNotifier: MoveResultNotifier = mockk(relaxed = true)
    private val board = ChessBoard(topParticipantSide = BLACK)
    private val uiEvents = GameplayUiEvents()
    private val mapper = GameplayMapper()

    private val gameOptions = GameOptions(isMoveSuggestionsOn = false, isPlayerWhite = true, DifficultyLevel(1))

    private val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)

    @Test
    fun `player clicks on empty square are ignored`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.B5))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player clicks on their opponent's piece are ignored`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G8))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player clicks on his piece during their opponent's turn are ignored`() = runTest {
        val board = ChessBoard(topParticipantSide = WHITE)
        val tested = PlayerParticipant(BLACK, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions.copy(isPlayerWhite = false))
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.B8))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player selects piece with move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = true))
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

            verifyPlayerSelectsPiece(awaitItem(), setOf(G1, F3, H3))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(G1)
        assertThat(state.legalMoves).containsExactly(Move(G1, F3), Move(G1, H3))
    }

    @Test
    fun `player selects piece without move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = false))
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

            verifyPlayerSelectsPiece(awaitItem(), setOf(G1))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(G1)
        assertThat(state.legalMoves).containsExactly(Move(G1, F3), Move(G1, H3))
    }

    @Test
    fun `player unselects piece`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

            verifyWhitePlayerInitialBoard(awaitItem())
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player selects one piece then selects another piece with move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = true))
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2, H3, H4))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(H2)
        assertThat(state.legalMoves).containsExactly(Move(H2, H3), Move(H2, H4))
    }

    @Test
    fun `player selects one piece then selects another piece without move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = false))
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(H2)
        assertThat(state.legalMoves).containsExactly(Move(H2, H3), Move(H2, H4))
    }

    @Test
    fun `player selects piece then selects empty square`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.C3))

            expectNoEvents()
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(G1)
        assertThat(state.legalMoves).containsExactly(Move(G1, F3), Move(G1, H3))
    }

    @Test
    fun `player selects piece then selects their opponent's piece`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G8))

            expectNoEvents()
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(G1)
        assertThat(state.legalMoves).containsExactly(Move(G1, F3), Move(G1, H3))
    }

    @Test
    fun `player piece selection is preserved when move turn is cancelled`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        val moveJob = launch { tested.doMove() }
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            moveJob.cancelAndJoin()

            expectNoEvents()
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(G1)
        assertThat(state.legalMoves).containsExactly(Move(G1, F3), Move(G1, H3))
    }

    @Test
    fun `player selects piece then selects legal target square`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

            verifyPlayerMakeMove(awaitItem(), KNIGHT, Move(G1, F3))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(G1, F3)))
        coVerify(exactly = 0) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `player selects pawn then selects promotion target square`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G7))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H8))

            val squares = WHITE_PLAYER_SQUARES
            val boardState = awaitItem()
            assertThat(boardState.pieces[G7 indexIn squares]).isEqualTo(WHITE_PAWN)
            assertThat(boardState.pieces[H8 indexIn squares]).isEqualTo(BLACK_ROOK)
            assertThat(boardState.highlights).isEqualTo(setOf(G7))
            assertThat(boardState.isPromotionManualConfirmationRequired).isTrue()
        }
        assertThat(tested.state).isEqualTo(PromotionConfirmationRequired(G7, H8))
    }

    @Test
    fun `player selects pawn to promote then confirms promotion`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G7))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H8))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(ConfirmPawnPromotionClicked(PieceUi(PieceTypeUi.QUEEN, isWhite = true)))

            val squares = WHITE_PLAYER_SQUARES
            val boardState = awaitItem()
            assertThat(boardState.pieces[G7 indexIn squares]).isEqualTo(NONE)
            assertThat(boardState.pieces[H8 indexIn squares]).isEqualTo(WHITE_QUEEN)
            assertThat(boardState.highlights).isEqualTo(setOf(H8))
            assertThat(boardState.isPromotionManualConfirmationRequired).isFalse()
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(G7, H8, WHITE_QUEEN)))
    }

    @Test
    fun `player selects pawn to promote then cancels promotion`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G7))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H8))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(DialogDismissRequested(PAWN_PROMOTION))

            val squares = WHITE_PLAYER_SQUARES
            val boardState = awaitItem()
            assertThat(boardState.pieces[G7 indexIn squares]).isEqualTo(WHITE_PAWN)
            assertThat(boardState.pieces[H8 indexIn squares]).isEqualTo(BLACK_ROOK)
            assertThat(boardState.highlights).isEmpty()
            assertThat(boardState.isPromotionManualConfirmationRequired).isFalse()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player selects pawn to promote then square clicks ignored until promotion completes`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G7))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H8))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(PromotionConfirmationRequired(G7, H8))
    }

    @Test
    fun `player selects pawn to promote then promotion cancels when move turn is cancelled`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        val moveJob = launch { tested.doMove() }
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G7))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H8))

        board.states().test {
            skipItems(1)

            moveJob.cancelAndJoin()

            val squares = WHITE_PLAYER_SQUARES
            val boardState = awaitItem()
            assertThat(boardState.pieces[G7 indexIn squares]).isEqualTo(WHITE_PAWN)
            assertThat(boardState.pieces[H8 indexIn squares]).isEqualTo(BLACK_ROOK)
            assertThat(boardState.highlights).isEmpty()
            assertThat(boardState.isPromotionManualConfirmationRequired).isFalse()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player makes unconfirmed move then unselects piece`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

            verifyWhitePlayerInitialBoard(awaitItem())
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `player makes unconfirmed move then clicks on square which pieced moved from`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(G1, F3)))
    }

    @Test
    fun `player makes unconfirmed king side castle move then selects affected rook piece`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/ppp2ppp/8/4p3/3p2P1/5N1B/PPPPPP1P/RNBQK2R w KQkq - 0 4")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.F1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[F1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[G1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[H1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(G1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(E1, G1)))
    }

    @Test
    fun `player makes unconfirmed king side castle move then clicks on square which king moved from`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/ppp2ppp/8/4p3/3p2P1/5N1B/PPPPPP1P/RNBQK2R w KQkq - 0 4")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[F1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[G1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[H1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(G1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(E1, G1)))
    }

    @Test
    fun `player makes unconfirmed king side castle move then clicks on square which rook moved from`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/ppp2ppp/8/4p3/3p2P1/5N1B/PPPPPP1P/RNBQK2R w KQkq - 0 4")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[F1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[G1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[H1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(G1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(E1, G1)))
    }

    @Test
    fun `player makes unconfirmed queen side castle move then selects affected rook piece`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/pp4pp/2pppp2/8/8/2NPB3/PPPQPPPP/R3KBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.C1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.D1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[D1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[C1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[A1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(C1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(E1, C1)))
    }

    @Test
    fun `player makes unconfirmed queen side castle move then clicks on square which king moved from`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/pp4pp/2pppp2/8/8/2NPB3/PPPQPPPP/R3KBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.C1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[D1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[C1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[A1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(C1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(E1, C1)))
    }

    @Test
    fun `player makes unconfirmed queen side castle move then clicks on square which rook moved from`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/pp4pp/2pppp2/8/8/2NPB3/PPPQPPPP/R3KBNR w KQkq - 0 5")
        val tested = PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.E1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.C1))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.A1))

            val squares = WHITE_PLAYER_SQUARES
            assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.pieces[D1 indexIn squares]).isEqualTo(WHITE_ROOK)
            assertThat(board.state.pieces[C1 indexIn squares]).isEqualTo(WHITE_KING)
            assertThat(board.state.pieces[A1 indexIn squares]).isEqualTo(NONE)
            assertThat(board.state.highlights).isEqualTo(setOf(C1))
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove((Move(E1, C1))))
    }

    @Test
    fun `player makes unconfirmed move then selects another piece with move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = true))
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2, H3, H4))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(H2)
        assertThat(state.legalMoves).containsExactly(Move(H2, H3), Move(H2, H4))
    }

    @Test
    fun `player makes unconfirmed move then selects another piece without move suggestions`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = false))
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2))
        }
        val state = tested.state as PieceSelected
        assertThat(state.from).isEqualTo(H2)
        assertThat(state.legalMoves).containsExactly(Move(H2, H3), Move(H2, H4))
    }

    @Test
    fun `player makes unconfirmed move then selects empty square`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.C3))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove((Move(G1, F3))))
    }

    @Test
    fun `player makes unconfirmed move then selects their opponent's piece`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.G8))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove((Move(G1, F3))))
    }

    @Test
    fun `player unconfirmed move is preserved when move turn is cancelled`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        val moveJob = launch { tested.doMove() }
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            moveJob.cancelAndJoin()

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(UnconfirmedMove(Move(G1, F3)))
    }

    @Test
    fun `player makes unconfirmed move then clicks on confirm move button`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(ConfirmedMove)
    }

    @Test
    fun `player clicks on confirm move button then square clicks ignored until move confirmed`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))
        uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(ConfirmedMove)
    }

    @Test
    fun `player clicks on confirm move button then move confirmed`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))

        val moveJob = launch { tested.doMove() }

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

            moveJob.join()
            verifyPlayerMakeMove(awaitItem(), KNIGHT, Move(G1, F3), confirmed = true)
        }
        assertThat(tested.state).isEqualTo(Idle)
        coVerify(exactly = 1) {
            moveResultNotifier.maybeNotifyCheck(any())
        }
    }

    @Test
    fun `player clicks on confirm move button when no unconfirmed move performed are ignored`() {
        runTest {
            tested.setup(gameOptions)
            advanceUntilIdle()

            board.states().test {
                skipItems(1)

                uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

                expectNoEvents()
            }
        }
    }

    @Test
    fun `player clicks on confirm move button during their opponent's turn are ignored`() = runTest {
        val board = ChessBoard(topParticipantSide = WHITE)
        val tested = PlayerParticipant(BLACK, board, moveResultNotifier, uiEvents, mapper)
        tested.setup(gameOptions.copy(isPlayerWhite = false))
        advanceUntilIdle()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

            expectNoEvents()
        }
        assertThat(tested.state).isEqualTo(Idle)
    }

    @Test
    fun `cleanup stop collecting ui events`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()

        uiEvents.handleUiEvent(SquareClicked(PositionUi.G1))
        uiEvents.handleUiEvent(SquareClicked(PositionUi.F3))
        tested.cleanup()

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(ConfirmMoveButtonClicked)

            expectNoEvents()
        }
        assertThat(tested.state).isInstanceOf(UnconfirmedMove::class.java)
    }

    @Test
    fun `move suggestion switch toggle on highlights selected piece and legal moves`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(MoveSuggestionsSwitchToggled(on = true))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2, H3, H4))
        }
    }

    @Test
    fun `move suggestion switch toggle off highlights selected piece`() = runTest {
        tested.setup(gameOptions.copy(isMoveSuggestionsOn = true))
        advanceUntilIdle()
        uiEvents.handleUiEvent(SquareClicked(PositionUi.H2))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(MoveSuggestionsSwitchToggled(on = false))

            verifyPlayerSelectsPiece(awaitItem(), setOf(H2))
        }
    }

    @Test
    fun `player clicks on undo move button round and their move is undone`() = runTest {
        tested.setup(gameOptions)
        advanceUntilIdle()
        board.loadMoves(toMovesLAN(after1RoundPgn))

        board.states().test {
            skipItems(1)

            uiEvents.handleUiEvent(UndoMoveButtonClicked)

            verifyWhitePlayerInitialBoard(awaitItem())
            assertThat(tested.state).isEqualTo(AbandonedMove)
        }
    }

    private fun verifyWhitePlayerInitialBoard(
        testedState: ChessBoardState
    ) = assertThat(testedState).isEqualTo(WHITE_PLAYER_BOARD)

    private fun verifyPlayerSelectsPiece(
        testedState: ChessBoardState,
        highlights: Set<Square>,
    ) = assertThat(testedState).isEqualTo(WHITE_PLAYER_BOARD.withHighlights(highlights))

    private fun verifyPlayerMakeMove(
        testedState: ChessBoardState,
        pieceType: PieceType,
        move: Move,
        confirmed: Boolean = false
    ) = assertThat(testedState).isEqualTo(
        WHITE_PLAYER_BOARD
            .replace(move.from, NONE)
            .replace(move.to, Piece.make(WHITE, pieceType))
            .let { state ->
                if (!confirmed) state
                    .withHighlight(move.to)
                    .withMoveManualConfirmationRequired()
                else state
                    .withMoves(move)
                    .withSideToMove(BLACK)
            }
    )
}
