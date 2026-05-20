package com.mudita.chess.navigation

import androidx.lifecycle.SavedStateHandle

fun <T : Route> RouteProvider(
    savedStateHandle: SavedStateHandle,
    extractor: (SavedStateHandle) -> T
): RouteProvider<T> = LazyRouteProvider {
    extractor(savedStateHandle)
}

interface RouteProvider<T> {
    val value: T
}

private class LazyRouteProvider<T : Route>(
    initializer: () -> T
) : RouteProvider<T> {
    override val value: T by lazy(initializer)
}
