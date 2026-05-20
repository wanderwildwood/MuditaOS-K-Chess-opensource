package com.mudita.chess.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

internal class AppNavigatorImpl(
    private val navController: NavController
) : AppNavigator {

    override fun navigateUp(result: Map<String, Any?>?) {
        navController
            .previousBackStackEntry
            ?.savedStateHandle
            ?.let { savedStateHandle ->
                result?.forEach { (key, value) -> savedStateHandle[key] = value }
            }
        navController.navigateUp()
    }

    override fun navigateTo(route: Route, options: NavOptions) {
        navController.navigate(route, options)
    }
}
