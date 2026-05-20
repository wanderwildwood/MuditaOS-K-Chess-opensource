package com.mudita.chess.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

fun NavActionsEmitter(): NavActionsEmitter = NavActionsEmitterImpl()

interface NavActionsEmitter {
    val navActions: Flow<NavAction>
    suspend fun emitNavAction(action: NavAction)
}

private class NavActionsEmitterImpl : NavActionsEmitter {
    private val _navActions = Channel<NavAction>()
    override val navActions: Flow<NavAction> = _navActions.receiveAsFlow()

    override suspend fun emitNavAction(action: NavAction) {
        _navActions.send(action)
    }
}
