package com.mudita.chess.games.usecase

import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveList
import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.games.model.Game
import com.mudita.chess.games.repository.GamesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveCurrentGameMovesUseCase internal constructor(
    private val repository: GamesRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(movesLAN: List<String>): Result<Unit> = withContext(ioDispatcher) {
        resultOf {
            val movesSAN = if (movesLAN.isNotEmpty()) {
                val moves = MoveList()
                moves.addAll(movesLAN.mapToModel())
                moves.toSanWithMoveNumbers()
            } else {
                null
            }
            repository.saveCurrentGame(Game(movesSAN))
        }
    }

    private fun List<String>.mapToModel(): List<Move> = mapIndexed { index, moveLAN ->
        val side = if (index % 2 == 0) WHITE else BLACK
        Move(moveLAN, side)
    }
}
