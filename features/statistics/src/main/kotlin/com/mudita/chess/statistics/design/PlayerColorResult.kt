package com.mudita.chess.statistics.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.R
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography500
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.ui.design.appColorBlack

@Composable
internal fun PlayerColorResult(
    @DrawableRes iconResId: Int,
    percentage: Int,
    label: String,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .size(125.dp)
        .drawBehind {
            drawDashedCircle()
            drawPercentageProgress(percentage)
        },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        modifier = modifier.size(34.dp),
        painter = painterResource(id = iconResId),
        contentDescription = null
    )
    Text(
        text = "$percentage%",
        style = AppTypography900.titleLarge
    )
    Text(
        text = label,
        style = AppTypography500.displaySmall
    )
}

@Suppress("MagicNumber")
private fun DrawScope.drawDashedCircle() {
    val dashLength = 8f
    val lengthBetweenDashes = 10f
    val dashedStroke = Stroke(
        width = 1.5f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength, lengthBetweenDashes),
            phase = 0f
        )
    )
    drawCircle(color = appColorBlack, style = dashedStroke)
}

private fun DrawScope.drawPercentageProgress(percentage: Int) {
    drawArc(
        color = appColorBlack,
        startAngle = 270f,
        sweepAngle = 360f * percentage / 100,
        useCenter = false,
        topLeft = Offset.Zero,
        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
    )
}

@Preview
@Composable
private fun PlayerColorResultPreview() = AppTheme {
    PlayerColorResult(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
        iconResId = R.drawable.ic_knight_white_outline_black,
        percentage = 25,
        label = "WHITE"
    )
}
