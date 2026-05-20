package com.mudita.chess.engine

data class SearchOptions(
    var fen: String? = null,
    var moves: List<String> = emptyList(),
    var whiteTimeLeft: Int = 0,
    var blackTimeLeft: Int = 0,
    var whiteTimeIncrement: Int = 0,
    var blackTimeIncrement: Int = 0,
    var movesToNextTimeControl: Int = 0,
    /**
     * Depth is a value of chess engine analysis that indicates the number of half moves
     * (a move made by one side) the engine looks ahead.
     * A higher depth value usually means better analysis results, but several other factors also go into play.
     */
    var depth: Int = 0,
    var nodes: Int = 0,
    var mate: Int = 0,
    var moveTimeMillis: Int = 0,
    var infinite: Boolean = false
)
