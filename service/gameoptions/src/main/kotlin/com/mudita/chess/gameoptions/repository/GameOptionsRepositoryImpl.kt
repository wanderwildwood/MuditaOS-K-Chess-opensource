package com.mudita.chess.gameoptions.repository

import com.mudita.chess.gameoptions.mapper.toDomain
import com.mudita.chess.gameoptions.mapper.toPrefs
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.model.GameOptionsPrefs
import com.mudita.chess.preferences.ComplexPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class GameOptionsRepositoryImpl(
    private val preferences: ComplexPreferences<GameOptionsPrefs>,
    private val ioDispatcher: CoroutineDispatcher
) : GameOptionsRepository {
    override suspend fun getGameOptions() = withContext(ioDispatcher) {
        preferences.get()?.toDomain() ?: GameOptions.DEFAULT
    }

    override suspend fun saveGameOptions(
        gameOptions: GameOptions
    ) = withContext(ioDispatcher) {
        preferences.put { gameOptions.toPrefs() }
    }
}
