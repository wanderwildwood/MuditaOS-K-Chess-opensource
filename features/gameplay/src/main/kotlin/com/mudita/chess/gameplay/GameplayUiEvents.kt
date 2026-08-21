package com.mudita.chess.gameplay

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
import com.mudita.chess.gameplay.model.GameplayDialogType.CHECK_INFO
import com.mudita.chess.gameplay.model.GameplayDialogType.GAME_MENU
import com.mudita.chess.gameplay.model.GameplayDialogType.PAWN_PROMOTION
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance

internal class GameplayUiEvents {
    private val events = MutableSharedFlow<GameplayUiEvent>()

    val appMinimizes = events
        .filterIsInstance<AppMinimized>()

    val screenStarts = events
        .filterIsInstance<ScreenStarted>()

    val backClicks = events
        .filterIsInstance<BackButtonClicked>()

    val squareClicks = events
        .filterIsInstance<SquareClicked>()

    val confirmPawnPromotionClicks = events
        .filterIsInstance<ConfirmPawnPromotionClicked>()

    val cancelPawnPromotionClicks = events
        .filterIsInstance<DialogDismissRequested>()
        .filter { it.dialogType == PAWN_PROMOTION }

    val cancelCheckInfoClicks = events
        .filterIsInstance<DialogDismissRequested>()
        .filter { it.dialogType == CHECK_INFO }

    val confirmMoveClicks = events
        .filterIsInstance<ConfirmMoveButtonClicked>()

    val pauseClicks = events
        .filterIsInstance<PauseButtonClicked>()

    val resumeClicks = events
        .filterIsInstance<ResumeButtonClicked>()

    val cancelGameMenuClicks = events
        .filterIsInstance<DialogDismissRequested>()
        .filter { it.dialogType == GAME_MENU }

    val newGameClicks = events
        .filterIsInstance<NewGameButtonClicked>()

    val exitGameClicks = events
        .filterIsInstance<ExitButtonClicked>()

    val endgameNewGameMenuClicks = events
        .filterIsInstance<EndgameNewGameButtonClicked>()

    val endgameMainMenuClicks = events
        .filterIsInstance<EndgameMainMenuButtonClicked>()

    val endgameUndoClicks = events
        .filterIsInstance<EndgameUndoButtonClicked>()

    val moveSuggestionsSwitchToggles = events
        .filterIsInstance<MoveSuggestionsSwitchToggled>()

    val gameMovesClicks = events
        .filterIsInstance<GameMovesButtonClicked>()

    val undoMoveClicks = events
        .filterIsInstance<UndoMoveButtonClicked>()

    suspend fun handleUiEvent(uiEvent: GameplayUiEvent) = events.emit(uiEvent)
}
