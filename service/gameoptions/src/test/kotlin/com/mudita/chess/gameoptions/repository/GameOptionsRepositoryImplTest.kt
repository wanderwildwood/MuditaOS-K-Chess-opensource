package com.mudita.chess.gameoptions.repository

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.model.GameOptionsPrefs
import com.mudita.chess.preferences.ComplexPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GameOptionsRepositoryImplTest {

    private val preferences: ComplexPreferences<GameOptionsPrefs> = mockk {
        coEvery { get() } returns GameOptionsPrefs(
            isMoveSuggestionsOn = true,
            isPlayerWhite = true,
            difficultyLevel = 1
        )
        coEvery { put(any()) } returns Unit
    }

    private val tested = GameOptionsRepositoryImpl(
        preferences = preferences,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `get games options returns game options`() = runTest {
        val result = tested.getGameOptions()

        assertThat(result).isEqualTo(
            GameOptions(
                isMoveSuggestionsOn = true,
                isPlayerWhite = true,
                difficultyLevel = DifficultyLevel(1)
            )
        )
    }

    @Test
    fun `save game options calls put in preferences`() = runTest {
        tested.saveGameOptions(
            GameOptions(
                isMoveSuggestionsOn = true,
                isPlayerWhite = true,
                difficultyLevel = DifficultyLevel(1)
            )
        )

        coVerify { preferences.put(any()) }
    }
}
