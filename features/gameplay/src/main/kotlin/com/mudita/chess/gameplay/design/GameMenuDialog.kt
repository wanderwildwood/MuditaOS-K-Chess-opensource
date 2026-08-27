package com.mudita.chess.gameplay.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.compontent.SwitchOption
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.compactColorScheme
import com.mudita.kompakt.commonUi.components.button.KompaktButtonAttributes
import com.mudita.kompakt.commonUi.components.button.KompaktPrimaryButton
import com.mudita.kompakt.commonUi.components.button.KompaktSecondaryButton
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
fun GameMenuDialog(
    isMoveSuggestionsOn: Boolean,
    onResumeClick: () -> Unit,
    onNewGameClick: () -> Unit,
    onExitClick: () -> Unit,
    onMoveSuggestionsSwitchToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTwoPlayerMode: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 3.dp,
            color = compactColorScheme.primary
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = compactColorScheme.secondary)
        ) {
            Content(
                isMoveSuggestionsOn = isMoveSuggestionsOn,
                isTwoPlayerMode = isTwoPlayerMode,
                onResumeClick = onResumeClick,
                onNewGameClick = onNewGameClick,
                onExitClick = onExitClick,
                onMoveSuggestionsSwitchToggle = onMoveSuggestionsSwitchToggle
            )
        }
    }
}

@Composable
private fun Content(
    isMoveSuggestionsOn: Boolean,
    isTwoPlayerMode: Boolean,
    onResumeClick: () -> Unit,
    onNewGameClick: () -> Unit,
    onExitClick: () -> Unit,
    onMoveSuggestionsSwitchToggle: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.height(20.dp))
    Title()
    Spacer(modifier = Modifier.height(24.dp))
    Options(
        isMoveSuggestionsOn = isMoveSuggestionsOn,
        isTwoPlayerMode = isTwoPlayerMode,
        onResumeClick = onResumeClick,
        onNewGameClick = onNewGameClick,
        onExitClick = onExitClick,
        onMoveSuggestionsSwitchToggle = onMoveSuggestionsSwitchToggle
    )
}

@Composable
private fun Title() =
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = RFrontitude.string.maps_common_screentitle_menu),
        textAlign = TextAlign.Center,
        style = KompaktTypography900.titleMedium
    )

@Composable
private fun Options(
    isMoveSuggestionsOn: Boolean,
    isTwoPlayerMode: Boolean,
    onResumeClick: () -> Unit,
    onNewGameClick: () -> Unit,
    onExitClick: () -> Unit,
    onMoveSuggestionsSwitchToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        ResumeButton { onResumeClick() }
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            text = stringResource(id = RFrontitude.string.chess_endingscreen_dialog_button_newgame),
            onClick = onNewGameClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            text = stringResource(id = RFrontitude.string.common_button_exit),
            onClick = onExitClick
        )
        if (!isTwoPlayerMode) {
            Spacer(modifier = Modifier.height(4.dp))
            SwitchOption(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                text = stringResource(id = RFrontitude.string.chess_gamepausemenu_toggle_button_movesuggestions),
                textStyle = KompaktTypography900.labelMedium,
                isSwitchedOn = isMoveSuggestionsOn,
                onSwitchToggle = onMoveSuggestionsSwitchToggle,
                verticalTouchAreaPadding = 12.dp
            )
        }
    }
}

@Composable
private fun ResumeButton(onResumeClick: () -> Unit) {
    KompaktPrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        text = stringResource(id = RFrontitude.string.common_button_resume),
        size = buttonAttributes(),
        onClick = onResumeClick
    )
}

@Composable
private fun SecondaryButton(text: String, onClick: () -> Unit) {
    KompaktSecondaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        text = text,
        attributes = buttonAttributes(),
        onClick = onClick
    )
}

@Composable
private fun buttonAttributes() =
    KompaktButtonAttributes.DynamicButton(
        height = 48.dp,
        textStyle = KompaktTypography900.labelLarge
    )

@Preview
@Composable
private fun GameMenuPreview() = KompaktTheme {
    Box {
        GameMenuDialog(
            modifier = Modifier.fillMaxWidth(),
            isMoveSuggestionsOn = true,
            onResumeClick = {},
            onNewGameClick = {},
            onExitClick = {},
            onMoveSuggestionsSwitchToggle = {}
        )
    }
}
