package com.mudita.chess.navigation.routes

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mudita.chess.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class GameplayRoute(
    val isPlayerWhite: Boolean,
    val isNewGame: Boolean = true,
    val isTwoPlayerMode: Boolean = false
) : Route {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<GameplayRoute>()
    }
}
