package com.mudita.chess.statistics

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.coroutines.MainDispatcherExtension
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.usecase.GetGameStatisticsUseCase
import com.mudita.chess.gamestatistics.usecase.RemoveGameStatisticsUseCase
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.statistics.StatisticsUiEvent.BackClicked
import com.mudita.chess.statistics.StatisticsUiEvent.ClearAllConfirmed
import com.mudita.chess.statistics.StatisticsUiEvent.NavigationUpClicked
import com.mudita.chess.statistics.fixtures.GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10
import com.mudita.chess.statistics.fixtures.MATCH_RESULTS_WON_5_LOST_1_DRAW_10
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class StatisticsViewModelTest {

    private val getGameStatisticsUseCase: GetGameStatisticsUseCase = mockk {
        coEvery { this@mockk.invoke() } returns Result.success(GAME_STATISTICS_WON_WHITE_5_LOST_BLACK_1_DRAW_WHITE_10)
    }

    private val removeGameStatisticsUseCase: RemoveGameStatisticsUseCase = mockk(relaxed = true)

    private val tested by lazy {
        StatisticsViewModel(
            mapper = StatisticsMapper(),
            getGameStatisticsUseCase = getGameStatisticsUseCase,
            removeGameStatisticsUseCase = removeGameStatisticsUseCase
        )
    }

    @Test
    fun `show empty screen if loaded game statistics are empty`() {
        coEvery { getGameStatisticsUseCase() } returns Result.success(GameStatistics.EMPTY)

        assertThat(tested.state)
            .isEqualTo(
                StatisticsUiState(
                    isContentVisible = true,
                    isEmptyContentVisible = true,
                    isClearAllButtonVisible = false
                )
            )
    }

    @Test
    fun `show empty screen if failed to load game statistics`() {
        coEvery { getGameStatisticsUseCase() } returns Result.failure(Exception())

        assertThat(tested.state)
            .isEqualTo(
                StatisticsUiState(
                    isContentVisible = true,
                    isEmptyContentVisible = true,
                    isClearAllButtonVisible = false
                )
            )
    }

    @Test
    fun `show statistics if loaded game statistics aren't empty`() {
        assertThat(tested.state)
            .isEqualTo(
                StatisticsUiState(
                    isContentVisible = true,
                    isEmptyContentVisible = false,
                    isClearAllButtonVisible = true,
                    playedAsWhitePercentage = 94,
                    playedAsBlackPercentage = 6,
                    matchResults = MATCH_RESULTS_WON_5_LOST_1_DRAW_10
                )
            )
    }

    @Test
    fun `BackClicked event should navigate up`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(BackClicked)

            assertThat(awaitItem()).isEqualTo(NavigateUp())
        }
    }

    @Test
    fun `NavigationUpClicked event should navigate up`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(NavigationUpClicked)

            assertThat(awaitItem()).isEqualTo(NavigateUp())
        }
    }

    @Test
    fun `ClearAllConfirmed event should clear statistics and show no statistics`() = runTest {
        tested.handleUiEvent(ClearAllConfirmed)

        assertThat(tested.state)
            .isEqualTo(
                StatisticsUiState(
                    isContentVisible = true,
                    isEmptyContentVisible = true,
                    isClearAllButtonVisible = false
                )
            )

        coVerify { removeGameStatisticsUseCase() }
    }
}
