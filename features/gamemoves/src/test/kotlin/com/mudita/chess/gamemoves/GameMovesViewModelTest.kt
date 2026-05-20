package com.mudita.chess.gamemoves

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.coroutines.MainDispatcherExtension
import com.mudita.chess.gamemoves.GameMovesUiEvent.BackClicked
import com.mudita.chess.gamemoves.GameMovesUiEvent.NavigationUpClicked
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.navigation.NavAction.NavigateUp
import com.mudita.chess.navigation.routes.GameMovesRoute
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi.D5
import com.mudita.chess.ui.model.PositionUi.D7
import com.mudita.chess.ui.model.PositionUi.F3
import com.mudita.chess.ui.model.PositionUi.G1
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
internal class GameMovesViewModelTest {

    private val args: GameMoveRouteProvider = mockk {
        every { value } returns GameMovesRoute(
            moves = listOf(
                MoveArg("N", "g1f3"),
                MoveArg("p", "d7d5")
            )
        )
    }
    private val mapper = GameMovesMapper()
    private val tested by lazy { GameMovesViewModel(args, mapper) }

    @Test
    fun `show moves list on init`() = runTest {
        tested.uiStates.test {
            val state = awaitItem()
            assertThat(state).isEqualTo(
                GameMovesUiState(
                    listOf(
                        MoveUi(pieceUi = PieceUi(PAWN, isWhite = false), from = D7, to = D5),
                        MoveUi(pieceUi = PieceUi(KNIGHT, isWhite = true), from = G1, to = F3),
                    )
                )
            )
        }
    }

    @Test
    fun `NavigationUpClicked navigates up`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(NavigationUpClicked)

            assertThat(awaitItem()).isEqualTo(NavigateUp())
        }
    }

    @Test
    fun `BackClicked navigates up`() = runTest {
        tested.navActions.test {
            tested.handleUiEvent(BackClicked)

            assertThat(awaitItem()).isEqualTo(NavigateUp())
        }
    }
}
