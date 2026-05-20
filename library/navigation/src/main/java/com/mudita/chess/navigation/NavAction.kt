package com.mudita.chess.navigation

import androidx.navigation.NavOptions
import androidx.navigation.navOptions

val EMPTY_NAV_OPTIONS = navOptions {}

sealed class NavAction {
    data class NavigateUp(
        val result: Map<String, Any?>? = null
    ) : NavAction()

    data class NavigateTo(
        val route: Route,
        val options: NavOptions = EMPTY_NAV_OPTIONS
    ) : NavAction()
}
