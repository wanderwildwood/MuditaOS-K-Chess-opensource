package com.mudita.chess.engine

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class UCICommandsTest {

    private val tested = UCICommands

    @Test
    fun `setOption builds valid command`() {
        val result = tested.setOption("Hash", 64)

        assertThat(result).isEqualTo("setoption name Hash value 64")
    }

    @Test
    fun `position builds command with startpos if fen not provided`() {
        val result = tested.position(fen = null, moves = emptyList())

        assertThat(result).isEqualTo("position startpos")
    }

    @Test
    fun `position builds command with provided fen`() {
        val result = tested.position(fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", moves = emptyList())

        assertThat(result).isEqualTo("position fen rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    }

    @Test
    fun `position builds command with provided moves`() {
        val result = tested.position(fen = null, moves = listOf("g2g4", "d7d5", "f1g2", "c8g4", "c2c4"))

        assertThat(result).isEqualTo("position startpos moves g2g4 d7d5 f1g2 c8g4 c2c4")
    }

    @Test
    fun `go builds valid command to search without any options provided`() {
        val result = tested.go()

        assertThat(result).isEqualTo("go")
    }

    @Test
    fun `go builds command to search with time control options`() {
        val result = tested.go(wTime = 15000, bTime = 15000, wInc = 100, bInc = 100, movesToGo = 50)

        assertThat(result).isEqualTo("go wtime 15000 btime 15000 winc 100 binc 100 movestogo 50")
    }

    @Test
    fun `go builds command to search x nodes only`() {
        val result = tested.go(nodes = 1000)

        assertThat(result).isEqualTo("go nodes 1000")
    }

    @Test
    fun `go builds command to search x plies only`() {
        val result = tested.go(depth = 128)

        assertThat(result).isEqualTo("go depth 128")
    }

    @Test
    fun `go builds command to search for a mate in x moves`() {
        val result = tested.go(mate = 1)

        assertThat(result).isEqualTo("go mate 1")
    }

    @Test
    fun `go builds command to search exactly x mseconds`() {
        val result = tested.go(moveTime = 3)

        assertThat(result).isEqualTo("go movetime 3")
    }

    @Test
    fun `go builds command to search until the 'stop' command received`() {
        val result = tested.go(infinite = true)

        assertThat(result).isEqualTo("go infinite")
    }
}
