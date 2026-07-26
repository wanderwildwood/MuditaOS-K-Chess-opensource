package com.mudita.chess.statistics.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900

@Composable
internal fun MatchResultItem(
    title: String,
    value: Int,
    valueWidth: Dp,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = 64.dp)
        .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        modifier = Modifier.width(valueWidth),
        text = value.toString(),
        style = AppTypography900.titleLarge
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = title,
        style = AppTypography900.labelMedium
    )
}

@Preview
@Composable
private fun MatchResultItemPreview() = AppTheme {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        val valueWidth = 72.dp
        MatchResultItem(
            title = "Title",
            value = 10,
            valueWidth = valueWidth
        )
        HorizontalDivider()
        MatchResultItem(
            title = "Title",
            value = 5,
            valueWidth = valueWidth
        )
        HorizontalDivider()
        MatchResultItem(
            title = "Title",
            value = 500,
            valueWidth = valueWidth
        )
        HorizontalDivider()
        MatchResultItem(
            title = "Title",
            value = 1000,
            valueWidth = valueWidth
        )
    }
}
