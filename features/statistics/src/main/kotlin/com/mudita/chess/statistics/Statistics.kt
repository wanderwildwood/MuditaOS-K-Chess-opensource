package com.mudita.chess.statistics

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import com.mudita.chess.statistics.StatisticsUiEvent.ClearAllConfirmed
import com.mudita.chess.statistics.StatisticsUiEvent.NavigationUpClicked
import com.mudita.chess.statistics.design.MatchResults
import com.mudita.chess.statistics.design.PlayedColorStatistics
import com.mudita.chess.statistics.model.MatchResultUi
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.design.AppArmedSecondaryButton
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTopAppBar
import com.mudita.chess.ui.design.AppTypography500
import org.koin.androidx.compose.koinViewModel
import com.mudita.chess.frontitude.R as RFrontitude

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
        containerColor = MaterialTheme.colorScheme.background,
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
                    uiState = uiState
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
            text = stringResource(id = RFrontitude.string.chess_statistics_emptystate_body_asyouwin),
            textAlign = TextAlign.Center,
            style = AppTypography500.bodyMedium
        )
    }

@Composable
private fun Content(
    uiState: StatisticsUiState,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    PlayedColorStatistics(
        playedAsWhitePercentage = uiState.playedAsWhitePercentage,
        playedAsBlackPercentage = uiState.playedAsBlackPercentage
    )
    Spacer(modifier = Modifier.height(18.dp))
    MatchResults(uiState.matchResults)
}

@Composable
internal fun StatisticsTopAppBar(
    uiEvent: (StatisticsUiEvent) -> Unit,
    isClearAllButtonVisible: Boolean
) {
    AppTopAppBar(
        title = stringResource(id = RFrontitude.string.common_screentitle_statistics),
        onNavigationIconClick = { uiEvent(NavigationUpClicked) },
        actions = {
            if (isClearAllButtonVisible) {
                // Statistics cannot be brought back: the button asks itself ("tap again"),
                // as New game and Exit do, rather than stacking a dialog over the screen.
                AppArmedSecondaryButton(
                    text = stringResource(id = RFrontitude.string.common_topbar_button_clearall),
                    attributes = AppButtonAttributes.Small,
                    onConfirm = { uiEvent(ClearAllConfirmed) }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    )
}

@KompaktPreview
@Composable
private fun StatisticsScreenPreview() {
    AppTheme {
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
                )
            ),
            uiEvent = {}
        )
    }
}

@KompaktPreview
@Composable
private fun StatisticsEmptyScreenPreview() {
    AppTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                isContentVisible = true,
                isEmptyContentVisible = true
            ),
            uiEvent = {}
        )
    }
}
