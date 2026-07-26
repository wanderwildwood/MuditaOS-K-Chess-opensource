package com.mudita.chess.ui.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.mmd.ThemeMMD
import com.mudita.mmd.black
import com.mudita.mmd.components.buttons.ButtonMMD
import com.mudita.mmd.components.buttons.ButtonDefaultsMMD
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.switcher.SwitchMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.mudita.mmd.eInkColorScheme
import com.mudita.mmd.white

/**
 * This app's screens use `primaryContainer` as their main page background color (e.g.
 * `Scaffold(containerColor = MaterialTheme.colorScheme.primaryContainer)`), but MMD's own
 * eInkColorScheme assigns that role a black "ink accent" tone (meant for filled buttons, not a
 * full-page canvas). Overriding it back to a light page/black-ink pairing keeps every existing
 * MaterialTheme.colorScheme.* call site correct without having to touch each screen individually.
 */
private val appColorScheme = eInkColorScheme.copy(
    primaryContainer = white,
    onPrimaryContainer = black
)

/**
 * App-level design system, built on the public com.mudita:MMD library (Mudita Mindful Design).
 * Replaces the private com.mudita:kompakt-ui artifact this app previously depended on, which is
 * not publicly resolvable outside Mudita's own infrastructure.
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) = ThemeMMD(colorScheme = appColorScheme, content = content)

val appColorBlack: Color = black
val appColorWhite: Color = white

/** Heavier-weight text roles, mirrors the sizes MMD's eInkTypography defines for each role. */
object AppTypography900 {
    val titleLarge: TextStyle @Composable get() = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
    val titleMedium: TextStyle @Composable get() = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
    val labelLarge: TextStyle @Composable get() = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
    val labelMedium: TextStyle @Composable get() = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
    val labelSmall: TextStyle @Composable get() = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
    val displaySmall: TextStyle @Composable get() = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black)
}

/** Medium-weight text roles. */
object AppTypography500 {
    val bodyMedium: TextStyle @Composable get() = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
    val labelSmall: TextStyle @Composable get() = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
    val displaySmall: TextStyle @Composable get() = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium)
}

/**
 * Sizing/styling knobs for [AppPrimaryButton]/[AppSecondaryButton], equivalent to the previous
 * KompaktButtonAttributes.DynamicButton value object.
 */
data class AppButtonAttributes(
    val height: Dp? = null,
    val contentPadding: PaddingValues? = null,
    val cornerRadius: Dp = 8.dp,
    val textStyle: TextStyle? = null,
    val borderStrokeWidth: Dp = 2.dp,
    val iconSize: Dp = 24.dp,
    val spaceBetweenIconAndText: Dp = 8.dp
) {
    companion object {
        val Small = AppButtonAttributes(
            height = 32.dp,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        )
        val Large = AppButtonAttributes(
            height = 52.dp,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: AppButtonAttributes = AppButtonAttributes()
) {
    ButtonMMD(
        onClick = onClick,
        modifier = size.height?.let { modifier.height(it) } ?: modifier,
        shape = RoundedCornerShape(size.cornerRadius),
        contentPadding = size.contentPadding ?: ButtonDefaultsMMD.contentPadding
    ) {
        Text(text = text, style = size.textStyle ?: MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int? = null,
    attributes: AppButtonAttributes = AppButtonAttributes()
) {
    OutlinedButtonMMD(
        onClick = onClick,
        modifier = attributes.height?.let { modifier.height(it) } ?: modifier,
        shape = RoundedCornerShape(attributes.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(attributes.borderStrokeWidth, MaterialTheme.colorScheme.primary),
        contentPadding = attributes.contentPadding ?: ButtonDefaultsMMD.contentPadding
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(attributes.iconSize)
            )
            Spacer(modifier = Modifier.width(attributes.spaceBetweenIconAndText))
        }
        Text(text = text, style = attributes.textStyle ?: MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AppIconButton(
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    touchAreaPadding: PaddingValues = PaddingValues(0.dp)
) {
    // Ripple is already disabled app-wide by ThemeMMD, so a plain IconButton behaves like the
    // previous no-ripple KompaktIconButton.
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(touchAreaPadding)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SwitchMMD(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
    )
}

enum class AppNavigationIcon { BACK, CLOSE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: AppNavigationIcon = AppNavigationIcon.BACK,
    onNavigationIconClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val icon = when (navigationIcon) {
        AppNavigationIcon.BACK -> Icons.AutoMirrored.Filled.ArrowBack
        AppNavigationIcon.CLOSE -> Icons.Filled.Close
    }
    TopAppBarMMD(
        modifier = modifier,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onNavigationIconClick != null) {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        },
        actions = actions
    )
}

@Composable
fun AppHorizontalDivider(modifier: Modifier = Modifier) {
    HorizontalDividerMMD(modifier = modifier)
}

/**
 * A framed icon + title + description card with a confirm/cancel button pair, matching the visual
 * language of the app's other framed dialogs (see CheckInfoDialog/LoadingDialog/PawnPromotionDialog).
 * Replaces the previous KompaktModal(kompaktModalType = Confirm(...)).
 */
@Composable
fun AppConfirmCard(
    title: String,
    description: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    textAlignment: TextAlign = TextAlign.Start
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(3.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        if (icon != null) {
            Icon(painter = painterResource(id = icon), contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(text = title, textAlign = textAlignment, style = AppTypography900.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, textAlign = textAlignment, style = AppTypography500.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppSecondaryButton(
                modifier = Modifier.weight(1f),
                text = cancelText,
                onClick = onCancel
            )
            AppPrimaryButton(
                modifier = Modifier.weight(1f),
                text = confirmText,
                onClick = onConfirm
            )
        }
    }
}

@Composable
fun AppDashedHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    dashWidth: Dp = 6.dp,
    gapWidth: Dp = 4.dp
) {
    Canvas(modifier = modifier.height(thickness)) {
        val strokeWidthPx = thickness.toPx()
        drawLine(
            color = color,
            start = Offset(0f, strokeWidthPx / 2),
            end = Offset(size.width, strokeWidthPx / 2),
            strokeWidth = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
                phase = 0f
            )
        )
    }
}
