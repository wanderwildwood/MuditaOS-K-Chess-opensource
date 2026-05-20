package com.mudita.chess.json.di

import com.mudita.chess.json.Json
import com.mudita.chess.json.KotlinXSerializationJson
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val jsonModule = module {
    singleOf(::KotlinXSerializationJson) bind Json::class
}
