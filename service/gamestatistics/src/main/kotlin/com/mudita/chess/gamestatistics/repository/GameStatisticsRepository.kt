package com.mudita.chess.gamestatistics.repository

import com.mudita.chess.gamestatistics.model.GameStatistics

internal interface GameStatisticsRepository {
    suspend fun getGameStatistics(): GameStatistics
    suspend fun saveGameStatistics(gameStatistics: GameStatistics)
    suspend fun removeGameStatistics()
}
