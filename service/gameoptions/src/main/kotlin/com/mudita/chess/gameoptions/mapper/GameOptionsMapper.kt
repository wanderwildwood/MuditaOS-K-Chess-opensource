package com.mudita.chess.gameoptions.mapper

import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.model.GameOptionsPrefs

internal fun GameOptionsPrefs.toDomain() = GameOptions(
    isMoveSuggestionsOn = isMoveSuggestionsOn,
    isPlayerWhite = isPlayerWhite,
    difficultyLevel = DifficultyLevel(difficultyLevel)
)

internal fun GameOptions.toPrefs() = GameOptionsPrefs(
    isMoveSuggestionsOn = isMoveSuggestionsOn,
    isPlayerWhite = isPlayerWhite,
    difficultyLevel = difficultyLevel.value
)
