package com.mudita.chess.statistics.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.chess.statistics.model.MatchResultUi
import com.mudita.chess.ui.design.AppDashedHorizontalDivider
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun MatchResults(
    matchResults: List<MatchResultUi>,
    modifier: Modifier = Modifier
) = Column(modifier = modifier) {
    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = stringResource(
            id = RFrontitude.string.chess_statistics_label_matchresults
        ),
        style = AppTypography900.titleMedium
    )

    val valueWidth = countValueWidth(matchResults)
    val dividerStartPadding = valueWidth + 32.dp
    matchResults.forEachIndexed { index, result ->
        MatchResultItem(
            title = stringResource(id = result.titleResId),
            value = result.value,
            valueWidth = valueWidth
        )
        if (index < matchResults.size - 1) {
            AppDashedHorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dividerStartPadding)
            )
        }
    }
}

@Composable
private fun countValueWidth(matchResults: List<MatchResultUi>): Dp {
    val maxDigits = matchResults.maxOf { it.value.toString().length }
    val valueWidth = maxDigits * DIGIT_WIDTH
    return valueWidth.dp
}

private const val DIGIT_WIDTH = 18

@Preview
@Composable
internal fun MatchResultsPreview() = AppTheme {
    Column {
        MatchResults(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            matchResults = listOf(
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_won,
                    value = 9
                ),
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_drawn,
                    value = 0
                )
            )
        )
        MatchResults(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            matchResults = listOf(
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_won,
                    value = 9
                ),
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_drawn,
                    value = 99
                )
            )
        )
        MatchResults(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            matchResults = listOf(
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_won,
                    value = 99
                ),
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_drawn,
                    value = 999
                )
            )
        )
        MatchResults(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            matchResults = listOf(
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_won,
                    value = 99
                ),
                MatchResultUi(
                    titleResId = RFrontitude.string.chess_statistics_label_drawn,
                    value = 9999
                )
            )
        )
    }
}
