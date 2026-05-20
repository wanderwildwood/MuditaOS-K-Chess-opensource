package com.mudita.chess.gamestatistics.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.gamestatistics.model.StatisticKey
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository

class AddToGameStatisticsUseCase internal constructor(
    private val gameStatisticsRepository: GameStatisticsRepository
) {

    suspend operator fun invoke(type: StatisticsType, isWhitePlayer: Boolean) = resultOf {
        val statistics = gameStatisticsRepository.getGameStatistics()

        val key = StatisticKey(type, isWhitePlayer)
        val currentCounter = statistics.counterMap.getOrDefault(key = key, defaultValue = 0)

        gameStatisticsRepository.saveGameStatistics(
            statistics.copy(
                counterMap = statistics.counterMap + (key to currentCounter + 1)
            )
        )
    }
}
