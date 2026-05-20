package com.mudita.chess.ui.resourceprovider.di

import com.mudita.chess.ui.resourceprovider.ResourceProvider
import com.mudita.chess.ui.resourceprovider.ResourceProviderImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val uiModule = module {
    factory<ResourceProvider> { ResourceProviderImpl(androidApplication()) }
}
