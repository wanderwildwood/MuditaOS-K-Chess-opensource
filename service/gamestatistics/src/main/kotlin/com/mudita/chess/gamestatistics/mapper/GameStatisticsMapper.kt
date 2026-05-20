package com.mudita.chess.gamestatistics.mapper

import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.GameStatisticsPrefs
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticKeyPrefs
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsTypePrefs

internal fun GameStatisticsPrefs.toDomain() = GameStatistics(
    counterMap = counterMap.mapKeys { (key, _) ->
        StatisticKey(
            type = key.type.toDomain(),
            isWhitePlayer = key.isWhitePlayer
        )
    }
)

internal fun StatisticsTypePrefs.toDomain() = when (this) {
    StatisticsTypePrefs.WON -> StatisticsType.WON
    StatisticsTypePrefs.LOST -> StatisticsType.LOST
    StatisticsTypePrefs.DRAW -> StatisticsType.DRAW
}

internal fun GameStatistics.toPrefs() = GameStatisticsPrefs(
    counterMap = counterMap.mapKeys { (key, _) ->
        StatisticKeyPrefs(
            type = key.type.toPrefs(),
            isWhitePlayer = key.isWhitePlayer
        )
    }
)

internal fun StatisticsType.toPrefs() = when (this) {
    StatisticsType.WON -> StatisticsTypePrefs.WON
    StatisticsType.LOST -> StatisticsTypePrefs.LOST
    StatisticsType.DRAW -> StatisticsTypePrefs.DRAW
}
