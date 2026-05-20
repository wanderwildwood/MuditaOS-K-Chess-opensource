package com.mudita.chess.appinfo

import android.content.Context
import com.mudita.chess.BuildConfig
import com.mudita.chess.R

class AppInfoImpl(context: Context) : AppInfo {
    override val appName = context.getString(R.string.app_name)
    override val versionName = BuildConfig.VERSION_NAME
    override val versionCode = BuildConfig.VERSION_CODE
    override val isDebug = BuildConfig.DEBUG
}
