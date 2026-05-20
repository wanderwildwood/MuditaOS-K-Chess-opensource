package com.mudita.chess.games.usecase

import com.github.bhlangonijr.chesslib.move.MoveList
import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.games.repository.GamesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetCurrentGameMovesUseCase internal constructor(
    private val repository: GamesRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Result<List<String>?> = withContext(ioDispatcher) {
        resultOf {
            repository.getCurrentGame()
                ?.let { game ->
                    val moves = MoveList()
                    game.movesSAN?.let {
                        moves.loadFromSan(game.movesSAN)
                    }
                    moves.map { it.toString() }
                }
        }
    }
}
