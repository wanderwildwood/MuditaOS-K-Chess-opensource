package com.mudita.chess.gamestatistics.model

data class GameStatistics(
    val counterMap: Map<StatisticKey, Int>
) {
    companion object {
        val EMPTY = GameStatistics(
            counterMap = emptyMap()
        )
    }
}

data class StatisticKey(
    val type: StatisticsType,
    val isWhitePlayer: Boolean
)

enum class StatisticsType {
    WON,
    LOST,
    DRAW
}
