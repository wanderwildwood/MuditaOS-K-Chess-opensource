package com.mudita.chess.gameplay.game

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameplay.fixtures.FIFTY_FIFTY_RULE_DRAW_FEN
import com.mudita.chess.gameplay.fixtures.INSUFFICIENT_MATERIAL_DRAW_FEN
import com.mudita.chess.gameplay.fixtures.STALEMATE_FEN
import com.mudita.chess.gameplay.game.DrawReason.FIFTY_MOVES
import com.mudita.chess.gameplay.game.DrawReason.INSUFFICIENT_MATERIAL
import com.mudita.chess.gameplay.game.DrawReason.REPETITION
import com.mudita.chess.gameplay.game.DrawReason.STALEMATE
import com.github.bhlangonijr.chesslib.Side.WHITE
import org.junit.jupiter.api.Test

internal class DrawReasonTest {

    @Test
    fun `repetition is named as repetition`() {
        assertThat(DrawReason.of(true, false, 4, false)).isEqualTo(REPETITION)
    }

    @Test
    fun `too few pieces is named as insufficient material`() {
        assertThat(DrawReason.of(false, true, 10, false)).isEqualTo(INSUFFICIENT_MATERIAL)
    }

    @Test
    fun `a hundred half moves is named as the fifty move rule`() {
        assertThat(DrawReason.of(false, false, 100, false)).isEqualTo(FIFTY_MOVES)
        assertThat(DrawReason.of(false, false, 205, false)).isEqualTo(FIFTY_MOVES)
    }

    @Test
    fun `ninety nine half moves is not yet a draw`() {
        assertThat(DrawReason.of(false, false, 99, false)).isNull()
    }

    @Test
    fun `no stalemate is named stalemate`() {
        assertThat(DrawReason.of(false, false, 3, true)).isEqualTo(STALEMATE)
    }

    @Test
    fun `reasons are tested in the order chesslib tests them`() {
        assertThat(DrawReason.of(true, true, 100, true)).isEqualTo(REPETITION)
        assertThat(DrawReason.of(false, true, 100, true)).isEqualTo(INSUFFICIENT_MATERIAL)
        assertThat(DrawReason.of(false, false, 100, true)).isEqualTo(FIFTY_MOVES)
    }

    @Test
    fun `nothing is named when nothing applies`() {
        assertThat(DrawReason.of(false, false, 0, false)).isNull()
    }

    // The same four, read from real positions on a real board.

    @Test
    fun `a board in the stalemate position reports stalemate`() {
        val board = ChessBoard(topParticipantSide = WHITE).apply { loadFen(STALEMATE_FEN) }

        assertThat(board.drawReason).isEqualTo(STALEMATE)
    }

    @Test
    fun `a board past fifty moves reports the fifty move rule`() {
        val board = ChessBoard(topParticipantSide = WHITE).apply { loadFen(FIFTY_FIFTY_RULE_DRAW_FEN) }

        assertThat(board.drawReason).isEqualTo(FIFTY_MOVES)
    }

    @Test
    fun `a board with a lone knight reports insufficient material`() {
        val board = ChessBoard(topParticipantSide = WHITE).apply { loadFen(INSUFFICIENT_MATERIAL_DRAW_FEN) }

        assertThat(board.drawReason).isEqualTo(INSUFFICIENT_MATERIAL)
    }

    @Test
    fun `a position seen three times reports repetition`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        val shuffle = listOf("g1f3", "g8f6", "f3g1", "f6g8")

        board.loadMoves(shuffle + shuffle)

        assertThat(board.isDraw).isTrue()
        assertThat(board.drawReason).isEqualTo(REPETITION)
    }

    @Test
    fun `an ordinary position has no draw reason`() {
        val board = ChessBoard(topParticipantSide = WHITE)
        board.loadMoves(listOf("e2e4", "e7e5"))

        assertThat(board.drawReason).isNull()
    }
}
