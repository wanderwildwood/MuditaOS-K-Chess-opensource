package com.mudita.chess.statistics.fixtures

import com.mudita.chess.statistics.model.MatchResultUi
import com.mudita.chess.frontitude.R as RFrontitude

internal val MATCH_RESULTS_WON_5 = listOf(
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 5),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 100)
)
internal val MATCH_RESULTS_WON_7 = listOf(
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 7),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 100)
)
internal val MATCH_RESULTS_LOST_1 = listOf(
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 1),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 0)
)
internal val MATCH_RESULTS_WON_5_LOST_1 = listOf(
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 5),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 0),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 1),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 83)
)
internal val MATCH_RESULTS_WON_5_LOST_1_DRAW_10 = listOf(
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_won, value = 5),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_drawn, value = 10),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_lost, value = 1),
    MatchResultUi(titleResId = RFrontitude.string.chess_statistics_label_percentageofwins, value = 31)
)