package com.mudita.chess.main

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.PagedColumn
import com.mudita.chess.ui.design.EInkDialog
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * What this is, whose it was first, what it carries, and where the source lives, with the Privacy
 * and Terms words and a second page of licences.
 *
 * Most of this app is Mudita's own Chess for the Kompakt, and the engine is Stockfish's; both are
 * named here because somebody who installed the APK and never saw the repository has no other
 * way to learn whose work it is, or that they have the source under the GPL.
 *
 * The words page by whole rows, and the button at the foot stays where it is.
 */
@Composable
internal fun AboutDialog(onDismiss: () -> Unit) {
    var isLicencesOpen by rememberSaveable { mutableStateOf(false) }
    EInkDialog(onDismiss = onDismiss) {
        if (isLicencesOpen) {
            LicencesPage(onBack = { isLicencesOpen = false })
        } else {
            AboutPage(onOpenLicences = { isLicencesOpen = true }, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun pageHeight() = with(LocalDensity.current) { (LocalWindowInfo.current.containerSize.height * PAGE_HEIGHT_SHARE).toDp() }

private const val PAGE_HEIGHT_SHARE = 0.6f

@Composable
private fun Heading(text: String) {
    TextMMD(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
    )
}

@Composable
private fun Section(heading: String, body: String) {
    Column {
        Heading(heading)
        Body(body)
    }
}

@Composable
private fun Body(text: String) {
    TextMMD(text = text, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(vertical = 2.dp))
}

@Composable
private fun ColumnScope.AboutPage(onOpenLicences: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember { context.versionName() }
    TextMMD(
        text = stringResource(RFrontitude.string.chess_about_title, versionName),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
    PagedColumn(modifier = Modifier.height(pageHeight())) {
        item { Body(stringResource(RFrontitude.string.chess_about_what)) }
        item { Body(stringResource(RFrontitude.string.chess_about_licence)) }
        item { Body(stringResource(RFrontitude.string.chess_about_engine)) }
        item {
            Link(stringResource(RFrontitude.string.chess_about_source)) {
                context.openWebPage("https://github.com/wanderwildwood/MuditaOS-K-Chess-opensource")
            }
        }
        item {
            Section(stringResource(RFrontitude.string.chess_about_privacy_title), stringResource(RFrontitude.string.chess_about_privacy))
        }
        item {
            Section(stringResource(RFrontitude.string.chess_about_terms_title), stringResource(RFrontitude.string.chess_about_terms))
        }
        item { Link(stringResource(RFrontitude.string.chess_about_licences_button), onClick = onOpenLicences) }
        item { Llama() }
    }
    Spacer(Modifier.height(10.dp))
    CloseButton(stringResource(RFrontitude.string.chess_about_close), onDismiss)
}

@Composable
private fun ColumnScope.LicencesPage(onBack: () -> Unit) {
    val context = LocalContext.current
    val gpl = remember { context.licenceParagraphs() }
    TextMMD(
        text = stringResource(RFrontitude.string.chess_licences_title),
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
    PagedColumn(modifier = Modifier.height(pageHeight())) {
        item {
            Section(stringResource(RFrontitude.string.chess_licences_app_title), stringResource(RFrontitude.string.chess_licences_app))
        }
        item {
            Section(stringResource(RFrontitude.string.chess_licences_apache_title), stringResource(RFrontitude.string.chess_licences_apache))
        }
        item {
            Section(stringResource(RFrontitude.string.chess_licences_font_title), stringResource(RFrontitude.string.chess_licences_font))
        }
        item { Heading(stringResource(RFrontitude.string.chess_licences_gpl_title)) }
        items(gpl) { paragraph -> Body(paragraph) }
    }
    Spacer(Modifier.height(10.dp))
    CloseButton(stringResource(RFrontitude.string.chess_about_back), onBack)
}

@Composable
private fun CloseButton(label: String, onClick: () -> Unit) {
    OutlinedButtonMMD(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        TextMMD(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Link(text: String, onClick: () -> Unit) {
    TextMMD(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

/**
 * A llama at the foot of the About, which opens the page a donation goes to. The site's address
 * sits at the start of the same line and opens the site; the llama and its words open the page.
 *
 * Straight to the checkout: the Donate button on the site only leads there anyway. The short
 * square.link form, which is what the site itself links to, so a regenerated checkout follows it.
 * A phone with nothing that opens a web address says so rather than doing nothing.
 */
@Composable
private fun Llama() {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextMMD(
            text = "wanderthe.dev",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clickable { context.openWebPage("https://wanderthe.dev") }
                .padding(vertical = 12.dp)
        )
        Spacer(Modifier.width(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .clickable { context.openWebPage("https://square.link/u/AGu8oT10") }
        ) {
            Image(
                painter = painterResource(R.drawable.llama),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(6.dp))
            TextMMD(
                text = stringResource(RFrontitude.string.chess_about_feed_the_llamas),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

private fun Context.versionName(): String =
    packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
