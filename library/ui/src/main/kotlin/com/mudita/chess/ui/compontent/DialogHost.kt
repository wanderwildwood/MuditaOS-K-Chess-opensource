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
    // Registered before the content so that a Back while one of its rows is armed only disarms
    // that row (the row's own handler comes later and so goes first); the next Back closes.
    BackHandler {
        onDismissRequest()
    }
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
}
