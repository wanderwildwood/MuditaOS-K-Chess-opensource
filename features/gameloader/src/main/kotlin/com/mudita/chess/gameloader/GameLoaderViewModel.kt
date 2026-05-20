package com.mudita.chess.gameloader

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.pgn.GameLoader
import com.mudita.chess.gameoptions.mapper.BEGINNER_MIN_LEVEL
import com.mudita.chess.gameoptions.mapper.MAX_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.mapper.MIN_DIFFICULTY_LEVEL
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.gameoptions.model.GameOptions
import com.mudita.chess.gameoptions.usecase.SaveGameOptionsUseCase
import com.mudita.chess.games.usecase.SaveCurrentGameMovesUseCase
import logcat.logcat

class GameLoaderViewModel(
    private val saveGameOptionsUseCase: SaveGameOptionsUseCase,
    private val saveCurrentGameMovesUseCase: SaveCurrentGameMovesUseCase
) : ViewModel() {

    suspend fun process(intent: Intent?) {
        val game = intent.parseGame()
        val playerSide = intent.parsePlayerSide()
        val difficulty = intent.parseDifficulty()
        logcat { "Received game = $game \n\nwith playerSide = $playerSide and difficulty = $difficulty" }

        when {
            game == null -> {
                logcat { "Unable to parse game!" }
            }

            else -> {
                val gameOptions = GameOptions(
                    isMoveSuggestionsOn = true,
                    isPlayerWhite = playerSide == Side.WHITE,
                    difficultyLevel = DifficultyLevel(difficulty)
                )
                val movesLAN = game.currentMoveList.map { it.toString() }
                saveGameOptionsUseCase(gameOptions)
                    .onFailure { logcat { "Unable to apply game options!" } }

                saveCurrentGameMovesUseCase(movesLAN)
                    .onSuccess { logcat { "Game loaded!" } }
                    .onFailure { logcat { "Unable to save game!" } }
            }
        }
    }

    private fun Intent?.parseGame() = this
        ?.extras
        ?.getString(PGN_EXTRA)
        ?.runCatching {
            val lines = split(';')
            GameLoader.loadNextGame(lines.iterator())
        }
        ?.getOrNull()

    private fun Intent?.parsePlayerSide() = this
        ?.extras
        ?.getString(PLAYER_SIDE)
        ?.uppercase()
        ?.runCatching { enumValueOf<Side>(this) }
        ?.getOrNull()
        ?: Side.WHITE

    private fun Intent?.parseDifficulty() = this
        ?.extras
        ?.getInt(DIFFICULTY)
        ?.takeIf { it in MIN_DIFFICULTY_LEVEL..MAX_DIFFICULTY_LEVEL }
        ?: BEGINNER_MIN_LEVEL

    private companion object {
        const val PGN_EXTRA = "PGN_EXTRA"
        const val PLAYER_SIDE = "PLAYER_SIDE"
        const val DIFFICULTY = "DIFFICULTY"
    }
}
