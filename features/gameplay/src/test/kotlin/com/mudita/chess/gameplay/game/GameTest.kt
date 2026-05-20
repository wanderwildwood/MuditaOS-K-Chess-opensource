package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameplay.fixtures.blackParticipantWonPgn
import com.mudita.chess.gameplay.fixtures.insufficientMaterialDrawPgn
import com.mudita.chess.gameplay.fixtures.whiteParticipantWonPgn
import com.mudita.chess.gameplay.fixtures.neverReturningCoroutine
import com.mudita.chess.gameplay.fixtures.toMovesLAN
import com.mudita.chess.gameplay.game.GameStatus.BLACK_WON
import com.mudita.chess.gameplay.game.GameStatus.CREATED
import com.mudita.chess.gameplay.game.GameStatus.DESTROYED
import com.mudita.chess.gameplay.game.GameStatus.DRAW
import com.mudita.chess.gameplay.game.GameStatus.PAUSED
import com.mudita.chess.gameplay.game.GameStatus.RESIGNED
import com.mudita.chess.gameplay.game.GameStatus.STARTED
import com.mudita.chess.gameplay.game.GameStatus.STOPPED
import com.mudita.chess.gameplay.game.GameStatus.WHITE_WON
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class GameTest {

    private val board = mockk<ChessBoard> {
        every { sideToMove } returns WHITE
        every { isEndgame } returns false
        every { isMate } returns false
        every { isDraw } returns false
        every { loadMoves(any()) } returns Unit
    }

    private val whiteParticipant = mockk<Participant>(relaxed = true) {
        coEvery { doMove() } coAnswers { neverReturningCoroutine() }
    }
    private val blackParticipant = mockk<Participant>(relaxed = true) {
        coEvery { doMove() } coAnswers { neverReturningCoroutine() }
    }

    private val tested = Game(board, whiteParticipant, blackParticipant, UnconfinedTestDispatcher())


    @Test
    fun `setup forwards game options to participants`() = runTest {
        val gameOptions = GameOptions(
            isMoveSuggestionsOn = false,
            isPlayerWhite = true,
            difficultyLevel = DifficultyLevel(5)
        )
        val tested = Game(mockk(), whiteParticipant, blackParticipant, UnconfinedTestDispatcher())

        tested.setup(gameOptions)

        coVerify { whiteParticipant.setup(gameOptions) }
        coVerify { blackParticipant.setup(gameOptions) }
    }

    @Test
    fun `start starts a game when game is created`() = runTest {
        val started = tested.start()

        assertThat(started).isTrue()
        assertThat(tested.status).isEqualTo(STARTED)
    }

    @Test
    fun `stop stops a game when game is started`() = runTest {
        tested.start()

        val stopped = tested.stop()

        assertThat(stopped).isTrue()
        assertThat(tested.status).isEqualTo(STOPPED)
    }

    @Test
    fun `stop do nothing when game is not started`() = runTest {
        val stopped = tested.stop()

        assertThat(stopped).isFalse()
        assertThat(tested.status).isEqualTo(CREATED)
    }

    @Test
    fun `start starts a game when game is stopped`() = runTest {
        tested.start()
        tested.stop()

        val started = tested.start()

        assertThat(started).isTrue()
        assertThat(tested.status).isEqualTo(STARTED)
    }

    @Test
    fun `resign ends a game when game is created`() = runTest {
        tested.resign()

        assertThat(tested.status).isEqualTo(RESIGNED)
    }

    @Test
    fun `resign ends a game when game is started`() = runTest {
        tested.start()

        tested.resign()

        assertThat(tested.status).isEqualTo(RESIGNED)
    }

    @Test
    fun `resign ends a game when game is stopped`() = runTest {
        tested.start()
        tested.stop()

        tested.resign()

        assertThat(tested.status).isEqualTo(RESIGNED)
    }

    @Test
    fun `start do nothing when game is resigned`() = runTest {
        tested.resign()

        val started = tested.start()

        assertThat(started).isFalse()
        assertThat(tested.status).isEqualTo(RESIGNED)
    }

    @Test
    fun `stop do nothing when game is resigned`() = runTest {
        tested.start()
        tested.resign()

        val stopped = tested.stop()

        assertThat(stopped).isFalse()
        assertThat(tested.status).isEqualTo(RESIGNED)
    }

    @Test
    fun `cleanup cleanups participants and destroys game`() = runTest {
        tested.cleanup()

        coVerify { whiteParticipant.cleanup() }
        coVerify { blackParticipant.cleanup() }
        assertThat(tested.status).isEqualTo(DESTROYED)
    }

    @Test
    fun `start do nothing when game is destroyed`() = runTest {
        tested.cleanup()

        val started = tested.start()

        assertThat(started).isFalse()
        assertThat(tested.status).isEqualTo(DESTROYED)
    }

    @Test
    fun `stop do nothing when game is destroyed`() = runTest {
        tested.cleanup()

        val stopped = tested.stop()

        assertThat(stopped).isFalse()
        assertThat(tested.status).isEqualTo(DESTROYED)
    }

    @Test
    fun `resign do nothing when game is destroyed`() = runTest {
        tested.cleanup()

        tested.resign()

        assertThat(tested.status).isEqualTo(DESTROYED)
    }

    @Test
    fun `pause pauses a game when game is started`() = runTest {
        tested.start()

        tested.pause()

        assertThat(tested.status).isEqualTo(PAUSED)
    }

    @Test
    fun `pause do nothing when game is not started`() = runTest {
        tested.pause()

        assertThat(tested.status).isEqualTo(CREATED)
    }

    @Test
    fun `startIfPaused starts a game when game is paused`() = runTest {
        tested.start()
        tested.pause()

        tested.startIfPaused()

        assertThat(tested.status).isEqualTo(STARTED)
    }

    @Test
    fun `startIfPaused do nothing when game is stopped`() = runTest {
        tested.start()
        tested.stop()

        tested.startIfPaused()

        assertThat(tested.status).isEqualTo(STOPPED)
    }

    @Test
    fun `load moves results in draw`() = runTest {
        every { board.isEndgame } returns true
        every { board.isDraw } returns true

        tested.loadMoves(toMovesLAN(insufficientMaterialDrawPgn))

        assertThat(tested.status).isEqualTo(DRAW)
    }

    @Test
    fun `load moves that leads to checkmate results in white won`() = runTest {
        every { board.isEndgame } returns true
        every { board.isMate } returns true

        tested.loadMoves(toMovesLAN(whiteParticipantWonPgn))

        assertThat(tested.status).isEqualTo(WHITE_WON)
    }

    @Test
    fun `load moves that leads to checkmate results in black won`() = runTest {
        every { board.isEndgame } returns true
        every { board.isMate } returns true
        every { board.sideToMove } returns BLACK

        tested.loadMoves(toMovesLAN(blackParticipantWonPgn))

        assertThat(tested.status).isEqualTo(BLACK_WON)
    }
}
