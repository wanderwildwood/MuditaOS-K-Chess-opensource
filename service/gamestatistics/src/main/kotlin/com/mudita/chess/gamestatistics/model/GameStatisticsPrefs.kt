package com.mudita.chess.gamestatistics.model

import kotlinx.serialization.Serializable

@Serializable
internal data class GameStatisticsPrefs(
    val counterMap: Map<StatisticKeyPrefs, Int> = emptyMap()
)

@Serializable
internal data class StatisticKeyPrefs(
    val type: StatisticsTypePrefs,
    val isWhitePlayer: Boolean
)

internal enum class StatisticsTypePrefs {
    WON,
    LOST,
    DRAW
}
