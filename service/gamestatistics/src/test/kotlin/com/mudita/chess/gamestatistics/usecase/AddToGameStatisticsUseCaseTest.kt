package com.mudita.chess.gamestatistics.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsType.LOST
import com.mudita.chess.gamestatistics.model.StatisticsType.WON
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AddToGameStatisticsUseCaseTest {

    private val gameStatisticsRepository: GameStatisticsRepository = mockk()

    private val tested = AddToGameStatisticsUseCase(
        gameStatisticsRepository = gameStatisticsRepository
    )

    @ParameterizedTest
    @CsvSource(
        value = [
            "WON,true",
            "WON,false",
            "LOST,true",
            "LOST,false",
            "DRAW,true",
            "DRAW,false",
        ]
    )
    fun `adding statistic with type and player color that already exists increments counter`(
        type: StatisticsType,
        isWhitePlayer: Boolean
    ) = runTest {
        coEvery { gameStatisticsRepository.getGameStatistics() } returns
                GameStatistics(
                    counterMap = mapOf(StatisticKey(type, isWhitePlayer) to 1)
                )

        tested(type, isWhitePlayer)

        coVerify {
            gameStatisticsRepository.saveGameStatistics(
                GameStatistics(
                    counterMap = mapOf(StatisticKey(type, isWhitePlayer) to 2)
                )
            )
        }
    }


    @ParameterizedTest
    @CsvSource(
        value = [
            "WON,false",
            "LOST,true",
            "LOST,false",
            "DRAW,true",
            "DRAW,false",
        ]
    )
    fun `adding statistic with type and player color that doesn't exists adds new entry with counter 1`(
        type: StatisticsType,
        isWhitePlayer: Boolean
    ) = runTest {
        coEvery { gameStatisticsRepository.getGameStatistics() } returns
                GameStatistics(
                    counterMap = mapOf(StatisticKey(type = WON, isWhitePlayer = true) to 1)
                )

        tested(type, isWhitePlayer)

        coVerify {
            gameStatisticsRepository.saveGameStatistics(
                GameStatistics(
                    counterMap = mapOf(
                        StatisticKey(type = WON, isWhitePlayer = true) to 1,
                        StatisticKey(type, isWhitePlayer) to 1
                    )
                )
            )
        }
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Failed to save game statistics")
        coEvery { gameStatisticsRepository.getGameStatistics() } returns
                GameStatistics(
                    counterMap = mapOf(StatisticKey(type = WON, isWhitePlayer = true) to 1)
                )
        coEvery { gameStatisticsRepository.saveGameStatistics(any()) } throws exception

        val result = tested(type = LOST, isWhitePlayer = false)

        assertThat(result).isEqualTo(Result.failure<Exception>(exception))
    }
}