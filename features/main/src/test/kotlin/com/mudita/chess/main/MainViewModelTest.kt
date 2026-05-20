package com.mudita.chess.main

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.coroutines.Dispatchers
import com.mudita.chess.coroutines.MainDispatcherExtension
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.GetGameOptionsUseCase
import com.mudita.chess.games.usecase.HasCurrentGameUseCase
import com.mudita.chess.main.MainUiEvent.PlayButtonClicked
import com.mudita.chess.main.MainUiEvent.StatisticsButtonClicked
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.navigation.routes.StatisticsRoute
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class MainViewModelTest {

    private val gameOptions = GameOptions.DEFAULT
    private val hasCurrentGameUseCase: HasCurrentGameUseCase = mockk(relaxed = true)
    private val getGameOptionsUseCase: GetGameOptionsUseCase = mockk {
        coEvery { this@mockk.invoke() } returns Result.success(gameOptions)
    }
    private val dispatchers: Dispatchers by lazy {
        mockk {
            every { io() } returns UnconfinedTestDispatcher()
        }
    }

    private val tested by lazy {
        MainViewModel(
            hasCurrentGameUseCase = hasCurrentGameUseCase,
            getGameOptionsUseCase = getGameOptionsUseCase,
            dispatchers = dispatchers
        )
    }

    @Test
    fun `on init navigates to gameplay when there is a current game`() = runTest {
        val gameOptions = GameOptions(
            isPlayerWhite = false,
            isMoveSuggestionsOn = true,
            difficultyLevel = DifficultyLevel(4)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        coEvery { hasCurrentGameUseCase() } returns true

        tested.navActions.test {
            assertThat(awaitItem()).isEqualTo(NavigateTo(GameplayRoute(isPlayerWhite = false, isNewGame = false)))
        }
        tested.states.test {
            assertThat(awaitItem()).isEqualTo(MainUiState(isLoading = null))
            assertThat(awaitItem()).isEqualTo(MainUiState(isLoading = false))
        }
    }

    @Test
    fun `on init does not navigate to gameplay when unable load game options`() = runTest {
        coEvery { getGameOptionsUseCase() } returns Result.failure(Error("read exception!"))
        coEvery { hasCurrentGameUseCase() } returns true

        tested.navActions.test {
            expectNoEvents()
        }
        tested.states.test {
            assertThat(awaitItem()).isEqualTo(MainUiState(isLoading = null))
            assertThat(awaitItem()).isEqualTo(MainUiState(isLoading = false))
        }
    }

    @Test
    fun `on init shows loading when loading current game takes too long`() = runTest {
        turbineScope {
            val loadTime = 100L
            coEvery { hasCurrentGameUseCase() } returns true
            coEvery { getGameOptionsUseCase() } coAnswers {
                delay(loadTime)
                Result.success(gameOptions)
            }

            val uiStatesTurbine = tested.states.testIn(this)
            val navActionTurbine = tested.navActions.testIn(this)

            assertThat(uiStatesTurbine.awaitItem())
                .isEqualTo(MainUiState(isLoading = null))
            assertThat(uiStatesTurbine.awaitItem())
                .isEqualTo(MainUiState(isLoading = true))
            advanceTimeBy(loadTime + 1)
            assertThat(navActionTurbine.awaitItem())
                .isEqualTo(NavigateTo(GameplayRoute(isPlayerWhite = true, isNewGame = false)))
            assertThat(uiStatesTurbine.awaitItem())
                .isEqualTo(MainUiState(isLoading = false))

            uiStatesTurbine.cancel()
            uiStatesTurbine.ensureAllEventsConsumed()
            navActionTurbine.cancel()
            navActionTurbine.ensureAllEventsConsumed()
        }
    }

    @Test
    fun `PlayButtonClicked event should load game options with success and navigate to options menu with loaded values`() = runTest {
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = DifficultyLevel(4)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(
            gameOptions
        )

        tested.navActions.test {
            tested.handleUiEvent(PlayButtonClicked)

            assertThat(awaitItem()).isEqualTo(
                NavigateTo(
                    OptionsMenuRoute(
                        isMoveSuggestionsOn = true,
                        isPlayerWhite = false,
                        difficultyLevel = 4
                    )
                )
            )
        }
    }

    @Test
    fun `PlayButtonClicked event should load game options with failure and navigate to options menu with default values`() = runTest {
        coEvery { getGameOptionsUseCase() } returns Result.failure(Exception())

        tested.navActions.test {
            tested.handleUiEvent(PlayButtonClicked)

            assertThat(awaitItem()).isEqualTo(
                NavigateTo(
                    OptionsMenuRoute(
                        isMoveSuggestionsOn = true,
                        isPlayerWhite = true,
                        difficultyLevel = 1
                    )
                )
            )
        }
    }

    @Test
    fun `StatisticsButtonClicked event should navigate to statistics`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(StatisticsButtonClicked)

            assertThat(awaitItem()).isEqualTo(NavigateTo(StatisticsRoute))
        }
    }

}
