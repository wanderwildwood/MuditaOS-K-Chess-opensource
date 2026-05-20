package com.mudita.chess.statistics

import androidx.lifecycle.viewModelScope
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.usecase.GetGameStatisticsUseCase
import com.mudita.chess.gamestatistics.usecase.RemoveGameStatisticsUseCase
import com.mudita.chess.mvvm.StateViewModel
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.NavActionsEmitter
import com.mudita.chess.statistics.StatisticsUiEvent.BackClicked
import com.mudita.chess.statistics.StatisticsUiEvent.ClearAllButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogCancelButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogClearStatisticsButtonClicked
import com.mudita.chess.statistics.StatisticsUiEvent.DialogDismissRequested
import com.mudita.chess.statistics.StatisticsUiEvent.NavigationUpClicked
import com.mudita.chess.statistics.model.MatchResultUi
import kotlinx.coroutines.launch

internal data class StatisticsUiState(
    val isContentVisible: Boolean = false,
    val isEmptyContentVisible: Boolean = false,
    val isClearAllButtonVisible: Boolean = false,
    val playedAsWhitePercentage: Int = 0,
    val playedAsBlackPercentage: Int = 0,
    val matchResults: List<MatchResultUi> = emptyList(),
    val isClearStatisticsDialogVisible: Boolean = false
)

internal sealed interface StatisticsUiEvent {
    data object BackClicked : StatisticsUiEvent
    data object NavigationUpClicked : StatisticsUiEvent
    data object ClearAllButtonClicked : StatisticsUiEvent
    data object DialogCancelButtonClicked : StatisticsUiEvent
    data object DialogClearStatisticsButtonClicked : StatisticsUiEvent
    data object DialogDismissRequested : StatisticsUiEvent
}

internal class StatisticsViewModel(
    private val mapper: StatisticsMapper,
    private val getGameStatisticsUseCase: GetGameStatisticsUseCase,
    private val removeGameStatisticsUseCase: RemoveGameStatisticsUseCase
) : StateViewModel<StatisticsUiState>(StatisticsUiState()),
    NavActionsEmitter by NavActionsEmitter() {

    init {
        viewModelScope.launch {
            getGameStatisticsUseCase()
                .onSuccess { showScreen(gameStatistics = it) }
                .onFailure { showScreen(GameStatistics.EMPTY) }
        }
    }

    private fun showScreen(gameStatistics: GameStatistics) {
        updateState {
            val noStatistics = gameStatistics.counterMap.isEmpty()
            if (noStatistics) {
                copy(
                    isContentVisible = true,
                    isEmptyContentVisible = true,
                    isClearAllButtonVisible = false,
                    playedAsWhitePercentage = 0,
                    playedAsBlackPercentage = 0,
                    matchResults = emptyList()
                )
            } else {
                val playedAsWhitePercentage = mapper.toPlayedAsWhitePercentage(gameStatistics)
                copy(
                    isContentVisible = true,
                    isEmptyContentVisible = false,
                    isClearAllButtonVisible = true,
                    playedAsWhitePercentage = playedAsWhitePercentage,
                    playedAsBlackPercentage = MAX_PERCENTAGE - playedAsWhitePercentage,
                    matchResults = mapper.toMatchResults(gameStatistics)
                )
            }
        }
    }

    fun handleUiEvent(uiEvent: StatisticsUiEvent) = viewModelScope.launch {
        when (uiEvent) {
            BackClicked,
            NavigationUpClicked -> emitNavAction(NavigateUp())

            ClearAllButtonClicked -> openClearStatisticsDialog()

            DialogCancelButtonClicked,
            DialogDismissRequested -> closeClearStatisticsDialog()

            DialogClearStatisticsButtonClicked -> onDialogClearStatisticsButtonClicked()
        }
    }

    private fun openClearStatisticsDialog() =
        updateState {
            copy(isClearStatisticsDialogVisible = true)
        }

    private fun closeClearStatisticsDialog() =
        updateState {
            copy(isClearStatisticsDialogVisible = false)
        }

    private fun onDialogClearStatisticsButtonClicked() = viewModelScope.launch {
        removeGameStatisticsUseCase()
        closeClearStatisticsDialog()
        showScreen(GameStatistics.EMPTY)
    }

    private companion object {
        const val MAX_PERCENTAGE = 100
    }
}
