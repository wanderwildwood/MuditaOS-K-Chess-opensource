package com.mudita.chess.gameoptions.model

data class GameOptions(
    val isMoveSuggestionsOn: Boolean,
    val isPlayerWhite: Boolean,
    val difficultyLevel: DifficultyLevel
) {
    companion object {
        val DEFAULT = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = true,
            difficultyLevel = DifficultyLevel(1)
        )
    }
}
