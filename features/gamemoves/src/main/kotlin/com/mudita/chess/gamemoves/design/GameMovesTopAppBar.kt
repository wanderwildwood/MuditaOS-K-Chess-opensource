package com.mudita.chess.gamemoves.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mudita.chess.gamemoves.GameMovesUiEvent
import com.mudita.chess.gamemoves.GameMovesUiEvent.NavigationUpClicked
import com.mudita.chess.ui.design.AppNavigationIcon
import com.mudita.chess.ui.design.AppTopAppBar
import com.mudita.chess.frontitude.R as RFrontitude

@Composable
internal fun GameMovesTopAppBar(uiEvent: (GameMovesUiEvent) -> Unit) {
    AppTopAppBar(
        title = stringResource(id = RFrontitude.string.chess_listofmoves_screentitle_listofmoves),
        navigationIcon = AppNavigationIcon.CLOSE,
        onNavigationIconClick = { uiEvent(NavigationUpClicked) }
    )
}
