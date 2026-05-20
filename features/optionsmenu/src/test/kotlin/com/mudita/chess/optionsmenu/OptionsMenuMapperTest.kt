package com.mudita.chess.optionsmenu

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.ui.model.TextUi
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream
import com.mudita.chess.frontitude.R as RFrontitude

class OptionsMenuMapperTest {

    private val tested = OptionsMenuMapper()

    @ParameterizedTest
    @MethodSource("difficultyLabelsParameters")
    fun `map returns difficulty label for difficulty level step`(
        difficultyLevelStep: Int,
        expectedLabelRes: Int,
        expectedLabelArg: Int
    ) {
        val result = tested.toDifficultyLevelLabel(difficultyLevelStep)

        assertThat(result).isEqualTo(TextUi.Res(expectedLabelRes, args = arrayOf(expectedLabelArg)))
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 13])
    fun `map throws exception when difficulty step out of range`(difficultyLevelStep: Int) {
        val exception = assertThrows<IllegalArgumentException> {
            tested.toDifficultyLevelLabel(difficultyLevelStep)
        }

        assertThat(exception.message).isEqualTo("Invalid difficulty level step: $difficultyLevelStep")
    }

    companion object {
        @JvmStatic
        fun difficultyLabelsParameters(): Stream<Arguments> =
            Stream.of(
                Arguments.of(1, RFrontitude.string.chess_optionsmenu_label_beginner, 500),
                Arguments.of(2, RFrontitude.string.chess_optionsmenu_label_beginner, 650),
                Arguments.of(3, RFrontitude.string.chess_optionsmenu_label_beginner, 800),
                Arguments.of(4, RFrontitude.string.chess_optionsmenu_label_beginner, 950),
                Arguments.of(5, RFrontitude.string.chess_optionsmenu_label_intermediate, 1100),
                Arguments.of(6, RFrontitude.string.chess_optionsmenu_label_intermediate, 1250),
                Arguments.of(7, RFrontitude.string.chess_optionsmenu_label_intermediate, 1400),
                Arguments.of(8, RFrontitude.string.chess_optionsmenu_label_intermediate, 1550),
                Arguments.of(9, RFrontitude.string.chess_optionsmenu_label_advanced, 1700),
                Arguments.of(10, RFrontitude.string.chess_optionsmenu_label_advanced, 1850),
                Arguments.of(11, RFrontitude.string.chess_optionsmenu_label_advanced, 2000),
                Arguments.of(12, RFrontitude.string.chess_optionsmenu_label_advanced, 2150),
            )
    }
}
