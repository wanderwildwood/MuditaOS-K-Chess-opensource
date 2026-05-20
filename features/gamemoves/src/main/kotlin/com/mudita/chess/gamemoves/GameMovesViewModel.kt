package com.mudita.chess.gamemoves

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mudita.chess.gamemoves.GameMovesUiEvent.BackClicked
import com.mudita.chess.gamemoves.GameMovesUiEvent.NavigationUpClicked
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.mvvm.StateHandler
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.NavActionsEmitter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal data class GameMovesUiState(
    val moves: List<MoveUi> = emptyList()
)

internal sealed interface GameMovesUiEvent {
    data object BackClicked : GameMovesUiEvent
    data object NavigationUpClicked : GameMovesUiEvent
}

internal class GameMovesViewModel(
    args: GameMoveRouteProvider,
    private val mapper: GameMovesMapper
) : ViewModel(),
    NavActionsEmitter by NavActionsEmitter() {

    private val uiState: StateHandler<GameMovesUiState> by lazy {
        val movesUi = mapper.toMovesUi(args.value.moves)
        StateHandler(GameMovesUiState(movesUi))
    }

    val uiStates: StateFlow<GameMovesUiState>
        get() = uiState.states

    fun handleUiEvent(uiEvent: GameMovesUiEvent) = viewModelScope.launch {
        when (uiEvent) {
            BackClicked,
            NavigationUpClicked -> emitNavAction(NavigateUp())
        }
    }
}
