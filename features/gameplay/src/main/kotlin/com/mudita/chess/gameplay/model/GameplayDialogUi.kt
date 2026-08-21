package com.mudita.chess.gameplay.model

import com.mudita.chess.gameplay.model.DialogAlignment.BOARD_CENTER
import com.mudita.chess.gameplay.model.DialogAlignment.BOTTOM
import com.mudita.chess.gameplay.model.GameplayDialogType.CHECK_INFO
import com.mudita.chess.gameplay.model.GameplayDialogType.GAME_MENU
import com.mudita.chess.gameplay.model.GameplayDialogType.LOADING
import com.mudita.chess.gameplay.model.GameplayDialogType.PAWN_PROMOTION
import com.mudita.chess.ui.model.PieceUi

enum class DialogAlignment {
    BOTTOM,
    BOARD_CENTER
}

internal enum class GameplayDialogType {
    LOADING,
    GAME_MENU,
    PAWN_PROMOTION,
    CHECK_INFO
}

internal sealed class GameplayDialogUi(
    val type: GameplayDialogType,
    val alignment: DialogAlignment = BOARD_CENTER
) {
    data object LoadingDialogUi : GameplayDialogUi(type = LOADING)

    data class GameMenuDialogUi(
        val isMoveSuggestionsOn: Boolean,
        val isTwoPlayerMode: Boolean = false
    ) : GameplayDialogUi(type = GAME_MENU, alignment = BOTTOM)

    data class PawnPromotionDialogUi(
        val promotionOptions: Set<PieceUi>
    ) : GameplayDialogUi(type = PAWN_PROMOTION)

    data class CheckInfoDialogUi(
        val king: PieceUi,
        val attackedBy: List<PieceUi>
    ) : GameplayDialogUi(type = CHECK_INFO)
}
