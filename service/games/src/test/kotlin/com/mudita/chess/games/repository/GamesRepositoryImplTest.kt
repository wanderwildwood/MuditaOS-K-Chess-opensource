package com.mudita.chess.games.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.database.Database
import com.mudita.chess.games.fixtures.MOVES_SAN
import com.mudita.chess.games.model.Game
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class GamesRepositoryImplTest {

    private val inMemorySqlDriver = JdbcSqliteDriver(IN_MEMORY)
        .apply { Database.Schema.create(this) }

    private val database = Database(inMemorySqlDriver)

    private val tested = GamesRepositoryImpl(
        database = database,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `save stores game in preferences`() = runTest {
        tested.saveCurrentGame(Game(MOVES_SAN))

        val savedGame = database.savedGamesQueries.selectById("CURRENT_GAME").executeAsOneOrNull()
        assertThat(savedGame?.movesSAN)
            .isEqualTo(MOVES_SAN)
    }

    @Test
    fun `save removes previous game if it exists`() = runTest {
        database.savedGamesQueries.upsert("CURRENT_GAME", "1. b5")

        tested.saveCurrentGame(Game(MOVES_SAN))

        val savedGame = database.savedGamesQueries.selectById("CURRENT_GAME").executeAsOneOrNull()
        assertThat(savedGame?.movesSAN)
            .isEqualTo(MOVES_SAN)
    }

    @Test
    fun `get returns game`() = runTest {
        database.savedGamesQueries.upsert("CURRENT_GAME", MOVES_SAN)

        val result = tested.getCurrentGame()

        assertThat(result).isEqualTo(Game(MOVES_SAN))
    }

    @Test
    fun `get returns null if game does not exist`() = runTest {
        val result = tested.getCurrentGame()

        assertThat(result).isNull()
    }

    @Test
    fun `has returns true if game exists`() = runTest {
        database.savedGamesQueries.upsert("CURRENT_GAME", MOVES_SAN)

        val result = tested.hasCurrentGame()

        assertThat(result).isTrue()
    }

    @Test
    fun `has returns false if game doesn't exist`() = runTest {
        val result = tested.hasCurrentGame()

        assertThat(result).isFalse()
    }

    @Test
    fun `remove removes game from preferences`() = runTest {
        database.savedGamesQueries.upsert("CURRENT_GAME", MOVES_SAN)

        tested.removeCurrentGame()

        val savedGame = database.savedGamesQueries.selectById("CURRENT_GAME").executeAsOneOrNull()
        assertThat(savedGame).isNull()
    }

    @Test
    fun `remove do nothing if game does not exist`() = runTest {
        tested.removeCurrentGame()

        val savedGame = database.savedGamesQueries.selectById("CURRENT_GAME").executeAsOneOrNull()
        assertThat(savedGame).isNull()
    }
}
