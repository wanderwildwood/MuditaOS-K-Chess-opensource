package com.mudita.chess.gameplay

import app.cash.turbine.turbineScope
import com.github.bhlangonijr.chesslib.Piece.BLACK_KING
import com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT
import com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN
import com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK
import com.github.bhlangonijr.chesslib.Piece.NONE
import com.github.bhlangonijr.chesslib.Piece.WHITE_KING
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN
import com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square.A2
import com.github.bhlangonijr.chesslib.Square.A4
import com.github.bhlangonijr.chesslib.Square.C2
import com.github.bhlangonijr.chesslib.Square.C6
import com.github.bhlangonijr.chesslib.Square.C7
import com.github.bhlangonijr.chesslib.Square.C8
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.D2
import com.github.bhlangonijr.chesslib.Square.D4
import com.github.bhlangonijr.chesslib.Square.D5
import com.github.bhlangonijr.chesslib.Square.D7
import com.github.bhlangonijr.chesslib.Square.E1
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E3
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.Square.E5
import com.github.bhlangonijr.chesslib.Square.E7
import com.github.bhlangonijr.chesslib.Square.E8
import com.github.bhlangonijr.chesslib.Square.F1
import com.github.bhlangonijr.chesslib.Square.G1
import com.github.bhlangonijr.chesslib.Square.G4
import com.github.bhlangonijr.chesslib.Square.H1
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.WHITE_PLAYER_SQUARES
import com.mudita.chess.gameplay.fixtures.ChessBoardStateData.BLACK_PLAYER_BOARD
import com.mudita.chess.gameplay.fixtures.ChessBoardStateData.WHITE_PLAYER_BOARD
import com.mudita.chess.gameplay.fixtures.blackInCheckPgn
import com.mudita.chess.gameplay.fixtures.indexIn
import com.mudita.chess.gameplay.fixtures.replace
import com.mudita.chess.gameplay.fixtures.toMovesLAN
import com.mudita.chess.gameplay.fixtures.whiteParticipantWonPgn
import com.mudita.chess.gameplay.fixtures.after1RoundPgn
import com.mudita.chess.gameplay.fixtures.after2RoundsPgn
import com.mudita.chess.gameplay.fixtures.afterHalfRoundPgn
import com.mudita.chess.gameplay.fixtures.withCheckInfo
import com.mudita.chess.gameplay.fixtures.withHighlight
import com.mudita.chess.gameplay.fixtures.withHighlights
import com.mudita.chess.gameplay.fixtures.withMoveManualConfirmationRequired
import com.mudita.chess.gameplay.fixtures.withMoves
import com.mudita.chess.gameplay.fixtures.withPieces
import com.mudita.chess.gameplay.fixtures.withPromotionManualConfirmationRequired
import com.mudita.chess.gameplay.fixtures.withSideToMove
import com.mudita.chess.gameplay.game.CheckInfo
import com.mudita.chess.gameplay.game.ChessBoard
import com.mudita.chess.gameplay.game.LocatedPiece
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChessBoardTest {

    @Test
    fun `ChessBoard constructs with default values for white player`() {
        val board = ChessBoard(topParticipantSide = BLACK)

        assertThat(board.sideToMove).isEqualTo(WHITE)
        assertThat(board.state).isEqualTo(WHITE_PLAYER_BOARD)
    }

    @Test
    fun `ChessBoard constructs with default values for black player`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        assertThat(board.sideToMove).isEqualTo(WHITE)
        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
    }

    @Test
    fun `ChessBoard constructs with empty pieces for white player when pieces position is not ready`() {
        val board = ChessBoard(topParticipantSide = BLACK, isPiecesPositionReady = false)

        assertThat(board.sideToMove).isEqualTo(WHITE)
        assertThat(board.state).isEqualTo(WHITE_PLAYER_BOARD.withPieces(emptyList()))
    }

    @Test
    fun `ChessBoard constructs with empty pieces for black player when pieces position is not ready`() {
        val board = ChessBoard(topParticipantSide = WHITE, isPiecesPositionReady = false)

        assertThat(board.sideToMove).isEqualTo(WHITE)
        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withPieces(emptyList()))
    }

    @Test
    fun `getPiece returns piece on given square`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        assertThat(board.getPiece(E2)).isEqualTo(WHITE_PAWN)
    }


    @Test
    fun `getPiece returns NONE on empty square`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        assertThat(board.getPiece(E5)).isEqualTo(NONE)
    }

    @Test
    fun `getPiece returns NONE when pieces position is not ready`() {
        val board = ChessBoard(topParticipantSide = WHITE, isPiecesPositionReady = false)

        assertThat(board.getPiece(E2)).isEqualTo(NONE)
    }

    @Test
    fun `getLegalMoves returns moves for given square`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        assertThat(board.getLegalMoves(E2)).isEqualTo(listOf(Move(E2, E3), Move(E2, E4)))
    }

    @Test
    fun `getLegalMoves returns empty list when pieces position is not ready`() {
        val board = ChessBoard(topParticipantSide = WHITE, isPiecesPositionReady = false)

        assertThat(board.getLegalMoves(E2)).isEmpty()
    }

    @Test
    fun `moves return only confirmed moves`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.loadMoves(listOf("e2e4"))

        board.doUnconfirmedMove(Move(E7, E5))

        assertThat(board.moves).isEqualTo(listOf(Move(E2, E4)))
    }

    @Test
    fun `setHighlighted highlights selected single square`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.setHighlighted(E2)

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withHighlight(E2))
    }

    @Test
    fun `setHighlighted highlights multiple selected square`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.setHighlighted(setOf(E2, E3, E4))

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withHighlights(setOf(E2, E3, E4)))
    }

    @Test
    fun `clearHighlighted removes highlighted squares`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.setHighlighted(setOf(E2, E3, E4))

        board.clearHighlighted()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
    }

    @Test
    fun `setPromotionManualConfirmationRequired produces board state with promotion manual confirmation required`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.setPromotionManualConfirmationRequired()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD.withPromotionManualConfirmationRequired())
    }

    @Test
    fun `clearPromotionManualConfirmationRequired produces board state without promotion manual confirmation required`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.setPromotionManualConfirmationRequired()

        board.clearPromotionManualConfirmationRequired()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
    }

    @Test
    fun `doUnconfirmedMove shows board state after simple move`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4))

        val expectedState = BLACK_PLAYER_BOARD
            .replace(E2, NONE)
            .replace(E4, WHITE_PAWN)
        assertThat(board.state).isEqualTo(expectedState)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `doUnconfirmedMove shows board state after castling move`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/ppp2ppp/8/4p3/3p2P1/5N1B/PPPPPP1P/RNBQK2R w KQkq - 0 4")

        board.doUnconfirmedMove(Move(E1, G1))

        val squares = WHITE_PLAYER_SQUARES
        assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(NONE)
        assertThat(board.state.pieces[F1 indexIn squares]).isEqualTo(WHITE_ROOK)
        assertThat(board.state.pieces[G1 indexIn squares]).isEqualTo(WHITE_KING)
        assertThat(board.state.pieces[H1 indexIn squares]).isEqualTo(NONE)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `doUnconfirmedMove with manual confirmation sets move manual confirmation required to true`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4), isManualConfirmationRequired = true)

        val expectedState = BLACK_PLAYER_BOARD
            .replace(E2, NONE)
            .replace(E4, WHITE_PAWN)
            .withMoveManualConfirmationRequired()
        assertThat(board.state).isEqualTo(expectedState)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `doUnconfirmedMove doesn't set check info if their opponent in check`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("2r1k3/8/2n5/8/8/8/2K5/8 b - - 0 1")

        board.doUnconfirmedMove(Move(C6, E5))

        assertThat(board.state.checkInfo).isNull()
    }

    @Test
    fun `undoUnconfirmedMove shows board state before simple move`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4))
        board.undoUnconfirmedMove()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoUnconfirmedMove shows board state before castling move`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("rnbqkbnr/ppp2ppp/8/4p3/3p2P1/5N1B/PPPPPP1P/RNBQK2R w KQkq - 0 4")

        board.doUnconfirmedMove(Move(E1, G1))
        board.undoUnconfirmedMove()

        val squares = WHITE_PLAYER_SQUARES
        assertThat(board.state.pieces[E1 indexIn squares]).isEqualTo(WHITE_KING)
        assertThat(board.state.pieces[F1 indexIn squares]).isEqualTo(NONE)
        assertThat(board.state.pieces[G1 indexIn squares]).isEqualTo(NONE)
        assertThat(board.state.pieces[H1 indexIn squares]).isEqualTo(WHITE_ROOK)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoUnconfirmedMove sets move manual confirmation required to false`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4), isManualConfirmationRequired = true)
        board.undoUnconfirmedMove()

        assertThat(board.state).isEqualTo(BLACK_PLAYER_BOARD)
        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoUnconfirmedMove throws state exception if no unconfirmed move performed`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        val error = assertThrows<IllegalStateException> {
            board.undoUnconfirmedMove()
        }
        assertThat(error.message).isEqualTo("There is no unconfirmed move to undo")
    }

    @Test
    fun `confirmMove changes side to move`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4))
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isFalse()
        val expectedState = BLACK_PLAYER_BOARD
            .replace(E2, NONE)
            .replace(E4, WHITE_PAWN)
            .withMoves(Move(E2, E4))
            .withSideToMove(BLACK)
        assertThat(board.state).isEqualTo(expectedState)
    }

    @Test
    fun `confirmMove sets move manual confirmation required to false`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.doUnconfirmedMove(Move(E2, E4), isManualConfirmationRequired = true)
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isFalse()
        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, NONE)
                .replace(E4, WHITE_PAWN)
                .withMoves(Move(E2, E4))
                .withSideToMove(BLACK)
        )
    }

    @Test
    fun `confirmMove do nothing if no unconfirmed move performed`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        val error = assertThrows<IllegalStateException> {
            board.confirmMove()
        }
        assertThat(error.message).isEqualTo("There is no unconfirmed move to confirm")
    }

    @Test
    fun `confirmMove sets check info with single attacker if their opponent in check`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("2r1k3/8/2n5/8/8/8/2K5/8 b - - 0 1")

        board.doUnconfirmedMove(Move(C6, E5))
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isTrue()
        assertThat(board.state.checkInfo).isEqualTo(
            CheckInfo(
                king = LocatedPiece(WHITE_KING, C2),
                attackedBy = listOf(LocatedPiece(BLACK_ROOK, C8)),
                acknowledgeRequired = true
            )
        )
    }

    @Test
    fun `confirmMove sets check info with multiple attackers if their opponent in check`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("2r1k3/8/2n5/8/8/8/2K5/8 b - - 0 1")

        board.doUnconfirmedMove(Move(C6, D4))
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isTrue()
        assertThat(board.state.checkInfo).isEqualTo(
            CheckInfo(
                king = LocatedPiece(WHITE_KING, C2),
                attackedBy = listOf(LocatedPiece(BLACK_KNIGHT, D4), LocatedPiece(BLACK_ROOK, C8)),
                acknowledgeRequired = true
            )
        )
    }

    @Test
    fun `confirmMove don't set check info if their opponent in checkmate`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        val moves = toMovesLAN(whiteParticipantWonPgn)
        val playedMoves = moves.dropLast(1)
        val moveToPlay = moves.last()
        board.loadMoves(playedMoves)

        board.doUnconfirmedMove(Move(moveToPlay, WHITE))
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isFalse()
        assertThat(board.state.checkInfo).isNull()
    }

    @Test
    fun `confirmMove clears check info if check is resolved`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("2r1k3/8/2n5/8/8/8/2K5/8 b - - 0 1")
        board.doUnconfirmedMove(Move(C6, D4))
        board.confirmMove()
        board.clearCheckAcknowledgeRequired()

        board.doUnconfirmedMove(Move(C2, D2))
        val moveResult = board.confirmMove()

        assertThat(moveResult.opponentInCheck).isFalse()
        assertThat(board.state.checkInfo).isNull()
    }

    @Test
    fun `clearCheckAcknowledgeRequired clear check info acknowledge required flag`() = runTest {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadFen("2r1k3/8/2n5/8/8/8/2K5/8 b - - 0 1")
        board.doUnconfirmedMove(Move(C6, D4))
        board.confirmMove()

        board.clearCheckAcknowledgeRequired()

        assertThat(board.state.checkInfo).isEqualTo(
            CheckInfo(
                king = LocatedPiece(WHITE_KING, C2),
                attackedBy = listOf(LocatedPiece(BLACK_KNIGHT, D4), LocatedPiece(BLACK_ROOK, C8)),
                acknowledgeRequired = false
            )
        )
    }

    @Test
    fun `loadMoves applies moves on a board and updates pieces position to ready`() {
        val board = ChessBoard(topParticipantSide = WHITE, isPiecesPositionReady = false)

        board.loadMoves(listOf("e2e4"))

        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(E2, NONE)
                .replace(E4, WHITE_PAWN)
                .withMoves(Move(E2, E4))
                .withSideToMove(BLACK)
        )
    }

    @Test
    fun `loadMoves applies moves on a board, detects check and updates pieces position to ready`() {
        val board = ChessBoard(topParticipantSide = WHITE, isPiecesPositionReady = false)

        board.loadMoves(toMovesLAN(blackInCheckPgn))

        assertThat(board.state).isEqualTo(
            BLACK_PLAYER_BOARD
                .replace(D1, NONE)
                .replace(E2, NONE)
                .replace(E4, WHITE_PAWN)
                .replace(D5, BLACK_PAWN)
                .replace(C6, BLACK_PAWN)
                .replace(C7, NONE)
                .replace(D7, WHITE_QUEEN)
                .withMoves(
                    Move(E2, E4), Move(D7, D5),
                    Move(D1, G4), Move(C7, C6),
                    Move(G4, D7)
                )
                .withSideToMove(BLACK)
                .withCheckInfo(
                    CheckInfo(
                        king = LocatedPiece(BLACK_KING, E8),
                        attackedBy = listOf(LocatedPiece(WHITE_QUEEN, D7)),
                        acknowledgeRequired = false
                    )
                )
        )
    }

    @Test
    fun `update produce state change on each invocation when moving and confirming`() = runTest {
        turbineScope {
            val board = ChessBoard(topParticipantSide = WHITE)
            val turbine = board.states().testIn(this@turbineScope)
            turbine.skipItems(1)

            board.update {
                doUnconfirmedMove(Move(E2, E4))
                setHighlighted(E4)
            }
            board.update {
                confirmMove()
                clearHighlighted()
            }

            assertThat(turbine.awaitItem()).isEqualTo(
                BLACK_PLAYER_BOARD
                    .replace(E2, NONE)
                    .replace(E4, WHITE_PAWN)
                    .withHighlight(E4)
            )

            assertThat(turbine.awaitItem()).isEqualTo(
                BLACK_PLAYER_BOARD
                    .replace(E2, NONE)
                    .replace(E4, WHITE_PAWN)
                    .withMoves(Move(E2, E4))
                    .withSideToMove(BLACK)
            )

            turbine.cancel()
            turbine.ensureAllEventsConsumed()
        }
    }

    @Test
    fun `update produce state change on each invocation when moving and undoing`() = runTest {
        turbineScope {
            val board = ChessBoard(topParticipantSide = WHITE)
            val turbine = board.states().testIn(this@turbineScope)
            turbine.skipItems(1)

            board.update {
                doUnconfirmedMove(Move(E2, E4))
                setHighlighted(E4)
            }
            board.update {
                undoUnconfirmedMove()
                clearHighlighted()
            }

            assertThat(turbine.awaitItem()).isEqualTo(
                BLACK_PLAYER_BOARD
                    .replace(E2, NONE)
                    .replace(E4, WHITE_PAWN)
                    .withHighlight(E4)
            )

            assertThat(turbine.awaitItem()).isEqualTo(
                BLACK_PLAYER_BOARD
            )

            turbine.cancel()
            turbine.ensureAllEventsConsumed()
        }
    }

    @Test
    fun `undoRound does nothing if no moves performed`() {
        val board = ChessBoard(topParticipantSide = WHITE)

        board.undoRound()

        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes half round after half round`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.loadMoves(toMovesLAN(afterHalfRoundPgn))

        board.undoRound()

        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes half round after half round and unconfirmed move`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.loadMoves(toMovesLAN(afterHalfRoundPgn))
        board.doUnconfirmedMove(Move(E2, E4))

        board.undoRound()

        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes 1 round after 1 round`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadMoves(toMovesLAN(after1RoundPgn))

        board.undoRound()

        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes 1 round after 1 round and unconfirmed move`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadMoves(toMovesLAN(after1RoundPgn))
        board.doUnconfirmedMove(Move(A2, A4))

        board.undoRound()

        assertThat(board.moves).isEmpty()
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes 1 round after 2 rounds`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadMoves(toMovesLAN(after2RoundsPgn))

        board.undoRound()

        assertThat(board.moves).isEqualTo(listOf(Move(E2, E4), Move(E7, E5)))
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }

    @Test
    fun `undoRound undoes 1 round after 2 rounds and unconfirmed move`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        board.loadMoves(toMovesLAN(after2RoundsPgn))
        board.doUnconfirmedMove(Move(A2, A4))

        board.undoRound()

        assertThat(board.moves).isEqualTo(listOf(Move(E2, E4), Move(E7, E5)))
        assertThat(board.sideToMove).isEqualTo(WHITE)
    }
}
