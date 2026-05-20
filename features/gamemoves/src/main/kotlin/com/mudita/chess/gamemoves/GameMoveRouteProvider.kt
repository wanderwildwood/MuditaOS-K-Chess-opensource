package com.mudita.chess.gamemoves

import androidx.lifecycle.SavedStateHandle
import com.mudita.chess.navigation.RouteProvider
import com.mudita.chess.navigation.routes.GameMovesRoute

class GameMoveRouteProvider(
    savedStateHandle: SavedStateHandle
) : RouteProvider<GameMovesRoute> by RouteProvider(savedStateHandle, GameMovesRoute::from)
