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

class GetCurrentGameMovesUseCaseTest {

    private val repository: GamesRepository = mockk()
    private val tested = GetCurrentGameMovesUseCase(repository, UnconfinedTestDispatcher())

    @Test
    fun `return success with moves`() = runTest {
        coEvery { repository.getCurrentGame() } returns Game(MOVES_SAN)

        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.success(MOVES_LAN))
    }

    @Test
    fun `returns success with empty list if game started but no moves performed`() = runTest {
        coEvery { repository.getCurrentGame() } returns Game(null)

        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.success(emptyList<String>()))
    }

    @Test
    fun `returns success with null if game does not exist`() = runTest {
        coEvery { repository.getCurrentGame() } returns null

        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.success(null))
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Removal failed")
        coEvery { repository.getCurrentGame() } throws exception

        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.failure<Unit>(exception))
        coVerify {
            repository.getCurrentGame()
        }
    }
}
