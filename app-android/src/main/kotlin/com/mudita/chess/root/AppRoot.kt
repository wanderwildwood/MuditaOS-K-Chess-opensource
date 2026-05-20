@file:JvmName("AppRootComposable")

package com.mudita.chess.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mudita.chess.navigation.AppNavigation
import com.mudita.chess.navigation.routes.MainRoute
import com.mudita.kompakt.commonUi.KompaktTheme

@Composable
internal fun AppRoot(
    navController: NavHostController = rememberNavController()
) {
    KompaktTheme {
        AppNavigation(
            navController = navController,
            startDestination = MainRoute::class
        )
    }
}
