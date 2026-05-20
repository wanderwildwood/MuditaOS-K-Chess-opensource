package com.mudita.chess.gamemoves.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mudita.chess.gamemoves.GameMovesUiEvent
import com.mudita.chess.gamemoves.GameMovesUiEvent.NavigationUpClicked
import com.mudita.kompakt.commonUi.components.appBar.KompaktTopAppBar
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.kompakt.commonUi.R as RCommonUi

@Composable
internal fun GameMovesTopAppBar(uiEvent: (GameMovesUiEvent) -> Unit) {
    KompaktTopAppBar(
        title = stringResource(id = RFrontitude.string.chess_listofmoves_screentitle_listofmoves),
        navigationIconResId = RCommonUi.drawable.close,
        onNavigationIconClick = { uiEvent(NavigationUpClicked) }
    )
}
