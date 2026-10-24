package com.mudita.chess.gameoptions.mapper

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.model.GameOptionsPrefs
import org.junit.jupiter.api.Test

class GameOptionsMapperTest {

    @Test
    fun `map domain model to prefs model`() {
        val model = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = DifficultyLevel(8)
        )

        val result = model.toPrefs()

        assertThat(result).isEqualTo(
            GameOptionsPrefs(
                isMoveSuggestionsOn = true,
                isPlayerWhite = false,
                difficultyLevel = 8
            )
        )
    }

    @Test
    fun `map prefs model to domain model`() {
        val model = GameOptionsPrefs(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = 8
        )

        val result = model.toDomain()

        assertThat(result).isEqualTo(
            GameOptions(
                isMoveSuggestionsOn = true,
                isPlayerWhite = false,
                difficultyLevel = DifficultyLevel(8)
            )
        )
    }

    @Test
    fun `map domain model with two player mode enabled to prefs model`() {
        val model = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = DifficultyLevel(8),
            isTwoPlayerMode = true
        )

        val result = model.toPrefs()

        assertThat(result).isEqualTo(
            GameOptionsPrefs(
                isMoveSuggestionsOn = true,
                isPlayerWhite = false,
                difficultyLevel = 8,
                isTwoPlayerMode = true
            )
        )
    }

    @Test
    fun `map prefs model with two player mode enabled to domain model`() {
        val model = GameOptionsPrefs(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = 8,
            isTwoPlayerMode = true
        )

        val result = model.toDomain()

        assertThat(result).isEqualTo(
            GameOptions(
                isMoveSuggestionsOn = true,
                isPlayerWhite = false,
                difficultyLevel = DifficultyLevel(8),
                isTwoPlayerMode = true
            )
        )
    }
}
