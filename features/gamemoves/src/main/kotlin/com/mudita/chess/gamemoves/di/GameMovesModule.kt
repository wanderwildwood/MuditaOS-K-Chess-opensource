package com.mudita.chess.gamemoves.di

import com.mudita.chess.gamemoves.GameMoveRouteProvider
import com.mudita.chess.gamemoves.GameMovesMapper
import com.mudita.chess.gamemoves.GameMovesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val gameMovesModule = module {
    factoryOf(::GameMoveRouteProvider)
    factoryOf(::GameMovesMapper)
    viewModelOf(::GameMovesViewModel)
}
