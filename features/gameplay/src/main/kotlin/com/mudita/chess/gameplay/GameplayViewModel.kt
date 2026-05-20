package com.mudita.chess.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.mudita.chess.coroutines.Dispatchers
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.GetGameOptionsUseCase
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.gameplay.game.ChessBoard.Companion.COMPLETE_ROUND_MOVES_COUNT
import com.mudita.chess.gameplay.game.ChessBoardState
import com.mudita.chess.gameplay.game.Game
import com.mudita.chess.gameplay.game.GameFactory
import com.mudita.chess.gameplay.game.GameStatus
import com.mudita.chess.gameplay.model.GameplayDialogType
import com.mudita.chess.gameplay.model.GameplayDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.GameMenuDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.LoadingDialogUi
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.gameplay.model.SquareUi
import com.mudita.chess.games.usecase.GetCurrentGameMovesUseCase
import com.mudita.chess.games.usecase.RemoveCurrentGameUseCase
import com.mudita.chess.games.usecase.SaveCurrentGameMovesUseCase
import com.mudita.chess.gamestatistics.model.StatisticsType.DRAW
import com.mudita.chess.gamestatistics.model.StatisticsType.LOST
import com.mudita.chess.gamestatistics.model.StatisticsType.WON
import com.mudita.chess.gamestatistics.usecase.AddToGameStatisticsUseCase
import com.mudita.chess.mvvm.StateHandler
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.NavActionsEmitter
import com.mudita.chess.navigation.routes.GameMovesRoute
import com.mudita.chess.navigation.routes.MainRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.navigation.routes.RootRoute
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import org.jetbrains.annotations.VisibleForTesting

internal typealias BoardUi = List<List<SquareUi>>

internal data class GameplayUiState(
    val board: BoardUi,
    val computer: ParticipantUi,
    val player: ParticipantUi,
    val isConfirmMoveButtonVisible: Boolean,
    val isGameMovesButtonVisible: Boolean = false,
    val isUndoMoveButtonVisible: Boolean = false,
    val dialog: GameplayDialogUi? = null
)

internal sealed interface GameplayUiEvent {
    data object AppMinimized : GameplayUiEvent
    data object ScreenStarted : GameplayUiEvent
    data object BackButtonClicked : GameplayUiEvent
    data class DialogDismissRequested(val dialogType: GameplayDialogType) : GameplayUiEvent
    data object PauseButtonClicked : GameplayUiEvent
    data object ResumeButtonClicked : GameplayUiEvent
    data object NewGameButtonClicked : GameplayUiEvent
    data object ExitButtonClicked : GameplayUiEvent
    data object EndgameNewGameButtonClicked : GameplayUiEvent
    data object EndgameMainMenuButtonClicked : GameplayUiEvent
    data class MoveSuggestionsSwitchToggled(val on: Boolean) : GameplayUiEvent
    data class SquareClicked(val position: PositionUi) : GameplayUiEvent
    data class ConfirmPawnPromotionClicked(val piece: PieceUi) : GameplayUiEvent
    data object ConfirmMoveButtonClicked : GameplayUiEvent
    data object GameMovesButtonClicked : GameplayUiEvent
    data object UndoMoveButtonClicked : GameplayUiEvent
}

@Suppress("TooManyFunctions", "LongParameterList")
internal class GameplayViewModel(
    args: GameplayRouteProvider,
    savedStateHandle: SavedStateHandle,
    private val mapper: GameplayMapper,
    private val uiEvents: GameplayUiEvents,
    private val getGameOptionsUseCase: GetGameOptionsUseCase,
    private val saveGameOptionsUseCase: SaveGameOptionsUseCase,
    private val getCurrentGameMovesUseCase: GetCurrentGameMovesUseCase,
    private val saveCurrentGameMovesUseCase: SaveCurrentGameMovesUseCase,
    private val removeCurrentGameUseCase: RemoveCurrentGameUseCase,
    private val addToGameStatisticsUseCase: AddToGameStatisticsUseCase,
    private val dispatchers: Dispatchers,
    gameFactory: GameFactory
) : ViewModel(),
    NavActionsEmitter by NavActionsEmitter() {

    private val game: Game
    private val playerSide: Side
    private val computerSide: Side

    private var gameOptions = GameOptions(
        isPlayerWhite = args.value.isPlayerWhite,
        isMoveSuggestionsOn = false,
        difficultyLevel = DifficultyLevel(1)
    )

    private val uiState: StateHandler<GameplayUiState>

    val states: StateFlow<GameplayUiState>
        get() = uiState.states
    val state: GameplayUiState
        get() = uiState.state

    init {
        val isNewGame = args.value.isNewGame
        val isPlayerWhite = args.value.isPlayerWhite
        val wasGameStarted = savedStateHandle.wasGameStarted()
        val isRestoringGame = !isNewGame || wasGameStarted
        playerSide = if (isPlayerWhite) WHITE else Side.BLACK
        computerSide = playerSide.flip()
        game = gameFactory.createPlayerVsComputer(
            playerSide = playerSide,
            isPiecesPositionReady = !isRestoringGame,
            uiEvents = uiEvents
        )
        uiState = StateHandler(initialUiState(game.board.state, isRestoringGame))

        setupUiLifecycleCollection()
        viewModelScope.launch {
            joinAll(loadOptionsAndSetupGame(), loadSavedGame())
            hideLoadingIfShown()

            val started = game.start()
            if (isRestoringGame && started) {
                game.stop()
            }

            setupUiUpdates()
            setupUiClicksCollection()
            setupGameAutoSave(skipInitialState = isRestoringGame)

            savedStateHandle.markGameStarted()
            try {
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    game.cleanup()
                }
            }
        }
    }

    fun handleUiEvent(uiEvent: GameplayUiEvent) = viewModelScope.launch {
        uiEvents.handleUiEvent(uiEvent)
    }

    private fun setupUiUpdates() =
        combine(game.statuses(), game.board.states()) { gameStatus, boardState ->
            updateUi(gameStatus, boardState)
        }.launchIn(viewModelScope)

    private fun updateUi(
        gameStatus: GameStatus,
        boardState: ChessBoardState
    ) = uiState.updateState {
        val dialog = mapper.toGameplayDialogUi(
            status = gameStatus,
            sideToMove = boardState.sideToMove,
            isMoveSuggestionsOn = gameOptions.isMoveSuggestionsOn,
            isPromotionManualConfirmationRequired = boardState.isPromotionManualConfirmationRequired,
            checkInfo = boardState.checkInfo
        )
        val isCompleteRoundMovesCountReached = boardState.moves.size >= COMPLETE_ROUND_MOVES_COUNT
        val computerMovedFirst = computer.isWhite && boardState.moves.isNotEmpty()
        copy(
            board = mapper.toBoardUi(boardState),
            player = player.copy(
                isSelected = boardState.isPlayerTurn()
            ),
            computer = computer.copy(
                isSelected = boardState.isComputerTurn()
            ),
            isConfirmMoveButtonVisible = boardState.isMoveManualConfirmationRequired,
            isGameMovesButtonVisible = isCompleteRoundMovesCountReached,
            isUndoMoveButtonVisible = isCompleteRoundMovesCountReached || computerMovedFirst,
            dialog = dialog
        )
    }

    private fun setupUiClicksCollection() {
        collectGameMovesClicks()
        collectNewGameClicks()
        collectExitGameClicks()
        collectEndgameNewGameClicks()
        collectEndgameMainMenuClicks()
        collectMoveSuggestionsSwitchToggles()
        collectEvents(
            merge(uiEvents.resumeClicks, uiEvents.cancelGameMenuClicks)
        ) { game.start() }
        collectEvents(
            merge(uiEvents.pauseClicks, uiEvents.backClicks)
        ) { game.stop() }
    }

    private fun setupUiLifecycleCollection() {
        collectEvents(uiEvents.screenStarts) {
            game.startIfPaused()
        }
        collectEvents(uiEvents.appMinimizes) {
            game.stop()
        }
    }

    private fun collectGameMovesClicks() = collectEvents(uiEvents.gameMovesClicks) {
        game.pause()
        val moves = mapper.toMoveArgs(game.board.movesBackup)
        emitNavAction(NavigateTo(GameMovesRoute(moves)))
    }

    private fun collectNewGameClicks() = collectEvents(uiEvents.newGameClicks) {
        game.resign()
        removeCurrentGameUseCase()
        navigateToOptionsMenu()
    }

    private fun collectExitGameClicks() = collectEvents(uiEvents.exitGameClicks) {
        game.resign()
        removeCurrentGameUseCase()
        navigateToMain()
    }

    private fun collectEndgameNewGameClicks() = collectEvents(uiEvents.endgameNewGameMenuClicks) {
        addGameToStatistics()
        removeCurrentGameUseCase()
        navigateToOptionsMenu()
    }

    private fun collectEndgameMainMenuClicks() = collectEvents(uiEvents.endgameMainMenuClicks) {
        addGameToStatistics()
        removeCurrentGameUseCase()
        navigateToMain()
    }

    private fun collectMoveSuggestionsSwitchToggles() = collectEvents(uiEvents.moveSuggestionsSwitchToggles) {
        gameOptions = gameOptions.copy(isMoveSuggestionsOn = it.on)
        updateGameMenuIfShown()
        saveGameOptionsUseCase(gameOptions)
            .onFailure { logcat { "Failed to save game options. ${it.asLog()}" } }
    }

    private fun updateGameMenuIfShown() = uiState.updateState {
        if (dialog is GameMenuDialogUi) {
            copy(dialog = dialog.copy(isMoveSuggestionsOn = gameOptions.isMoveSuggestionsOn))
        } else {
            this
        }
    }

    private fun hideLoadingIfShown() = uiState.updateState {
        if (dialog is LoadingDialogUi) copy(dialog = null) else this
    }

    private suspend fun addGameToStatistics() {
        val type = when (game.status) {
            GameStatus.WHITE_WON,
            GameStatus.BLACK_WON -> if (game.board.state.isPlayerTurn()) WON else LOST

            GameStatus.DRAW -> DRAW
            else -> throw IllegalStateException("Game is not finished and has ${game.status} status")
        }
        val isPlayerWhite = playerSide == WHITE

        logcat { "Adding $type to statistics for a $playerSide player" }
        addToGameStatisticsUseCase(type = type, isWhitePlayer = isPlayerWhite)
            .onFailure { logcat { "Failed to save $type for a $isPlayerWhite player" } }
    }

    private fun <T> collectEvents(events: Flow<T>, processor: suspend (T) -> Unit) =
        events.onEach(processor).launchIn(viewModelScope)

    private fun CoroutineScope.loadOptionsAndSetupGame() = launch(dispatchers.io()) {
        val options = getGameOptionsUseCase()
            .onFailure { logcat { "Failed to load game options, falling-back to default. ${it.asLog()}" } }
            .getOrDefault(GameOptions.DEFAULT)
        gameOptions = gameOptions.copy(
            isMoveSuggestionsOn = options.isMoveSuggestionsOn,
            difficultyLevel = options.difficultyLevel
        )
        game.setup(options)
    }

    private fun CoroutineScope.loadSavedGame() = launch(dispatchers.io()) {
        getCurrentGameMovesUseCase()
            .onSuccess { game.loadMoves(it.orEmpty()) }
            .onFailure {
                logcat { "Failed load game moves. ${it.asLog()}" }
                removeCurrentGameUseCase()
                navigateToMain()
            }
    }

    private fun setupGameAutoSave(skipInitialState: Boolean) =
        game.board.states()
            .drop(if (skipInitialState) 1 else 0)
            .map { it.moves }
            .distinctUntilChanged()
            .map { moves -> moves.map { it.toString() } }
            .onEach { saveCurrentGameMovesUseCase(it) }
            .launchIn(viewModelScope)

    private fun initialUiState(initialBoardState: ChessBoardState, isRestoringGame: Boolean): GameplayUiState {
        val isPlayerSelected = !isRestoringGame && initialBoardState.isPlayerTurn()
        val isComputerSelected = !isRestoringGame && initialBoardState.isComputerTurn()
        return GameplayUiState(
            board = mapper.toBoardUi(initialBoardState),
            player = mapper.toPlayer(playerSide, isSelected = isPlayerSelected),
            computer = mapper.toComputer(computerSide, isSelected = isComputerSelected),
            isConfirmMoveButtonVisible = initialBoardState.isMoveManualConfirmationRequired,
            dialog = LoadingDialogUi.takeIf { isRestoringGame }
        )
    }

    private suspend fun navigateToMain() = emitNavAction(
        NavigateTo(
            route = MainRoute,
            options = navOptions { popUpTo<RootRoute>() }
        )
    )

    private suspend fun navigateToOptionsMenu() = emitNavAction(
        NavigateTo(
            route = OptionsMenuRoute(
                isPlayerWhite = gameOptions.isPlayerWhite,
                isMoveSuggestionsOn = gameOptions.isMoveSuggestionsOn,
                difficultyLevel = gameOptions.difficultyLevel.value
            ),
            options = navOptions { popUpTo<MainRoute>() }
        )
    )

    private fun ChessBoardState.isPlayerTurn() = playerSide == sideToMove
    private fun ChessBoardState.isComputerTurn() = computerSide == sideToMove

    private fun SavedStateHandle.markGameStarted() =
        set(KEY_GAME_STARTED_BEFORE, true)

    private fun SavedStateHandle.wasGameStarted() =
        get<Boolean>(KEY_GAME_STARTED_BEFORE) == true

    companion object {
        @VisibleForTesting
        const val KEY_GAME_STARTED_BEFORE = "GAME_STARTED_BEFORE"
    }
}
