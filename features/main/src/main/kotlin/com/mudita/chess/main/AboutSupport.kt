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

internal fun Context.versionName(): String =
    packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
