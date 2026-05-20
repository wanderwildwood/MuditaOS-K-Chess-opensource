package com.mudita.chess.gameplay

import androidx.lifecycle.SavedStateHandle
import com.mudita.chess.navigation.RouteProvider
import com.mudita.chess.navigation.routes.GameplayRoute

class GameplayRouteProvider(
    savedStateHandle: SavedStateHandle
) : RouteProvider<GameplayRoute> by RouteProvider(savedStateHandle, GameplayRoute::from)
