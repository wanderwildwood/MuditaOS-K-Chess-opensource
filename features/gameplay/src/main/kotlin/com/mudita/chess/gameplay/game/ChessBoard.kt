package com.mudita.chess.gameplay.game

import androidx.annotation.VisibleForTesting
import com.github.bhlangonijr.chesslib.Bitboard
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType.KING
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.game.GameContext
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import logcat.logcat

@Suppress("TooManyFunctions")
internal class ChessBoard(
    topParticipantSide: Side,
    private var isPiecesPositionReady: Boolean = true
) {

    private val squares = squaresFromTopLeftToBottomRight(topParticipantSide)
    private val board = Board()
    private var highlighted: Set<Square> = emptySet()

    private var updating = false
    private var isLastMoveConfirmed = true
    private var isLastMoveManualConfirmationRequired = false
    private var isPromotionManualConfirmationRequired = false
    private var checkInfo: CheckInfo? = null

    private val _state = MutableStateFlow(
        ChessBoardState(
            sideToMove = sideToMove,
            squares = squares,
            pieces = boardPieces()
        )
    )

    val state: ChessBoardState
        get() = _state.value

    val sideToMove: Side
        get() = if (isEndgame || !isLastMoveCompleted()) board.sideToMove.flip() else board.sideToMove

    val moves: List<Move>
        get() = movesBackup
            .map { it.move }

    val movesBackup: List<MoveBackup>
        get() = when (isLastMoveConfirmed) {
            true -> board.backup
            false -> board.backup.dropLast(1)
        }

    val context: GameContext
        get() = board.context

    val isEndgame: Boolean
        get() = isMate || isDraw

    val isMate: Boolean
        get() = isLastMoveConfirmed && board.isMated

    val isDraw: Boolean
        get() = isLastMoveConfirmed && board.isDraw

    fun getPiece(square: Square): Piece = when (isPiecesPositionReady) {
        true -> board.getPiece(square)
        false -> Piece.NONE
    }

    fun getLegalMoves(
        square: Square
    ): List<Move> = when (isPiecesPositionReady) {
        true -> board.legalMoves().filter { it.from == square }
        false -> emptyList()
    }

    fun <R> update(block: ChessBoard.() -> R): R {
        updating = true
        val result = block(this)
        rebuildState()
        updating = false
        return result
    }

    fun setHighlighted(square: Square) = setHighlighted(setOf(square))

    fun setHighlighted(squares: Set<Square>) {
        highlighted = squares
        if (!updating) rebuildState()
    }

    fun clearHighlighted() =
        setHighlighted(emptySet())

    fun setPromotionManualConfirmationRequired() {
        isPromotionManualConfirmationRequired = true
        if (!updating) rebuildState()
    }

    fun clearPromotionManualConfirmationRequired() {
        isPromotionManualConfirmationRequired = false
        if (!updating) rebuildState()
    }

    fun doUnconfirmedMove(move: Move, isManualConfirmationRequired: Boolean = false) {
        board.doMove(move)
        isLastMoveConfirmed = false
        isLastMoveManualConfirmationRequired = isManualConfirmationRequired
        if (!updating) rebuildState()
    }

    fun undoUnconfirmedMove() {
        check(!isLastMoveConfirmed) {
            "There is no unconfirmed move to undo"
        }
        board.undoMove()
        isLastMoveConfirmed = true
        isLastMoveManualConfirmationRequired = false
        if (!updating) rebuildState()
    }

    fun confirmMove(): MoveResult {
        check(!isLastMoveConfirmed) {
            "There is no unconfirmed move to confirm"
        }
        isLastMoveConfirmed = true
        isLastMoveManualConfirmationRequired = false
        checkInfo = evaluateCheckInfo(checkAcknowledgeRequired = true)
        if (!updating) rebuildState()
        return MoveResult(
            opponentInCheck = checkInfo != null
        )
    }

    fun clearCheckAcknowledgeRequired() {
        checkInfo = checkInfo?.copy(acknowledgeRequired = false)
        if (!updating) rebuildState()
    }

    fun undoRound() {
        if (moves.isEmpty()) {
            logcat { "There is no move to undo" }
            return
        }

        if (!isLastMoveConfirmed) {
            undoUnconfirmedMove()
        }
        repeat(moves.size.coerceAtMost(COMPLETE_ROUND_MOVES_COUNT)) {
            board.undoMove()
        }
        checkInfo = evaluateCheckInfo(checkAcknowledgeRequired = false)

        if (!updating) rebuildState()
    }

    fun states(): Flow<ChessBoardState> {
        return _state.asSharedFlow()
    }

    fun loadMoves(moves: List<String>) {
        generateSequence(WHITE, Side::flip)
            .zip(moves.asSequence())
            .map { (side, moveLAN) -> Move(moveLAN, side) }
            .forEach { move -> board.doMove(move, false) }
        checkInfo = evaluateCheckInfo(checkAcknowledgeRequired = false)
        isPiecesPositionReady = true
        rebuildState()
    }

    @VisibleForTesting
    fun loadFen(fen: String) {
        board.loadFromFen(fen)
        rebuildState()
    }

    private fun evaluateCheckInfo(checkAcknowledgeRequired: Boolean): CheckInfo? {
        if (isEndgame) return null
        val kingSquare = board.getKingSquare(sideToMove)
        val attackedByBitboard = board.squareAttackedBy(kingSquare, sideToMove.flip())
        val attackedBySquares = Bitboard.bbToSquareList(attackedByBitboard)
        val attackedBy = attackedBySquares.map { square ->
            LocatedPiece(board.getPiece(square), square)
        }
        return attackedBy.takeIf { it.isNotEmpty() }?.let {
            CheckInfo(
                king = LocatedPiece(Piece.make(sideToMove, KING), kingSquare),
                attackedBy = it,
                acknowledgeRequired = checkAcknowledgeRequired
            )
        }
    }

    private fun isLastMoveCompleted(): Boolean =
        isLastMoveConfirmed && checkInfo?.acknowledgeRequired != true

    private fun rebuildState() = _state.update {
        it.copy(
            sideToMove = sideToMove,
            pieces = boardPieces(),
            highlights = highlighted,
            isMoveManualConfirmationRequired = isLastMoveManualConfirmationRequired,
            isPromotionManualConfirmationRequired = isPromotionManualConfirmationRequired,
            checkInfo = checkInfo,
            moves = moves
        )
    }

    private fun boardPieces(): List<Piece> = when (isPiecesPositionReady) {
        true -> squares.map(::getPiece)
        false -> emptyList()
    }

    private fun squaresFromTopLeftToBottomRight(topParticipantSide: Side): List<Square> {
        val allSquares = Square.entries.filter { it != Square.NONE }
        return when (topParticipantSide) {
            BLACK -> allSquares.chunked(BOARD_SIZE).reversed()
            WHITE -> allSquares.chunked(BOARD_SIZE).map { it.reversed() }
        }.flatten()
    }

    companion object {
        const val BOARD_SIZE = 8
        const val COMPLETE_ROUND_MOVES_COUNT = 2
    }
}
