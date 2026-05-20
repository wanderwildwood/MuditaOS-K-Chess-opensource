package com.mudita.chess.statistics

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mudita.chess.navigation.AppNavigator
import com.mudita.chess.navigation.NavActionsEffect
import com.mudita.chess.statistics.StatisticsUiEvent.BackClicked
import com.mudita.chess.statistics.StatisticsUiEvent.ClearAllButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogCancelButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogClearStatisticsButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogDismissRequested
import com.mudita.chess.statistics.StatisticsUiEvent.NavigationUpClicked
import com.mudita.chess.statistics.design.MatchResults
import com.mudita.chess.statistics.design.PlayedColorStatistics
import com.mudita.chess.statistics.model.MatchResultUi
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.compontent.DialogHost
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography500
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.components.appBar.KompaktTopAppBar
import com.mudita.kompakt.commonUi.components.button.KompaktButtonAttributes
import com.mudita.kompakt.commonUi.components.button.KompaktSecondaryButton
import com.mudita.kompakt.commonUi.components.modal.KompaktModal
import com.mudita.kompakt.commonUi.components.modal.KompaktModalType.Confirm
import org.koin.androidx.compose.koinViewModel
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.kompakt.commonUi.R as RCommonUi

@Composable
fun Statistics(navigator: AppNavigator) {
    StatisticsInternal(
        viewModel = koinViewModel(),
        navigator = navigator
    )
}

@Composable
internal fun StatisticsInternal(
    viewModel: StatisticsViewModel,
    navigator: AppNavigator
) {
    val uiState by viewModel.states.collectAsState()
    StatisticsScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
    BackHandler {
        viewModel.handleUiEvent(BackClicked)
    }
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
}

@Composable
private fun StatisticsScreen(
    uiState: StatisticsUiState,
    uiEvent: (StatisticsUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = { StatisticsTopAppBar(uiEvent, uiState.isClearAllButtonVisible) }
    ) { contentPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(top = 16.dp)

        if (uiState.isContentVisible) {
            if (uiState.isEmptyContentVisible) {
                EmptyContent(modifier = contentModifier)
            } else {
                Content(
                    modifier = contentModifier,
                    uiState = uiState,
                    uiEvent = uiEvent
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) =
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = RFrontitude.string.common_search_error_h1_wecouldntfind),
            textAlign = TextAlign.Center,
            style = KompaktTypography900.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = RFrontitude.string.chess_statistics_emptystate_body_asyouwin),
            textAlign = TextAlign.Center,
            style = KompaktTypography500.bodyMedium
        )
    }

@Composable
private fun Content(
    uiState: StatisticsUiState,
    uiEvent: (StatisticsUiEvent) -> Unit,
    modifier: Modifier = Modifier
) = Box {
    Column(modifier = modifier) {
        PlayedColorStatistics(
            playedAsWhitePercentage = uiState.playedAsWhitePercentage,
            playedAsBlackPercentage = uiState.playedAsBlackPercentage
        )
        Spacer(modifier = Modifier.height(18.dp))
        MatchResults(uiState.matchResults)
    }
    if (uiState.isClearStatisticsDialogVisible) {
        DialogHost(
            dialogContentAlignment = Alignment.BottomCenter,
            onDismissRequest = { uiEvent(DialogDismissRequested) }
        ) {
            KompaktModal(
                kompaktModalType = Confirm(
                    title = stringResource(id = RFrontitude.string.chess_statistics_dialog_h1_clearallstatistics),
                    description = stringResource(id = RFrontitude.string.chess_statistics_dialog_body_thiswillgiveyou),
                    confirmText = stringResource(id = RFrontitude.string.chess_statistics_dialog_button_clearstatistics),
                    cancelText = stringResource(id = RFrontitude.string.common_dialog_button_cancel),
                    onConfirm = { uiEvent(DialogClearStatisticsButtonClicked) },
                    onCancel = { uiEvent(DialogCancelButtonClicked) }
                )
            )
        }
    }
}

@Composable
internal fun StatisticsTopAppBar(
    uiEvent: (StatisticsUiEvent) -> Unit,
    isClearAllButtonVisible: Boolean
) {
    KompaktTopAppBar(
        title = stringResource(id = RFrontitude.string.common_screentitle_statistics),
        navigationIconResId = RCommonUi.drawable.arrow_left,
        onNavigationIconClick = { uiEvent(NavigationUpClicked) },
        actionView = {
            if (isClearAllButtonVisible) {
                KompaktSecondaryButton(
                    text = stringResource(id = RFrontitude.string.common_topbar_button_clearall),
                    attributes = KompaktButtonAttributes.Small,
                    onClick = { uiEvent(ClearAllButtonClicked) }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    )
}

@KompaktPreview
@Composable
private fun StatisticsScreenPreview() {
    KompaktTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                isContentVisible = true,
                isEmptyContentVisible = false,
                playedAsWhitePercentage = 75,
                playedAsBlackPercentage = 25,
                matchResults = listOf(
                    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 10),
                    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 30),
                    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 5),
                    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 30)
                ),
                isClearStatisticsDialogVisible = false
            ),
            uiEvent = {}
        )
    }
}

@KompaktPreview
@Composable
private fun StatisticsEmptyScreenPreview() {
    KompaktTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                isContentVisible = true,
                isEmptyContentVisible = true
            ),
            uiEvent = {}
        )
    }
}
