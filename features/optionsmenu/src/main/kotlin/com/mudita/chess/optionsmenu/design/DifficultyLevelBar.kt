@file:JvmName("DifficultyLevelBarComposable")

package com.mudita.chess.optionsmenu.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.gameoptions.mapper.MAX_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.mapper.MIN_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.mapper.elo
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.ui.R
import com.mudita.chess.ui.model.TextUi
import com.mudita.chess.ui.model.stringify
import com.mudita.chess.ui.design.AppIconButton
import com.mudita.chess.ui.design.AppTypography500
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun DifficultyLevelBar(
    difficultyLevelStep: Int,
    difficultyLevelLabel: TextUi?,
    onMinusIconClick: () -> Unit,
    onPlusIconClick: () -> Unit,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControllerIcon(
                iconResId = R.drawable.ic_minus_circle_border,
                onClick = onMinusIconClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Steps(
                difficultyLevelStep = difficultyLevelStep,
                onStepClick = onStepClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            ControllerIcon(
                iconResId = R.drawable.ic_plus_circle_border,
                onClick = onPlusIconClick
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Label(difficultyLevelLabel)
    }
}

@Composable
private fun ControllerIcon(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit
) {
    AppIconButton(
        onClick = onClick,
        iconSize = 21.75.dp,
        iconResId = iconResId,
        touchAreaPadding = PaddingValues(0.dp)
    )
}

/**
 * The steps share whatever width the two buttons leave. At a fixed width each, fourteen of them
 * and the buttons came to more than the Kompakt's 360dp, and the plus was squeezed to a dot.
 */
@Composable
private fun RowScope.Steps(
    difficultyLevelStep: Int,
    onStepClick: (Int) -> Unit
) {
    for (i in MIN_DIFFICULTY_LEVEL..MAX_DIFFICULTY_LEVEL) {
        val selected = i <= difficultyLevelStep
        val stepResId = if (selected) {
            R.drawable.ic_step_filled
        } else {
            R.drawable.ic_step_unfilled
        }
        Image(
            modifier = Modifier
                .weight(1f)
                .height(30.dp)
                .clickable { onStepClick(i) }
                .padding(horizontal = 2.dp),
            painter = painterResource(id = stepResId),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
    }
}

@Composable
private fun Label(difficultyLevelLabel: TextUi?) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = difficultyLevelLabel?.stringify().orEmpty(),
        textAlign = TextAlign.Center,
        style = AppTypography500.labelSmall
    )
}

@Preview
@Composable
private fun DifficultyLevelBarPreview() {
    DifficultyLevelBar(
        difficultyLevelStep = 1,
        difficultyLevelLabel = TextUi.Res(
            RFrontitude.string.chess_optionsmenu_label_beginner,
            args = arrayOf(DifficultyLevel(1).elo())
        ),
        onMinusIconClick = {},
        onPlusIconClick = {},
        onStepClick = {}
    )
}
