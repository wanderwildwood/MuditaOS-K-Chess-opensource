package com.mudita.chess.games.repository

import com.mudita.chess.database.Database
import com.mudita.chess.games.model.Game
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class GamesRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher
) : GamesRepository {

    override suspend fun hasCurrentGame(): Boolean = withContext(ioDispatcher) {
        database.savedGamesQueries
            .selectExistsById(CURRENT_GAME_ID)
            .executeAsOne()
    }

    override suspend fun saveCurrentGame(game: Game) {
        withContext(ioDispatcher) {
            database.savedGamesQueries.upsert(
                id = CURRENT_GAME_ID,
                movesSAN = game.movesSAN
            )
        }
    }

    override suspend fun getCurrentGame(): Game? = withContext(ioDispatcher) {
        database.savedGamesQueries
            .selectById(CURRENT_GAME_ID)
            .executeAsOneOrNull()
            ?.let { Game(movesSAN = it.movesSAN) }
    }

    override suspend fun removeCurrentGame() {
        withContext(ioDispatcher) {
            database.savedGamesQueries.deleteById(CURRENT_GAME_ID)
        }
    }

    private companion object {
        const val CURRENT_GAME_ID = "CURRENT_GAME"
    }
}
