package com.mudita.chess.statistics.fixtures

import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticsType.DRAW
import com.mudita.chess.gamestatistics.model.StatisticsType.LOST
import com.mudita.chess.gamestatistics.model.StatisticsType.WON

internal val GAME_STATISTICS_LOST_WHITE_1 = GameStatistics(counterMap = mapOf(StatisticKey(type = LOST, isWhitePlayer = true) to 1))
internal val GAME_STATISTICS_WON_BLACK_5 = GameStatistics(counterMap = mapOf(StatisticKey(type = WON, isWhitePlayer = false) to 5))
internal val GAME_STATISTICS_WON_WHITE_5 = GameStatistics(counterMap = mapOf(StatisticKey(type = WON, isWhitePlayer = true) to 5))
internal val GAME_STATISTICS_WON_WHITE_5_WON_BLACK_2 = GameStatistics(
    counterMap = mapOf(
        StatisticKey(type = WON, isWhitePlayer = true) to 5,
        StatisticKey(type = WON, isWhitePlayer = false) to 2
    )

)
internal val GAME_STATISTICS_WON_WHITE_5_LOST_WHITE_1 = GameStatistics(
    counterMap = mapOf(
        StatisticKey(type = WON, isWhitePlayer = true) to 5,
        StatisticKey(type = LOST, isWhitePlayer = true) to 1
    )
)
internal val GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1 = GameStatistics(
    counterMap = mapOf(
        StatisticKey(type = WON, isWhitePlayer = true) to 5,
        StatisticKey(type = LOST, isWhitePlayer = false) to 1
    )
)

internal val GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10 = GameStatistics(
    counterMap = mapOf(
        StatisticKey(type = WON, isWhitePlayer = true) to 5,
        StatisticKey(type = LOST, isWhitePlayer = false) to 1,
        StatisticKey(type = DRAW, isWhitePlayer = true) to 10
    )
)