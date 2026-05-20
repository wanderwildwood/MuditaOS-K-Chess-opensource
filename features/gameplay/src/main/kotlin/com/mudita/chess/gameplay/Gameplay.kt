package com.mudita.chess.gameplay

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.mudita.chess.gameplay.GameplayUiEvent.AppMinimized
import com.mudita.chess.gameplay.GameplayUiEvent.BackButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmMoveButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.GameMovesButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.PauseButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ScreenStarted
import com.mudita.chess.gameplay.GameplayUiEvent.SquareClicked
import com.mudita.chess.gameplay.GameplayUiEvent.UndoMoveButtonClicked
import com.mudita.chess.gameplay.design.Board
import com.mudita.chess.gameplay.design.BottomMenu
import com.mudita.chess.gameplay.design.GameplayDialog
import com.mudita.chess.gameplay.design.Participant
import com.mudita.chess.gameplay.game.ChessBoard
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.navigation.AppNavigator
import com.mudita.chess.navigation.NavActionsEffect
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.OnLifecycleEvent
import com.mudita.kompakt.commonUi.KompaktTheme
import org.koin.androidx.compose.koinViewModel
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
fun Gameplay(
    navigator: AppNavigator
) {
    GameplayInternal(
        navigator = navigator,
        viewModel = koinViewModel()
    )
}

@Composable
private fun GameplayInternal(
    navigator: AppNavigator,
    viewModel: GameplayViewModel
) {
    val uiState by viewModel.states.collectAsState()
    GameplayScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
    BackHandler { viewModel.handleUiEvent(BackButtonClicked) }
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
    OnLifecycleEvent { _, event ->
        if (event == ON_START) {
            viewModel.handleUiEvent(ScreenStarted)
        }
    }
    // assumptions: single activity application
    OnLifecycleEvent((LocalActivity.current as ComponentActivity)) { _, event ->
        if (event == ON_STOP) {
            viewModel.handleUiEvent(AppMinimized)
        }
    }
}

@Composable
private fun GameplayScreen(
    uiState: GameplayUiState,
    uiEvent: (GameplayUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { contentPadding ->
        var boardBounds by remember {
            mutableStateOf(Rect(0f, 0f, 0f, 0f))
        }
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Participant(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    participant = uiState.computer
                )
                Board(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            boardBounds = it.boundsInRoot()
                        },
                    board = uiState.board,
                    onSquareClick = { uiEvent(SquareClicked(position = it)) }
                )
                Participant(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    participant = uiState.player
                )
                BottomMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onPauseButtonClick = { uiEvent(PauseButtonClicked) },
                    isConfirmMoveButtonVisible = uiState.isConfirmMoveButtonVisible,
                    onConfirmMoveButtonClicked = { uiEvent(ConfirmMoveButtonClicked) },
                    isGameMovesButtonVisible = uiState.isGameMovesButtonVisible,
                    onGameMovesButtonClicked = { uiEvent(GameMovesButtonClicked) },
                    isUndoMoveButtonVisible = uiState.isUndoMoveButtonVisible,
                    onUndoMoveButtonClicked = { uiEvent(UndoMoveButtonClicked) }
                )
            }
            if (!boardBounds.isEmpty) {
                uiState.dialog?.let {
                    GameplayDialog(
                        boardBounds = boardBounds,
                        dialog = it,
                        uiEvent = uiEvent
                    )
                }
            }
        }
    }
}

@KompaktPreview
@Composable
private fun GameplayScreenPreview() = KompaktTheme {
    val mapper = GameplayMapper()
    GameplayScreen(
        uiState = GameplayUiState(
            board = mapper.toBoardUi(ChessBoard(topParticipantSide = BLACK).state),
            computer = ParticipantUi(
                nameResId = RFrontitude.string.common_label_computer,
                isWhite = false,
                isSelected = false
            ),
            player = ParticipantUi(
                nameResId = RFrontitude.string.common_label_you,
                isWhite = true,
                isSelected = true
            ),
            isConfirmMoveButtonVisible = true,
            isGameMovesButtonVisible = true,
            isUndoMoveButtonVisible = true,
            dialog = null
        ),
        uiEvent = {}
    )
}
