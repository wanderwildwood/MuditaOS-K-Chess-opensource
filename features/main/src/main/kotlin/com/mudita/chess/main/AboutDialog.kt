package com.mudita.chess.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * This app's About: only the words and the address. The dialog, its drawers, its one Close button,
 * the Licenses drawer, the links, the contact row and the llama are the shared HouseAbout.kt, the
 * same in every app.
 *
 * Most of this app is Mudita's own Chess for the Kompakt, and the engine is Stockfish's; both are
 * named here because somebody who installed the APK and never saw the repository has no other
 * way to learn whose work it is, or that they have the source under the GPL.
 */
@Composable
internal fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val version = remember { context.versionName() }
    val data = AboutData(
        title = stringResource(RFrontitude.string.chess_about_title, version),
        sections = listOf(
            AboutSection(null, stringResource(RFrontitude.string.chess_about_what)),
            AboutSection(
                stringResource(RFrontitude.string.chess_about_privacy_title),
                stringResource(RFrontitude.string.chess_about_privacy),
            ),
            AboutSection(stringResource(RFrontitude.string.privacy_who_title), stringResource(RFrontitude.string.privacy_who)),
            AboutSection(stringResource(RFrontitude.string.privacy_contact_title), stringResource(RFrontitude.string.privacy_contact)),
            AboutSection(stringResource(RFrontitude.string.privacy_rights_title), stringResource(RFrontitude.string.privacy_rights)),
            AboutSection(
                stringResource(RFrontitude.string.chess_about_terms_title),
                stringResource(RFrontitude.string.chess_about_terms),
            ),
            AboutSection(null, stringResource(RFrontitude.string.chess_about_licence)),
            AboutSection(null, stringResource(RFrontitude.string.chess_about_engine)),
        ),
        sourceUrl = SOURCE_URL,
    )
    val labels = AboutLabels(
        close = stringResource(RFrontitude.string.chess_about_close),
        licences = stringResource(RFrontitude.string.chess_about_licences_button),
        feedTheLlamas = stringResource(RFrontitude.string.chess_about_feed_the_llamas),
        noBrowser = { address -> context.getString(RFrontitude.string.chess_about_no_browser_address, address) },
        writeTo = { address -> context.getString(RFrontitude.string.about_write_to, address) },
        noMailApp = { address -> context.getString(RFrontitude.string.about_no_mail_app, address) },
        openDrawer = { heading -> context.getString(RFrontitude.string.about_open_drawer, heading) },
        closeDrawer = { heading -> context.getString(RFrontitude.string.about_close_drawer, heading) },
        contact = stringResource(RFrontitude.string.about_contact_head),
        links = stringResource(RFrontitude.string.about_links_head),
    )
    HouseAboutDialog(data, labels, R.drawable.llama, onDismiss)
}
