package com.mudita.chess.gameplay

import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameplay.game.ChessBoard
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

/**
 * Regression tests for the local 2-player crash where tapping a piece could kill the app with
 * `MoveGeneratorException: Couldn't generate Legal moves` wrapping an NPE.
 *
 * In 2-player mode both PlayerParticipants subscribe to the same square-click flow and both
 * evaluate `side == board.sideToMove` on the multi-threaded IO dispatcher, while the participant
 * whose turn it is mutates the very same chesslib Board via doMove/undoMove. Reading `sideToMove`
 * transitively runs full legal-move generation, so a read overlapping a write could observe a
 * half-applied move, find an empty from-square, and dereference `Piece.NONE.getPieceType()`
 * (which is null) inside chesslib's `Board.isMoveLegal`.
 *
 * These tests drive that exact read/write overlap directly. Before ChessBoard serialized access to
 * the board they fail within a few iterations; they pass once every access goes through the lock.
 */
class ChessBoardConcurrencyTest {

    @Test
    fun `reading sideToMove while a move is applied and undone never throws`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        val failure = AtomicReference<Throwable?>(null)
        val start = CountDownLatch(1)

        val writer = thread {
            start.await()
            repeat(ITERATIONS) {
                runCatching {
                    board.doUnconfirmedMove(Move(E2, E4))
                    board.undoUnconfirmedMove()
                }.onFailure { failure.compareAndSet(null, it) }
            }
        }

        val readers = List(READER_THREADS) {
            thread {
                start.await()
                repeat(ITERATIONS) {
                    runCatching { board.sideToMove }.onFailure { failure.compareAndSet(null, it) }
                }
            }
        }

        start.countDown()
        (readers + writer).forEach { it.join(THREAD_TIMEOUT_MS) }

        assertThat(failure.get()).isNull()
    }

    @Test
    fun `generating legal moves while a move is applied and undone never throws`() {
        val board = ChessBoard(topParticipantSide = BLACK)
        val failure = AtomicReference<Throwable?>(null)
        val start = CountDownLatch(1)

        val writer = thread {
            start.await()
            repeat(ITERATIONS) {
                runCatching {
                    board.doUnconfirmedMove(Move(E2, E4))
                    board.undoUnconfirmedMove()
                }.onFailure { failure.compareAndSet(null, it) }
            }
        }

        val readers = List(READER_THREADS) {
            thread {
                start.await()
                repeat(ITERATIONS) {
                    runCatching { board.getLegalMoves(E2) }.onFailure { failure.compareAndSet(null, it) }
                }
            }
        }

        start.countDown()
        (readers + writer).forEach { it.join(THREAD_TIMEOUT_MS) }

        assertThat(failure.get()).isNull()
    }

    private companion object {
        const val ITERATIONS = 300
        const val READER_THREADS = 4
        val THREAD_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(30)
    }
}
