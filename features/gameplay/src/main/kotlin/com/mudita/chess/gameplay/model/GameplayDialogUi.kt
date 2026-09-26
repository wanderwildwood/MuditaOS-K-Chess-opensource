package com.mudita.chess.gameplay.model

import androidx.annotation.StringRes
import com.mudita.chess.gameplay.model.DialogAlignment.BOARD_CENTER
import com.mudita.chess.gameplay.model.DialogAlignment.BOTTOM
import com.mudita.chess.gameplay.model.GameplayDialogType.CHECK_INFO
import com.mudita.chess.gameplay.model.GameplayDialogType.ENDGAME_DRAW
import com.mudita.chess.gameplay.model.GameplayDialogType.ENDGAME_VICTORY
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
    CHECK_INFO,
    ENDGAME_VICTORY,
    ENDGAME_DRAW
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

    data class CheckInfoDialogUi(
        val king: PieceUi,
        val attackedBy: List<PieceUi>
    ) : GameplayDialogUi(type = CHECK_INFO)

    data class VictoryDialogUi(
        @StringRes val titleResId: Int
    ) : GameplayDialogUi(type = ENDGAME_VICTORY, alignment = BOTTOM)

    data object DrawDialogUi : GameplayDialogUi(type = ENDGAME_DRAW, alignment = BOTTOM)
}
