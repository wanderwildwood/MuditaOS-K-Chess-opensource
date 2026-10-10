package com.mudita.chess.ui.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mudita.mmd.ThemeMMD
import com.mudita.mmd.black
import com.mudita.mmd.components.buttons.ButtonDefaultsMMD
import com.mudita.mmd.components.buttons.ButtonMMD
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.switcher.SwitchMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD
import com.mudita.mmd.white
import kotlinx.coroutines.delay
import com.mudita.chess.frontitude.R as RFrontitude

/**
 * App-level design system, built on the public com.mudita:MMD library (Mudita Mindful Design).
 * Replaces the private com.mudita:kompakt-ui artifact this app previously depended on, which is
 * not publicly resolvable outside Mudita's own infrastructure.
 */
@Composable
fun AppTheme(content: @Composable () -> Unit) = ThemeMMD(colorScheme = monochrome, content = content)

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
    val spaceBetweenIconAndText: Dp = 8.dp,
    /** Keep the label on one line and shrink it to fit, for buttons with a fixed height. */
    val isLabelShrunkToFit: Boolean = false
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
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = size.contentPadding ?: ButtonDefaultsMMD.contentPadding
    ) {
        ButtonLabel(text, size)
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
        ButtonLabel(text, attributes)
    }
}

/**
 * A button for something that cannot be taken back. The first tap arms it, and it says so in its
 * own face rather than stacking a dialog over the game; a second tap does it. Left alone for four
 * seconds it disarms, so a stray tap leaves nothing live for whoever picks the phone up next.
 */
@Composable
fun AppArmedSecondaryButton(
    text: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    attributes: AppButtonAttributes = AppButtonAttributes()
) {
    var isArmed by remember { mutableStateOf(false) }
    LaunchedEffect(isArmed) {
        if (isArmed) {
            delay(ARMED_TIMEOUT_MILLIS)
            isArmed = false
        }
    }
    AppSecondaryButton(
        modifier = modifier,
        text = if (isArmed) stringResource(RFrontitude.string.chess_common_button_tapagain, text) else text,
        attributes = attributes.copy(isLabelShrunkToFit = true),
        onClick = {
            if (isArmed) {
                isArmed = false
                onConfirm()
            } else {
                isArmed = true
            }
        }
    )
}

private const val ARMED_TIMEOUT_MILLIS = 4_000L

@Composable
private fun ButtonLabel(text: String, attributes: AppButtonAttributes) {
    val style = attributes.textStyle ?: MaterialTheme.typography.labelLarge
    if (attributes.isLabelShrunkToFit) {
        BasicText(
            text = text,
            style = style.merge(color = LocalContentColor.current),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = style.fontSize)
        )
    } else {
        Text(text = text, style = style)
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
    // The padding is touch area, so the click goes on the outside of it: callers draw a border
    // around the whole padded box, and a tap anywhere inside that border has to count. Ripple is
    // already disabled app-wide by ThemeMMD.
    Box(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick)
            .padding(touchAreaPadding)
            .size(48.dp),
        contentAlignment = Alignment.Center
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
    onCheckedChange: ((Boolean) -> Unit)?,
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

/**
 * The frame a card on the game screen sits in: the same rim and corner as [EInkDialog], so a
 * card drawn into the layout and a dialog in its own window read as one thing.
 */
fun Modifier.eInkFrame(): Modifier = composed {
    val shape = RoundedCornerShape(12.dp)
    clip(shape)
        .border(2.dp, MaterialTheme.colorScheme.onSurface, shape)
        .background(MaterialTheme.colorScheme.surface)
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
            .eInkFrame()
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
