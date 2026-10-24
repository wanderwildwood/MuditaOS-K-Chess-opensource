package com.mudita.chess.gameplay.game

/** Why a game was drawn, for a result line that says more than "it's a draw". */
internal enum class DrawReason {
    REPETITION,
    FIFTY_MOVES,
    INSUFFICIENT_MATERIAL,
    STALEMATE;

    companion object {
        /** Half moves without a capture or pawn move at which the fifty-move rule draws the game. */
        const val FIFTY_MOVE_HALF_MOVES = 100

        /**
         * Which rule ended the game, tested in the order chesslib's own `isDraw` tests them, so the
         * reason named is the one that made the board call the game drawn.
         */
        fun of(
            isRepetition: Boolean,
            isInsufficientMaterial: Boolean,
            halfMoveCounter: Int,
            isStalemate: Boolean
        ): DrawReason? = when {
            isRepetition -> REPETITION
            isInsufficientMaterial -> INSUFFICIENT_MATERIAL
            halfMoveCounter >= FIFTY_MOVE_HALF_MOVES -> FIFTY_MOVES
            isStalemate -> STALEMATE
            else -> null
        }
    }
}
