package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.CastleRight
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.NONE
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.game.GameContext
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.GameplayMapper
import com.mudita.chess.gameplay.GameplayUiEvents
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.AbandonedMove
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.ConfirmedMove
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.Idle
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.PieceSelected
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.PromotionConfirmationRequired
import com.mudita.chess.gameplay.game.PlayerParticipant.MoveState.UnconfirmedMove
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import kotlin.coroutines.coroutineContext

@Suppress("TooManyFunctions")
internal class PlayerParticipant(
    override val side: Side,
    private val board: ChessBoard,
    private val moveResultNotifier: MoveResultNotifier,
    private val uiEvents: GameplayUiEvents,
    private val mapper: GameplayMapper
) : Participant {

    private var isMoveSuggestionsOn = false
    private val _state = MutableStateFlow<MoveState>(Idle)

    private var uiEventsScope: CoroutineScope? = null

    val state: MoveState
        get() = _state.value

    override suspend fun setup(options: GameOptions) {
        isMoveSuggestionsOn = options.isMoveSuggestionsOn

        val scope = CoroutineScope(coroutineContext) + SupervisorJob()
        uiEvents.squareClicks
            .filter { side == board.sideToMove }
            .map { mapper.toSquare(it.position) }
            .onEach { onSquareClicked(it) }
            .launchIn(scope)
        uiEvents.confirmPawnPromotionClicks
            .filter { side == board.sideToMove }
            .map { mapper.toPiece(it.piece) }
            .onEach { onConfirmPawnPromotionClicked(it) }
            .launchIn(scope)
        uiEvents.cancelPawnPromotionClicks
            .filter { side == board.sideToMove }
            .onEach { cancelPawnPromotion() }
            .launchIn(scope)
        uiEvents.confirmMoveClicks
            .filter { side == board.sideToMove }
            .onEach { onConfirmMoveClicked() }
            .launchIn(scope)
        uiEvents.moveSuggestionsSwitchToggles
            .onEach { onMoveSuggestionsSwitchToggled(isMoveSuggestionsOn = it.on) }
            .launchIn(scope)
        uiEventsScope = scope
    }

    override suspend fun doMove() {
        try {
            awaitState { it in setOf(ConfirmedMove, AbandonedMove) }
        } catch (e: CancellationException) {
            ifState<PromotionConfirmationRequired> {
                cancelPawnPromotion()
            }
            throw e
        }

        // Decided under the board lock: an undo tap can land between the confirm tap and here, and
        // it takes the move back, so a stale ConfirmedMove must not be confirmed a second time.
        val moveResult = atomically {
            if (state == ConfirmedMove) {
                board.update {
                    clearHighlighted()
                    confirmMove()
                }
            } else {
                null
            }
        }
        moveToState(Idle)
        moveResult?.let { moveResultNotifier.maybeNotifyCheck(it) }
    }

    override suspend fun cleanup() {
        uiEventsScope?.cancel()
    }

    /**
     * Every handler reads [state] and acts on the board, and runs on the multi-threaded IO
     * dispatcher alongside the game loop's [doMove]. Holding the board lock across the read and
     * the act keeps them from interleaving: a square tapped just as a move is confirmed would
     * otherwise undo an "unconfirmed" move that is already confirmed, and crash.
     */
    private fun onSquareClicked(square: Square) = atomically { handleSquareClick(square) }

    private fun handleSquareClick(square: Square) {
        when (val currentState = state) {
            Idle -> {
                selectPieceIfMine(square, undoUnconfirmed = false)
            }

            is PieceSelected -> {
                if (square == currentState.from) {
                    unSelectPiece(undoUnconfirmed = false)
                } else {
                    val legalMoves = currentState.legalMoves.filter { it.to == square }
                    val isIllegalMove = legalMoves.isEmpty()
                    val isLegalMove = legalMoves.size == 1
                    val isLegalPromotionMove = legalMoves.size > 1
                    when {
                        isIllegalMove -> selectPieceIfMine(square, undoUnconfirmed = false)
                        isLegalMove -> doUnconfirmedMove(legalMoves[0])
                        isLegalPromotionMove -> startPawnPromotion(from = currentState.from, to = square)
                    }
                }
            }

            is UnconfirmedMove -> {
                if (currentState.move.to == square) {
                    unSelectPiece(undoUnconfirmed = true)
                } else if (!(square affectedBy currentState.move)) {
                    selectPieceIfMine(square, undoUnconfirmed = true)
                }
            }

            is PromotionConfirmationRequired, ConfirmedMove, AbandonedMove -> Unit
        }
    }

    private fun onConfirmPawnPromotionClicked(piece: Piece) = atomically {
        ifState<PromotionConfirmationRequired> { promoting ->
            doUnconfirmedMove(Move(promoting.from, promoting.to, piece))
        }
    }

    private fun onConfirmMoveClicked() = atomically {
        ifState<UnconfirmedMove> {
            moveToState(ConfirmedMove)
        }
    }

    private fun onMoveSuggestionsSwitchToggled(isMoveSuggestionsOn: Boolean) {
        this.isMoveSuggestionsOn = isMoveSuggestionsOn

        ifState<PieceSelected> {
            board.highlightForPieceSelected(it.from, it.legalMoves)
        }
    }

    override fun undoMove() = atomically {
        // The turn may have passed between the tap and now; the tap was not meant for that turn
        if (side == board.sideToMove) {
            board.update {
                clearHighlighted()
                undoRound()
            }
            moveToState(AbandonedMove)
        }
    }

    private fun selectPieceIfMine(square: Square, undoUnconfirmed: Boolean) {
        board.getMinePiece(square) ?: return
        board.update {
            if (undoUnconfirmed) undoUnconfirmedMove()

            val legalMoves = getLegalMoves(square)
            highlightForPieceSelected(square, legalMoves)

            moveToState(PieceSelected(square, legalMoves))
        }
    }

    private fun unSelectPiece(undoUnconfirmed: Boolean) {
        board.update {
            if (undoUnconfirmed) undoUnconfirmedMove()
            clearHighlighted()
        }
        moveToState(Idle)
    }

    private fun doUnconfirmedMove(move: Move) {
        board.update {
            if (move.promotion != NONE) clearPromotionManualConfirmationRequired()
            doUnconfirmedMove(move, isManualConfirmationRequired = true)
            setHighlighted(move.to)
        }
        moveToState(UnconfirmedMove(move))
    }

    private fun startPawnPromotion(from: Square, to: Square) {
        board.update {
            setHighlighted(from)
            setPromotionManualConfirmationRequired()
        }
        moveToState(PromotionConfirmationRequired(from, to))
    }

    private fun cancelPawnPromotion() = atomically {
        ifState<PromotionConfirmationRequired> {
            board.update {
                clearPromotionManualConfirmationRequired()
                clearHighlighted()
            }
            moveToState(Idle)
        }
    }

    private fun ChessBoard.getMinePiece(square: Square): Piece? =
        getPiece(square).takeIf { it.pieceSide == side }

    private fun ChessBoard.highlightForPieceSelected(square: Square, legalMoves: List<Move>) {
        val highlighted = buildSet {
            add(square)
            if (isMoveSuggestionsOn) addAll(legalMoves.map { it.to }.toSet())
        }

        setHighlighted(highlighted)
    }

    private fun GameContext.getAssociatedMoves(move: Move): Set<Move> {
        return when {
            isKingSideCastle(move) -> getRookCastleMove(side, CastleRight.KING_SIDE)
            isQueenSideCastle(move) -> getRookCastleMove(side, CastleRight.QUEEN_SIDE)
            else -> null
        }.let(::setOfNotNull)
    }

    private infix fun Square.affectedBy(move: Move): Boolean =
        buildSet {
            add(move)
            addAll(board.context.getAssociatedMoves(move))
        }.any { it.from == this || it.to == this }

    /** Runs [block] under the board lock, without making the board its receiver. */
    private fun <R> atomically(block: () -> R): R = board.update { block() }

    private suspend fun awaitState(predicate: suspend (MoveState) -> Boolean) =
        _state.dropWhile { !predicate(it) }.first()

    private fun moveToState(state: MoveState) {
        _state.update { state }
    }

    private inline fun <reified T : MoveState> ifState(block: (T) -> Unit) =
        state.takeIf { it is T }?.let { block(it as T) }

    sealed interface MoveState {
        data object Idle : MoveState
        data class PieceSelected(val from: Square, val legalMoves: List<Move>) : MoveState
        data class PromotionConfirmationRequired(val from: Square, val to: Square) : MoveState
        data class UnconfirmedMove(val move: Move) : MoveState
        data object ConfirmedMove : MoveState
        data object AbandonedMove : MoveState
    }
}
