package com.mudita.chess.gameplay.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.compontent.Piece
import com.mudita.chess.ui.model.PieceTypeUi
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppSecondaryButton
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun PawnPromotionDialog(
    promotionOptions: Set<PieceUi>,
    onOptionConfirmed: (PieceUi) -> Unit,
    modifier: Modifier = Modifier,
    selectedOption: Int = 0
) {
    var selectedOptionIndex by remember { mutableIntStateOf(selectedOption) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = RFrontitude.string.chess_gameplay_dialog_h1_promoteinto),
            textAlign = TextAlign.Center,
            style = AppTypography900.labelMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            promotionOptions.forEachIndexed { index, option ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { selectedOptionIndex = index }
                ) {
                    Piece(piece = option, modifier = Modifier.size(34.dp))
                    if (selectedOptionIndex == index) PieceSelector(isWhite = false, modifier.size(40.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppSecondaryButton(
            text = stringResource(id = RFrontitude.string.common_dialog_button_confirm),
            attributes = AppButtonAttributes.Small,
            onClick = { onOptionConfirmed(promotionOptions.elementAt(selectedOptionIndex)) }
        )
    }
}

@KompaktPreview
@Composable
private fun WhitePawnPromotionDialog() {
    PawnPromotionDialog(
        promotionOptions = setOf(
            PieceUi(type = PieceTypeUi.QUEEN, isWhite = true),
            PieceUi(type = PieceTypeUi.ROOK, isWhite = true),
            PieceUi(type = PieceTypeUi.BISHOP, isWhite = true),
            PieceUi(type = PieceTypeUi.KNIGHT, isWhite = true)
        ),
        onOptionConfirmed = {}
    )
}

@KompaktPreview
@Composable
private fun BlackPawnPromotionDialog() {
    PawnPromotionDialog(
        promotionOptions = setOf(
            PieceUi(type = PieceTypeUi.QUEEN, isWhite = false),
            PieceUi(type = PieceTypeUi.ROOK, isWhite = false),
            PieceUi(type = PieceTypeUi.BISHOP, isWhite = false),
            PieceUi(type = PieceTypeUi.KNIGHT, isWhite = false)
        ),
        onOptionConfirmed = {}
    )
}
