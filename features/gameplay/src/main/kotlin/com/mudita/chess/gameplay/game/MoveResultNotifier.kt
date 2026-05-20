package com.mudita.chess.gameplay.game

import com.mudita.chess.gameplay.GameplayUiEvents
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

internal class MoveResultNotifier(
    private val board: ChessBoard,
    private val uiEvents: GameplayUiEvents
) {

    suspend fun maybeNotifyCheck(moveResult: MoveResult) {
        if (!moveResult.opponentInCheck) return
        try {
            merge(acknowledgeDelay(), acknowledgeCancellation()).first()
        } finally {
            board.clearCheckAcknowledgeRequired()
        }
    }

    private fun acknowledgeDelay(): Flow<Unit> = flow {
        delay(CHECK_ACKNOWLEDGE_DURATION_MILLIS)
        emit(Unit)
    }

    private fun acknowledgeCancellation(): Flow<Unit> = uiEvents
        .cancelCheckInfoClicks
        .take(1)
        .map { /* to Unit */ }

    private companion object {
        const val CHECK_ACKNOWLEDGE_DURATION_MILLIS = 2000L
    }
}
