package com.mudita.chess.gamestatistics.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RemoveGameStatisticsUseCaseTest {

    private val gameStatisticsRepository: GameStatisticsRepository = mockk(relaxed = true)

    private val tested by lazy { RemoveGameStatisticsUseCase(gameStatisticsRepository) }

    @Test
    fun `returns success`() = runTest {
        val result = tested()

        assertThat(result).isEqualTo(Result.success(Unit))

        coEvery {
            gameStatisticsRepository.removeGameStatistics()
        }
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Failed to remove game statistics")
        coEvery { gameStatisticsRepository.removeGameStatistics() } throws exception

        val result = tested()

        assertThat(result).isEqualTo(Result.failure<Exception>(exception))
    }
}
