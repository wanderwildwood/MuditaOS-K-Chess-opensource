package com.mudita.chess.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudita.chess.coroutines.Dispatchers
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.GetGameOptionsUseCase
import com.mudita.chess.games.usecase.HasCurrentGameUseCase
import com.mudita.chess.main.MainUiEvent.PlayButtonClicked
import com.mudita.chess.main.MainUiEvent.StatisticsButtonClicked
import com.mudita.chess.mvvm.StateHandler
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.NavActionsEmitter
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.navigation.routes.StatisticsRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

internal data class MainUiState(
    /**
     * Don't use true as default value due:
     * Composable function after constructing VM manages to render loader frame always
     * which blinks before content when launching application.
     * VM set loading true if not able to determine game existence in small amount of time 20ms.
     */
    val isLoading: Boolean? = null
)

internal sealed interface MainUiEvent {
    data object PlayButtonClicked : MainUiEvent
    data object StatisticsButtonClicked : MainUiEvent
}

internal class MainViewModel(
    private val hasCurrentGameUseCase: HasCurrentGameUseCase,
    private val getGameOptionsUseCase: GetGameOptionsUseCase,
    private val dispatchers: Dispatchers
) : ViewModel(),
    StateHandler<MainUiState> by StateHandler(MainUiState()),
    NavActionsEmitter by NavActionsEmitter() {

    init {
        viewModelScope.launch(dispatchers.io()) {
            val loadJob = loadLastGameIfExist()
            val isCompleted = loadJob.isCompletedAfter(GAME_LOADING_VISIBLE_THRESHOLD_MILLIS, dispatchers.io())
            updateState { copy(isLoading = !isCompleted) }
            if (!isCompleted) {
                loadJob.join()
                updateState { copy(isLoading = false) }
            }
        }
    }

    fun handleUiEvent(uiEvent: MainUiEvent) {
        when (uiEvent) {
            PlayButtonClicked -> onPlayButtonClicked()
            StatisticsButtonClicked -> onStatisticsButtonClicked()
        }
    }

    private fun onPlayButtonClicked() = viewModelScope.launch(dispatchers.io()) {
        val gameOptions = getGameOptionsUseCase().getOrDefault(GameOptions.DEFAULT)
        val optionsMenuRoute = OptionsMenuRoute(
            isPlayerWhite = gameOptions.isPlayerWhite,
            isMoveSuggestionsOn = gameOptions.isMoveSuggestionsOn,
            difficultyLevel = gameOptions.difficultyLevel.value
        )
        emitNavAction(NavigateTo(optionsMenuRoute))
    }

    private fun onStatisticsButtonClicked() = viewModelScope.launch {
        emitNavAction(NavigateTo(StatisticsRoute))
    }

    private fun CoroutineScope.loadLastGameIfExist() = launch(dispatchers.io()) {
        val gameExistsDeferred = async(dispatchers.io()) { hasCurrentGameUseCase() }
        val gameOptionsDeferred = async(dispatchers.io()) { getGameOptionsUseCase().getOrNull() }
        val (gameExists, gameOptions) = awaitAll(gameExistsDeferred, gameOptionsDeferred)
            .let { (existsResult, optionsResult) -> existsResult as Boolean to optionsResult as GameOptions? }
        if (gameExists && gameOptions != null) {
            emitNavAction(NavigateTo(GameplayRoute(isPlayerWhite = gameOptions.isPlayerWhite, isNewGame = false)))
        }
    }

    private suspend fun Job.isCompletedAfter(
        afterTime: Long,
        dispatcher: CoroutineDispatcher
    ): Boolean =
        flow {
            delay(afterTime)
            emit(isCompleted)
        }.flowOn(dispatcher).single()

    private companion object {
        private const val GAME_LOADING_VISIBLE_THRESHOLD_MILLIS = 20L
    }
}
