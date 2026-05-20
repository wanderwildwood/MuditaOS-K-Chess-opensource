package com.mudita.chess.ui.compontent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DialogHost(
    dialogContentAlignment: Alignment,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dialogContent: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = dialogContentAlignment,
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismissRequest() }
        )
        Box(
            modifier.clickable(false) {}
        ) {
            dialogContent()
        }
    }
    BackHandler {
        onDismissRequest()
    }
}
