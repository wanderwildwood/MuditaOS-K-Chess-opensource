package com.mudita.chess.gamemoves

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mudita.chess.gamemoves.GameMovesUiEvent.BackClicked
import com.mudita.chess.gamemoves.design.GameMoveListItem
import com.mudita.chess.gamemoves.design.GameMovesTopAppBar
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.navigation.AppNavigator
import com.mudita.chess.navigation.NavActionsEffect
import com.mudita.chess.ui.KompaktPreview
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi.C1
import com.mudita.chess.ui.model.PositionUi.C2
import com.mudita.chess.ui.model.PositionUi.D2
import com.mudita.chess.ui.model.PositionUi.D3
import com.mudita.chess.ui.model.PositionUi.E2
import com.mudita.chess.ui.model.PositionUi.E4
import com.mudita.kompakt.commonUi.KompaktTheme
import com.mudita.kompakt.commonUi.components.DashedHorizontalDivider
import com.mudita.kompakt.commonUi.components.fakeScroll.KompaktLazyFakeScroll
import org.koin.androidx.compose.koinViewModel

private const val MOVES_ON_PAGE = 7

@Composable
fun GameMoves(navigator: AppNavigator) {
    GameMovesInternal(
        viewModel = koinViewModel(),
        navigator = navigator
    )
}

@Composable
internal fun GameMovesInternal(
    viewModel: GameMovesViewModel,
    navigator: AppNavigator
) {
    val uiState by viewModel.uiStates.collectAsState()
    GameMovesScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
    BackHandler {
        viewModel.handleUiEvent(BackClicked)
    }
    NavActionsEffect(
        actions = viewModel.navActions,
        navigator = navigator
    )
}

@Composable
private fun GameMovesScreen(
    uiState: GameMovesUiState,
    uiEvent: (GameMovesUiEvent) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = { GameMovesTopAppBar(uiEvent) }
    ) { contentPadding ->
        KompaktLazyFakeScroll(
            step = MOVES_ON_PAGE,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            items(uiState.moves) { moveUi ->
                GameMoveListItem(moveUi = moveUi)
                DashedHorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@KompaktPreview
@Composable
private fun GameMovesScreenPreview() {
    KompaktTheme {
        GameMovesScreen(
            uiState = GameMovesUiState(
                listOf(
                    MoveUi(pieceUi = PieceUi(type = PAWN, isWhite = true), from = E2, to = E4),
                    MoveUi(pieceUi = PieceUi(type = PAWN, isWhite = false), from = C1, to = C2),
                    MoveUi(pieceUi = PieceUi(type = BISHOP, isWhite = true), from = D2, to = D3),
                    MoveUi(pieceUi = PieceUi(type = BISHOP, isWhite = false), from = C1, to = C2),
                    MoveUi(pieceUi = PieceUi(type = KING, isWhite = true), from = D2, to = D3),
                    MoveUi(pieceUi = PieceUi(type = KING, isWhite = false), from = E2, to = E4),
                    MoveUi(pieceUi = PieceUi(type = QUEEN, isWhite = true), from = D2, to = D3),
                    MoveUi(pieceUi = PieceUi(type = QUEEN, isWhite = false), from = E2, to = E4),
                    MoveUi(pieceUi = PieceUi(type = ROOK, isWhite = true), from = D2, to = D3),
                    MoveUi(pieceUi = PieceUi(type = ROOK, isWhite = false), from = E2, to = E4)
                )
            ),
            uiEvent = {}
        )
    }
}
