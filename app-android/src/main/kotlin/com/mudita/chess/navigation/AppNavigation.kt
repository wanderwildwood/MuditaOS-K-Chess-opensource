@file:JvmName("AppNavigationComposable")

package com.mudita.chess.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mudita.chess.gamemoves.GameMoves
import com.mudita.chess.gameplay.Gameplay
import com.mudita.chess.main.Main
import com.mudita.chess.navigation.routes.GameMovesRoute
import com.mudita.chess.navigation.routes.GameplayRoute
import com.mudita.chess.navigation.routes.MainRoute
import com.mudita.chess.navigation.routes.OptionsMenuRoute
import com.mudita.chess.navigation.routes.RootRoute
import com.mudita.chess.navigation.routes.StatisticsRoute
import com.mudita.chess.optionsmenu.OptionsMenu
import com.mudita.chess.statistics.Statistics
import kotlin.reflect.KClass

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: KClass<out Route>
) {
    val navigator = remember(navController) { AppNavigatorImpl(navController) }
    NavHost(
        navController = navController,
        route = RootRoute::class,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<MainRoute> {
            Main(navigator = navigator)
        }
        composable<OptionsMenuRoute> {
            OptionsMenu(navigator = navigator)
        }
        composable<GameplayRoute> {
            Gameplay(navigator = navigator)
        }
        composable<GameMovesRoute>(GameMovesRoute.typeMap) {
            GameMoves(navigator = navigator)
        }
        composable<StatisticsRoute> {
            Statistics(navigator = navigator)
        }
    }
}
