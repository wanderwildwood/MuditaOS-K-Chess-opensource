package com.mudita.chess.initialization

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class AndroidLogcatLoggerInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AndroidLogcatLogger.installOnDebuggableApp(
            application = context as Application,
            minPriority = LogPriority.VERBOSE
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
