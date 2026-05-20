package com.mudita.chess.gameoptions.repository

import com.mudita.chess.gameoptions.model.GameOptions

internal interface GameOptionsRepository {
    suspend fun getGameOptions(): GameOptions
    suspend fun saveGameOptions(gameOptions: GameOptions)
}
