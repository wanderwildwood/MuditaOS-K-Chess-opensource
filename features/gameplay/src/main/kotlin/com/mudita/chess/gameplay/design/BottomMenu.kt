package com.mudita.chess.gameplay.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.R
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.colorWhite
import com.mudita.kompakt.commonUi.components.KompaktIconButton
import com.mudita.kompakt.commonUi.components.button.KompaktButtonAttributes
import com.mudita.kompakt.commonUi.components.button.KompaktPrimaryButton
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun BottomMenu(
    onPauseButtonClick: () -> Unit,
    isConfirmMoveButtonVisible: Boolean,
    onConfirmMoveButtonClicked: () -> Unit,
    isGameMovesButtonVisible: Boolean,
    onGameMovesButtonClicked: () -> Unit,
    isUndoMoveButtonVisible: Boolean,
    onUndoMoveButtonClicked: () -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        BottomMenuIconButton(
            iconResId = R.drawable.ic_pause,
            touchAreaPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            onClicks = onPauseButtonClick
        )
        if (isGameMovesButtonVisible) {
            Spacer(modifier = Modifier.width(6.dp))
            BottomMenuIconButton(
                iconResId = R.drawable.ic_history,
                touchAreaPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                onClicks = onGameMovesButtonClicked
            )
        }
        if (isUndoMoveButtonVisible) {
            Spacer(modifier = Modifier.width(6.dp))
            BottomMenuIconButton(
                iconResId = R.drawable.ic_undo,
                touchAreaPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                onClicks = onUndoMoveButtonClicked
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isConfirmMoveButtonVisible) {
            Spacer(modifier = Modifier.width(6.dp))
            KompaktPrimaryButton(
                text = stringResource(id = RFrontitude.string.chess_gameplay_topbar_button_confirmmove).uppercase(),
                size = KompaktButtonAttributes.DynamicButton(
                    height = 36.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    cornerRadius = 8.dp,
                    textStyle = KompaktTypography900.labelSmall
                ),
                onClick = onConfirmMoveButtonClicked
            )
        }
    }
}

@Composable
private fun BottomMenuIconButton(
    iconResId: Int,
    touchAreaPadding: PaddingValues,
    onClicks: () -> Unit,
    modifier: Modifier = Modifier
) {
    KompaktIconButton(
        modifier = modifier
            .border(
                border = BorderStroke(
                    width = 2.dp,
                    color = ButtonDefaults.buttonColors().containerColor
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        touchAreaPadding = touchAreaPadding,
        iconResId = iconResId,
        onClick = onClicks
    )
}

@Preview
@Composable
private fun BottomMenuPreview() {
    KompaktTheme {
        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorWhite),
            onPauseButtonClick = {},
            isConfirmMoveButtonVisible = true,
            onConfirmMoveButtonClicked = {},
            isGameMovesButtonVisible = true,
            onGameMovesButtonClicked = {},
            isUndoMoveButtonVisible = true,
            onUndoMoveButtonClicked = {}
        )
    }
}
