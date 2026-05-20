package com.mudita.chess.navigation

import androidx.navigation.NavOptions

interface AppNavigator {
    fun navigateUp(result: Map<String, Any?>? = null)
    fun navigateTo(route: Route, options: NavOptions)
}
