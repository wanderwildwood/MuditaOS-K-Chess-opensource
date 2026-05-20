package com.mudita.chess.games.repository

import com.mudita.chess.games.model.Game

internal interface GamesRepository {
    suspend fun hasCurrentGame(): Boolean
    suspend fun saveCurrentGame(game: Game)
    suspend fun getCurrentGame(): Game?
    suspend fun removeCurrentGame()
}
