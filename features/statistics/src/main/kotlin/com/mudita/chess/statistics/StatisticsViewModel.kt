package com.mudita.chess.statistics

import androidx.lifecycle.viewModelScope
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.usecase.GetGameStatisticsUseCase
import com.mudita.chess.gamestatistics.usecase.RemoveGameStatisticsUseCase
import com.mudita.chess.mvvm.StateViewModel
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.NavActionsEmitter
import com.mudita.chess.statistics.StatisticsUiEvent.BackClicked
import com.mudita.chess.statistics.StatisticsUiEvent.ClearAllConfirmed
import com.mudita.chess.statistics.StatisticsUiEvent.NavigationUpClicked
import com.mudita.chess.statistics.model.MatchResultUi
import kotlinx.coroutines.launch

internal data class StatisticsUiState(
    val isContentVisible: Boolean = false,
    val isEmptyContentVisible: Boolean = false,
    val isClearAllButtonVisible: Boolean = false,
    val playedAsWhitePercentage: Int = 0,
    val playedAsBlackPercentage: Int = 0,
    val matchResults: List<MatchResultUi> = emptyList()
)

internal sealed interface StatisticsUiEvent {
    data object BackClicked : StatisticsUiEvent
    data object NavigationUpClicked : StatisticsUiEvent

    /** The second tap on the armed Clear all: the first only asks, on the button itself. */
    data object ClearAllConfirmed : StatisticsUiEvent
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

            ClearAllConfirmed -> clearStatistics()
        }
    }

    private fun clearStatistics() = viewModelScope.launch {
        removeGameStatisticsUseCase()
        showScreen(GameStatistics.EMPTY)
    }

    private companion object {
        const val MAX_PERCENTAGE = 100
    }
}
