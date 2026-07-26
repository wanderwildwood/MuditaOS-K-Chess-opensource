@file:JvmName("AppRootComposable")

package com.mudita.chess.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mudita.chess.navigation.AppNavigation
import com.mudita.chess.navigation.routes.MainRoute
import com.mudita.chess.ui.design.AppTheme

@Composable
internal fun AppRoot(
    navController: NavHostController = rememberNavController()
) {
    AppTheme {
        AppNavigation(
            navController = navController,
            startDestination = MainRoute::class
        )
    }
}
