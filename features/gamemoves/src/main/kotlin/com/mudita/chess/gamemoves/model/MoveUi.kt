package com.mudita.chess.gamemoves.model

import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi

internal data class MoveUi(
    val pieceUi: PieceUi,
    val from: PositionUi,
    val to: PositionUi
)
