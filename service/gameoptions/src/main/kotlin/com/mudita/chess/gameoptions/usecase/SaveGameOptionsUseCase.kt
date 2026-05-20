package com.mudita.chess.gameoptions.usecase

import com.mudita.chess.coroutines.resultOf
import com.mudita.chess.gameoptions.repository.GameOptionsRepository
import com.mudita.chess.gameoptions.model.GameOptions

class SaveGameOptionsUseCase internal constructor(
    private val gameOptionsRepository: GameOptionsRepository
) {

    suspend operator fun invoke(gameOptions: GameOptions) = resultOf {
        gameOptionsRepository.saveGameOptions(gameOptions)
    }
}
