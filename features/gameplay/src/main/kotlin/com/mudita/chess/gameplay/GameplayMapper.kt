package com.mudita.chess.gameplay

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square
import com.mudita.chess.gameplay.GameplayMapper.DialogsMapping.createCheckInfoDialog
import com.mudita.chess.gameplay.GameplayMapper.DialogsMapping.createPawnPromotionDialogFor
import com.mudita.chess.gameplay.GameplayMapper.DialogsMapping.createVictoryDialogFor
import com.mudita.chess.gameplay.GameplayMapper.PieceMapping.toDomain
import com.mudita.chess.gameplay.GameplayMapper.PieceMapping.toUi
import com.mudita.chess.gameplay.GameplayMapper.PieceTypeMapping.toDomain
import com.mudita.chess.gameplay.GameplayMapper.PieceTypeMapping.toUi
import com.mudita.chess.gameplay.GameplayMapper.SquareMapping.toDomain
import com.mudita.chess.gameplay.GameplayMapper.SquareMapping.toUi
import com.mudita.chess.gameplay.game.CheckInfo
import com.mudita.chess.gameplay.game.ChessBoard.Companion.BOARD_SIZE
import com.mudita.chess.gameplay.game.ChessBoardState
import com.mudita.chess.gameplay.game.GameStatus
import com.mudita.chess.gameplay.game.GameStatus.BLACK_WON
import com.mudita.chess.gameplay.game.GameStatus.DRAW
import com.mudita.chess.gameplay.game.GameStatus.STARTED
import com.mudita.chess.gameplay.game.GameStatus.STOPPED
import com.mudita.chess.gameplay.game.GameStatus.WHITE_WON
import com.mudita.chess.gameplay.model.GameplayDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.CheckInfoDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.DrawDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.GameMenuDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.PawnPromotionDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.VictoryDialogUi
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.gameplay.model.SquareUi
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import com.mudita.chess.frontitude.R as RFrontitude

internal class GameplayMapper {

    fun toBoardUi(chessBoardState: ChessBoardState): BoardUi = with(chessBoardState) {
        squares
            .withIndex()
            .chunked(BOARD_SIZE)
            .map { row ->
                row.map { (index, square) ->
                    val allSquaresToHighlight = highlights + checkInfo.highlights()
                    val piece = pieces.getOrNull(index)
                    SquareUi(
                        position = square.toUi(),
                        piece = piece?.toUi(),
                        isHighlighted = square in allSquaresToHighlight,
                        isWhite = square.isLightSquare
                    )
                }
            }
    }

    fun toComputer(side: Side, isSelected: Boolean = false) =
        ParticipantUi(
            nameResId = RFrontitude.string.common_label_computer,
            isWhite = side == WHITE,
            isSelected = isSelected
        )

    fun toPlayer(side: Side, isSelected: Boolean = false) =
        ParticipantUi(
            nameResId = RFrontitude.string.common_label_you,
            isWhite = side == WHITE,
            isSelected = isSelected
        )

    fun toWhiteParticipant(isSelected: Boolean = false) =
        ParticipantUi(
            nameResId = RFrontitude.string.common_label_white,
            isWhite = true,
            isSelected = isSelected
        )

    fun toBlackParticipant(isSelected: Boolean = false) =
        ParticipantUi(
            nameResId = RFrontitude.string.common_label_black,
            isWhite = false,
            isSelected = isSelected
        )

    fun toGameplayDialogUi(
        status: GameStatus,
        sideToMove: Side,
        isMoveSuggestionsOn: Boolean,
        isPromotionManualConfirmationRequired: Boolean,
        checkInfo: CheckInfo?,
        isTwoPlayerMode: Boolean = false
    ): GameplayDialogUi? =
        when (status) {
            STOPPED -> GameMenuDialogUi(isMoveSuggestionsOn, isTwoPlayerMode)

            WHITE_WON -> createVictoryDialogFor(side = WHITE)

            BLACK_WON -> createVictoryDialogFor(side = BLACK)

            DRAW -> DrawDialogUi

            STARTED -> when {
                isPromotionManualConfirmationRequired ->
                    createPawnPromotionDialogFor(sideToMove)

                checkInfo?.acknowledgeRequired == true ->
                    createCheckInfoDialog(checkInfo)

                else -> null
            }

            else -> null
        }

    fun toSquare(positionUi: PositionUi) = positionUi.toDomain()

    fun toPiece(pieceUi: PieceUi): Piece = pieceUi.toDomain()

    fun toMoveArgs(moveBackup: List<MoveBackup>): List<MoveArg> =
        moveBackup.map {
            MoveArg(it.movingPiece.fenSymbol, it.move.toString())
        }

    private fun CheckInfo?.highlights(): Set<Square> = this
        ?.let {
            buildSet {
                add(king.square)
                addAll(attackedBy.map { it.square })
            }
        }.orEmpty()

    private object DialogsMapping {
        fun createPawnPromotionDialogFor(side: Side): PawnPromotionDialogUi =
            listOf(PieceTypeUi.QUEEN, PieceTypeUi.ROOK, PieceTypeUi.BISHOP, PieceTypeUi.KNIGHT)
                .map { type -> PieceUi(type = type, isWhite = side == WHITE) }
                .toSet()
                .let(::PawnPromotionDialogUi)

        fun createCheckInfoDialog(checkInfo: CheckInfo): CheckInfoDialogUi? {
            val kingUi = checkInfo.king.piece.toUi()
            val attackedBy = checkInfo.attackedBy.mapNotNull { it.piece.toUi() }
            return kingUi?.let { CheckInfoDialogUi(it, attackedBy) }
        }

        fun createVictoryDialogFor(side: Side): VictoryDialogUi {
            val titleResId = if (side == WHITE) {
                RFrontitude.string.chess_endingscreen_dialog_h1_whitewins
            } else {
                RFrontitude.string.chess_endingscreen_dialog_h1_blackwins
            }
            return VictoryDialogUi(titleResId)
        }
    }

    private object SquareMapping {
        fun Square.toUi(): PositionUi = when (this) {
            Square.NONE -> throw IllegalArgumentException("Square.NONE is not allowed here")
            else -> PositionUi.valueOf(this.name)
        }

        fun PositionUi.toDomain(): Square = Square.valueOf(this.name)
    }

    private object PieceTypeMapping {
        fun PieceType.toUi(): PieceTypeUi? = when (this) {
            PieceType.NONE -> null
            else -> PieceTypeUi.valueOf(this.name)
        }

        fun PieceTypeUi.toDomain(): PieceType = PieceType.valueOf(this.name)
    }

    private object PieceMapping {
        fun Piece.toUi(): PieceUi? = pieceType?.toUi()?.let { type ->
            PieceUi(type = type, isWhite = WHITE == pieceSide)
        }

        fun PieceUi.toDomain(): Piece = Piece.make(
            if (isWhite) WHITE else BLACK,
            type.toDomain()
        )
    }
}
