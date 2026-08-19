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
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Suppress("TooManyFunctions")
internal class ChessBoard(
    topParticipantSide: Side,
    private var isPiecesPositionReady: Boolean = true
) {

    private val squares = squaresFromTopLeftToBottomRight(topParticipantSide)
    private val board = Board()

    /**
     * chesslib's [Board] is not thread-safe, and this instance is shared by every participant plus
     * the game loop in [Game], all of which run on the multi-threaded IO dispatcher. In local
     * 2-player mode a single square click fans out to *two* PlayerParticipants, so one participant
     * can be inside [Board.doMove] while the other is reading [sideToMove] -- which transitively
     * runs full legal-move generation over the same mutable bitboards.
     *
     * When a read observes a half-applied move it can see a from-square that is already empty,
     * and chesslib's `Board.isMoveLegal` dereferences `Piece.NONE.getPieceType()` (which is null)
     * without a guard when `fullValidation` is false -- crashing the app with a
     * `MoveGeneratorException` wrapping an NPE.
     *
     * Every access to [board] and to the mutable state below is therefore serialized through this
     * lock. It is reentrant so that [update] blocks can freely call the other members.
     */
    private val lock = ReentrantLock()

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
        get() = lock.withLock {
            if (isEndgame || !isLastMoveCompleted()) board.sideToMove.flip() else board.sideToMove
        }

    val moves: List<Move>
        get() = movesBackup
            .map { it.move }

    val movesBackup: List<MoveBackup>
        get() = lock.withLock {
            when (isLastMoveConfirmed) {
                true -> board.backup.toList()
                false -> board.backup.dropLast(1)
            }
        }

    val context: GameContext
        get() = lock.withLock { board.context }

    val isEndgame: Boolean
        get() = lock.withLock { isMate || isDraw }

    val isMate: Boolean
        get() = lock.withLock { isLastMoveConfirmed && board.isMated }

    val isDraw: Boolean
        get() = lock.withLock { isLastMoveConfirmed && board.isDraw }

    fun getPiece(square: Square): Piece = lock.withLock {
        when (isPiecesPositionReady) {
            true -> board.getPiece(square)
            false -> Piece.NONE
        }
    }

    fun getLegalMoves(
        square: Square
    ): List<Move> = lock.withLock {
        when (isPiecesPositionReady) {
            true -> board.legalMoves().filter { it.from == square }
            false -> emptyList()
        }
    }

    fun <R> update(block: ChessBoard.() -> R): R = lock.withLock {
        updating = true
        try {
            val result = block(this)
            rebuildState()
            result
        } finally {
            // Without the finally, a throwing block would leave `updating` stuck true and
            // silently suppress every later rebuildState() call.
            updating = false
        }
    }

    fun setHighlighted(square: Square) = setHighlighted(setOf(square))

    fun setHighlighted(squares: Set<Square>) = lock.withLock {
        highlighted = squares
        if (!updating) rebuildState()
    }

    fun clearHighlighted() =
        setHighlighted(emptySet())

    fun setPromotionManualConfirmationRequired() = lock.withLock {
        isPromotionManualConfirmationRequired = true
        if (!updating) rebuildState()
    }

    fun clearPromotionManualConfirmationRequired() = lock.withLock {
        isPromotionManualConfirmationRequired = false
        if (!updating) rebuildState()
    }

    fun doUnconfirmedMove(move: Move, isManualConfirmationRequired: Boolean = false) = lock.withLock {
        board.doMove(move)
        isLastMoveConfirmed = false
        isLastMoveManualConfirmationRequired = isManualConfirmationRequired
        if (!updating) rebuildState()
    }

    fun undoUnconfirmedMove() = lock.withLock {
        check(!isLastMoveConfirmed) {
            "There is no unconfirmed move to undo"
        }
        board.undoMove()
        isLastMoveConfirmed = true
        isLastMoveManualConfirmationRequired = false
        if (!updating) rebuildState()
    }

    fun confirmMove(): MoveResult = lock.withLock {
        check(!isLastMoveConfirmed) {
            "There is no unconfirmed move to confirm"
        }
        isLastMoveConfirmed = true
        isLastMoveManualConfirmationRequired = false
        checkInfo = evaluateCheckInfo(checkAcknowledgeRequired = true)
        if (!updating) rebuildState()
        MoveResult(
            opponentInCheck = checkInfo != null
        )
    }

    fun clearCheckAcknowledgeRequired() = lock.withLock {
        checkInfo = checkInfo?.copy(acknowledgeRequired = false)
        if (!updating) rebuildState()
    }

    fun undoRound() {
        lock.withLock {
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
    }

    fun states(): Flow<ChessBoardState> {
        return _state.asSharedFlow()
    }

    fun loadMoves(moves: List<String>) = lock.withLock {
        generateSequence(WHITE, Side::flip)
            .zip(moves.asSequence())
            .map { (side, moveLAN) -> Move(moveLAN, side) }
            .forEach { move -> board.doMove(move, false) }
        checkInfo = evaluateCheckInfo(checkAcknowledgeRequired = false)
        isPiecesPositionReady = true
        rebuildState()
    }

    @VisibleForTesting
    fun loadFen(fen: String) = lock.withLock {
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
