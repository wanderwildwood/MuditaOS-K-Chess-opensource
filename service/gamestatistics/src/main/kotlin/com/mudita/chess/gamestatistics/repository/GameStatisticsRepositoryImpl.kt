package com.mudita.chess.gamestatistics.repository

import com.mudita.chess.gamestatistics.mapper.toDomain
import com.mudita.chess.gamestatistics.mapper.toPrefs
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.GameStatisticsPrefs
import com.mudita.chess.preferences.ComplexPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class GameStatisticsRepositoryImpl(
    private val preferences: ComplexPreferences<GameStatisticsPrefs>,
    private val ioDispatcher: CoroutineDispatcher
) : GameStatisticsRepository {
    override suspend fun getGameStatistics(): GameStatistics = withContext(ioDispatcher) {
        preferences.get()?.toDomain() ?: GameStatistics.EMPTY
    }

    override suspend fun saveGameStatistics(
        gameStatistics: GameStatistics
    ) = withContext(ioDispatcher) {
        preferences.put { gameStatistics.toPrefs() }
    }

    override suspend fun removeGameStatistics() = withContext(ioDispatcher) {
        preferences.put { GameStatistics.EMPTY.toPrefs() }
    }
}
