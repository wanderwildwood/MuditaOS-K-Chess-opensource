package com.mudita.chess.ui.compontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.KompaktTypography900
import com.mudita.kompakt.commonUi.components.KompaktSwitch

@Composable
fun SwitchOption(
    text: String,
    isSwitchedOn: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    indicatorWidth: Dp = 48.dp,
    indicatorHeight: Dp = 30.dp,
    verticalTouchAreaPadding: Dp = 16.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp + indicatorWidth),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = text,
            style = textStyle
        )
        KompaktSwitch(
            modifier = Modifier.align(Alignment.CenterEnd),
            width = indicatorWidth,
            height = indicatorHeight,
            isSwitchedOn = isSwitchedOn,
            onSwitchToggle = onSwitchToggle,
            verticalTouchAreaPadding = verticalTouchAreaPadding
        )
    }
}

@Preview
@Composable
private fun SwitchOptionPreview() = KompaktTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        SwitchOption(
            modifier = Modifier.fillMaxWidth(),
            text = "Move suggestions",
            textStyle = KompaktTypography900.titleMedium,
            isSwitchedOn = true,
            onSwitchToggle = {}
        )
    }
}
