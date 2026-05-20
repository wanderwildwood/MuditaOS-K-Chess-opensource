package com.mudita.chess.gameplay.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.compontent.Piece
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.compactColorScheme
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.chess.ui.R as RCommonUi

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CheckInfoDialog(
    king: PieceUi,
    attackedBy: List<PieceUi>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, compactColorScheme.primary, shape = RoundedCornerShape(16.dp))
            .background(color = compactColorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = RFrontitude.string.chess_gameplay_notification_check),
            textAlign = TextAlign.Center,
            style = KompaktTypography900.labelMedium
        )
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            attackedBy.forEach { piece ->
                Piece(
                    piece = piece,
                    modifier = modifier.size(34.dp)
                )
            }
            Image(
                painter = painterResource(id = RCommonUi.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)

            )
            Piece(
                piece = king,
                modifier = modifier.size(34.dp)
            )
        }
    }
}

@KompaktPreview
@Composable
private fun BlackCheckInfoDialogPreview() {
    CheckInfoDialog(
        king = PieceUi(type = KING, isWhite = false),
        attackedBy = listOf(
            PieceUi(type = KNIGHT, isWhite = true)
        )
    )
}

@KompaktPreview
@Composable
private fun WhiteCheckInfoDialogPreview() {
    CheckInfoDialog(
        king = PieceUi(type = KING, isWhite = true),
        attackedBy = listOf(
            PieceUi(type = KNIGHT, isWhite = false)
        )
    )
}

@KompaktPreview
@Composable
private fun MultipleAttackedByCheckInfoDialogPreview() {
    CheckInfoDialog(
        king = PieceUi(type = KING, isWhite = true),
        attackedBy = listOf(
            PieceUi(type = KNIGHT, isWhite = false),
            PieceUi(type = ROOK, isWhite = false)
        )
    )
}
