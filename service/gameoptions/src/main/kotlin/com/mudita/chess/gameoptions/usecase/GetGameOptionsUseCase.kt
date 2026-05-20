package com.mudita.chess.gameoptions.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.gameoptions.repository.GameOptionsRepository

class GetGameOptionsUseCase internal constructor(
    private val gameOptionsRepository: GameOptionsRepository
) {

    suspend operator fun invoke() = resultOf {
        gameOptionsRepository.getGameOptions()
    }
}
