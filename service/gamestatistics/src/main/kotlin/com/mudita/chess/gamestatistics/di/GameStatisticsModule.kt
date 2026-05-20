package com.mudita.chess.gamestatistics.di

import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.gamestatistics.mapper.toPrefs
import com.mudita.chess.gamestatistics.model.GameStatistics
import com.mudita.chess.gamestatistics.model.GameStatisticsPrefs
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepository
import com.mudita.chess.gamestatistics.repository.GameStatisticsRepositoryImpl
import com.mudita.chess.gamestatistics.usecase.AddToGameStatisticsUseCase
import com.mudita.chess.gamestatistics.usecase.GetGameStatisticsUseCase
import com.mudita.chess.gamestatistics.usecase.RemoveGameStatisticsUseCase
import com.mudita.chess.preferences.PreferencesFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val gameStatisticsModule = module {
    single(named(GAME_STATISTICS_PREFERENCES)) {
        get<PreferencesFactory>().create(
            name = GAME_STATISTICS_PREFERENCES,
            type = GameStatisticsPrefs::class.java,
            initialValue = GameStatistics.EMPTY.toPrefs()
        )
    }
    single<GameStatisticsRepository> {
        GameStatisticsRepositoryImpl(
            preferences = get(named(GAME_STATISTICS_PREFERENCES)),
            ioDispatcher = get(named(IO))
        )
    }
    useCases()
}

private fun Module.useCases() {
    factoryOf(::GetGameStatisticsUseCase)
    factoryOf(::AddToGameStatisticsUseCase)
    factoryOf(::RemoveGameStatisticsUseCase)
}

private const val GAME_STATISTICS_PREFERENCES = "game_statistics_preferences"
