package com.mudita.chess.gameplay.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.gameplay.model.EndgameUi
import com.mudita.chess.ui.R
import com.mudita.chess.ui.design.AppButtonAttributes
import com.mudita.chess.ui.design.AppIconButton
import com.mudita.chess.ui.design.AppPrimaryButton
import com.mudita.chess.ui.design.AppSecondaryButton
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.ui.design.appColorWhite
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * What a finished game shows, in the strip where the game controls normally live.
 *
 * It takes the place of [BottomMenu] rather than covering the board, so the move that ended the
 * game stays where it can be looked at. Undo is the first control because it is the one that gets
 * used: taking the round back is how you find out what you should have played instead.
 */
@Composable
internal fun EndgameMenu(
    isUndoButtonVisible: Boolean,
    onUndoButtonClicked: () -> Unit,
    onNewGameButtonClicked: () -> Unit,
    onMainMenuButtonClicked: () -> Unit,
    modifier: Modifier
) {
    // Matched to the confirm-move button in BottomMenu, so the strip keeps the same weight when
    // a game ends as it had while the game was being played.
    val buttonAttributes = AppButtonAttributes(
        height = 36.dp,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        cornerRadius = 8.dp,
        textStyle = AppTypography900.labelSmall
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // A game restored from a stored position has no moves behind it, so there is nothing to
        // take back and no reason to offer the control.
        if (isUndoButtonVisible) {
            AppIconButton(
                modifier = Modifier.border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = ButtonDefaults.buttonColors().containerColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
                touchAreaPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                iconResId = R.drawable.ic_undo,
                onClick = onUndoButtonClicked
            )
        }
        // Undo sits hard left where the in-play undo button is, and the actions that end this
        // game sit right where Confirm move does. Nothing moves position when a game finishes.
        Spacer(modifier = Modifier.weight(1f))
        AppSecondaryButton(
            text = stringResource(id = RFrontitude.string.chess_endingscreen_dialog_button_mainmenu),
            attributes = buttonAttributes,
            onClick = onMainMenuButtonClicked
        )
        Spacer(modifier = Modifier.width(6.dp))
        AppPrimaryButton(
            text = stringResource(id = RFrontitude.string.chess_endingscreen_dialog_button_newgame),
            size = buttonAttributes,
            onClick = onNewGameButtonClicked
        )
    }
}

/**
 * The result, shown where the turn indicator sits during play.
 *
 * That strip answers "what is the state of this game" while it is being played, and a finished
 * game is still an answer to that question - whereas "WHITE" next to a board nobody can move on
 * is not. Same height and weight as the indicator it stands in for, minus the dashed frame, which
 * means "to move" and would be a lie here.
 */
@Composable
internal fun EndgameResult(
    endgame: EndgameUi,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = endgame.resultResId).uppercase(),
            style = AppTypography900.displaySmall
        )
    }
}

@Preview
@Composable
private fun EndgameMenuPreview() {
    AppTheme {
        EndgameMenu(
            modifier = Modifier
                .fillMaxWidth()
                .background(appColorWhite),
            isUndoButtonVisible = true,
            onUndoButtonClicked = {},
            onNewGameButtonClicked = {},
            onMainMenuButtonClicked = {}
        )
    }
}
