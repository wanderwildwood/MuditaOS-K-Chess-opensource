package com.mudita.chess.statistics.di

import com.mudita.chess.statistics.StatisticsMapper
import com.mudita.chess.statistics.StatisticsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val statisticsModule = module {
    factoryOf(::StatisticsMapper)
    viewModelOf(::StatisticsViewModel)
}
