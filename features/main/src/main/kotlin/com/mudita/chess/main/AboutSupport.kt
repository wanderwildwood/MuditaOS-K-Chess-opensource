package com.mudita.chess.main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * The AOSP WebView test harness ships on the Kompakt and registers for web addresses; it is not
 * a browser, and it crashes on the first real page. Anything that resolves only to it counts as
 * no browser at all. Never a WebView of our own: the address goes to a browser or nowhere.
 */
private const val WEBVIEW_SHELL = "org.chromium.webview_shell"

internal fun Context.openWebPage(address: String) {
    val opened = try {
        val intent = Intent(Intent.ACTION_VIEW, address.toUri())
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val browser = packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .firstOrNull { it.activityInfo?.packageName != WEBVIEW_SHELL }
        if (browser == null) {
            false
        } else {
            // Straight to the browser found, so the shell cannot be chosen by the system.
            startActivity(intent.setPackage(browser.activityInfo.packageName))
            true
        }
    } catch (e: ActivityNotFoundException) {
        Log.e("ChessAbout", "Could not open $address", e)
        false
    } catch (e: SecurityException) {
        Log.e("ChessAbout", "Could not open $address", e)
        false
    }
    if (!opened) {
        Toast.makeText(this, getString(RFrontitude.string.chess_about_no_browser), Toast.LENGTH_LONG).show()
    }
}

/** A licence text shipped in the APK under assets/, a paragraph to a row, markup dropped. */
internal fun Context.licenceParagraphs(asset: String = "LICENSE.md"): List<String> =
    try {
        assets.open(asset).bufferedReader().use { it.readText() }.toParagraphs()
    } catch (e: java.io.IOException) {
        Log.e("ChessAbout", "Could not read the licence text $asset", e)
        emptyList()
    }

internal fun String.toParagraphs(): List<String> =
    replace("\r\n", "\n")
        .split(Regex("\n\\s*\n"))
        .map { paragraph ->
            paragraph.lines().joinToString(" ") { it.trim() }
                .trimStart('#', ' ')
                .replace("**", "")
                .replace("*", "")
                .trim()
        }
        .filter { it.isNotEmpty() }

/** One piece of other people's work that ships inside the APK, and the licence it comes under. */
internal data class Library(val name: String, val by: String, val licence: String, val link: String)

internal const val SOURCE_URL = "https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource"

/** Everything the APK carries that is not Chess+'s own code, as the build pulls it in. */
internal val LIBRARIES: List<Library> = listOf(
    Library("Mudita's Chess (the app this began as)", "Mudita", "no licence file of its own, carried on under the GPL", "https://github.com/mudita/MuditaOS-K-Chess-opensource"),
    Library("Fairy-Stockfish 14 and its network file", "Fabian Fichter, the Stockfish developers", "GPL-3.0-or-later", "https://github.com/fairy-stockfish/Fairy-Stockfish"),
    Library("Mudita Mindful Design (MMD)", "Mudita", "Apache-2.0", "https://github.com/mudita/MMD"),
    Library("chesslib", "Ben-Hur Langoni Junior", "Apache-2.0", "https://github.com/bhlangonijr/chesslib"),
    Library("AndroidX and Jetpack Compose", "Google", "Apache-2.0", "https://developer.android.com/jetpack"),
    Library("Koin", "Kotzilla and contributors", "Apache-2.0", "https://insert-koin.io"),
    Library("Kotlin, kotlinx.coroutines and kotlinx.serialization", "JetBrains", "Apache-2.0", "https://kotlinlang.org"),
    Library("SQLDelight", "Cash App", "Apache-2.0", "https://github.com/sqldelight/sqldelight"),
    Library("Okio and logcat", "Square", "Apache-2.0", "https://github.com/square/okio"),
    Library("Lato typeface, supplied with MMD", "Łukasz Dziedzic", "OFL-1.1", "https://www.latofonts.com"),
)

internal fun Context.versionName(): String =
    packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
