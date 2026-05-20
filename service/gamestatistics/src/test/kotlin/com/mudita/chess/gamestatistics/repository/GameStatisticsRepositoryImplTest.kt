package com.mudita.chess.gamestatistics.repository

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.GameStatisticsPrefs
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticKeyPrefs
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsTypePrefs
import com.mudita.chess.preferences.ComplexPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GameStatisticsRepositoryImplTest {

    private val preferences: ComplexPreferences<GameStatisticsPrefs> = mockk {
        coEvery { get() } returns GameStatisticsPrefs(
            counterMap = mapOf(
                StatisticKeyPrefs(type = StatisticsTypePrefs.WON, isWhitePlayer = true) to 1
            )
        )
        coEvery { put(any()) } returns Unit
    }

    private val tested = GameStatisticsRepositoryImpl(
        preferences = preferences,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `get games statistics returns game statistics`() = runTest {
        val result = tested.getGameStatistics()

        assertThat(result).isEqualTo(
            GameStatistics(
                counterMap = mapOf(
                    StatisticKey(type = StatisticsType.WON, isWhitePlayer = true) to 1
                )
            )
        )
    }

    @Test
    fun `save game statistics calls put in preferences`() = runTest {
        tested.saveGameStatistics(
            GameStatistics(
                counterMap = mapOf(
                    StatisticKey(type = StatisticsType.WON, isWhitePlayer = true) to 1
                )
            )
        )

        coVerify { preferences.put(any()) }
    }

    @Test
    fun `remove games statistics removes statistics`() = runTest {
        tested.removeGameStatistics()

        coVerify { preferences.put(any()) }
    }
}
