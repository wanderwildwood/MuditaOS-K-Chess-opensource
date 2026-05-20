package com.mudita.chess.gamestatistics.mapper

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.GameStatisticsPrefs
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticKeyPrefs
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsTypePrefs
import org.junit.jupiter.api.Test

class GameStatisticsMapperTest {

    @Test
    fun `map domain model to prefs model`() {
        val model = GameStatistics(
            counterMap = mapOf(
                StatisticKey(type = StatisticsType.WON, isWhitePlayer = true) to 3,
                StatisticKey(type = StatisticsType.WON, isWhitePlayer = false) to 5,
                StatisticKey(type = StatisticsType.LOST, isWhitePlayer = true) to 1,
                StatisticKey(type = StatisticsType.DRAW, isWhitePlayer = true) to 2,
            )
        )

        val result = model.toPrefs()

        assertThat(result).isEqualTo(
            GameStatisticsPrefs(
                counterMap = mapOf(
                    StatisticKeyPrefs(type = StatisticsTypePrefs.WON, isWhitePlayer = true) to 3,
                    StatisticKeyPrefs(type = StatisticsTypePrefs.WON, isWhitePlayer = false) to 5,
                    StatisticKeyPrefs(type = StatisticsTypePrefs.LOST, isWhitePlayer = true) to 1,
                    StatisticKeyPrefs(type = StatisticsTypePrefs.DRAW, isWhitePlayer = true) to 2,
                )
            )
        )
    }

    @Test
    fun `map prefs model to domain model`() {
        val model = GameStatisticsPrefs(
            counterMap = mapOf(
                StatisticKeyPrefs(type = StatisticsTypePrefs.WON, isWhitePlayer = true) to 3,
                StatisticKeyPrefs(type = StatisticsTypePrefs.WON, isWhitePlayer = false) to 5,
                StatisticKeyPrefs(type = StatisticsTypePrefs.LOST, isWhitePlayer = true) to 1,
                StatisticKeyPrefs(type = StatisticsTypePrefs.DRAW, isWhitePlayer = true) to 2,
            )
        )

        val result = model.toDomain()

        assertThat(result).isEqualTo(
            GameStatistics(
                counterMap = mapOf(
                    StatisticKey(type = StatisticsType.WON, isWhitePlayer = true) to 3,
                    StatisticKey(type = StatisticsType.WON, isWhitePlayer = false) to 5,
                    StatisticKey(type = StatisticsType.LOST, isWhitePlayer = true) to 1,
                    StatisticKey(type = StatisticsType.DRAW, isWhitePlayer = true) to 2,
                )
            )
        )
    }
}