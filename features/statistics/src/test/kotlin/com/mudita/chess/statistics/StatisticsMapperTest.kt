package com.mudita.chess.statistics

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_LOST_WHITE_1
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_BLACK_5
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5_LOST_WHITE_1
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5_WON_BLACK_2
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_LOST_1
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_WON_5
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_WON_5_LOST_1
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_WON_5_LOST_1_DRAW_10
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_WON_7
import com.mudita.chess.statistics.model.MatchResultUi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class StatisticsMapperTest {

    private val tested = StatisticsMapper()

    @ParameterizedTest
    @MethodSource("provideGameStatisticsToPlayedAsWhitePercentage")
    fun `map to played as white percentage should return percentage value`(
        gameStatistics: GameStatistics,
        expected: Int
    ) {
        val result = tested.toPlayedAsWhitePercentage(gameStatistics)

        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("provideGameStatisticsToMatchResults")
    fun `map to match results should return match results ui`(
        gameStatistics: GameStatistics,
        expected: List<MatchResultUi>
    ) {
        val result = tested.toMatchResults(gameStatistics)

        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun provideGameStatisticsToPlayedAsWhitePercentage(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(GAME_STATISTICS_WON_BLACK_5, 0),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5, 100),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_WON_BLACK_2, 71),
                Arguments.of(GAME_STATISTICS_LOST_WHITE_1, 100),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_WHITE_1, 100),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1, 83),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10, 94),
            )
        }

        @JvmStatic
        fun provideGameStatisticsToMatchResults(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(GAME_STATISTICS_WON_BLACK_5, MATCH_RESULTS_WON_5),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5, MATCH_RESULTS_WON_5),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_WON_BLACK_2, MATCH_RESULTS_WON_7),
                Arguments.of(GAME_STATISTICS_LOST_WHITE_1, MATCH_RESULTS_LOST_1),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_WHITE_1, MATCH_RESULTS_WON_5_LOST_1),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1, MATCH_RESULTS_WON_5_LOST_1),
                Arguments.of(GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10, MATCH_RESULTS_WON_5_LOST_1_DRAW_10),
            )
        }
    }
}