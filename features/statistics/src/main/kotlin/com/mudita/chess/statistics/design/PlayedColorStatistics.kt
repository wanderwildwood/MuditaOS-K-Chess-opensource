package com.mudita.chess.statistics.design

import com.mudita.chess.ui.design.DarkMode
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun PlayedColorStatistics(
    playedAsWhitePercentage: Int,
    playedAsBlackPercentage: Int,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(
            id = RFrontitude.string.chess_statistics_label_youplayedas
        ),
        style = AppTypography900.titleMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Dark mode turns every color, so it is given the other knight and shows the right one.
        val dark = DarkMode.on
        PlayerColorResult(
            iconResId = if (dark) com.mudita.chess.ui.R.drawable.ic_knight_black_outline_white else com.mudita.chess.ui.R.drawable.ic_knight_white_outline_black,
            percentage = playedAsWhitePercentage,
            label = stringResource(id = RFrontitude.string.common_label_white)
        )
        PlayerColorResult(
            iconResId = if (dark) com.mudita.chess.ui.R.drawable.ic_knight_white_outline_black else com.mudita.chess.ui.R.drawable.ic_knight_black_outline_white,
            percentage = playedAsBlackPercentage,
            label = stringResource(id = RFrontitude.string.common_label_black)
        )
    }
}

@Preview
@Composable
private fun PlayedColorStatisticsPreview() = AppTheme {
    PlayedColorStatistics(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        playedAsWhitePercentage = 50,
        playedAsBlackPercentage = 50
    )
}
