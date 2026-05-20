package com.mudita.chess.coroutines.di

import com.mudita.chess.coroutines.Dispatchers
import com.mudita.chess.coroutines.di.DispatcherQualifierName.DEFAULT
import com.mudita.chess.coroutines.di.DispatcherQualifierName.IO
import com.mudita.chess.coroutines.di.DispatcherQualifierName.MAIN
import com.mudita.chess.coroutines.di.DispatcherQualifierName.MAIN_IMMEDIATE
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutinesModule = module {
    singleOf(::Dispatchers)
    factory<CoroutineDispatcher>(qualifier = named(DEFAULT)) { get<Dispatchers>().default() }
    factory<CoroutineDispatcher>(qualifier = named(IO)) { get<Dispatchers>().io() }
    factory<CoroutineDispatcher>(qualifier = named(MAIN)) { get<Dispatchers>().main() }
    factory<CoroutineDispatcher>(qualifier = named(MAIN_IMMEDIATE)) { get<Dispatchers>().mainImmediate() }
}

enum class DispatcherQualifierName {
    DEFAULT, IO, MAIN, MAIN_IMMEDIATE
}
