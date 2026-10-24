package com.mudita.chess.gameoptions.model

data class GameOptions(
    val isMoveSuggestionsOn: Boolean,
    val isPlayerWhite: Boolean,
    val difficultyLevel: DifficultyLevel,
    val isTwoPlayerMode: Boolean = false,
    /** A short tick from the phone when a move of the player's own is accepted. Off unless asked for. */
    val isVibrateOn: Boolean = false
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
