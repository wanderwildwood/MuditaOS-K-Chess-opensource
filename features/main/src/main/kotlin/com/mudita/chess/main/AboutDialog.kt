package com.mudita.chess.main

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mudita.chess.ui.design.EInkDialog
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * What this is, whose it was first, what it carries, and where the source lives.
 *
 * Most of this app is Mudita's own Chess for the Kompakt, and the engine is Stockfish's; both are
 * named here because somebody who installed the APK and never saw the repository has no other
 * way to learn whose work it is, or that they have the source under the GPL.
 */
@Composable
internal fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember { context.versionName() }
    EInkDialog(onDismiss = onDismiss) {
        TextMMD(
            text = stringResource(RFrontitude.string.chess_about_title, versionName),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(14.dp))
        TextMMD(text = stringResource(RFrontitude.string.chess_about_what), style = MaterialTheme.typography.labelSmall)

        Spacer(Modifier.height(14.dp))
        TextMMD(text = stringResource(RFrontitude.string.chess_about_licence), style = MaterialTheme.typography.labelSmall)
        TextMMD(text = stringResource(RFrontitude.string.chess_about_engine), style = MaterialTheme.typography.labelSmall)

        Spacer(Modifier.height(14.dp))
        TextMMD(
            text = "github.com/wanderwildwood/MuditaOS-K-Chess-opensource",
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(Modifier.height(14.dp))
        Llama()

        Spacer(Modifier.height(18.dp))
        OutlinedButtonMMD(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            TextMMD(text = stringResource(RFrontitude.string.chess_about_close), style = MaterialTheme.typography.bodySmall)
        }
    }
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
                .clickable { context.openWebPage("https://wanderthe.dev") }
                .padding(vertical = 4.dp)
        )
        Spacer(Modifier.width(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { context.openWebPage("https://square.link/u/AGu8oT10") }
                .padding(vertical = 4.dp)
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

private fun Context.openWebPage(address: String) {
    runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, address.toUri()))
    }.onFailure {
        Toast.makeText(this, getString(RFrontitude.string.chess_about_no_browser), Toast.LENGTH_SHORT).show()
    }
}

private fun Context.versionName(): String =
    packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
