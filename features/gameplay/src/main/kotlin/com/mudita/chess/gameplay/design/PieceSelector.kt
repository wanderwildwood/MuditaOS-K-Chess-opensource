package com.mudita.chess.gameplay.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mudita.chess.ui.R

@Composable
internal fun PieceSelector(
    isWhite: Boolean,
    modifier: Modifier = Modifier
) {
    val selectorColor = if (isWhite) {
        R.drawable.selector_white
    } else {
        R.drawable.selector_black
    }

    Image(
        modifier = modifier.size(38.dp),
        painter = painterResource(id = selectorColor),
        contentDescription = null
    )
}
