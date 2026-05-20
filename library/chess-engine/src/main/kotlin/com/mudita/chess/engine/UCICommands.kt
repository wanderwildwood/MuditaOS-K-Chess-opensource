package com.mudita.chess.engine

internal object UCICommands {

    const val UCI = "uci"
    const val STOP = "stop"
    const val QUIT = "quit"
    const val NEW_GAME = "ucinewgame"

    fun setOption(option: String, value: Any) =
        "setoption name $option value $value"

    fun position(
        fen: String?,
        moves: List<String>
    ): String =
        buildString {
            append("position ${if (fen === null) "startpos" else "fen $fen"}")
            if (moves.isNotEmpty()) {
                append(" moves ")
                append(moves.joinToString(separator = " "))
            }
        }

    @Suppress("LongParameterList")
    fun go(
        wTime: Int = 0,
        bTime: Int = 0,
        wInc: Int = 0,
        bInc: Int = 0,
        movesToGo: Int = 0,
        depth: Int = 0,
        nodes: Int = 0,
        mate: Int = 0,
        moveTime: Int = 0,
        infinite: Boolean = false
    ): String =
        buildString {
            append("go")

            sequenceOf(
                "wtime" to wTime,
                "btime" to bTime,
                "winc" to wInc,
                "binc" to bInc,
                "movestogo" to movesToGo,
                "depth" to depth,
                "nodes" to nodes,
                "mate" to mate,
                "movetime" to moveTime
            ).forEach { (key, value) ->
                if (value > 0) append(" $key $value")
            }

            if (infinite) append(" infinite")
        }
}
