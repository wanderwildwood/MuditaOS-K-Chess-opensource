package com.mudita.chess.gameplay.usecase

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.Square.C5
import com.github.bhlangonijr.chesslib.Square.C7
import com.github.bhlangonijr.chesslib.Square.D2
import com.github.bhlangonijr.chesslib.Square.D4
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.engine.ChessEngine
import com.mudita.chess.engine.UCIOptions
import com.mudita.chess.gameoptions.mapper.elo
import com.mudita.chess.gameoptions.model.DifficultyLevel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GetComputerMoveUseCaseTest {

    private val engine = mockk<ChessEngine>(relaxed = true)

    private val tested = GetComputerMoveUseCase(engine, UnconfinedTestDispatcher())

    @Test
    fun `runs move calculation and returns simple move`() = runTest {
        val difficultyLevel = DifficultyLevel(3)
        val earlierMoves = listOf(Move(D2, D4))
        coEvery { engine.calculateBestMove(any()) } returns "c7c5"

        val move = tested(earlierMoves, Side.BLACK, difficultyLevel)

        assertThat(move).isEqualTo(Move(C7, C5))
        coVerify {
            engine.setOptions(
                mapOf(
                    UCIOptions.LIMIT_STRENGTH to true,
                    UCIOptions.ELO to difficultyLevel.elo()
                )
            )
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "A7,A8,q,WHITE,WHITE_QUEEN",
            "A7,A8,r,WHITE,WHITE_ROOK",
            "A7,A8,b,WHITE,WHITE_BISHOP",
            "A7,A8,n,WHITE,WHITE_KNIGHT",
            "H2,H1,q,BLACK,BLACK_QUEEN",
            "H2,H1,r,BLACK,BLACK_ROOK",
            "H2,H1,b,BLACK,BLACK_BISHOP",
            "H2,H1,n,BLACK,BLACK_KNIGHT",
        ]
    )
    fun `runs move calculation and returns move with promotion`(
        from: Square,
        to: Square,
        promotionFenSymbol: String,
        movingSide: Side,
        piece: Piece
    ) = runTest {
        val difficultyLevel = DifficultyLevel(3)
        val moveLAN = "${(from.name + to.name).lowercase()}$promotionFenSymbol"
        coEvery { engine.calculateBestMove(any()) } returns moveLAN

        val move = tested(listOf(), movingSide, difficultyLevel)

        assertThat(move).isEqualTo(Move(from, to, piece))
        coVerify {
            engine.setOptions(
                mapOf(
                    UCIOptions.LIMIT_STRENGTH to true,
                    UCIOptions.ELO to difficultyLevel.elo()
                )
            )
        }
    }
}
