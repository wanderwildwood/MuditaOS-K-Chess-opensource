package com.mudita.chess.gameoptions.mapper

import com.mudita.chess.gameoptions.model.DifficultyLevel

fun DifficultyLevel.elo(): Int =
    when (value) {
        in MIN_DIFFICULTY_LEVEL..MAX_DIFFICULTY_LEVEL -> ELO_MIN + (value - 1) * ELO_STEP
        else -> throw IllegalArgumentException("Invalid difficulty level: $value")
    }

/**
 * Values based on
 *
 * - [what-elo-are-the-various-stockfish-levels (lichess.org)](https://lichess.org/forum/general-chess-discussion/what-elo-are-the-various-stockfish-levels)
 * - [fishnet sources (github.com/lichess-org)](https://github.com/lichess-org/fishnet/blob/master/src/api.rs#L275)
 */
@Suppress("MagicNumber")
fun DifficultyLevel.searchDepth(): Int =
    when (value) {
        in BEGINNER_MIN_LEVEL..ADVANCED_MIN_LEVEL -> 5
        10 -> 8
        11 -> 13
        12 -> 21
        else -> throw IllegalArgumentException("Invalid difficulty level: $value")
    }

/**
 * Values based on
 *
 * - [what-elo-are-the-various-stockfish-levels (lichess.org)](https://lichess.org/forum/general-chess-discussion/what-elo-are-the-various-stockfish-levels)
 * - [fishnet sources (github.com/lichess-org)](https://github.com/lichess-org/fishnet/blob/master/src/api.rs#L249)
 */
@Suppress("MagicNumber")
fun DifficultyLevel.moveTimeMillis(): Int =
    when (value) {
        in BEGINNER_MIN_LEVEL..ADVANCED_MIN_LEVEL -> value * 50
        10 -> 500
        11 -> 600
        12 -> 1000
        else -> throw IllegalArgumentException("Invalid difficulty level: $value")
    }

const val BEGINNER_MIN_LEVEL = 1
const val BEGINNER_MAX_LEVEL = 4
const val INTERMEDIATE_MIN_LEVEL = 5
const val INTERMEDIATE_MAX_LEVEL = 8
const val ADVANCED_MIN_LEVEL = 9
const val ADVANCED_MAX_LEVEL = 12
const val MIN_DIFFICULTY_LEVEL = BEGINNER_MIN_LEVEL
const val MAX_DIFFICULTY_LEVEL = ADVANCED_MAX_LEVEL
private const val ELO_MIN = 500
private const val ELO_STEP = 150
