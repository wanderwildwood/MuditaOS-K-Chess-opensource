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
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.design.AppSwitch
import com.mudita.chess.ui.design.AppTheme
import com.mudita.chess.ui.design.AppTypography900

@Composable
fun SwitchOption(
    text: String,
    isSwitchedOn: Boolean,
    onSwitchToggle: (Boolean) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 64.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = text,
            style = textStyle
        )
        AppSwitch(
            modifier = Modifier.align(Alignment.CenterEnd),
            checked = isSwitchedOn,
            onCheckedChange = onSwitchToggle
        )
    }
}

@Preview
@Composable
private fun SwitchOptionPreview() = AppTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        SwitchOption(
            modifier = Modifier.fillMaxWidth(),
            text = "Move suggestions",
            textStyle = AppTypography900.titleMedium,
            isSwitchedOn = true,
            onSwitchToggle = {}
        )
    }
}
