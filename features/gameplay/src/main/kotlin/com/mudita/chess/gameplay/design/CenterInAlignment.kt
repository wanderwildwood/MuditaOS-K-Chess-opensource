package com.mudita.chess.gameplay.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Suppress("FunctionNaming", "ktlint:standard:function-naming")
fun Alignment.Companion.CenterIn(bounds: Rect): Alignment = CenterInAlignment(bounds)

@Immutable
private data class CenterInAlignment(
    val boardBounds: Rect
) : Alignment {
    override fun align(
        size: IntSize,
        space: IntSize,
        layoutDirection: LayoutDirection
    ): IntOffset {
        val x = boardBounds.left + (boardBounds.width - size.width) / 2f
        val y = boardBounds.top + (boardBounds.height - size.height) / 2f
        return IntOffset(x.roundToInt(), y.roundToInt())
    }
}
