package com.mudita.chess.gameplay.model

import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi

internal data class SquareUi(
    val position: PositionUi,
    val piece: PieceUi? = null,
    val isHighlighted: Boolean = false,
    val isWhite: Boolean
)
