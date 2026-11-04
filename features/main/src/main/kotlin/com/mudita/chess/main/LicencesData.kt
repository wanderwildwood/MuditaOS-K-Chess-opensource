package com.mudita.chess.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mudita.chess.ui.LicenceText
import com.mudita.chess.ui.LicencesData
import com.mudita.chess.ui.LicencesLabels
import com.mudita.chess.ui.generatedLicencesResId
import com.mudita.chess.ui.readGeneratedComponents
import com.mudita.chess.frontitude.R as RFrontitude

internal const val SOURCE_URL = "https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource"

/** Each distinct licence text once: family id and the raw resource. */
internal val LICENCE_RAW: List<Pair<String, Int>> = listOf(
    "gpl" to R.raw.gpl_3_0,
    "apache" to R.raw.apache_2_0,
    "ofl" to R.raw.ofl_1_1,
)

@Composable
internal fun rememberLicencesData(): LicencesData = LicencesData(
    appName = "Chess+",
    sourceUrl = SOURCE_URL,
    licenceLine = stringResource(RFrontitude.string.chess_licences_app),
    // One row per artifact in the APK, listed by the build (the AboutLibraries plugin in
    // app-android writes res/raw/aboutlibraries.json there; config/libraries adds what Gradle
    // cannot see: Mudita's Chess, Fairy-Stockfish, Lato). This module cannot see the app's R,
    // so the file is found by name.
    components = rememberGeneratedLicences(),
    texts = LICENCE_RAW.map { (id, raw) ->
        LicenceText(
            id,
            stringResource(
                when (id) {
                    "gpl" -> RFrontitude.string.chess_licences_full_gpl
                    "apache" -> RFrontitude.string.chess_licences_full_apache
                    else -> RFrontitude.string.chess_licences_full_ofl
                }
            ),
            raw
        )
    },
)

@Composable
internal fun rememberLicencesLabels(): LicencesLabels {
    val none = stringResource(RFrontitude.string.chess_about_no_browser)
    val noText = stringResource(RFrontitude.string.chess_licences_no_text, "@@")
    return LicencesLabels(
        title = stringResource(RFrontitude.string.chess_licences_title),
        back = stringResource(RFrontitude.string.chess_about_back),
        sourceHeading = stringResource(RFrontitude.string.chess_licences_source_title),
        noText = { licence -> noText.replace("@@", licence) },
        noBrowser = { none },
    )
}

@Composable
private fun rememberGeneratedLicences() = LocalContext.current.let { context ->
    remember(context) { readGeneratedComponents(context, generatedLicencesResId(context)) }
}
