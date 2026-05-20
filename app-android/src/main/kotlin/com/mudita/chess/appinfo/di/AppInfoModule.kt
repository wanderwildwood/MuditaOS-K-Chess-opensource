package com.mudita.chess.appinfo.di

import com.mudita.chess.appinfo.AppInfo
import com.mudita.chess.appinfo.AppInfoImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appInfoModule = module {
    single<AppInfo> { AppInfoImpl(androidContext()) }
}
