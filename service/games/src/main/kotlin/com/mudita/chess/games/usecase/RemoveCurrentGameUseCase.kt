package com.mudita.chess.games.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.games.repository.GamesRepository

class RemoveCurrentGameUseCase internal constructor(
    private val repository: GamesRepository
) {

    suspend operator fun invoke() = resultOf {
        repository.removeCurrentGame()
    }
}
