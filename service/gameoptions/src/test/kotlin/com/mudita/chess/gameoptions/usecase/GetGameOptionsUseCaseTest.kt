package com.mudita.chess.gameoptions.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.repository.GameOptionsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetGameOptionsUseCaseTest {

    private val gameOptionsRepository: GameOptionsRepository = mockk()

    private val tested by lazy { GetGameOptionsUseCase(gameOptionsRepository) }

    @Test
    fun `returns success with game options`() = runTest {
        coEvery { gameOptionsRepository.getGameOptions() } returns
                GameOptions(
                    isMoveSuggestionsOn = false,
                    isPlayerWhite = true,
                    difficultyLevel = DifficultyLevel(7)
                )

        val result = tested()

        assertThat(result).isEqualTo(
            Result.success(
                GameOptions(
                    isMoveSuggestionsOn = false,
                    isPlayerWhite = true,
                    difficultyLevel = DifficultyLevel(7)
                )
            )
        )
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Game options not found")
        coEvery { gameOptionsRepository.getGameOptions() } throws exception

        val result = tested()

        assertThat(result).isEqualTo(Result.failure<Exception>(exception))
    }
}
