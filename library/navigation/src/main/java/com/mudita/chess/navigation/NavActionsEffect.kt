package com.mudita.chess.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mudita.chess.navigation.NavAction.NavigateTo
import com.mudita.chess.navigation.NavAction.NavigateUp
import kotlinx.coroutines.flow.Flow

@Composable
fun NavActionsEffect(
    actions: Flow<NavAction>,
    navigator: AppNavigator
) {
    LaunchedEffect(Unit) {
        actions.collect { navAction ->
            when (navAction) {
                is NavigateUp -> navigator.navigateUp(navAction.result)
                is NavigateTo -> navigator.navigateTo(navAction.route, navAction.options)
            }
        }
    }
}
