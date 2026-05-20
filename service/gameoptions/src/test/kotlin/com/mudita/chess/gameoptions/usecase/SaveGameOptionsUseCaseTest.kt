package com.mudita.chess.gameoptions.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.repository.GameOptionsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SaveGameOptionsUseCaseTest {

    private val gameOptionsRepository: GameOptionsRepository = mockk()

    private val tested by lazy { SaveGameOptionsUseCase(gameOptionsRepository) }

    @Test
    fun `returns success`() = runTest {
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = false,
            isPlayerWhite = false,
            difficultyLevel = DifficultyLevel(3)
        )
        coEvery { gameOptionsRepository.saveGameOptions(gameOptions) } returns Unit

        val result = tested(gameOptions)

        assertThat(result).isEqualTo(Result.success(Unit))
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = false,
            isPlayerWhite = false,
            difficultyLevel = DifficultyLevel(3)
        )
        val exception = Exception("Game options not saved")
        coEvery { gameOptionsRepository.saveGameOptions(gameOptions) } throws exception

        val result = tested(gameOptions)

        assertThat(result).isEqualTo(Result.failure<Exception>(exception))
    }
}
