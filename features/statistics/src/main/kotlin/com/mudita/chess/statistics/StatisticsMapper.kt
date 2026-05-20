package com.mudita.chess.statistics

import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsType.DRAW
import com.mudita.chess.gamestatistics.model.StatisticsType.LOST
import com.mudita.chess.gamestatistics.model.StatisticsType.WON
import com.mudita.chess.statistics.model.MatchResultUi
import kotlin.math.roundToInt
import com.mudita.chess.frontitude.R as RFrontitude

internal class StatisticsMapper {

    fun toPlayedAsWhitePercentage(gameStatistics: GameStatistics): Int = with(gameStatistics) {
        val allGamesCount = counterMap.values.sum()
        val playedAsWhiteCount = counterMap.filterKeys { it.isWhitePlayer }.values.sum()
        return calculatePercentage(value = playedAsWhiteCount, all = allGamesCount)
    }

    fun toMatchResults(gameStatistics: GameStatistics): List<MatchResultUi> = with(gameStatistics) {
        buildList {
            val winsCount = sumCountOf(type = WON)
            val allGamesCount = counterMap.values.sum()

            add(toWonResultUi(winsCount = winsCount))
            add(toDrawnResultUi())
            add(toLostResultUi())
            add(toPercentageOfWinsResultUi(winsCount = winsCount, allGamesCount = allGamesCount))
        }
    }

    private fun toWonResultUi(winsCount: Int) =
        MatchResultUi(
            titleResId = RFrontitude.string.chess_statistics_label_won,
            value = winsCount
        )

    private fun GameStatistics.toDrawnResultUi() =
        MatchResultUi(
            titleResId = RFrontitude.string.chess_statistics_label_drawn,
            value = sumCountOf(type = DRAW)
        )

    private fun GameStatistics.toLostResultUi() =
        MatchResultUi(
            titleResId = RFrontitude.string.chess_statistics_label_lost,
            value = sumCountOf(type = LOST)
        )

    private fun toPercentageOfWinsResultUi(winsCount: Int, allGamesCount: Int) =
        MatchResultUi(
            titleResId = RFrontitude.string.chess_statistics_label_percentageofwins,
            value = calculatePercentage(value = winsCount, all = allGamesCount)
        )

    private fun calculatePercentage(value: Int, all: Int) =
        (value.toDouble() / all * PERCENTAGE_MULTIPLIER).roundToInt()

    private fun GameStatistics.sumCountOf(type: StatisticsType) =
        counterMap.filterKeys { it.type == type }.values.sum()

    private companion object {
        const val PERCENTAGE_MULTIPLIER = 100
    }
}
