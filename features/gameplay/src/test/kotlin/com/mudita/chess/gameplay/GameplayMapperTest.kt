package com.mudita.chess.gameplay

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP
import com.github.bhlangonijr.chesslib.Piece.BLACK_KING
import com.github.bhlangonijr.chesslib.Piece.BLACK_KNIGHT
import com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN
import com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN
import com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK
import com.github.bhlangonijr.chesslib.Piece.NONE
import com.github.bhlangonijr.chesslib.Piece.WHITE_BISHOP
import com.github.bhlangonijr.chesslib.Piece.WHITE_KING
import com.github.bhlangonijr.chesslib.Piece.WHITE_KNIGHT
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN
import com.github.bhlangonijr.chesslib.Piece.WHITE_ROOK
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Side.BLACK
import com.github.bhlangonijr.chesslib.Side.WHITE
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.Square.A5
import com.github.bhlangonijr.chesslib.Square.A8
import com.github.bhlangonijr.chesslib.Square.B1
import com.github.bhlangonijr.chesslib.Square.B5
import com.github.bhlangonijr.chesslib.Square.C1
import com.github.bhlangonijr.chesslib.Square.C3
import com.github.bhlangonijr.chesslib.Square.C6
import com.github.bhlangonijr.chesslib.Square.C7
import com.github.bhlangonijr.chesslib.Square.C8
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.D2
import com.github.bhlangonijr.chesslib.Square.D3
import com.github.bhlangonijr.chesslib.Square.D4
import com.github.bhlangonijr.chesslib.Square.D5
import com.github.bhlangonijr.chesslib.Square.D7
import com.github.bhlangonijr.chesslib.Square.D8
import com.github.bhlangonijr.chesslib.Square.E1
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.Square.E5
import com.github.bhlangonijr.chesslib.Square.E7
import com.github.bhlangonijr.chesslib.Square.E8
import com.github.bhlangonijr.chesslib.Square.F1
import com.github.bhlangonijr.chesslib.Square.F2
import com.github.bhlangonijr.chesslib.Square.F3
import com.github.bhlangonijr.chesslib.Square.F4
import com.github.bhlangonijr.chesslib.Square.F5
import com.github.bhlangonijr.chesslib.Square.F6
import com.github.bhlangonijr.chesslib.Square.F8
import com.github.bhlangonijr.chesslib.Square.G1
import com.github.bhlangonijr.chesslib.Square.G8
import com.github.bhlangonijr.chesslib.Square.H1
import com.github.bhlangonijr.chesslib.Square.H4
import com.github.bhlangonijr.chesslib.move.Move
import com.google.common.truth.Truth.assertThat
import com.mudita.chess.gameplay.fixtures.BoardPiecesData.BLACK_PLAYER_PIECES
import com.mudita.chess.gameplay.fixtures.BoardPiecesData.WHITE_PLAYER_PIECES
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.BLACK_PLAYER_SQUARES
import com.mudita.chess.gameplay.fixtures.BoardSquaresData.WHITE_PLAYER_SQUARES
import com.mudita.chess.gameplay.fixtures.BoardUiData.BLACK_PLAYER_EMPTY_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.BLACK_PLAYER_INITIAL_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.WHITE_PLAYER_EMPTY_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.WHITE_PLAYER_INITIAL_BOARD_UI
import com.mudita.chess.gameplay.fixtures.BoardUiData.WHITE_PLAYER_POSITION_UIS
import com.mudita.chess.gameplay.fixtures.ChessBoardStateData.WHITE_PLAYER_BOARD
import com.mudita.chess.gameplay.fixtures.get
import com.mudita.chess.gameplay.fixtures.replace
import com.mudita.chess.gameplay.fixtures.withCheckInfo
import com.mudita.chess.gameplay.game.CheckInfo
import com.mudita.chess.gameplay.game.ChessBoardState
import com.mudita.chess.gameplay.game.GameStatus.BLACK_WON
import com.mudita.chess.gameplay.game.GameStatus.DRAW
import com.mudita.chess.gameplay.game.GameStatus.STARTED
import com.mudita.chess.gameplay.game.GameStatus.STOPPED
import com.mudita.chess.gameplay.game.GameStatus.WHITE_WON
import com.mudita.chess.gameplay.game.LocatedPiece
import com.mudita.chess.gameplay.model.EndgameUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.CheckInfoDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.GameMenuDialogUi
import com.mudita.chess.gameplay.model.GameplayDialogUi.PawnPromotionDialogUi
import com.mudita.chess.gameplay.model.ParticipantUi
import com.mudita.chess.navigation.routes.MoveArg
import com.mudita.chess.ui.model.PieceTypeUi.BISHOP
import com.mudita.chess.ui.model.PieceTypeUi.KING
import com.mudita.chess.ui.model.PieceTypeUi.KNIGHT
import com.mudita.chess.ui.model.PieceTypeUi.QUEEN
import com.mudita.chess.ui.model.PieceTypeUi.ROOK
import com.mudita.chess.ui.model.PieceUi
import com.mudita.chess.ui.model.PositionUi
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import com.mudita.chess.frontitude.R as RFrontitude
import com.mudita.chess.ui.model.PositionUi as Ui

internal class GameplayMapperTest {

    private val tested = GameplayMapper()

    @Test
    fun `toBoardUi maps white player board without pieces to empty board ui`() {
        val state = ChessBoardState(squares = WHITE_PLAYER_SQUARES)

        val result = tested.toBoardUi(state)

        assertThat(result).isEqualTo(WHITE_PLAYER_EMPTY_BOARD_UI)
    }

    @Test
    fun `toBoardUi maps black player board without pieces to empty board ui`() {
        val state = ChessBoardState(squares = BLACK_PLAYER_SQUARES)

        val result = tested.toBoardUi(state)

        assertThat(result).isEqualTo(BLACK_PLAYER_EMPTY_BOARD_UI)
    }

    @Test
    fun `toBoardUi maps white player board start position to board ui`() {
        val squares = WHITE_PLAYER_SQUARES
        val pieces = WHITE_PLAYER_PIECES

        val state = ChessBoardState(
            squares = squares,
            pieces = pieces
        )

        val result = tested.toBoardUi(state)

        assertThat(result).isEqualTo(WHITE_PLAYER_INITIAL_BOARD_UI)
    }

    @Test
    fun `toBoardUi maps black player board start position to board ui`() {
        val squares = BLACK_PLAYER_SQUARES
        val pieces = BLACK_PLAYER_PIECES

        val state = ChessBoardState(
            squares = squares,
            pieces = pieces
        )

        val result = tested.toBoardUi(state)

        assertThat(result).isEqualTo(BLACK_PLAYER_INITIAL_BOARD_UI)
    }

    @Test
    fun `toBoardUi marks square highlighted if highlights set contains it`() {
        val state = ChessBoardState(
            squares = WHITE_PLAYER_SQUARES,
            pieces = WHITE_PLAYER_PIECES,
            highlights = setOf(D2, D3, D4)
        )

        val result = tested.toBoardUi(state)

        assertThat(result[Ui.D2].isHighlighted).isTrue()
        assertThat(result[Ui.D3].isHighlighted).isTrue()
        assertThat(result[Ui.D4].isHighlighted).isTrue()
    }

    @Test
    fun `toBoardUi marks square highlighted if check info contains it`() {
        val checkInfo = CheckInfo(
            king = LocatedPiece(WHITE_KING, E1),
            attackedBy = listOf(LocatedPiece(BLACK_BISHOP, H4)),
            acknowledgeRequired = true
        )
        val state = WHITE_PLAYER_BOARD
            .replace(F8, NONE)
            .replace(E7, NONE)
            .replace(E5, BLACK_PAWN)
            .replace(D4, WHITE_PAWN)
            .replace(F4, WHITE_PAWN)
            .replace(H4, BLACK_BISHOP)
            .replace(F3, WHITE_KNIGHT)
            .replace(D2, NONE)
            .replace(F2, NONE)
            .replace(G1, NONE)
            .withCheckInfo(checkInfo)

        val result = tested.toBoardUi(state)

        assertThat(result[Ui.E1].isHighlighted).isTrue()
        assertThat(result[Ui.H4].isHighlighted).isTrue()
    }

    @Test
    fun `toBoardUi ignores invalid piece`() {
        val state = ChessBoardState(
            squares = WHITE_PLAYER_SQUARES,
            pieces = listOf(NONE)
        )

        val result = tested.toBoardUi(state)

        assertThat(result).isEqualTo(WHITE_PLAYER_EMPTY_BOARD_UI)
    }

    @ParameterizedTest
    @MethodSource("provideParticipantParameters")
    fun `to computer should return computer participant ui`(
        side: Side,
        isWhite: Boolean,
        isSelected: Boolean
    ) {
        val result = tested.toComputer(
            side = side,
            isSelected = isSelected
        )

        assertThat(result).isEqualTo(
            ParticipantUi(
                nameResId = RFrontitude.string.common_label_computer,
                isWhite = isWhite,
                isSelected = isSelected
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideParticipantParameters")
    fun `to player should return player participant ui`(
        side: Side,
        isWhite: Boolean,
        isSelected: Boolean
    ) {
        val result = tested.toPlayer(
            side = side,
            isSelected = isSelected
        )

        assertThat(result).isEqualTo(
            ParticipantUi(
                nameResId = RFrontitude.string.common_label_you,
                isWhite = isWhite,
                isSelected = isSelected
            )
        )
    }

    @ParameterizedTest
    @MethodSource("provideToSquareParameters")
    fun `map position ui to square`(position: PositionUi, square: Square) {
        val result = tested.toSquare(position)
        assertThat(result).isEqualTo(square)
    }

    @Test
    fun `toGameplayDialogUi when game status is stopped maps to game menu dialog ui with suggestions on`() {
        val result = tested.toGameplayDialogUi(
            STOPPED,
            mockk(),
            isMoveSuggestionsOn = true,
            isPromotionManualConfirmationRequired = false,
            checkInfo = null
        )

        assertThat(result).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true))
    }

    @Test
    fun `toGameplayDialogUi when game status is stopped maps to game menu dialog ui with suggestions off`() {
        val result = tested.toGameplayDialogUi(
            STOPPED,
            mockk(),
            isMoveSuggestionsOn = false,
            isPromotionManualConfirmationRequired = false,
            checkInfo = null
        )

        assertThat(result).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = false))
    }

    @Test
    fun `toGameplayDialogUi when game status is stopped in two player mode maps to game menu dialog ui with two player mode`() {
        val result = tested.toGameplayDialogUi(
            STOPPED,
            mockk(),
            isMoveSuggestionsOn = true,
            isPromotionManualConfirmationRequired = false,
            checkInfo = null,
            isTwoPlayerMode = true
        )

        assertThat(result).isEqualTo(GameMenuDialogUi(isMoveSuggestionsOn = true, isTwoPlayerMode = true))
    }

    @Test
    fun `toGameplayDialogUi when promotion confirmation is required maps to pawn promotion dialog ui for with side`() {
        val result = tested.toGameplayDialogUi(
            STARTED,
            WHITE,
            isMoveSuggestionsOn = false,
            isPromotionManualConfirmationRequired = true,
            checkInfo = null
        )

        assertThat(result).isEqualTo(
            PawnPromotionDialogUi(
                setOf(
                    PieceUi(QUEEN, isWhite = true),
                    PieceUi(ROOK, isWhite = true),
                    PieceUi(BISHOP, isWhite = true),
                    PieceUi(KNIGHT, isWhite = true)
                )
            )
        )
    }

    @Test
    fun `toGameplayDialogUi when promotion confirmation is required maps to pawn promotion dialog ui for black side`() {
        val result = tested.toGameplayDialogUi(
            STARTED,
            BLACK,
            isMoveSuggestionsOn = false,
            isPromotionManualConfirmationRequired = true,
            checkInfo = null
        )

        assertThat(result).isEqualTo(
            PawnPromotionDialogUi(
                setOf(
                    PieceUi(QUEEN, isWhite = false),
                    PieceUi(ROOK, isWhite = false),
                    PieceUi(BISHOP, isWhite = false),
                    PieceUi(KNIGHT, isWhite = false)
                )
            )
        )
    }

    @Test
    fun `toGameplayDialogUi when check acknowledge required is true maps to check info dialog ui`() {
        val checkInfo = CheckInfo(
            king = LocatedPiece(WHITE_KING, E1),
            attackedBy = listOf(LocatedPiece(BLACK_BISHOP, H4)),
            acknowledgeRequired = true
        )

        val result = tested.toGameplayDialogUi(
            STARTED,
            WHITE,
            isMoveSuggestionsOn = false,
            isPromotionManualConfirmationRequired = false,
            checkInfo = checkInfo
        )

        assertThat(result).isEqualTo(
            CheckInfoDialogUi(
                king = PieceUi(type = KING, isWhite = true),
                attackedBy = listOf(PieceUi(type = BISHOP, isWhite = false))
            )
        )
    }

    @Test
    fun `toGameplayDialogUi when check acknowledge required is false maps null dialog ui`() {
        val checkInfo = CheckInfo(
            king = LocatedPiece(WHITE_KING, E1),
            attackedBy = listOf(LocatedPiece(BLACK_BISHOP, H4)),
            acknowledgeRequired = false
        )

        val result = tested.toGameplayDialogUi(
            STARTED,
            WHITE,
            isMoveSuggestionsOn = false,
            isPromotionManualConfirmationRequired = false,
            checkInfo = checkInfo
        )

        assertThat(result).isNull()
    }

    @Test
    fun `toEndgameUi when game status is white won maps to a white win result`() {
        val result = tested.toEndgameUi(status = WHITE_WON)

        assertThat(result).isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_whitewins))
    }

    @Test
    fun `toEndgameUi when game status is black won maps to a black win result`() {
        val result = tested.toEndgameUi(status = BLACK_WON)

        assertThat(result).isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_blackwins))
    }

    @Test
    fun `toEndgameUi when game status is draw maps to a draw result`() {
        val result = tested.toEndgameUi(status = DRAW)

        assertThat(result).isEqualTo(EndgameUi(RFrontitude.string.chess_endingscreen_dialog_h1_itsadraw))
    }

    @Test
    fun `toMoveArgs maps move backups to move args`() {
        val moveBackups = listOf(
            mockMoveBackup(WHITE_PAWN, Move(E2, E4)),
            mockMoveBackup(BLACK_PAWN, Move(C7, C6)),
            mockMoveBackup(WHITE_ROOK, Move(H1, H4)),
            mockMoveBackup(BLACK_ROOK, Move(A8, A5)),
            mockMoveBackup(WHITE_KNIGHT, Move(B1, C3)),
            mockMoveBackup(BLACK_KNIGHT, Move(G8, F6)),
            mockMoveBackup(WHITE_BISHOP, Move(F1, B5)),
            mockMoveBackup(BLACK_BISHOP, Move(C8, F5)),
            mockMoveBackup(WHITE_QUEEN, Move(D1, D4)),
            mockMoveBackup(BLACK_QUEEN, Move(D8, D5)),
            mockMoveBackup(WHITE_KING, Move(E1, C1)),
            mockMoveBackup(BLACK_KING, Move(E8, G8)),
            mockMoveBackup(WHITE_PAWN, Move(D7, D8, WHITE_QUEEN)),
            mockMoveBackup(BLACK_PAWN, Move(E2, E1, WHITE_QUEEN))
        )

        val args = tested.toMoveArgs(moveBackups)

        assertThat(args).isEqualTo(
            listOf(
                MoveArg("P", "e2e4"),
                MoveArg("p", "c7c6"),
                MoveArg("R", "h1h4"),
                MoveArg("r", "a8a5"),
                MoveArg("N", "b1c3"),
                MoveArg("n", "g8f6"),
                MoveArg("B", "f1b5"),
                MoveArg("b", "c8f5"),
                MoveArg("Q", "d1d4"),
                MoveArg("q", "d8d5"),
                MoveArg("K", "e1c1"),
                MoveArg("k", "e8g8"),
                MoveArg("P", "d7d8q"),
                MoveArg("p", "e2e1q")
            )
        )
    }

    private fun mockMoveBackup(piece: Piece, move: Move): MoveBackup = mockk<MoveBackup> {
        every { getMovingPiece() } returns piece
        every { getMove() } returns move
    }

    companion object {
        @JvmStatic
        fun provideParticipantParameters(): Stream<Arguments> =
            Stream.of(
                Arguments.of(WHITE, true, false),
                Arguments.of(WHITE, true, true),
                Arguments.of(BLACK, false, false),
                Arguments.of(BLACK, false, true),
            )

        @JvmStatic
        fun provideToSquareParameters(): Stream<Arguments> =
            WHITE_PLAYER_POSITION_UIS.flatten().zip(WHITE_PLAYER_SQUARES)
                .map { (position, square) ->
                    Arguments.of(position, square)
                }.stream()
    }
}
