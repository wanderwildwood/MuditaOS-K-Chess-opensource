package com.mudita.chess.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mudita.chess.ui.LicenceComponent
import com.mudita.chess.ui.LicenceText
import com.mudita.chess.ui.LicencesData
import com.mudita.chess.ui.LicencesLabels
import com.mudita.chess.frontitude.R as RFrontitude

internal const val SOURCE_URL = "https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource"

/** Everything the APK carries that is not Chess+'s own code, as the build pulls it in. */
internal val LICENCE_COMPONENTS: List<LicenceComponent> = listOf(
    LicenceComponent("Mudita's Chess (the app this began as)", "Mudita", "no licence file of its own, carried on under the GPL", "https://github.com/mudita/MuditaOS-K-Chess-opensource"),
    LicenceComponent("Fairy-Stockfish 14 and its network file", "Fabian Fichter, the Stockfish developers", "GPL-3.0-or-later", "https://github.com/fairy-stockfish/Fairy-Stockfish"),
    LicenceComponent("Mudita Mindful Design (MMD)", "Mudita", "Apache-2.0", "https://github.com/mudita/MMD"),
    LicenceComponent("chesslib", "Ben-Hur Langoni Junior", "Apache-2.0", "https://github.com/bhlangonijr/chesslib"),
    LicenceComponent("AndroidX and Jetpack Compose", "Google", "Apache-2.0", "https://developer.android.com/jetpack"),
    LicenceComponent("Koin", "Kotzilla and contributors", "Apache-2.0", "https://insert-koin.io"),
    LicenceComponent("Kotlin, kotlinx.coroutines and kotlinx.serialization", "JetBrains", "Apache-2.0", "https://kotlinlang.org"),
    LicenceComponent("SQLDelight", "Cash App", "Apache-2.0", "https://github.com/sqldelight/sqldelight"),
    LicenceComponent("Okio and logcat", "Square", "Apache-2.0", "https://github.com/square/okio"),
    LicenceComponent("Lato typeface, supplied with MMD", "Łukasz Dziedzic", "OFL-1.1", "https://www.latofonts.com"),
)

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
    components = LICENCE_COMPONENTS,
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
