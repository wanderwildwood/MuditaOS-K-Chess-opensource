package com.mudita.chess.gameoptions.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GameOptionsPrefs(
    val isMoveSuggestionsOn: Boolean,
    val isPlayerWhite: Boolean,
    val difficultyLevel: Int
)
