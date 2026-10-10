package com.mudita.chess.engine.process

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.engine.ChessEngineTokens.ENGINE_INTRO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.File
import java.io.OutputStream

class ChessEngineProcessImplTest {

    private val testScheduler = TestCoroutineScheduler()

    private val engineFile = File("/data/app/com.wanderwildwood.chessplus/lib/arm64/libstockfish.so")
    private var requestedCommand: String? = null
    private val process = mockk<Process>(relaxed = true) {
        every { inputStream } returns "$ENGINE_INTRO\n".byteInputStream()
        every { isAlive } returns true
    }
    private val processBuilder = mockk<ProcessBuilder> {
        every { directory(any()) } returns this
        every { start() } returns process
    }

    private val tested = ChessEngineProcessImpl(testScheduler, engineFile) { command ->
        requestedCommand = command
        processBuilder
    }

    @Test
    fun `start launches process`() {
        tested.start()

        verify { processBuilder.start() }
    }

    @Test
    fun `start runs the engine the app ships, not one from the system`() {
        tested.start()

        assertThat(requestedCommand).isEqualTo(engineFile.absolutePath)
        verify { processBuilder.directory(engineFile.parentFile) }
    }

    @Test
    fun `monitor reads from process input stream and publishes lines on channel`() = runTest {
        tested.start()

        tested.monitor()

        tested.receiveLines().test {
            assertThat(awaitItem()).isEqualTo(ENGINE_INTRO)
        }
    }

    @Test
    fun `stop cancel monitoring and kills process`() = runTest {
        tested.start()
        tested.monitor()

        tested.receiveLines().test {
            skipItems(1)
            tested.stop()
            awaitComplete()
        }
        verify { process.destroy() }
    }

    @Test
    fun `writeLine writes to process output stream`() = runTest {
        val outputStream = mockk<OutputStream>(relaxed = true)
        every { process.outputStream } returns outputStream
        tested.start()
        tested.monitor()

        tested.writeLine("uci")

        verify {
            outputStream.write("uci\n".toByteArray())
            outputStream.flush()
        }
    }
}
