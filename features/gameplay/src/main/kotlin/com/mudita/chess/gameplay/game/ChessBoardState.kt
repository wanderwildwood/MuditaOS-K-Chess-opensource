package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move

data class ChessBoardState(
    val sideToMove: Side = Side.WHITE,
    val squares: List<Square> = emptyList(),
    val pieces: List<Piece> = emptyList(),
    val highlights: Set<Square> = emptySet(),
    val isMoveManualConfirmationRequired: Boolean = false,
    val isPromotionManualConfirmationRequired: Boolean = false,
    val checkInfo: CheckInfo? = null,
    val moves: List<Move> = emptyList()
)

data class LocatedPiece(
    val piece: Piece,
    val square: Square
)

data class CheckInfo(
    val king: LocatedPiece,
    val attackedBy: List<LocatedPiece>,
    val acknowledgeRequired: Boolean
)
