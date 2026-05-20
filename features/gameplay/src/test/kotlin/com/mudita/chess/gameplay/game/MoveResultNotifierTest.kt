package com.mudita.chess.gameplay.game

import com.mudita.chess.gameplay.GameplayUiEvent.DialogDismissRequested
import com.mudita.chess.gameplay.GameplayUiEvents
import com.mudita.chess.gameplay.model.GameplayDialogType.CHECK_INFO
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MoveResultNotifierTest {

    private val board: ChessBoard = mockk(relaxed = true)
    private val uiEvents: GameplayUiEvents = GameplayUiEvents()

    private val tested = MoveResultNotifier(board, uiEvents)

    @Test
    fun `maybeNotifyCheck clears check acknowledge required when delay passes`() = runTest {
        launch {
            tested.maybeNotifyCheck(MoveResult(opponentInCheck = true))
        }
        runCurrent()
        verify(exactly = 0) { board.clearCheckAcknowledgeRequired() }

        advanceTimeBy(CHECK_ACKNOWLEDGE_DURATION_MILLIS + 1)

        verify(exactly = 1) { board.clearCheckAcknowledgeRequired() }
    }

    @Test
    fun `maybeNotifyCheck clears check acknowledge required when acknowledge is cancelled by ui event`() = runTest {
        launch {
            tested.maybeNotifyCheck(MoveResult(opponentInCheck = true))
        }
        runCurrent()
        verify(exactly = 0) { board.clearCheckAcknowledgeRequired() }

        uiEvents.handleUiEvent(DialogDismissRequested(CHECK_INFO))

        runCurrent()
        verify(exactly = 1) { board.clearCheckAcknowledgeRequired() }
    }

    @Test
    fun `maybeNotifyCheck clears check acknowledge required when acknowledge coroutine canceled`() = runTest {
        val job = launch {
            tested.maybeNotifyCheck(MoveResult(opponentInCheck = true))
        }
        runCurrent()
        verify(exactly = 0) { board.clearCheckAcknowledgeRequired() }

        job.cancelAndJoin()

        verify(exactly = 1) { board.clearCheckAcknowledgeRequired() }
    }

    @Test
    fun `maybeNotifyCheck do nothing when opponent isn't in check`() = runTest {
        tested.maybeNotifyCheck(MoveResult(opponentInCheck = false))

        verify(exactly = 0) {
            board.clearCheckAcknowledgeRequired()
        }
    }

    private companion object {
        const val CHECK_ACKNOWLEDGE_DURATION_MILLIS = 2000L
    }
}
