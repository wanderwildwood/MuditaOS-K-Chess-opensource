package com.mudita.chess.gameplay.design

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.design.AppArmedSecondaryButton
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppPrimaryButton
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.ui.design.eInkFrame
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * Said when the saved game cannot be read. Keep leaves it in place for a later try; Discard
 * deletes it, and asks with a second tap because that cannot be undone.
 */
@Composable
internal fun SavedGameUnreadableDialog(
    onKeepClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .eInkFrame()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = RFrontitude.string.chess_savedgame_unreadable),
            textAlign = TextAlign.Center,
            style = AppTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = RFrontitude.string.chess_savedgame_button_keep),
            size = buttonAttributes(),
            onClick = onKeepClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppArmedSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = RFrontitude.string.chess_savedgame_button_discard),
            attributes = buttonAttributes(),
            onConfirm = onDiscardClick
        )
    }
}

@Composable
private fun buttonAttributes() =
    AppButtonAttributes(height = 48.dp, textStyle = AppTypography900.labelLarge)

@KompaktPreview
@Composable
private fun SavedGameUnreadableDialogPreview() = AppTheme {
    SavedGameUnreadableDialog(onKeepClick = {}, onDiscardClick = {})
}
