package com.mudita.chess.preferences.di

import com.mudita.chess.preferences.PreferencesFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {
    single {
        PreferencesFactory(
            context = androidContext(),
            json = get()
        )
    }
}
