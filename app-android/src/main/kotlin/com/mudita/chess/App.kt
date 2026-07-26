package com.mudita.chess

import android.app.Application
import com.mudita.chess.BuildConfig.DEBUG
import com.mudita.chess.appinfo.AppInfo
import com.mudita.chess.appinfo.di.appInfoModule
import com.mudita.chess.coroutines.di.coroutinesModule
import com.mudita.chess.database.di.databaseModule
import com.mudita.chess.engine.di.chessEngineModule
import com.mudita.chess.gamemoves.di.gameMovesModule
import com.mudita.chess.gameoptions.di.gameOptionsModule
import com.mudita.chess.gameplay.di.gameplayModule
import com.mudita.chess.games.di.gamesModule
import com.mudita.chess.gamestatistics.di.gameStatisticsModule
import com.mudita.chess.json.di.jsonModule
import com.mudita.chess.main.di.mainModule
import com.mudita.chess.optionsmenu.di.optionsMenuModule
import com.mudita.chess.preferences.di.preferencesModule
import com.mudita.chess.statistics.di.statisticsModule
import com.mudita.chess.ui.resourceprovider.di.uiModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appInfoModule,
                chessEngineModule,
                coroutinesModule,
                jsonModule,
                databaseModule,
                preferencesModule,
                uiModule,
                // services
                gameOptionsModule,
                gamesModule,
                gameStatisticsModule,
                // features
                mainModule,
                optionsMenuModule,
                gameMovesModule,
                gameplayModule,
                statisticsModule
            )

            val appInfo: AppInfo = get()
            if (appInfo.isDebug == DEBUG) {
                androidLogger(Level.DEBUG)
            }
        }
    }
}
