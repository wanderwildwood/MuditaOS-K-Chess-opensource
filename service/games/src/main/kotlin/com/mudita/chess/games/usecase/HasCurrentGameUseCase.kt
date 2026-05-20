package com.mudita.chess.games.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.games.repository.GamesRepository

class HasCurrentGameUseCase internal constructor(
    private val repository: GamesRepository
) {
    suspend operator fun invoke(): Boolean = resultOf {
        repository.hasCurrentGame()
    }.getOrDefault(false)
}
