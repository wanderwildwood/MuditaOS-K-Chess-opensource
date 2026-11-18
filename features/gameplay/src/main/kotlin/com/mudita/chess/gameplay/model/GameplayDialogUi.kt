package com.mudita.chess.gameplay.model

import com.mudita.chess.gameplay.model.DialogAlignment.BOARD_CENTER
import com.mudita.chess.gameplay.model.DialogAlignment.BOTTOM
import com.mudita.chess.gameplay.model.GameplayDialogType.CHECK_INFO
import com.mudita.chess.gameplay.model.GameplayDialogType.ENGINE_UNAVAILABLE
import com.mudita.chess.gameplay.model.GameplayDialogType.GAME_MENU
import com.mudita.chess.gameplay.model.GameplayDialogType.LOADING
import com.mudita.chess.gameplay.model.GameplayDialogType.PAWN_PROMOTION
import com.mudita.chess.gameplay.model.GameplayDialogType.SAVED_GAME_UNREADABLE
import com.mudita.chess.ui.model.PieceUi

enum class DialogAlignment {
    BOTTOM,
    BOARD_CENTER
}

internal enum class GameplayDialogType {
    LOADING,
    GAME_MENU,
    PAWN_PROMOTION,
    CHECK_INFO,
    ENGINE_UNAVAILABLE,
    SAVED_GAME_UNREADABLE
}

internal sealed class GameplayDialogUi(
    val type: GameplayDialogType,
    val alignment: DialogAlignment = BOARD_CENTER
) {
    data object LoadingDialogUi : GameplayDialogUi(type = LOADING)

    data class GameMenuDialogUi(
        val isMoveSuggestionsOn: Boolean
    ) : GameplayDialogUi(type = GAME_MENU, alignment = BOTTOM)

    data class PawnPromotionDialogUi(
        val promotionOptions: Set<PieceUi>
    ) : GameplayDialogUi(type = PAWN_PROMOTION)

    /** The computer player cannot run on this phone; the only way on is back to the menu. */
    data object EngineUnavailableDialogUi : GameplayDialogUi(type = ENGINE_UNAVAILABLE)

    /** The saved game could not be read; Keep leaves it for a later try, Discard deletes it. */
    data object SavedGameUnreadableDialogUi : GameplayDialogUi(type = SAVED_GAME_UNREADABLE)

    /**
     * A new game was asked for while the saved game could not be read, and the new one would be
     * saved over it. Start deletes it and starts; Keep leaves it and goes back, as on the message above.
     */
    data object NewGameOverUnreadableDialogUi : GameplayDialogUi(type = SAVED_GAME_UNREADABLE)

    data class CheckInfoDialogUi(
        val king: PieceUi,
        val attackedBy: List<PieceUi>
    ) : GameplayDialogUi(type = CHECK_INFO)
}
