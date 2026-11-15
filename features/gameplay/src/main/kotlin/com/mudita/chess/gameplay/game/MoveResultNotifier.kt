package com.mudita.chess.gameplay.game

import com.mudita.chess.gameplay.GameplayUiEvents
import kotlinx.coroutines.flow.first

internal class MoveResultNotifier(
    private val board: ChessBoard,
    private val uiEvents: GameplayUiEvents
) {

    /**
     * "Check" stays up until it is pressed away, not for four seconds: on this panel a notice on
     * a timer could be gone before it had been read. The next move waits for that press.
     */
    suspend fun maybeNotifyCheck(moveResult: MoveResult) {
        if (!moveResult.opponentInCheck) return
        try {
            uiEvents.cancelCheckInfoClicks.first()
        } finally {
            board.clearCheckAcknowledgeRequired()
        }
    }
}
