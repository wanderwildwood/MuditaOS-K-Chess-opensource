@file:JvmName("MainComposable")

package com.mudita.chess.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mudita.chess.main.MainUiEvent.PlayButtonClicked
import com.mudita.chess.main.MainUiEvent.StatisticsButtonClicked
import com.mudita.chess.navigation.AppNavigator
import com.mudita.chess.navigation.NavActionsEffect
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppPrimaryButton
import com.mudita.chess.ui.design.AppSecondaryButton
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900
import org.koin.androidx.compose.koinViewModel
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.chess.ui.R as RCommonUi

@Composable
fun Main(navigator: AppNavigator) {
    MainInternal(
        viewModel = koinViewModel(),
        navigator = navigator
    )
}

@Composable
private fun MainInternal(
    viewModel: MainViewModel,
    navigator: AppNavigator
) {
    val uiState by viewModel.states.collectAsState()
    MainScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    uiEvent: (MainUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (uiState.isLoading) {
                true -> MainLoading()
                false -> MainLoaded(uiEvent)
                null -> Unit
            }
        }
    }
}

@Composable
private fun BoxScope.MainLoading() {
    Column(
        modifier = Modifier
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(RFrontitude.string.common_status_loading),
            style = AppTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(RCommonUi.drawable.spinner), contentDescription = null)
    }
}

@Composable
private fun BoxScope.MainLoaded(uiEvent: (MainUiEvent) -> Unit) {
    Image(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 105.dp),
        painter = painterResource(id = R.drawable.image_players),
        contentDescription = null
    )
    BottomButtons(uiEvent)
}

@Composable
private fun BoxScope.BottomButtons(uiEvent: (MainUiEvent) -> Unit) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        AppPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = RFrontitude.string.chess_optionsmenu_button_play),
            size = AppButtonAttributes.Large,
            onClick = { uiEvent(PlayButtonClicked) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppSecondaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = RFrontitude.string.common_screentitle_statistics),
            attributes = AppButtonAttributes.Large,
            onClick = { uiEvent(StatisticsButtonClicked) }
        )
    }
}

@KompaktPreview
@Composable
private fun MainScreenLoadedPreview() {
    AppTheme {
        MainScreen(
            uiState = MainUiState(isLoading = false),
            uiEvent = {}
        )
    }
}

@KompaktPreview
@Composable
private fun MainScreenLoadingPreview() {
    AppTheme {
        MainScreen(
            uiState = MainUiState(isLoading = true),
            uiEvent = {}
        )
    }
}
