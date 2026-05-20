package com.mudita.chess.optionsmenu

import com.mudita.chess.gameoptions.mapper.ADVANCED_MAX_LEVEL
import com.mudita.chess.gameoptions.mapper.ADVANCED_MIN_LEVEL
import com.mudita.chess.gameoptions.mapper.BEGINNER_MAX_LEVEL
import com.mudita.chess.gameoptions.mapper.BEGINNER_MIN_LEVEL
import com.mudita.chess.gameoptions.mapper.INTERMEDIATE_MAX_LEVEL
import com.mudita.chess.gameoptions.mapper.INTERMEDIATE_MIN_LEVEL
import com.mudita.chess.gameoptions.mapper.elo
import com.mudita.chess.gameoptions.model.DifficultyLevel
import com.mudita.chess.ui.model.TextUi
import com.mudita.chess.frontitude.R as RFrontitude

internal class OptionsMenuMapper {

    fun toDifficultyLevelLabel(difficultyLevelStep: Int): TextUi {
        val difficultyLevelLabelResId = when (difficultyLevelStep) {
            in BEGINNER_MIN_LEVEL..BEGINNER_MAX_LEVEL ->
                RFrontitude.string.chess_optionsmenu_label_beginner

            in INTERMEDIATE_MIN_LEVEL..INTERMEDIATE_MAX_LEVEL ->
                RFrontitude.string.chess_optionsmenu_label_intermediate

            in ADVANCED_MIN_LEVEL..ADVANCED_MAX_LEVEL ->
                RFrontitude.string.chess_optionsmenu_label_advanced

            else -> throw IllegalArgumentException(
                "Invalid difficulty level step: $difficultyLevelStep"
            )
        }
        val elo = DifficultyLevel(difficultyLevelStep).elo()
        return TextUi.Res(difficultyLevelLabelResId, args = arrayOf(elo))
    }
}
