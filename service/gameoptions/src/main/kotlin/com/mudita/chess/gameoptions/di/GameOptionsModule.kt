package com.mudita.chess.gameoptions.di

import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.gameoptions.mapper.toPrefs
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.model.GameOptionsPrefs
import com.mudita.chess.gameoptions.repository.GameOptionsRepository
import com.mudita.chess.gameoptions.repository.GameOptionsRepositoryImpl
import com.mudita.chess.gameoptions.usecase.GetGameOptionsUseCase
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.preferences.PreferencesFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val gameOptionsModule = module {
    single(named(GAME_OPTIONS_PREFERENCES)) {
        get<PreferencesFactory>().create(
            name = GAME_OPTIONS_PREFERENCES,
            type = GameOptionsPrefs::class.java,
            initialValue = GameOptions.DEFAULT.toPrefs()
        )
    }
    single<GameOptionsRepository> {
        GameOptionsRepositoryImpl(
            preferences = get(named(GAME_OPTIONS_PREFERENCES)),
            ioDispatcher = get(named(IO))
        )
    }
    useCases()
}

private fun Module.useCases() {
    factoryOf(::GetGameOptionsUseCase)
    factoryOf(::SaveGameOptionsUseCase)
}

private const val GAME_OPTIONS_PREFERENCES = "game_options_preferences"
