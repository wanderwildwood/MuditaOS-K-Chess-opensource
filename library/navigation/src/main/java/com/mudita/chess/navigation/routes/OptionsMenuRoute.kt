package com.mudita.chess.navigation.routes

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mudita.chess.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
data class OptionsMenuRoute(
    val isMoveSuggestionsOn: Boolean,
    val isPlayerWhite: Boolean,
    val difficultyLevel: Int,
    val isTwoPlayerMode: Boolean = false
) : Route {
    companion object {
        fun from(savedStateHandle: SavedStateHandle) =
            savedStateHandle.toRoute<OptionsMenuRoute>()
    }
}
