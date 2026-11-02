package com.mudita.chess.main

import android.content.Context

internal fun Context.versionName(): String =
    packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
