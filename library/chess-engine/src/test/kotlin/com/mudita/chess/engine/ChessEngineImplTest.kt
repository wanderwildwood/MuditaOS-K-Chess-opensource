package com.mudita.chess.engine

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.engine.ChessEngineTokens.BEST_MOVE_RESPONSE
import com.mudita.chess.engine.ChessEngineTokens.ENGINE_INTRO
import com.mudita.chess.engine.ChessEngineTokens.READY_OK
import com.mudita.chess.engine.ChessEngineTokens.SEARCH_RESPONSE
import com.mudita.chess.engine.ChessEngineTokens.UCI_RESPONSE
import com.mudita.chess.engine.UCICommands.NEW_GAME
import com.mudita.chess.engine.UCICommands.QUIT
import com.mudita.chess.engine.UCICommands.STOP
import com.mudita.chess.engine.UCICommands.UCI
import com.mudita.chess.engine.UCIOptions.HASH_SIZE
import com.mudita.chess.engine.UCIOptions.NET_PATH
import com.mudita.chess.engine.net.ChessEngineNet
import com.mudita.chess.engine.process.ChessEngineProcess
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.io.File

class ChessEngineImplTest {

    private val processLines = Channel<String>(Channel.BUFFERED)
    private val netFile = File("chess-engine/net/nn-3475407dc199.nnue")

    private val process = mockk<ChessEngineProcess>(relaxed = true) {
        every { start() } coAnswers {
            processLines.send(ENGINE_INTRO)
        }
        every { writeLine(UCI) } coAnswers {
            processLines.send(UCI_RESPONSE)
        }
        every { writeLine(UCICommands.setOption(HASH_SIZE, 64)) } just Runs
        every { writeLine(UCICommands.setOption(NET_PATH, netFile.absoluteFile)) } just Runs
        every { receiveLines() } returns processLines.receiveAsFlow()
    }
    private val net = mockk<ChessEngineNet> {
        every { load() } returns netFile
    }

    private val tested = ChessEngineImpl(process, net)

    @Test
    fun `start launches process and apply initial options`() = runTest {
        tested.start()

        tested.awaitReady()
        verifyOrder {
            process.start()
            process.writeLine(UCI)
            process.writeLine(UCICommands.setOption(HASH_SIZE, 64))
            process.writeLine(UCICommands.setOption(NET_PATH, netFile.absoluteFile))
        }
    }

    @Test
    fun `start launches process and skips net option if not found`() = runTest {
        every { net.load() } returns null

        tested.start()

        tested.awaitReady()
        verify(exactly = 0) {
            process.writeLine(match {
                it.startsWith("setoption name $NET_PATH")
            })
        }
    }

    @Test
    fun `start skips process launch and configuration process if already started`() = runTest {
        tested.start()
        tested.start()

        verify(exactly = 1) {
            process.start()
        }
    }

    @Test
    fun `setOptions applies all options to engine`() = runTest {
        tested.start()

        tested.setOptions(mapOf(UCIOptions.LIMIT_STRENGTH to true, UCIOptions.ELO to 500))

        verifyOrder {
            process.writeLine(UCICommands.setOption(UCIOptions.LIMIT_STRENGTH, true))
            process.writeLine(UCICommands.setOption(UCIOptions.ELO, 500))
        }
    }

    @Test
    fun `setOptions await engine ready before applying options to engine`() = runTest {
        val job = launch {
            tested.setOptions(mapOf(UCIOptions.LIMIT_STRENGTH to true, UCIOptions.ELO to 500))
        }
        tested.start()
        job.join()

        verifyOrder {
            process.writeLine(UCICommands.setOption(UCIOptions.LIMIT_STRENGTH, true))
            process.writeLine(UCICommands.setOption(UCIOptions.ELO, 500))
        }
    }

    @Test
    fun `newGame sends new game command to engine`() = runTest {
        every { process.writeLine(NEW_GAME) } coAnswers {
            processLines.send(READY_OK)
        }
        tested.start()

        tested.newGame()

        verifyOrder {
            process.writeLine(NEW_GAME)
        }
    }

    @Test
    fun `newGame await engine ready before sending new game command to engine`() = runTest {
        every { process.writeLine(NEW_GAME) } coAnswers {
            processLines.send(READY_OK)
        }

        val job = launch {
            tested.newGame()
        }
        tested.start()
        job.join()

        verifyOrder {
            process.writeLine(NEW_GAME)
        }
    }

    @Test
    fun `calculateBestMove applies position and sends go command to engine`() = runTest {
        every { process.writeLine(UCICommands.go()) } coAnswers {
            processLines.send(BEST_MOVE_RESPONSE)
        }
        tested.start()

        val move = tested.calculateBestMove(SearchOptions())

        assertThat(move).isEqualTo("e2e3")
        verifyOrder {
            process.writeLine(UCICommands.position(null, emptyList()))
            process.writeLine(UCICommands.go())
        }
    }

    @Test
    fun `calculateBestMove await engine ready before applying position and go command to engine`() = runTest {
        every { process.writeLine(UCICommands.go()) } coAnswers {
            processLines.send(BEST_MOVE_RESPONSE)
        }

        var move: String? = null
        val job = launch {
            move = tested.calculateBestMove(SearchOptions())
        }
        tested.start()
        job.join()

        assertThat(move).isEqualTo("e2e3")
        verifyOrder {
            process.writeLine(UCICommands.position(null, emptyList()))
            process.writeLine(UCICommands.go())
        }
    }

    @Test
    fun `calculateBestMove sends stop command to engine if calculation job cancelled`() = runTest {
        every { process.writeLine(UCICommands.go()) } coAnswers {
            processLines.send(SEARCH_RESPONSE.take(5))
        }
        every { process.writeLine(STOP) } coAnswers {
            processLines.send(BEST_MOVE_RESPONSE)
        }
        tested.start()
        val calculateJob = launch {
            val move = tested.calculateBestMove(SearchOptions())
            fail("Method returned $move event though calculation was cancelled")
        }
        val cancelJob = launch {
            calculateJob.cancelAndJoin()
        }
        cancelJob.join()

        tested.awaitReady()
    }

    @Test
    fun `cancelMoveCalculation sends stop command to engine`() = runTest {
        every { process.writeLine(UCICommands.go()) } coAnswers {
            processLines.send(SEARCH_RESPONSE)
        }
        every { process.writeLine(STOP) } coAnswers {
            processLines.send(BEST_MOVE_RESPONSE)
        }
        tested.start()
        var move: String? = null
        val job = launch {
            move = tested.calculateBestMove(SearchOptions())
        }
        testScheduler.advanceUntilIdle()

        tested.cancelMoveCalculation()
        job.join()

        assertThat(move).isEqualTo("e2e3")
        verifyOrder {
            process.writeLine(STOP)
        }
    }

    @Test
    fun `cancelMoveCalculation don't send stop command to engine if not calculating a move`() = runTest {
        tested.start()

        tested.cancelMoveCalculation()

        verify(exactly = 0) {
            process.writeLine(STOP)
        }
    }

    @Test
    fun `stop sends quit command to engine and stos process`() = runTest {
        tested.start()

        tested.stop()

        tested.awaitUninitialized()
        verifyOrder {
            process.writeLine(QUIT)
            process.stop()
        }
    }

    private suspend fun Channel<String>.send(lines: List<String>) =
        lines.forEach { send(it) }
}
