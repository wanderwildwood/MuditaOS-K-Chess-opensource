package com.mudita.chess.games.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.games.fixtures.MOVES_LAN
import com.mudita.chess.games.fixtures.MOVES_SAN
import com.mudita.chess.games.model.Game
import com.mudita.chess.games.repository.GamesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SaveCurrentGameMovesUseCaseTest {

    private val repository: GamesRepository = mockk(relaxed = true)
    private val tested = SaveCurrentGameMovesUseCase(repository, UnconfinedTestDispatcher())

    @Test
    fun `return success for game with moves`() = runTest {
        val result = tested.invoke(MOVES_LAN)

        assertThat(result).isEqualTo(Result.success(Unit))

        coEvery {
            repository.saveCurrentGame(Game(MOVES_SAN))
        }
    }

    @Test
    fun `return success for game without moves`() = runTest {
        val result = tested.invoke(emptyList())

        assertThat(result).isEqualTo(Result.success(Unit))

        coEvery {
            repository.saveCurrentGame(Game(null))
        }
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Save failed")
        coEvery { repository.saveCurrentGame(any()) } throws exception

        val result = tested.invoke(MOVES_LAN)

        assertThat(result).isEqualTo(Result.failure<Unit>(exception))
        coVerify {
            repository.saveCurrentGame(any())
        }
    }
}
