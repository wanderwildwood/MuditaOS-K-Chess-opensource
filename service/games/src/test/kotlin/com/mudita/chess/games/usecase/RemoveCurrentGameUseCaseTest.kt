package com.mudita.chess.games.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.games.repository.GamesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RemoveCurrentGameUseCaseTest {

    private val repository: GamesRepository = mockk(relaxed = true)
    private val tested = RemoveCurrentGameUseCase(repository)

    @Test
    fun `returns success`() = runTest {
        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.success(Unit))
        coEvery {
            repository.removeCurrentGame()
        }
    }

    @Test
    fun `returns failure with exception`() = runTest {
        val exception = Exception("Removal failed")
        coEvery { repository.removeCurrentGame() } throws exception

        val result = tested.invoke()

        assertThat(result).isEqualTo(Result.failure<Unit>(exception))
        coVerify {
            repository.removeCurrentGame()
        }
    }
}
