package com.mudita.chess.optionsmenu

import androidx.lifecycle.viewModelScope
import com.mudita.chess.gameoptions.mapper.MAX_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.mapper.MIN_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.mvvm.StateViewModel
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.NavActionsEmitter
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelMinusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelPlusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelStepClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.GameModeSelected
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.NavigationUpClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayButtonClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayerColorSelected
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.SaveGameState
import com.mudita.chess.ui.model.TextUi
import kotlinx.coroutines.launch
import logcat.logcat

internal data class OptionsMenuUiState(
    val isTwoPlayerMode: Boolean = false,
    val isMoveSuggestionsOn: Boolean = true,
    val isWhiteSelected: Boolean = true,
    val difficultyLevelStep: Int = 1,
    val difficultyLevelLabel: TextUi? = null
)

internal sealed interface OptionsMenuUiEvent {
    data object NavigationUpClicked : OptionsMenuUiEvent
    data class GameModeSelected(val isTwoPlayerMode: Boolean) : OptionsMenuUiEvent
    data object MoveSuggestionsSwitchToggled : OptionsMenuUiEvent
    data class PlayerColorSelected(val isWhiteSelected: Boolean) : OptionsMenuUiEvent
    data object DifficultyLevelMinusIconClicked : OptionsMenuUiEvent
    data object DifficultyLevelPlusIconClicked : OptionsMenuUiEvent
    data class DifficultyLevelStepClicked(val step: Int) : OptionsMenuUiEvent
    data object PlayButtonClicked : OptionsMenuUiEvent
    data object SaveGameState : OptionsMenuUiEvent
}

internal class OptionsMenuViewModel(
    args: OptionsMenuRouteProvider,
    private val mapper: OptionsMenuMapper,
    private val saveGameOptionsUseCase: SaveGameOptionsUseCase
) : StateViewModel<OptionsMenuUiState>(initialState(args.value, mapper)),
    NavActionsEmitter by NavActionsEmitter() {

    fun handleUiEvent(uiEvent: OptionsMenuUiEvent) {
        when (uiEvent) {
            is NavigationUpClicked -> onNavigationUpClicked()
            is GameModeSelected -> onGameModeSelected(uiEvent)
            is MoveSuggestionsSwitchToggled -> onMoveSuggestionsSwitchToggled()
            is PlayerColorSelected -> onPlayerColorSelected(uiEvent)
            is DifficultyLevelMinusIconClicked -> onDifficultyLevelMinusIconClicked()
            is DifficultyLevelPlusIconClicked -> onDifficultyLevelPlusIconClicked()
            is DifficultyLevelStepClicked -> onDifficultyLevelStepClicked(uiEvent)
            is PlayButtonClicked -> onNavigateToGame()
            is SaveGameState -> onSaveGameState()
        }
    }

    private fun onNavigationUpClicked() = viewModelScope.launch {
        emitNavAction(NavigateUp())
    }

    private fun onGameModeSelected(uiEvent: GameModeSelected) =
        updateState {
            copy(isTwoPlayerMode = uiEvent.isTwoPlayerMode)
        }

    private fun onMoveSuggestionsSwitchToggled() =
        updateState {
            copy(isMoveSuggestionsOn = !isMoveSuggestionsOn)
        }

    private fun onPlayerColorSelected(uiEvent: PlayerColorSelected) {
        updateState {
            copy(isWhiteSelected = uiEvent.isWhiteSelected)
        }
    }

    private fun onDifficultyLevelMinusIconClicked() {
        if (state.difficultyLevelStep <= MIN_DIFFICULTY_LEVEL) return

        updateState {
            val difficultyLevelStep = difficultyLevelStep - 1
            copy(
                difficultyLevelStep = difficultyLevelStep,
                difficultyLevelLabel = mapper.toDifficultyLevelLabel(difficultyLevelStep)
            )
        }
    }

    private fun onDifficultyLevelPlusIconClicked() {
        if (state.difficultyLevelStep >= MAX_DIFFICULTY_LEVEL) return

        updateState {
            val difficultyLevelStep = difficultyLevelStep + 1
            copy(
                difficultyLevelStep = difficultyLevelStep,
                difficultyLevelLabel = mapper.toDifficultyLevelLabel(difficultyLevelStep)
            )
        }
    }

    private fun onDifficultyLevelStepClicked(uiEvent: DifficultyLevelStepClicked) {
        updateState {
            copy(
                difficultyLevelStep = uiEvent.step,
                difficultyLevelLabel = mapper.toDifficultyLevelLabel(uiEvent.step)
            )
        }
    }

    private fun onNavigateToGame() = viewModelScope.launch {
        emitNavAction(
            NavigateTo(
                GameplayRoute(
                    isPlayerWhite = state.isWhiteSelected,
                    isTwoPlayerMode = state.isTwoPlayerMode
                )
            )
        )
    }

    private fun onSaveGameState() = viewModelScope.launch {
        saveGameOptionsUseCase(
            GameOptions(
                isMoveSuggestionsOn = state.isMoveSuggestionsOn,
                isPlayerWhite = state.isWhiteSelected,
                difficultyLevel = DifficultyLevel(state.difficultyLevelStep),
                isTwoPlayerMode = state.isTwoPlayerMode
            )
        ).onFailure { logcat { "Failed to save game options" } }
    }
}

private fun initialState(
    route: OptionsMenuRoute,
    mapper: OptionsMenuMapper
): OptionsMenuUiState =
    OptionsMenuUiState(
        isTwoPlayerMode = route.isTwoPlayerMode,
        isMoveSuggestionsOn = route.isMoveSuggestionsOn,
        isWhiteSelected = route.isPlayerWhite,
        difficultyLevelStep = route.difficultyLevel,
        difficultyLevelLabel = mapper.toDifficultyLevelLabel(route.difficultyLevel)
    )
