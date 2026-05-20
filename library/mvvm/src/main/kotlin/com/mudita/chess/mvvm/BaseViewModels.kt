package com.mudita.chess.mvvm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

fun <UiState> StateHandler(defaultState: UiState): StateHandler<UiState> = StateHandlerImpl(defaultState)
fun <UiAction> ActionsHandler(): ActionsHandler<UiAction> = ActionsHandlerImpl()

interface StateHandler<UiState> {
    val states: StateFlow<UiState>
    val state: UiState
    fun setState(state: UiState)
    fun updateState(block: UiState.() -> UiState)
}

interface ActionsHandler<UiAction> {
    val actions: Flow<UiAction>
    suspend fun emitAction(action: UiAction)
}

private class StateHandlerImpl<UiState>(defaultState: UiState) : StateHandler<UiState> {
    private val _state = MutableStateFlow(defaultState)
    override val state: UiState
        get() = _state.value
    override val states: StateFlow<UiState> = _state.asStateFlow()

    override fun setState(state: UiState) {
        _state.value = state
    }

    override fun updateState(block: UiState.() -> UiState) {
        _state.update(block)
    }
}

private class ActionsHandlerImpl<UiAction> : ActionsHandler<UiAction> {
    private val _actions = Channel<UiAction>()
    override val actions: Flow<UiAction> = _actions.receiveAsFlow()

    override suspend fun emitAction(action: UiAction) {
        _actions.send(action)
    }
}

open class StateViewModel<UiState>(defaultState: UiState) :
    ViewModel(),
    StateHandler<UiState> by StateHandlerImpl(defaultState)

open class ActionsViewModel<UiAction> :
    ViewModel(),
    ActionsHandler<UiAction> by ActionsHandlerImpl()

open class StateActionsViewModel<UiState, UiAction>(defaultState: UiState) :
    ViewModel(),
    StateHandler<UiState> by StateHandlerImpl(defaultState),
    ActionsHandler<UiAction> by ActionsHandlerImpl()
