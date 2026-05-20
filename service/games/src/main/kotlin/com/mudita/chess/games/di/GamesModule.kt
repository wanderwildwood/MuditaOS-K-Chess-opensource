package com.mudita.chess.games.di

import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.games.repository.GamesRepository
import com.mudita.chess.games.repository.GamesRepositoryImpl
import com.mudita.chess.games.usecase.GetCurrentGameMovesUseCase
import com.mudita.chess.games.usecase.HasCurrentGameUseCase
import com.mudita.chess.games.usecase.RemoveCurrentGameUseCase
import com.mudita.chess.games.usecase.SaveCurrentGameMovesUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val gamesModule = module {
    single<GamesRepository> {
        GamesRepositoryImpl(
            database = get(),
            ioDispatcher = get(named(IO))
        )
    }
    useCases()
}

private fun Module.useCases() {
    factoryOf(::HasCurrentGameUseCase)
    factory { GetCurrentGameMovesUseCase(get(), get(named(IO))) }
    factory { SaveCurrentGameMovesUseCase(get(), get(named(IO))) }
    factoryOf(::RemoveCurrentGameUseCase)
}
