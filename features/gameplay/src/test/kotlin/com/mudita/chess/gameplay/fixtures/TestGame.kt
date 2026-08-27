package com.mudita.chess.gameplay.fixtures

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.gameplay.GameplayMapper
import com.mudita.chess.gameplay.GameplayUiEvents
import com.mudita.chess.gameplay.game.ChessBoard
import com.mudita.chess.gameplay.game.ComputerParticipant
import com.mudita.chess.gameplay.game.Game
import com.mudita.chess.gameplay.game.GameFactory
import com.mudita.chess.gameplay.game.MoveResultNotifier
import com.mudita.chess.gameplay.game.PlayerParticipant
import com.mudita.chess.gameplay.usecase.GetComputerMoveUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

internal class TestGame(
    val mapper: GameplayMapper,
    val computerMoveUseCase: GetComputerMoveUseCase = defaultGetComputerMoveMock(),
) {

    var startingFen: String? = null
    var playedMoves: List<Move>? = null
    var dispatcher: CoroutineDispatcher? = null

    fun playerVsComputerFactory(): GameFactory = mockk {
        every { createPlayerVsComputer(any(), any(), any()) } answers {
            createPlayerVsComputerGame(
                playerSide = args[0] as Side,
                isPiecesPositionReady = args[1] as Boolean,
                uiEvents = args[2] as GameplayUiEvents
            )
        }
    }

    fun twoPlayerLocalFactory(): GameFactory = mockk {
        every { createTwoPlayerLocal(any(), any()) } answers {
            createTwoPlayerLocalGame(
                isPiecesPositionReady = args[0] as Boolean,
                uiEvents = args[1] as GameplayUiEvents
            )
        }
    }

    private fun createPlayerVsComputerGame(
        playerSide: Side,
        isPiecesPositionReady: Boolean,
        uiEvents: GameplayUiEvents
    ): Game {
        val computerSide = playerSide.flip()
        val board = ChessBoard(
            topParticipantSide = computerSide,
            isPiecesPositionReady = isPiecesPositionReady
        )
        val moveResultNotifier = MoveResultNotifier(board, uiEvents)
        startingFen?.let(board::loadFen)
        playedMoves?.map { it.toString() }?.let(board::loadMoves)
        val participants = listOf(
            PlayerParticipant(playerSide, board, moveResultNotifier, uiEvents, mapper),
            ComputerParticipant(computerSide, board, moveResultNotifier, mockk(relaxed = true), computerMoveUseCase)
        ).associateBy { it.side }
        return Game(
            board = board,
            whiteParticipant = participants.getValue(WHITE),
            blackParticipant = participants.getValue(BLACK),
            ioDispatcher = dispatcher ?: UnconfinedTestDispatcher()
        )
    }

    private fun createTwoPlayerLocalGame(
        isPiecesPositionReady: Boolean,
        uiEvents: GameplayUiEvents
    ): Game {
        val board = ChessBoard(
            topParticipantSide = BLACK,
            isPiecesPositionReady = isPiecesPositionReady
        )
        val moveResultNotifier = MoveResultNotifier(board, uiEvents)
        startingFen?.let(board::loadFen)
        playedMoves?.map { it.toString() }?.let(board::loadMoves)
        val participants = listOf(
            PlayerParticipant(WHITE, board, moveResultNotifier, uiEvents, mapper),
            PlayerParticipant(BLACK, board, moveResultNotifier, uiEvents, mapper)
        ).associateBy { it.side }
        return Game(
            board = board,
            whiteParticipant = participants.getValue(WHITE),
            blackParticipant = participants.getValue(BLACK),
            ioDispatcher = dispatcher ?: UnconfinedTestDispatcher()
        )
    }

    private companion object {
        fun defaultGetComputerMoveMock(): GetComputerMoveUseCase = mockk {
            coEvery { this@mockk.invoke(any(), any(), any()) } coAnswers {
                neverReturningCoroutine()
            }
        }
    }
}
