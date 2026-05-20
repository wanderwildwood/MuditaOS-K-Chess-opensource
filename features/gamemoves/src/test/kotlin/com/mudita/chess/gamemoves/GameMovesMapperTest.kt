package com.mudita.chess.gamemoves

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gamemoves.model.MoveUi
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.PAWN
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi.D5
import com.mudita.chess.ui.model.PositionUi.D7
import com.mudita.chess.ui.model.PositionUi.E1
import com.mudita.chess.ui.model.PositionUi.E2
import com.mudita.chess.ui.model.PositionUi.F3
import com.mudita.chess.ui.model.PositionUi.G1
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GameMovesMapperTest {

    private val tested = GameMovesMapper()

    @Test
    fun `toMovesUi returns moves in order from most recent to least recent`() {
        val moveArgs = listOf(
            MoveArg("N", "g1f3"),
            MoveArg("p", "d7d5")
        )

        val result = tested.toMovesUi(moveArgs)

        assertThat(result).isEqualTo(
            listOf(
                MoveUi(pieceUi = PieceUi(PAWN, isWhite = false), from = D7, to = D5),
                MoveUi(pieceUi = PieceUi(KNIGHT, isWhite = true), from = G1, to = F3),
            )
        )
    }

    @Test
    fun `toMovesUi maps promotion piece if available as piece to display`() {
        val moveArgs = listOf(
            MoveArg("p", "e2e1q")
        )

        val result = tested.toMovesUi(moveArgs)

        assertThat(result).isEqualTo(
            listOf(
                MoveUi(pieceUi = PieceUi(QUEEN, isWhite = true), from = E2, to = E1),
            )
        )
    }

    @Test
    fun `toMovesUi throws for corrupter input`() {
        val moveArgs = listOf(
            MoveArg(".", "e2e1")
        )

        assertThrows<IllegalStateException> {
            tested.toMovesUi(moveArgs)

        }
    }
}
