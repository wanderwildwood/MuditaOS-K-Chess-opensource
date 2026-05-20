package com.mudita.chess.gameoptions.mapper

import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameoptions.model.DifficultyLevel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DifficultyLevelMapperTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "1, 500",
            "2, 650",
            "3, 800",
            "4, 950",
            "5, 1100",
            "6, 1250",
            "7, 1400",
            "8, 1550",
            "9, 1700",
            "10, 1850",
            "11, 2000",
            "12, 2150"

        ]
    )
    fun `difficulty level to elo conversion`(level: Int, elo: Int) {
        assertThat(DifficultyLevel(level).elo()).isEqualTo(elo)
    }

    @Test
    fun `invalid difficulty level elo conversion throws exception`() {
        assertThrows<IllegalArgumentException> {
            DifficultyLevel(13).elo()
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1, 5",
            "2, 5",
            "3, 5",
            "4, 5",
            "5, 5",
            "6, 5",
            "7, 5",
            "8, 5",
            "9, 5",
            "10, 8",
            "11, 13",
            "12, 21"
        ]
    )
    fun `difficulty level to search depth conversion`(level: Int, depth: Int) {
        assertThat(DifficultyLevel(level).searchDepth()).isEqualTo(depth)
    }

    @Test
    fun `invalid difficulty level search depth conversion throws exception`() {
        assertThrows<IllegalArgumentException> {
            DifficultyLevel(13).searchDepth()
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1, 50",
            "2, 100",
            "3, 150",
            "4, 200",
            "5, 250",
            "6, 300",
            "7, 350",
            "8, 400",
            "9, 450",
            "10, 500",
            "11, 600",
            "12, 1000"
        ]
    )
    fun `difficulty level to move time conversion`(level: Int, time: Int) {
        assertThat(DifficultyLevel(level).moveTimeMillis()).isEqualTo(time)
    }

    @Test
    fun `invalid difficulty level move time conversion throws exception`() {
        assertThrows<IllegalArgumentException> {
            DifficultyLevel(13).moveTimeMillis()
        }
    }
}
