package com.mudita.chess.gamestatistics.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository

class RemoveGameStatisticsUseCase internal constructor(
    private val gameStatisticsRepository: GameStatisticsRepository
) {

    suspend operator fun invoke() = resultOf {
        gameStatisticsRepository.removeGameStatistics()
    }
}
