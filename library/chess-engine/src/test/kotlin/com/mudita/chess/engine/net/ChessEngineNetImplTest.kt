package com.mudita.chess.engine.net

import android.content.Context
import android.content.res.AssetManager
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

class ChessEngineNetImplTest {

    private val assetManager = mockk<AssetManager>()

    private val appContext = mockk<Context> {
        every { filesDir } returns File("$JVM_TEST_FILE/data/data/com.mudita.chess/files")
        every { assets } returns assetManager
    }

    private val tested = ChessEngineNetImpl(appContext)

    @Test
    fun `load copies properly nnue file from application asset to files dir`() {
        every { assetManager.open("nn-3475407dc199.nnue") } returns "Test NNUE".byteInputStream()

        val copied = tested.load()

        assertThat(copied).isNotNull()
        assertThat(copied?.parentFile?.listFiles()).hasLength(1) // assert no .tmp file left
        assertThat(copied?.bufferedReader()?.readText()).isEqualTo("Test NNUE") // assert content
    }

    @Test
    fun `load skip coping when nnue file already exists in files dir`() {
        File("$JVM_TEST_FILE/data/data/com.mudita.chess/files/chess-engine/net/nn-3475407dc199.nnue").run {
            parentFile?.mkdirs()
            createNewFile()
            writeText("Test NNUE")
        }

        val copied = tested.load()

        assertThat(copied).isNotNull()
    }

    @AfterEach
    fun tearDown() {
        File(JVM_TEST_FILE).deleteRecursively()
    }

    companion object {
        private const val JVM_TEST_FILE = "test-files"
    }
}
