package com.mudita.chess.optionsmenu.di

import com.mudita.chess.optionsmenu.OptionsMenuMapper
import com.mudita.chess.optionsmenu.OptionsMenuRouteProvider
import com.mudita.chess.optionsmenu.OptionsMenuViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val optionsMenuModule = module {
    factoryOf(::OptionsMenuRouteProvider)
    factoryOf(::OptionsMenuMapper)
    viewModelOf(::OptionsMenuViewModel)
}
