package com.mudita.chess.games.usecase

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.games.repository.GamesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class HasCurrentGameUseCaseTest {

    private val repository: GamesRepository = mockk()
    private val tested = HasCurrentGameUseCase(repository)

    @Test
    fun `return success with repository result`() = runTest {
        coEvery { repository.hasCurrentGame() } returns true

        val result = tested.invoke()

        assertThat(result).isTrue()
    }

    @Test
    fun `returns success with false if repository throws exception`() = runTest {
        val exception = Exception("Query failed")
        coEvery { repository.hasCurrentGame() } throws exception

        val result = tested.invoke()

        assertThat(result).isFalse()
    }
}
