package com.mudita.chess.optionsmenu

import androidx.lifecycle.SavedStateHandle
import com.mudita.chess.navigation.RouteProvider
import com.mudita.chess.navigation.routes.OptionsMenuRoute

class OptionsMenuRouteProvider(
    savedStateHandle: SavedStateHandle
) : RouteProvider<OptionsMenuRoute> by RouteProvider(savedStateHandle, OptionsMenuRoute::from)
