package com.mudita.chess.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.navOptions
import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square.A1
import com.github.bhlangonijr.chesslib.Square.A2
import com.github.bhlangonijr.chesslib.Square.A3
import com.github.bhlangonijr.chesslib.Square.A4
import com.github.bhlangonijr.chesslib.Square.A6
import com.github.bhlangonijr.chesslib.Square.A7
import com.github.bhlangonijr.chesslib.Square.A8
import com.github.bhlangonijr.chesslib.Square.B1
import com.github.bhlangonijr.chesslib.Square.B8
import com.github.bhlangonijr.chesslib.Square.C1
import com.github.bhlangonijr.chesslib.Square.C3
import com.github.bhlangonijr.chesslib.Square.C6
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.D2
import com.github.bhlangonijr.chesslib.Square.D4
import com.github.bhlangonijr.chesslib.Square.D5
import com.github.bhlangonijr.chesslib.Square.D7
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.Square.E5
import com.github.bhlangonijr.chesslib.Square.E7
import com.github.bhlangonijr.chesslib.Square.E8
import com.github.bhlangonijr.chesslib.Square.F3
import com.github.bhlangonijr.chesslib.Square.F6
import com.github.bhlangonijr.chesslib.Square.F8
import com.github.bhlangonijr.chesslib.Square.G1
import com.github.bhlangonijr.chesslib.Square.G5
import com.github.bhlangonijr.chesslib.Square.G6
import com.github.bhlangonijr.chesslib.Square.G7
import com.github.bhlangonijr.chesslib.Square.G8
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.coroutines.Dispatchers
import com.mudita.chess.coroutines.MainDispatcherExtension
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.GetGameOptionsUseCase
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.gameplay.GameplayUiEvent.AppMinimized
import com.mudita.chess.gameplay.GameplayUiEvent.BackButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmMoveButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmPawnPromotionClicked
import com.mudita.chess.gameplay.GameplayUiEvent.DialogDismissRequested
import com.mudita.chess.gameplay.GameplayUiEvent.EndgameMainMenuButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.EndgameNewGameButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.EndgameUndoButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ExitButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.GameMovesButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.gameplay.GameplayUiEvent.NewGameButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.PauseButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ResumeButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ScreenStarted
import com.mudita.chess.gameplay.GameplayUiEvent.SquareClicked
import com.mudita.chess.gameplay.GameplayUiEvent.UndoMoveButtonClicked
import com.mudita.chess.gameplay.GameplayViewModel.Companion.KEY_GAME_STARTED_BEFORE
import com.mudita.chess.gameplay.fixtures.BLACK_PARTICIPANT_WON_FEN
import com.mudita.chess.gameplay.fixtures.BoardUiData.BLACK_PLAYER_EMPTY_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.BLACK_PLAYER_INITIAL_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.WHITE_PLAYER_EMPTY_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.WHITE_PLAYER_INITIAL_BOARD_UI
import com.mudita.chess.gameplay.fixtures.FIFTY_FIFTY_RULE_DRAW_FEN
import com.mudita.chess.gameplay.fixtures.INSUFFICIENT_MATERIAL_DRAW_FEN
import com.mudita.chess.gameplay.fixtures.STALEMATE_FEN
import com.mudita.chess.gameplay.fixtures.TestGame
import com.mudita.chess.gameplay.fixtures.WHITE_PARTICIPANT_WON_FEN
import com.mudita.chess.gameplay.fixtures.get
import com.mudita.chess.gameplay.fixtures.neverReturningCoroutine
import com.mudita.chess.gameplay.fixtures.replace
import com.mudita.chess.gameplay.model.GameplayDialogType.GAME_MENU
import com.mudita.chess.gameplay.model.EndgameUi
import com.mudita.chess.gameplay.model.GameplayDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.GameMenuDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.LoadingDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.PawnPromotionDialogUi
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.gameplay.model.SquareUi
import com.mudita.chess.games.usecase.GetCurrentGameMovesUseCase
import com.mudita.chess.games.usecase.RemoveCurrentGameUseCase
import com.mudita.chess.games.usecase.SaveCurrentGameMovesUseCase
import com.mudita.chess.gamestatistics.model.StatisticsType
import com.mudita.chess.gamestatistics.model.StatisticsType.DRAW
import com.mudita.chess.gamestatistics.model.StatisticsType.LOST
import com.mudita.chess.gamestatistics.model.StatisticsType.WON
import com.mudita.chess.gamestatistics.usecase.AddToGameStatisticsUseCase
import com.mudita.chess.navigation.NavAction
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.routes.GameMovesRoute
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.MainRoute
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.navigation.routes.RootRoute
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.chess.ui.model.PositionUi as Ui

@ExtendWith(MainDispatcherExtension::class)
internal class GameplayViewModelTest {

    private val gameOptions = GameOptions(
        isMoveSuggestionsOn = true,
        isPlayerWhite = true,
        difficultyLevel = DifficultyLevel(1)
    )

    private val args: GameplayRouteProvider = mockk()
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val mapper: GameplayMapper = spyk(GameplayMapper())
    private val uiEvents: GameplayUiEvents = spyk(GameplayUiEvents())

    private val getGameOptionsUseCase: GetGameOptionsUseCase = mockk {
        coEvery { this@mockk.invoke() } returns Result.success(gameOptions)
    }

    private val saveGameOptionsUseCase: SaveGameOptionsUseCase = mockk(relaxed = true)

    private val getCurrentGameMovesUseCase: GetCurrentGameMovesUseCase = mockk {
        coEvery { this@mockk.invoke() } returns Result.success(emptyList())
    }

    private val saveCurrentGameMovesUseCase: SaveCurrentGameMovesUseCase = mockk(relaxed = true)

    private val removeCurrentGameUseCase: RemoveCurrentGameUseCase = mockk(relaxed = true)

    private val addToGameStatisticsUseCase: AddToGameStatisticsUseCase = mockk(relaxed = true)

    private val dispatchers: Dispatchers by lazy {
        mockk {
            every { io() } returns UnconfinedTestDispatcher()
        }
    }

    private val testGame = TestGame(mapper)

    private val tested by lazy {
        GameplayViewModel(
            args = args,
            savedStateHandle = savedStateHandle,
            mapper = mapper,
            uiEvents = uiEvents,
            getGameOptionsUseCase = getGameOptionsUseCase,
            saveGameOptionsUseCase = saveGameOptionsUseCase,
            getCurrentGameMovesUseCase = getCurrentGameMovesUseCase,
            saveCurrentGameMovesUseCase = saveCurrentGameMovesUseCase,
            removeCurrentGameUseCase = removeCurrentGameUseCase,
            addToGameStatisticsUseCase = addToGameStatisticsUseCase,
            dispatchers = dispatchers,
            gameFactory = testGame.playerVsComputerFactory()
        )
    }

    private val testedTwoPlayerLocal by lazy {
        GameplayViewModel(
            args = args,
            savedStateHandle = savedStateHandle,
            mapper = mapper,
            uiEvents = uiEvents,
            getGameOptionsUseCase = getGameOptionsUseCase,
            saveGameOptionsUseCase = saveGameOptionsUseCase,
            getCurrentGameMovesUseCase = getCurrentGameMovesUseCase,
            saveCurrentGameMovesUseCase = saveCurrentGameMovesUseCase,
            removeCurrentGameUseCase = removeCurrentGameUseCase,
            addToGameStatisticsUseCase = addToGameStatisticsUseCase,
            dispatchers = dispatchers,
            gameFactory = testGame.twoPlayerLocalFactory()
        )
    }

    @Test
    fun `on init show starting board when player is white`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_INITIAL_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = true
                ),
                isConfirmMoveButtonVisible = false
            )
        )
        coVerify(exactly = 1) {
            saveCurrentGameMovesUseCase(emptyList())
        }
    }

    @Test
    fun `on init show starting board when player is black`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false)

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_INITIAL_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = true
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false
            )
        )
        coVerify(exactly = 1) {
            saveCurrentGameMovesUseCase(emptyList())
        }
    }

    @Test
    fun `on init show empty board screen when starting saved game for black player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { neverReturningCoroutine() }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_EMPTY_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = LoadingDialogUi
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show empty board screen when starting saved game for white player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { neverReturningCoroutine() }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_EMPTY_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = LoadingDialogUi
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show empty board screen when returning to started game for black player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { neverReturningCoroutine() }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_EMPTY_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = LoadingDialogUi
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show empty board screen when returning to started game for white player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isNewGame = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { neverReturningCoroutine() }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_EMPTY_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = LoadingDialogUi
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to started game for white player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isNewGame = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.success(listOf("e2e4")) }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true))),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = true
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to started game for black player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.success(listOf("e2e4", "e7e5")) }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                    .replace(SquareUi(position = Ui.E7, isWhite = false, piece = null))
                    .replace(SquareUi(position = Ui.E5, isWhite = false, piece = PieceUi(PAWN, isWhite = false))),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = true
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = false
                ),
                isGameMovesButtonVisible = true,
                isUndoMoveButtonVisible = true,
                isConfirmMoveButtonVisible = false,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to saved game for white player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.success(listOf("e2e4")) }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true))),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = true
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = false
                ),
                isConfirmMoveButtonVisible = false,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to saved game for black player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.success(listOf("e2e4", "e7e5")) }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                    .replace(SquareUi(position = Ui.E7, isWhite = false, piece = null))
                    .replace(SquareUi(position = Ui.E5, isWhite = false, piece = PieceUi(PAWN, isWhite = false))),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = true
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = false
                ),
                isGameMovesButtonVisible = true,
                isConfirmMoveButtonVisible = false,
                isUndoMoveButtonVisible = true,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to saved game with check for white player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers {
            Result.success(listOf("d2d4", "e7e5", "d4e5", "d8g5", "e5e6", "g5d2"))
        }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.E1, isWhite = false, piece = PieceUi(KING, isWhite = true), isHighlighted = true))
                    .replace(
                        SquareUi(
                            position = Ui.D2,
                            isWhite = false,
                            piece = PieceUi(QUEEN, isWhite = false),
                            isHighlighted = true
                        )
                    )
                    .replace(SquareUi(position = Ui.E6, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                    .replace(SquareUi(position = Ui.E7, isWhite = false, piece = null))
                    .replace(SquareUi(position = Ui.D8, isWhite = false, piece = null)),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = false,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = true,
                    isSelected = true
                ),
                isGameMovesButtonVisible = true,
                isConfirmMoveButtonVisible = false,
                isUndoMoveButtonVisible = true,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init show loaded board when returning to saved game with check for black player`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = false)
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.success(listOf("e2e4", "d7d5", "d1g4", "c7c6", "g4d7")) }

        assertThat(tested.state).isEqualTo(
            GameplayUiState(
                board = BLACK_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.D1, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                    .replace(SquareUi(position = Ui.D5, isWhite = true, piece = PieceUi(PAWN, isWhite = false)))
                    .replace(SquareUi(position = Ui.C6, isWhite = true, piece = PieceUi(PAWN, isWhite = false)))
                    .replace(SquareUi(position = Ui.D7, isWhite = true, piece = PieceUi(QUEEN, isWhite = true), isHighlighted = true))
                    .replace(SquareUi(position = Ui.C7, isWhite = false, piece = null))
                    .replace(SquareUi(position = Ui.E8, isWhite = true, piece = PieceUi(KING, isWhite = false), isHighlighted = true)),
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_computer,
                    isWhite = true,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_you,
                    isWhite = false,
                    isSelected = true
                ),
                isGameMovesButtonVisible = true,
                isConfirmMoveButtonVisible = false,
                isUndoMoveButtonVisible = true,
                dialog = GameMenuDialogUi(isMoveSuggestionsOn = true)
            )
        )
        coVerify(exactly = 0) {
            saveCurrentGameMovesUseCase(any())
        }
    }

    @Test
    fun `on init navigates to main when unable to load game`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isNewGame = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        coEvery { getCurrentGameMovesUseCase.invoke() } coAnswers { Result.failure(Error("game read error")) }

        tested.navActions.test {
            verifyNavigatedToMain(awaitItem())
        }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `on init shows game menu when return to ongoing game which was started before`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
    }

    @Test
    fun `on init shows game menu with two player mode when returning to an ongoing two player game`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isTwoPlayerMode = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true

        assertThat(testedTwoPlayerLocal.state.dialog).isEqualTo(
            GameMenuDialogUi(isMoveSuggestionsOn = true, isTwoPlayerMode = true)
        )
    }

    @ParameterizedTest
    @MethodSource("provideInitEndgameParameters")
    fun `on init shows endgame when return to completed game which was started before`(
        fen: String,
        endgame: EndgameUi
    ) = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        every { savedStateHandle.get<Boolean>(KEY_GAME_STARTED_BEFORE) } returns true
        testGame.startingFen = fen

        assertThat(tested.state.endgame).isEqualTo(endgame)
    }

    @Test
    fun `on init show starting board in two player mode with white at the bottom`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false, isTwoPlayerMode = true)

        assertThat(testedTwoPlayerLocal.state).isEqualTo(
            GameplayUiState(
                board = WHITE_PLAYER_INITIAL_BOARD_UI,
                topParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_black,
                    isWhite = false,
                    isSelected = false
                ),
                bottomParticipant = ParticipantUi(
                    nameResId = RFrontitude.string.common_label_white,
                    isWhite = true,
                    isSelected = true
                ),
                isConfirmMoveButtonVisible = false
            )
        )
    }

    @Test
    fun `white player makes first move in two player mode then black player can move`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isTwoPlayerMode = true)

        testedTwoPlayerLocal.states.test {
            skipItems(1)

            testedTwoPlayerLocal.handleUiEvent(SquareClicked(Ui.E2))
            awaitItem()
            testedTwoPlayerLocal.handleUiEvent(SquareClicked(Ui.E4))
            awaitItem()
            testedTwoPlayerLocal.handleUiEvent(ConfirmMoveButtonClicked)
            val moveConfirmedState = awaitItem()

            assertThat(moveConfirmedState.topParticipant.isSelected).isTrue()
            assertThat(moveConfirmedState.bottomParticipant.isSelected).isFalse()

            testedTwoPlayerLocal.handleUiEvent(SquareClicked(Ui.E7))
            awaitItem()
            testedTwoPlayerLocal.handleUiEvent(SquareClicked(Ui.E5))
            awaitItem()
            testedTwoPlayerLocal.handleUiEvent(ConfirmMoveButtonClicked)
            val blackMoveConfirmedState = awaitItem()

            assertThat(blackMoveConfirmedState.topParticipant.isSelected).isFalse()
            assertThat(blackMoveConfirmedState.bottomParticipant.isSelected).isTrue()
        }
    }

    @Test
    fun `EndgameNewGameButtonClicked does not add two player game result to statistics`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true, isTwoPlayerMode = true)
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = true,
            difficultyLevel = DifficultyLevel(1),
            isTwoPlayerMode = true
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        testGame.startingFen = WHITE_PARTICIPANT_WON_FEN

        testedTwoPlayerLocal.states.test {
            skipItems(1)
        }

        testedTwoPlayerLocal.handleUiEvent(EndgameNewGameButtonClicked)

        testedTwoPlayerLocal.navActions.test {
            verifyNavigatedToOptionsMenu(awaitItem(), gameOptions)
        }
        coVerify(exactly = 0) { addToGameStatisticsUseCase(type = any(), isWhitePlayer = any()) }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `computer makes first move`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false)
        coEvery {
            testGame.computerMoveUseCase.invoke(
                moves = emptyList(),
                sideToMove = WHITE,
                difficultyLevel = DifficultyLevel(1)
            )
        } returns Move(E2, E4)

        tested.states.test {
            clearMocks(saveCurrentGameMovesUseCase, answers = false)

            val pawnUi = PieceUi(PAWN, isWhite = true)

            val pieceSelectedState = awaitItem()
            val pieceSelectedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = pawnUi, isHighlighted = true))
            assertThat(pieceSelectedState.board).isEqualTo(pieceSelectedBoard)
            assertThat(pieceSelectedState.topParticipant.isSelected).isTrue()
            assertThat(pieceSelectedState.bottomParticipant.isSelected).isFalse()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            val movedState = awaitItem()
            val movedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi, isHighlighted = true))
            assertThat(movedState.board).isEqualTo(movedBoard)
            assertThat(movedState.topParticipant.isSelected).isTrue()
            assertThat(movedState.bottomParticipant.isSelected).isFalse()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            val moveConfirmedState = awaitItem()
            val moveConfirmedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi))
            assertThat(moveConfirmedState.board).isEqualTo(moveConfirmedBoard)
            assertThat(moveConfirmedState.topParticipant.isSelected).isFalse()
            assertThat(moveConfirmedState.bottomParticipant.isSelected).isTrue()
            assertThat(moveConfirmedState.isGameMovesButtonVisible).isFalse()
            assertThat(moveConfirmedState.isUndoMoveButtonVisible).isTrue()
            coVerify(exactly = 1) {
                saveCurrentGameMovesUseCase(listOf("e2e4"))
            }
        }
    }

    @Test
    fun `player makes first move with move suggestions`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = true))

        tested.states.test {
            clearMocks(saveCurrentGameMovesUseCase, answers = false)

            skipItems(1)
            val pawnUi = PieceUi(PAWN, isWhite = true)

            tested.handleUiEvent(SquareClicked(Ui.E2))
            val pieceSelectedState = awaitItem()
            val pieceSelectedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = pawnUi, isHighlighted = true))
                .replace(SquareUi(position = Ui.E3, isWhite = false, isHighlighted = true))
                .replace(SquareUi(position = Ui.E4, isWhite = true, isHighlighted = true))
            assertThat(pieceSelectedState.board).isEqualTo(pieceSelectedBoard)
            assertThat(pieceSelectedState.topParticipant.isSelected).isFalse()
            assertThat(pieceSelectedState.bottomParticipant.isSelected).isTrue()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            tested.handleUiEvent(SquareClicked(Ui.E4))
            val movedState = awaitItem()
            val movedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi, isHighlighted = true))
            assertThat(movedState.board).isEqualTo(movedBoard)
            assertThat(movedState.topParticipant.isSelected).isFalse()
            assertThat(movedState.bottomParticipant.isSelected).isTrue()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            tested.handleUiEvent(ConfirmMoveButtonClicked)
            val moveConfirmedState = awaitItem()
            val moveConfirmedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi))
            assertThat(moveConfirmedState.board).isEqualTo(moveConfirmedBoard)
            assertThat(moveConfirmedState.topParticipant.isSelected).isTrue()
            assertThat(moveConfirmedState.bottomParticipant.isSelected).isFalse()
            assertThat(moveConfirmedState.isGameMovesButtonVisible).isFalse()
            assertThat(moveConfirmedState.isUndoMoveButtonVisible).isFalse()
            coVerify(exactly = 1) {
                saveCurrentGameMovesUseCase(listOf("e2e4"))
            }
        }

    }

    @Test
    fun `player makes first move without move suggestions`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = false))

        tested.states.test {
            clearMocks(saveCurrentGameMovesUseCase, answers = false)

            skipItems(1)
            val pawnUi = PieceUi(PAWN, isWhite = true)

            tested.handleUiEvent(SquareClicked(Ui.E2))
            val pieceSelectedState = awaitItem()
            val pieceSelectedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = pawnUi, isHighlighted = true))
            assertThat(pieceSelectedState.board).isEqualTo(pieceSelectedBoard)
            assertThat(pieceSelectedState.topParticipant.isSelected).isFalse()
            assertThat(pieceSelectedState.bottomParticipant.isSelected).isTrue()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            tested.handleUiEvent(SquareClicked(Ui.E4))
            val movedState = awaitItem()
            val movedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi, isHighlighted = true))
            assertThat(movedState.board).isEqualTo(movedBoard)
            assertThat(movedState.topParticipant.isSelected).isFalse()
            assertThat(movedState.bottomParticipant.isSelected).isTrue()
            coVerify(exactly = 0) {
                saveCurrentGameMovesUseCase(any())
            }

            tested.handleUiEvent(ConfirmMoveButtonClicked)
            val moveConfirmedState = awaitItem()
            val moveConfirmedBoard = WHITE_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = pawnUi))
            assertThat(moveConfirmedState.board).isEqualTo(moveConfirmedBoard)
            assertThat(moveConfirmedState.topParticipant.isSelected).isTrue()
            assertThat(moveConfirmedState.bottomParticipant.isSelected).isFalse()
            assertThat(moveConfirmedState.isGameMovesButtonVisible).isFalse()
            assertThat(moveConfirmedState.isUndoMoveButtonVisible).isFalse()
            coVerify(exactly = 1) {
                saveCurrentGameMovesUseCase(listOf("e2e4"))
            }
        }
    }

    @Test
    fun `when players makes first round moves then game moves button becomes visible`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        testGame.playedMoves = listOf(Move(G1, F3), Move(D7, D5))

        assertThat(tested.state.isGameMovesButtonVisible).isTrue()
    }

    @Test
    fun `when players makes first round moves then undo move button becomes visible`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        testGame.playedMoves = listOf(Move(G1, F3), Move(D7, D5))

        assertThat(tested.state.isUndoMoveButtonVisible).isTrue()
    }

    @Test
    fun `when computer makes first move then undo move button becomes visible`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false)
        testGame.playedMoves = listOf(Move(G1, F3))

        assertThat(tested.state.isUndoMoveButtonVisible).isTrue()
    }

    @Test
    fun `player makes pawn to promotion`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        testGame.startingFen = "rnbqkb1r/pppp2Pp/4p2n/8/8/8/PPPPPP1P/RNBQKBNR w KQkq - 0 5"
        val pawnUi = PieceUi(PAWN, isWhite = true)
        val pawnSquare = SquareUi(position = Ui.G7, piece = pawnUi, isWhite = false)
        val promotionOptions = setOf(
            PieceUi(QUEEN, isWhite = true),
            PieceUi(ROOK, isWhite = true),
            PieceUi(BISHOP, isWhite = true),
            PieceUi(KNIGHT, isWhite = true)
        )

        tested.states.test {
            skipItems(1)

            uiEvents.handleUiEvent(SquareClicked(Ui.G7))
            val pieceSelectedState = awaitItem()
            assertThat(pieceSelectedState.board[Ui.G7]).isEqualTo(pawnSquare.copy(isHighlighted = true))

            uiEvents.handleUiEvent(SquareClicked(Ui.H8))
            val promotionConfirmationState = awaitItem()
            assertThat(promotionConfirmationState.board[Ui.G7]).isEqualTo(pawnSquare.copy(isHighlighted = true))
            assertThat(promotionConfirmationState.dialog).isEqualTo(PawnPromotionDialogUi(promotionOptions))

            uiEvents.handleUiEvent(ConfirmPawnPromotionClicked(PieceUi(QUEEN, isWhite = true)))
            val promotionConfirmedState = awaitItem()
            assertThat(promotionConfirmedState.board[Ui.G7]).isEqualTo(pawnSquare.copy(piece = null))
            assertThat(promotionConfirmedState.board[Ui.H8]).isEqualTo(
                SquareUi(
                    position = Ui.H8,
                    piece = PieceUi(QUEEN, isWhite = true),
                    isWhite = false,
                    isHighlighted = true
                )
            )
            assertThat(promotionConfirmedState.dialog).isNull()
        }
    }

    @Test
    fun `BackButtonClicked opens game menu with suggestions on`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = true))

        tested.handleUiEvent(BackButtonClicked)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
    }

    @Test
    fun `BackButtonClicked opens game menu with suggestions off`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = false))

        tested.handleUiEvent(BackButtonClicked)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = false))
    }

    @Test
    fun `PauseButtonClicked opens game menu with suggestions on`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = true))

        tested.handleUiEvent(PauseButtonClicked)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
    }

    @Test
    fun `PauseButtonClicked opens game menu with suggestions off`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = false))

        tested.handleUiEvent(PauseButtonClicked)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = false))
    }

    @Test
    fun `ScreenStarted doesn't open any dialog`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = false))

        tested.handleUiEvent(ScreenStarted)

        assertThat(tested.state.dialog).isEqualTo(null)
    }

    @Test
    fun `AppMinimized opens game menu with suggestions on`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = true))

        tested.handleUiEvent(AppMinimized)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
    }

    @Test
    fun `AppMinimized opens game menu with suggestions off`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions.copy(isMoveSuggestionsOn = false))

        tested.handleUiEvent(AppMinimized)

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = false))
    }

    @Test
    fun `ResumeButtonClicked closes game menu`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        tested.handleUiEvent(PauseButtonClicked)

        tested.handleUiEvent(ResumeButtonClicked)

        assertThat(tested.state.dialog).isNull()
    }

    @Test
    fun `DialogDismissClicked of GAME_MENU closes game menu`() {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        tested.handleUiEvent(PauseButtonClicked)

        tested.handleUiEvent(DialogDismissRequested(GAME_MENU))

        assertThat(tested.state.dialog).isNull()
    }

    @Test
    fun `NewGameButtonClicked navigates to options menu`() = runTest {
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = false,
            isPlayerWhite = true,
            difficultyLevel = DifficultyLevel(8)
        )
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)

        tested.handleUiEvent(NewGameButtonClicked)

        tested.navActions.test {
            verifyNavigatedToOptionsMenu(awaitItem(), gameOptions)
        }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `ExitButtonClicked navigates to main`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        tested.handleUiEvent(ExitButtonClicked)

        tested.navActions.test {
            verifyNavigatedToMain(awaitItem())
        }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `MoveSuggestionsSwitchToggled updates game menu with move suggestions on and saves game options`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        tested.handleUiEvent(PauseButtonClicked)

        tested.handleUiEvent(MoveSuggestionsSwitchToggled(on = true))

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
        coVerify { saveGameOptionsUseCase(gameOptions.copy(isMoveSuggestionsOn = true)) }
    }

    @Test
    fun `MoveSuggestionsSwitchToggled updates game menu with move suggestions off and saves game options`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        tested.handleUiEvent(PauseButtonClicked)

        tested.handleUiEvent(MoveSuggestionsSwitchToggled(on = false))

        assertThat(tested.state.dialog).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = false))
        coVerify { saveGameOptionsUseCase(gameOptions.copy(isMoveSuggestionsOn = false)) }
    }

    @Test
    fun `GameMovesButtonClicked navigates to game moves`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        testGame.playedMoves = listOf(Move(G1, F3), Move(D7, D5))

        tested.handleUiEvent(GameMovesButtonClicked)

        tested.navActions.test {
            assertThat(awaitItem()).isEqualTo(
                NavigateTo(
                    GameMovesRoute(
                        listOf(
                            MoveArg("N", "g1f3"),
                            MoveArg("p", "d7d5")
                        )
                    )
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideEndgameParameters")
    fun `EndgameNewGameButtonClicked adds game result to statistics and navigates to game options`(
        fen: String,
        isPlayerWhite: Boolean,
        type: StatisticsType,
        endgame: EndgameUi
    ) = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = isPlayerWhite)
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = isPlayerWhite,
            difficultyLevel = DifficultyLevel(1)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        testGame.startingFen = fen

        tested.states.test {
            assertThat(awaitItem().endgame).isEqualTo(endgame)
        }

        tested.handleUiEvent(EndgameNewGameButtonClicked)

        tested.navActions.test {
            verifyNavigatedToOptionsMenu(awaitItem(), gameOptions)
        }
        coVerify { addToGameStatisticsUseCase(type = type, isWhitePlayer = isPlayerWhite) }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `EndgameUndoButtonClicked takes the game back out of its endgame`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        // Moves actually played, rather than a position loaded from a FEN: undo needs history
        // behind it, and this repetition reaches a draw with twelve moves to take back.
        testGame.playedMoves = listOf(
            Move(A2, A4), Move(A7, A6),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7)
        )

        assertThat(tested.state.endgame)
            .isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw))

        tested.handleUiEvent(EndgameUndoButtonClicked)

        // The strip goes because the game is genuinely running again, not because the UI was
        // dismissed - and a game you undid is not a game you lost, so nothing is recorded.
        assertThat(tested.state.endgame).isNull()
        coVerify(exactly = 0) { addToGameStatisticsUseCase(any(), any()) }
        coVerify(exactly = 0) { removeCurrentGameUseCase() }
    }

    @ParameterizedTest
    @MethodSource("provideEndgameParameters")
    fun `EndgameMainMenuButtonClicked adds game results to statistics and navigates to main`(
        fen: String,
        isPlayerWhite: Boolean,
        type: StatisticsType,
        endgame: EndgameUi
    ) = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = isPlayerWhite)
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = isPlayerWhite,
            difficultyLevel = DifficultyLevel(1)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        testGame.startingFen = fen

        tested.states.test {
            assertThat(awaitItem().endgame).isEqualTo(endgame)
        }

        tested.handleUiEvent(EndgameMainMenuButtonClicked)

        tested.navActions.test {
            verifyNavigatedToMain(awaitItem())
        }
        coVerify { addToGameStatisticsUseCase(type = type, isWhitePlayer = isPlayerWhite) }
        coVerify { removeCurrentGameUseCase() }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `EndgameNewGameButtonClicked adds draw (repetition rule) to statistics and navigates to game options`(
        isPlayerWhite: Boolean
    ) = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = isPlayerWhite)
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = isPlayerWhite,
            difficultyLevel = DifficultyLevel(1)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        testGame.playedMoves = listOf(
            Move(A2, A4), Move(A7, A6),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7)
        )

        assertThat(tested.state.endgame).isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw))

        tested.handleUiEvent(EndgameNewGameButtonClicked)

        tested.navActions.test {
            verifyNavigatedToOptionsMenu(awaitItem(), gameOptions)
        }
        coVerify { addToGameStatisticsUseCase(type = DRAW, isWhitePlayer = isPlayerWhite) }
        coVerify { removeCurrentGameUseCase() }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `EndgameMainMenuButtonClicked adds draw (repetition rule) to statistics and navigates to main`(
        isPlayerWhite: Boolean
    ) = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = isPlayerWhite)
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = true,
            isPlayerWhite = isPlayerWhite,
            difficultyLevel = DifficultyLevel(1)
        )
        coEvery { getGameOptionsUseCase() } returns Result.success(gameOptions)
        testGame.playedMoves = listOf(
            Move(A2, A4), Move(A7, A6),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7),
            Move(A2, A1), Move(A7, A8),
            Move(A1, A2), Move(A8, A7)
        )

        assertThat(tested.state.endgame).isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw))

        tested.handleUiEvent(EndgameMainMenuButtonClicked)

        tested.navActions.test {
            verifyNavigatedToMain(awaitItem())
        }
        coVerify { addToGameStatisticsUseCase(type = DRAW, isWhitePlayer = isPlayerWhite) }
        coVerify { removeCurrentGameUseCase() }
    }

    @Test
    fun `UndoMoveButtonClicked undoes round after 1 round played when white player started`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = true)
        testGame.playedMoves = listOf(Move(E2, E4), Move(E7, E5))

        tested.states.test {
            skipItems(1)

            uiEvents.handleUiEvent(UndoMoveButtonClicked)
            val moveUndoneState = awaitItem()
            assertThat(moveUndoneState.board).isEqualTo(WHITE_PLAYER_INITIAL_BOARD_UI)
        }
    }

    @Test
    fun `UndoMoveButtonClicked undoes computer move then computer makes move again after 0,5 round played when white computer started`() =
        runTest {
            every { args.value } returns GameplayRoute(isPlayerWhite = false)
            coEvery {
                testGame.computerMoveUseCase(
                    moves = emptyList(),
                    sideToMove = WHITE,
                    difficultyLevel = DifficultyLevel(1)
                )
            } returns Move(A2, A3)
            testGame.playedMoves = listOf(Move(E2, E4))

            tested.states.test {
                skipItems(1)

                uiEvents.handleUiEvent(UndoMoveButtonClicked)
                val moveUndoneState = awaitItem()
                val moveUndoneBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                assertThat(moveUndoneState.board).isEqualTo(moveUndoneBoard)

                skipItems(1)

                val computerMovingState = awaitItem()
                val computerMovingBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.A2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.A3, isWhite = false, piece = PieceUi(PAWN, isWhite = true), isHighlighted = true))
                assertThat(computerMovingState.board).isEqualTo(computerMovingBoard)

                val computerMovedState = awaitItem()
                val computerMovedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                    .replace(SquareUi(position = Ui.A2, isWhite = true, piece = null))
                    .replace(SquareUi(position = Ui.A3, isWhite = false, piece = PieceUi(PAWN, isWhite = true)))
                assertThat(computerMovedState.board).isEqualTo(computerMovedBoard)
            }
        }

    @Test
    fun `UndoMoveButtonClicked undoes round after 1,5 round played when white computer started`() = runTest {
        every { args.value } returns GameplayRoute(isPlayerWhite = false)
        testGame.playedMoves = listOf(Move(E2, E4), Move(E7, E5), Move(A2, A3))

        tested.states.test {
            skipItems(1)

            uiEvents.handleUiEvent(UndoMoveButtonClicked)
            val moveUndoneState = awaitItem()
            val expectedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
            assertThat(moveUndoneState.board).isEqualTo(expectedBoard)
        }
    }

    @Test
    fun `UndoMoveButtonClicked undoes 1 round after being checked by computer`() = runTest {
        val playedMoves = listOf(
            Move(E2, E4), Move(E7, E5),
            Move(G1, F3), Move(B8, C6),
            Move(B1, C3), Move(G7, G6),
            Move(D2, D4), Move(E5, D4),
            Move(C3, D5), Move(F8, G7),
            Move(C1, G5), Move(G8, E7),
            Move(F3, D4), Move(G7, D4),
            Move(D1, D4), Move(C6, D4),
        )
        every { args.value } returns GameplayRoute(isPlayerWhite = false)
        testGame.playedMoves = playedMoves
        coEvery {
            testGame.computerMoveUseCase(
                moves = playedMoves,
                sideToMove = WHITE,
                difficultyLevel = DifficultyLevel(1)
            )
        } returns Move(D5, F6)

        tested.states.test {
            skipItems(2)
            advanceUntilIdle()

            val checkedState = awaitItem()
            assertThat(checkedState.board[Ui.F6].isHighlighted).isTrue()
            assertThat(checkedState.board[Ui.E8].isHighlighted).isTrue()
            advanceUntilIdle()

            uiEvents.handleUiEvent(UndoMoveButtonClicked)

            skipItems(1)

            val moveUndoneState = awaitItem()
            val moveUndoneBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.G1, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.D1, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.C1, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.B1, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.D2, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                .replace(SquareUi(position = Ui.D4, isWhite = false, piece = PieceUi(QUEEN, isWhite = true)))
                .replace(SquareUi(position = Ui.G5, isWhite = false, piece = PieceUi(BISHOP, isWhite = true)))
                .replace(SquareUi(position = Ui.D5, isWhite = true, piece = PieceUi(KNIGHT, isWhite = true)))
                .replace(SquareUi(position = Ui.G6, isWhite = true, piece = PieceUi(PAWN, isWhite = false)))
                .replace(SquareUi(position = Ui.C6, isWhite = true, piece = PieceUi(KNIGHT, isWhite = false)))
                .replace(SquareUi(position = Ui.G7, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.E7, isWhite = false, piece = PieceUi(KNIGHT, isWhite = false)))
                .replace(SquareUi(position = Ui.G8, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.F8, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.B8, isWhite = false, piece = null))
            assertThat(moveUndoneState.board[Ui.F6].isHighlighted).isFalse()
            assertThat(moveUndoneState.board[Ui.E8].isHighlighted).isFalse()
            assertThat(moveUndoneState.board).isEqualTo(moveUndoneBoard)
        }
    }

    @Test
    fun `UndoMoveButtonClicked undoes 1 round to check by computer`() = runTest {
        val playedMoves = listOf(
            Move(E2, E4), Move(E7, E5),
            Move(G1, F3), Move(B8, C6),
            Move(B1, C3), Move(G7, G6),
            Move(D2, D4), Move(E5, D4),
            Move(C3, D5), Move(F8, G7),
            Move(C1, G5), Move(G8, E7),
            Move(F3, D4), Move(G7, D4),
            Move(D1, D4), Move(C6, D4),
            Move(D5, F6), Move(E8, F8),
            Move(F6, D5)
        )
        every { args.value } returns GameplayRoute(isPlayerWhite = false)
        testGame.playedMoves = playedMoves

        tested.states.test {
            skipItems(1)

            uiEvents.handleUiEvent(UndoMoveButtonClicked)

            val moveUndoneToCheckedState = awaitItem()
            val moveUndoneToCheckedBoard = BLACK_PLAYER_INITIAL_BOARD_UI
                .replace(SquareUi(position = Ui.G1, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.D1, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.C1, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.B1, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.E2, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.D2, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.E4, isWhite = true, piece = PieceUi(PAWN, isWhite = true)))
                .replace(SquareUi(position = Ui.D4, isWhite = false, piece = PieceUi(KNIGHT, isWhite = false)))
                .replace(SquareUi(position = Ui.G5, isWhite = false, piece = PieceUi(BISHOP, isWhite = true)))
                .replace(SquareUi(position = Ui.G6, isWhite = true, piece = PieceUi(PAWN, isWhite = false)))
                .replace(SquareUi(position = Ui.F6, isWhite = false, piece = PieceUi(KNIGHT, isWhite = true), isHighlighted = true))
                .replace(SquareUi(position = Ui.G7, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.E7, isWhite = false, piece = PieceUi(KNIGHT, isWhite = false)))
                .replace(SquareUi(position = Ui.G8, isWhite = true, piece = null))
                .replace(SquareUi(position = Ui.F8, isWhite = false, piece = null))
                .replace(SquareUi(position = Ui.E8, isWhite = true, piece = PieceUi(KING, isWhite = false), isHighlighted = true))
                .replace(SquareUi(position = Ui.B8, isWhite = false, piece = null))
            assertThat(moveUndoneToCheckedState.board).isEqualTo(moveUndoneToCheckedBoard)
        }
    }

    private fun verifyNavigatedToMain(action: NavAction) {
        assertThat(action).isEqualTo(NavigateTo(
            route = MainRoute,
            options = navOptions { popUpTo<RootRoute>() }
        ))
    }

    private fun verifyNavigatedToOptionsMenu(action: NavAction, gameOptions: GameOptions) {
        assertThat(action).isEqualTo(NavigateTo(
            route = OptionsMenuRoute(
                isMoveSuggestionsOn = gameOptions.isMoveSuggestionsOn,
                isPlayerWhite = gameOptions.isPlayerWhite,
                difficultyLevel = gameOptions.difficultyLevel.value,
                isTwoPlayerMode = gameOptions.isTwoPlayerMode
            ),
            options = navOptions { popUpTo<MainRoute>() }
        ))
    }

    companion object {
        @JvmStatic
        fun provideInitEndgameParameters(): Stream<Arguments> =
            Stream.of(
                Arguments.of(WHITE_PARTICIPANT_WON_FEN, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_whitewins)),
                Arguments.of(BLACK_PARTICIPANT_WON_FEN, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_blackwins)),
                Arguments.of(STALEMATE_FEN, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
            )

        @JvmStatic
        fun provideEndgameParameters(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    WHITE_PARTICIPANT_WON_FEN,
                    true,
                    WON,
                    EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_whitewins)
                ),
                Arguments.of(
                    WHITE_PARTICIPANT_WON_FEN,
                    false,
                    LOST,
                    EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_whitewins)
                ),
                Arguments.of(
                    BLACK_PARTICIPANT_WON_FEN,
                    true,
                    LOST,
                    EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_blackwins)
                ),
                Arguments.of(
                    BLACK_PARTICIPANT_WON_FEN,
                    false,
                    WON,
                    EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_blackwins)
                ),
                Arguments.of(STALEMATE_FEN, true, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
                Arguments.of(STALEMATE_FEN, false, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
                Arguments.of(FIFTY_FIFTY_RULE_DRAW_FEN, true, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
                Arguments.of(FIFTY_FIFTY_RULE_DRAW_FEN, false, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
                Arguments.of(INSUFFICIENT_MATERIAL_DRAW_FEN, true, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw)),
                Arguments.of(INSUFFICIENT_MATERIAL_DRAW_FEN, false, DRAW, EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw))
            )
    }
}
