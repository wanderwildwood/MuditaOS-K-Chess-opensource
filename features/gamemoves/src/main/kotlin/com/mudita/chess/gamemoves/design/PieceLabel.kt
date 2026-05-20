package com.mudita.chess.gamemoves.design

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun PieceLabel(piece: PieceUi) {
    Text(
        text = stringResource(id = getPieceResId(piece = piece)),
        style = KompaktTypography900.labelMedium
    )
}

@Composable
@StringRes
private fun getPieceResId(piece: PieceUi) =
    if (piece.isWhite) {
        getWhitePieceResId(piece.type)
    } else {
        getBlackPieceResId(piece.type)
    }

@Composable
@StringRes
private fun getWhitePieceResId(type: PieceTypeUi) =
    when (type) {
        PAWN -> RFrontitude.string.chess_common_label_whitepawn
        ROOK -> RFrontitude.string.chess_common_label_whiterook
        KNIGHT -> RFrontitude.string.chess_common_label_whiteknight
        BISHOP -> RFrontitude.string.chess_common_label_whitebishop
        QUEEN -> RFrontitude.string.chess_common_label_whitequeen
        KING -> RFrontitude.string.chess_common_label_whiteking
    }

@Composable
@StringRes
private fun getBlackPieceResId(type: PieceTypeUi) =
    when (type) {
        PAWN -> RFrontitude.string.chess_common_label_blackpawn
        ROOK -> RFrontitude.string.chess_common_label_blackrook
        KNIGHT -> RFrontitude.string.chess_common_label_blackknight
        BISHOP -> RFrontitude.string.chess_common_label_blackbishop
        QUEEN -> RFrontitude.string.chess_common_label_blackqueen
        KING -> RFrontitude.string.chess_common_label_blackking
    }
