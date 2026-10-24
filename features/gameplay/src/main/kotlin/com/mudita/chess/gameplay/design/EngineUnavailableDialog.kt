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
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppPrimaryButton
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.ui.design.eInkFrame
import com.mudita.chess.frontitude.R as RFrontitude

/** Said when the computer player cannot run on this phone; two-player games are unaffected. */
@Composable
internal fun EngineUnavailableDialog(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .eInkFrame()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = RFrontitude.string.chess_engine_unavailable),
            textAlign = TextAlign.Center,
            style = AppTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = RFrontitude.string.chess_endingscreen_dialog_button_mainmenu),
            size = AppButtonAttributes(height = 48.dp, textStyle = AppTypography900.labelLarge),
            onClick = onMenuClick
        )
    }
}

@KompaktPreview
@Composable
private fun EngineUnavailableDialogPreview() {
    EngineUnavailableDialog(onMenuClick = {})
}
