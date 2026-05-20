package com.mudita.chess.gamemoves.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.compontent.Piece
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi.E2
import com.mudita.chess.ui.model.PositionUi.E4
import com.mudita.kompakt.commonUi.KompaktTypography500
import com.mudita.chess.ui.R as RCommonUi

@Composable
internal fun GameMoveListItem(
    moveUi: MoveUi
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Piece(
            modifier = Modifier.size(28.dp),
            piece = moveUi.pieceUi
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            PieceLabel(piece = moveUi.pieceUi)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = moveUi.from.name.lowercase(),
                    style = KompaktTypography500.labelSmall
                )
                Image(
                    painter = painterResource(id = RCommonUi.drawable.arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = moveUi.to.name.lowercase(),
                    style = KompaktTypography500.labelSmall
                )
            }
        }
    }
}

@KompaktPreview
@Composable
internal fun GameMoveListItemPreview() {
    GameMoveListItem(moveUi = MoveUi(pieceUi = PieceUi(type = PAWN, isWhite = true), from = E2, to = E4))
}
