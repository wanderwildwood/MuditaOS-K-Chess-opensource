package com.mudita.chess.gamestatistics.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticsType.WON
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GetGameStatisticsUseCaseTest {

    private val gameStatisticsRepository: GameStatisticsRepository = mockk()

    private val tested by lazy { GetGameStatisticsUseCase(gameStatisticsRepository) }

    @Test
    fun `returns success with game statistics`() = runTest {
        val gameStatistics = GameStatistics(
            counterMap = mapOf(StatisticKey(type = WON, isWhitePlayer = true) to 1)
        )
        coEvery { gameStatisticsRepository.getGameStatistics() } returns
                gameStatistics

        val result = tested()

        assertThat(result).isEqualTo(Result.success(gameStatistics))
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Game statistics not found")
        coEvery { gameStatisticsRepository.getGameStatistics() } throws exception

        val result = tested()

        assertThat(result).isEqualTo(Result.failure<Exception>(exception))
    }
}
