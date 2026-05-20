package com.mudita.chess.database.di

import com.mudita.chess.database.Database
import com.mudita.chess.database.DatabaseProvider
import com.mudita.chess.database.DatabaseProvider.Files.DEFAULT
import com.mudita.chess.database.SqlDelightDatabaseProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<DatabaseProvider> { SqlDelightDatabaseProvider(androidContext()) }
    single<Database> {
        get<DatabaseProvider>()
            .provideDatabase(DEFAULT)
    }
}
