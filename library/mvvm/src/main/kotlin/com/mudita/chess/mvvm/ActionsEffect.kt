@file:JvmName("ActionsEffectComposable")

package com.mudita.chess.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <UiAction> ActionsEffect(
    actions: Flow<UiAction>,
    handler: suspend (UiAction) -> Unit
) {
    LaunchedEffect(Unit) {
        actions.collect { handler.invoke(it) }
    }
}
