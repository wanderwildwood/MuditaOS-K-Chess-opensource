package com.mudita.chess.gameplay.game

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.mudita.chess.gameplay.GameplayUiEvents
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parameterArrayOf
import org.koin.core.parameter.parametersOf

internal class GameFactory : KoinComponent {

    fun createPlayerVsComputer(
        playerSide: Side,
        isPiecesPositionReady: Boolean,
        uiEvents: GameplayUiEvents
    ): Game {
        val computerSide = playerSide.flip()
        val board = get<ChessBoard> { parametersOf(isPiecesPositionReady, computerSide) }
        val moveResultNotifier = get<MoveResultNotifier> { parametersOf(board, uiEvents) }
        val participants = listOf(
            get<PlayerParticipant> { parametersOf(playerSide, board, moveResultNotifier, uiEvents) },
            get<ComputerParticipant> { parametersOf(computerSide, board, moveResultNotifier) }
        ).associateBy { it.side }
        return get<Game> {
            parameterArrayOf(board, participants.getValue(WHITE), participants.getValue(BLACK), moveResultNotifier)
        }
    }
}
