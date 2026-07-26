package com.mudita.chess.gameoptions.model

data class GameOptions(
    val isMoveSuggestionsOn: Boolean,
    val isPlayerWhite: Boolean,
    val difficultyLevel: DifficultyLevel,
    val isTwoPlayerMode: Boolean = false
) {
    companion object {
        val DEFAULT = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = true,
            difficultyLevel = DifficultyLevel(1),
            isTwoPlayerMode = false
        )
    }
}
