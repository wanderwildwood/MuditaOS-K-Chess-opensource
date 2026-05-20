@file:JvmName("OptionsMenuComposable")

package com.mudita.chess.optionsmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.mudita.chess.gameoptions.mapper.elo
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.navigation.AppNavigator
import com.mudita.chess.navigation.NavActionsEffect
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelMinusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelPlusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelStepClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.NavigationUpClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayButtonClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayerColorSelected
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.SaveGameState
import com.mudita.chess.optionsmenu.design.DifficultyLevelBar
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.OnLifecycleEvent
import com.mudita.chess.ui.R
import com.mudita.chess.ui.model.TextUi
import com.mudita.chess.ui.compontent.SwitchOption
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.components.appBar.KompaktTopAppBar
import com.mudita.kompakt.commonUi.components.button.KompaktButtonAttributes
import com.mudita.kompakt.commonUi.components.button.KompaktPrimaryButton
import com.mudita.kompakt.commonUi.components.button.KompaktSecondaryButton
import org.koin.androidx.compose.koinViewModel
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.kompakt.commonUi.R as RCommonUi

@Composable
fun OptionsMenu(navigator: AppNavigator) {
    OptionsMenuInternal(
        viewModel = koinViewModel(),
        navigator = navigator
    )
}

@Composable
internal fun OptionsMenuInternal(
    viewModel: OptionsMenuViewModel,
    navigator: AppNavigator
) {
    val uiState by viewModel.states.collectAsState()
    OptionsMenuScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
    NavActionsEffect(actions = viewModel.navActions, navigator = navigator)
    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            viewModel.handleUiEvent(SaveGameState)
        }
    }
}

@Composable
private fun OptionsMenuScreen(
    uiState: OptionsMenuUiState,
    uiEvent: (OptionsMenuUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = { OptionsMenuTopAppBar(uiEvent) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(bottom = 16.dp)
        ) {
            SwitchOption(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                text = stringResource(id = RFrontitude.string.chess_gamepausemenu_toggle_button_movesuggestions),
                textStyle = KompaktTypography900.titleMedium,
                isSwitchedOn = uiState.isMoveSuggestionsOn,
                onSwitchToggle = { uiEvent(MoveSuggestionsSwitchToggled) }
            )
            PlayerColor(
                isWhiteSelected = uiState.isWhiteSelected,
                uiEvent = uiEvent
            )
            DifficultyLevel(
                difficultyLevelStep = uiState.difficultyLevelStep,
                difficultyLevelLabel = uiState.difficultyLevelLabel,
                uiEvent = uiEvent
            )
            Spacer(modifier = Modifier.weight(1f))
            KompaktPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                text = stringResource(id = RFrontitude.string.chess_optionsmenu_button_play),
                size = KompaktTheme.buttonStyle.large,
                onClick = { uiEvent(PlayButtonClicked) }
            )
        }
    }
}

@Composable
private fun OptionsMenuTopAppBar(uiEvent: (OptionsMenuUiEvent) -> Unit) {
    KompaktTopAppBar(
        title = stringResource(id = RFrontitude.string.common_label_options),
        navigationIconResId = RCommonUi.drawable.arrow_left,
        onNavigationIconClick = { uiEvent(NavigationUpClicked) }
    )
}

@Composable
private fun PlayerColor(
    isWhiteSelected: Boolean,
    uiEvent: (OptionsMenuUiEvent) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(
            text = stringResource(
                id = RFrontitude.string.chess_optionsmenu_label_selectplayercolor
            ),
            style = KompaktTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            KompaktSecondaryButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = RFrontitude.string.common_label_white),
                iconResId = R.drawable.ic_knight_white_transparent,
                attributes = KompaktButtonAttributes.DynamicButton(
                    spaceBetweenIconAndText = 0.dp,
                    borderStrokeWidth = if (isWhiteSelected) 4.dp else 2.dp,
                    iconSize = 24.dp
                ),
                onClick = { uiEvent(PlayerColorSelected(isWhiteSelected = true)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            KompaktSecondaryButton(
                modifier = Modifier.weight(1f),
                text = stringResource(id = RFrontitude.string.common_label_black),
                iconResId = R.drawable.ic_knight_black_transparent,
                attributes = KompaktButtonAttributes.DynamicButton(
                    spaceBetweenIconAndText = 0.dp,
                    borderStrokeWidth = if (!isWhiteSelected) 4.dp else 2.dp,
                    height = 40.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    cornerRadius = 8.dp,
                    iconSize = 24.dp
                ),
                onClick = { uiEvent(PlayerColorSelected(isWhiteSelected = false)) }
            )
        }
    }
}

@Composable
private fun DifficultyLevel(
    difficultyLevelStep: Int,
    difficultyLevelLabel: TextUi?,
    uiEvent: (OptionsMenuUiEvent) -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(
            id = RFrontitude.string.chess_optionsmenu_label_difficultylevel
        ),
        style = KompaktTypography900.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    DifficultyLevelBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 11.dp),
        difficultyLevelStep = difficultyLevelStep,
        difficultyLevelLabel = difficultyLevelLabel,
        onMinusIconClick = { uiEvent(DifficultyLevelMinusIconClicked) },
        onPlusIconClick = { uiEvent(DifficultyLevelPlusIconClicked) },
        onStepClick = { uiEvent(DifficultyLevelStepClicked(step = it)) }
    )
}

@KompaktPreview
@Composable
private fun OptionsMenuScreenPreview() {
    KompaktTheme {
        OptionsMenuScreen(
            uiState = OptionsMenuUiState(
                isMoveSuggestionsOn = true,
                isWhiteSelected = true,
                difficultyLevelStep = 1,
                difficultyLevelLabel = TextUi.Res(
                    RFrontitude.string.chess_optionsmenu_label_beginner,
                    args = arrayOf(DifficultyLevel(1).elo())
                )
            ),
            uiEvent = {}
        )
    }
}
