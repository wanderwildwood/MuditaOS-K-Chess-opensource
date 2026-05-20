package com.mudita.chess.gameplay.usecase

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import com.mudita.chess.engine.ChessEngine
import com.mudita.chess.engine.SearchOptions
import com.mudita.chess.engine.UCIOptions
import com.mudita.chess.gameoptions.mapper.elo
import com.mudita.chess.gameoptions.mapper.moveTimeMillis
import com.mudita.chess.gameoptions.mapper.searchDepth
import com.mudita.chess.gameoptions.model.DifficultyLevel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetComputerMoveUseCase(
    private val engine: ChessEngine,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(
        moves: List<Move>,
        sideToMove: Side,
        difficultyLevel: DifficultyLevel
    ): Move = withContext(ioDispatcher) {
        engine.setOptions(getLimitStrengthOptions(difficultyLevel))
        engine.calculateBestMove(
            SearchOptions(
                moves = moves.map { it.toString() },
                moveTimeMillis = difficultyLevel.moveTimeMillis(),
                depth = difficultyLevel.searchDepth()
            )
        ).let {
            Move(it, sideToMove)
        }
    }

    private fun getLimitStrengthOptions(difficultyLevel: DifficultyLevel) = mapOf(
        UCIOptions.LIMIT_STRENGTH to true,
        UCIOptions.ELO to difficultyLevel.elo()
    )
}
