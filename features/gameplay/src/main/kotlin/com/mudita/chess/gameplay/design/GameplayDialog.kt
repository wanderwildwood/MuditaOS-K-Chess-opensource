package com.mudita.chess.gameplay.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.stringResource
import com.mudita.chess.gameplay.GameplayUiEvent
import com.mudita.chess.gameplay.GameplayUiEvent.ConfirmPawnPromotionClicked
import com.mudita.chess.gameplay.GameplayUiEvent.DialogDismissRequested
import com.mudita.chess.gameplay.GameplayUiEvent.ExitButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.MoveSuggestionsSwitchToggled
import com.mudita.chess.gameplay.GameplayUiEvent.NewGameButtonClicked
import com.mudita.chess.gameplay.GameplayUiEvent.ResumeButtonClicked
import com.mudita.chess.gameplay.model.DialogAlignment.BOARD_CENTER
import com.mudita.chess.gameplay.model.DialogAlignment.BOTTOM
import com.mudita.chess.gameplay.model.GameplayDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.CheckInfoDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.GameMenuDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.LoadingDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.PawnPromotionDialogUi
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.R
import com.mudita.chess.ui.compontent.DialogHost
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.model.PieceUi

@Composable
internal fun GameplayDialog(
    boardBounds: Rect,
    dialog: GameplayDialogUi,
    uiEvent: (GameplayUiEvent) -> Unit
) {
    val dialogContentAlignment = when (dialog.alignment) {
        BOTTOM -> Alignment.BottomCenter
        BOARD_CENTER -> Alignment.CenterIn(boardBounds)
    }
    DialogHost(
        dialogContentAlignment = dialogContentAlignment,
        onDismissRequest = { uiEvent(DialogDismissRequested(dialog.type)) }
    ) {
        when (dialog) {
            LoadingDialogUi -> LoadingDialog()
            is GameMenuDialogUi -> GameMenuDialog(
                isMoveSuggestionsOn = dialog.isMoveSuggestionsOn,
                isTwoPlayerMode = dialog.isTwoPlayerMode,
                onResumeClick = { uiEvent(ResumeButtonClicked) },
                onNewGameClick = { uiEvent(NewGameButtonClicked) },
                onExitClick = { uiEvent(ExitButtonClicked) },
                onMoveSuggestionsSwitchToggle = { uiEvent(MoveSuggestionsSwitchToggled(on = it)) }
            )

            is PawnPromotionDialogUi -> PawnPromotionDialog(
                dialog.promotionOptions,
                onOptionConfirmed = { uiEvent(ConfirmPawnPromotionClicked(piece = it)) }
            )

            is CheckInfoDialogUi -> CheckInfoDialog(
                king = dialog.king,
                attackedBy = dialog.attackedBy
            )

        }
    }
}

@KompaktPreview
@Composable
@Suppress("MagicNumber")
private fun LoadingDialogUiPreview() = AppTheme {
    GameplayDialog(
        boardBounds = Rect(450f, 300f, 450f, 300f),
        dialog = LoadingDialogUi,
        uiEvent = {}
    )
}

@KompaktPreview
@Composable
private fun GameMenuDialogUiPreview() = AppTheme {
    GameplayDialog(
        boardBounds = Rect(0f, 0f, 0f, 0f),
        dialog = GameMenuDialogUi(isMoveSuggestionsOn = true),
        uiEvent = {}
    )
}

@KompaktPreview
@Composable
@Suppress("MagicNumber")
private fun PawnPromotionDialogUiPreview() = AppTheme {
    GameplayDialog(
        boardBounds = Rect(450f, 300f, 450f, 300f),
        dialog = PawnPromotionDialogUi(
            promotionOptions = setOf(
                PieceUi(type = QUEEN, isWhite = true),
                PieceUi(type = ROOK, isWhite = true),
                PieceUi(type = BISHOP, isWhite = true),
                PieceUi(type = KNIGHT, isWhite = true)
            )
        ),
        uiEvent = {}
    )
}
