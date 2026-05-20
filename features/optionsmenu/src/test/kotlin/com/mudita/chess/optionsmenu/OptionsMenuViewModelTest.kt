package com.mudita.chess.optionsmenu

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.coroutines.MainDispatcherExtension
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelMinusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelPlusIconClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.DifficultyLevelStepClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.NavigationUpClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayButtonClicked
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.PlayerColorSelected
import com.mudita.chess.optionsmenu.OptionsMenuUiEvent.SaveGameState
import com.mudita.chess.ui.model.TextUi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import com.mudita.chess.frontitude.R as RFrontitude

@ExtendWith(MainDispatcherExtension::class)
class OptionsMenuViewModelTest {

    private val args: OptionsMenuRouteProvider = mockk {
        every { value } returns OptionsMenuRoute(
            isMoveSuggestionsOn = true,
            isPlayerWhite = true,
            difficultyLevel = 1
        )
    }

    private val mapper: OptionsMenuMapper = mockk {
        every { toDifficultyLevelLabel(any()) } returns TextUi.Res(
            RFrontitude.string.chess_optionsmenu_label_beginner,
            args = arrayOf(500)
        )
    }

    private val saveGameOptionsUseCase: SaveGameOptionsUseCase = mockk {
        coEvery { this@mockk.invoke(any()) } returns Result.success(Unit)
    }

    private val tested by lazy {
        OptionsMenuViewModel(
            args = args,
            mapper = mapper,
            saveGameOptionsUseCase = saveGameOptionsUseCase
        )
    }

    @Test
    fun `show screen with initial values`() {
        every { args.value } returns OptionsMenuRoute(
            isMoveSuggestionsOn = true,
            isPlayerWhite = false,
            difficultyLevel = 4
        )

        assertThat(tested.state)
            .isEqualTo(
                OptionsMenuUiState(
                    isMoveSuggestionsOn = true,
                    isWhiteSelected = false,
                    difficultyLevelStep = 4,
                    difficultyLevelLabel = TextUi.Res(
                        RFrontitude.string.chess_optionsmenu_label_beginner,
                        args = arrayOf(500)
                    )
                )
            )
    }

    @Test
    fun `NavigationUpClicked event should navigate up`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(NavigationUpClicked)

            assertThat(awaitItem()).isEqualTo(NavigateUp())
        }
    }

    @Test
    fun `MoveSuggestionsSwitchToggled event should set move suggestions enabled to false`() {
        tested.handleUiEvent(MoveSuggestionsSwitchToggled)

        assertThat(tested.state.isMoveSuggestionsOn).isEqualTo(false)
    }

    @Test
    fun `MoveSuggestionsSwitchToggled event should set move suggestions enabled to true when was disabled before`() {
        tested.updateState { copy(isMoveSuggestionsOn = false) }

        tested.handleUiEvent(MoveSuggestionsSwitchToggled)

        assertThat(tested.state.isMoveSuggestionsOn).isEqualTo(true)
    }

    @Test
    fun `PlayerColorSelected event with white selected false should set white selected to false`() {
        tested.handleUiEvent(PlayerColorSelected(isWhiteSelected = false))

        assertThat(tested.state.isWhiteSelected).isEqualTo(false)
    }

    @Test
    fun `PlayerColorSelected event with white selected true should set white selected to true`() {
        tested.handleUiEvent(PlayerColorSelected(isWhiteSelected = true))

        assertThat(tested.state.isWhiteSelected).isEqualTo(true)
    }

    @Test
    fun `DifficultyLevelMinusIconClicked event should decrement difficulty level`() {
        tested.updateState { copy(difficultyLevelStep = 2) }

        tested.handleUiEvent(DifficultyLevelMinusIconClicked)

        assertThat(tested.state.difficultyLevelStep).isEqualTo(1)
        assertThat(tested.state.difficultyLevelLabel).isNotNull()
    }

    @Test
    fun `DifficultyLevelMinusIconClicked event should not decrement difficulty level when at min level`() {
        tested.handleUiEvent(DifficultyLevelMinusIconClicked)

        assertThat(tested.state.difficultyLevelStep).isEqualTo(1)
        assertThat(tested.state.difficultyLevelLabel).isNotNull()
    }

    @Test
    fun `DifficultyLevelPlusIconClicked event should increment difficulty level`() {
        tested.handleUiEvent(DifficultyLevelPlusIconClicked)

        assertThat(tested.state.difficultyLevelStep).isEqualTo(2)
        assertThat(tested.state.difficultyLevelLabel).isNotNull()
    }

    @Test
    fun `DifficultyLevelPlusIconClicked event should not increment difficulty level when at max level`() {
        tested.updateState { copy(difficultyLevelStep = 12) }

        tested.handleUiEvent(DifficultyLevelPlusIconClicked)

        assertThat(tested.state.difficultyLevelStep).isEqualTo(12)
        assertThat(tested.state.difficultyLevelLabel).isNotNull()
    }

    @Test
    fun `DifficultyLevelStepClicked event should change difficulty level`() {
        tested.updateState { copy(difficultyLevelStep = 12) }

        tested.handleUiEvent(DifficultyLevelStepClicked(step = 3))

        assertThat(tested.state.difficultyLevelStep).isEqualTo(3)
        assertThat(tested.state.difficultyLevelLabel).isNotNull()
    }

    @Test
    fun `PlayButtonClicked event should navigate to game screen with white player`() = runTest {
        tested.handleUiEvent(PlayerColorSelected(isWhiteSelected = true))
        tested.navActions.test {
            tested.handleUiEvent(PlayButtonClicked)

            assertThat(awaitItem()).isEqualTo(NavigateTo(GameplayRoute(isPlayerWhite = true)))
        }
    }

    @Test
    fun `PlayButtonClicked event should navigate to game screen with black player`() = runTest {
        tested.handleUiEvent(PlayerColorSelected(isWhiteSelected = false))

        tested.navActions.test {
            tested.handleUiEvent(PlayButtonClicked)

            assertThat(awaitItem()).isEqualTo(NavigateTo(GameplayRoute(isPlayerWhite = false)))
        }
    }

    @Test
    fun `SaveGameState event should save game options`() = runTest {
        tested.handleUiEvent(MoveSuggestionsSwitchToggled)
        tested.handleUiEvent(PlayerColorSelected(isWhiteSelected = false))
        tested.handleUiEvent(DifficultyLevelPlusIconClicked)

        tested.handleUiEvent(SaveGameState)

        coVerify {
            saveGameOptionsUseCase(
                GameOptions(
                    isMoveSuggestionsOn = false,
                    isPlayerWhite = false,
                    difficultyLevel = DifficultyLevel(2)
                )
            )
        }
    }
}
