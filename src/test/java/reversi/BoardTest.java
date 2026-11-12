package reversi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    private static final Move[] MULTI_DIRECTION_GAME = {
            new Move(3, 2, Mark.BLACK), new Move(3, 1, Mark.WHITE),
            new Move(0, 0, Mark.BLACK), new Move(1, 3, Mark.WHITE),
            new Move(3, 3, Mark.BLACK), new Move(2, 3, Mark.WHITE),
            new Move(1, 0, Mark.BLACK), new Move(0, 2, Mark.WHITE),
            new Move(3, 0, Mark.BLACK), new Move(2, 0, Mark.WHITE),
            new Move(0, 1, Mark.BLACK), new Move(0, 3, Mark.BLACK)
    };

    @Test
    void shouldCreateDefault8x8BoardWithFourDiscs() {
        // when
        Board board = new Board();
        // then
        assertThat(board.size()).isEqualTo(8);
        assertThat(board.count(Mark.BLACK)).isEqualTo(2);
        assertThat(board.count(Mark.WHITE)).isEqualTo(2);
        assertThat(board.count(Mark.EMPTY)).isEqualTo(60);
        assertThat(board.getCell(3, 3)).isEqualTo(Mark.WHITE);
        assertThat(board.getCell(3, 4)).isEqualTo(Mark.BLACK);
        assertThat(board.getCell(4, 3)).isEqualTo(Mark.BLACK);
        assertThat(board.getCell(4, 4)).isEqualTo(Mark.WHITE);
    }

    @Test
    void shouldCreate4x4BoardWithCenteredDiscs() {
        // when
        Board board = new Board(4);
        // then
        assertThat(board.size()).isEqualTo(4);
        assertThat(board.toArray()).isDeepEqualTo(new Mark[][]{
                {Mark.EMPTY, Mark.EMPTY, Mark.EMPTY, Mark.EMPTY},
                {Mark.EMPTY, Mark.WHITE, Mark.BLACK, Mark.EMPTY},
                {Mark.EMPTY, Mark.BLACK, Mark.WHITE, Mark.EMPTY},
                {Mark.EMPTY, Mark.EMPTY, Mark.EMPTY, Mark.EMPTY}
        });
    }

    @Test
    void shouldCreateOtherEvenSizes() {
        // when
        Board board6 = new Board(6);
        Board board10 = new Board(10);
        // then
        assertThat(board6.count(Mark.EMPTY)).isEqualTo(32);
        assertThat(board10.count(Mark.EMPTY)).isEqualTo(96);
        assertThat(board6.getCell(2, 2)).isEqualTo(Mark.WHITE);
        assertThat(board10.getCell(4, 5)).isEqualTo(Mark.BLACK);
    }

    @Test
    void shouldRejectNonPositiveBoardSizes() {
        assertThrows(IllegalArgumentException.class, () -> new Board(0));
        assertThrows(IllegalArgumentException.class, () -> new Board(-2));
    }

    @Test
    void shouldRejectBoardsSmallerThanFour() {
        assertThrows(IllegalArgumentException.class, () -> new Board(2));
    }

    @Test
    void shouldRejectOddBoardSizes() {
        assertThrows(IllegalArgumentException.class, () -> new Board(5));
        assertThrows(IllegalArgumentException.class, () -> new Board(7));
    }

    @Test
    void shouldDescribeInvalidBoardSizeInException() {
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Board(5));
        // then
        assertThat(exception).hasMessage("Board size must be even and at least 4 "
                + "and divided by two without a remainder");
    }

    @Test
    void shouldListInitialBlackMovesInRowMajorOrder() {
        // given
        Board board = new Board();
        // then
        assertThat(board.availableMoves(Mark.BLACK)).isDeepEqualTo(new int[][]{
                {2, 3}, {3, 2}, {4, 5}, {5, 4}
        });
    }

    @Test
    void shouldListInitialWhiteMovesInRowMajorOrder() {
        // given
        Board board = new Board();
        // then
        assertThat(board.availableMoves(Mark.WHITE)).isDeepEqualTo(new int[][]{
                {2, 4}, {3, 5}, {4, 2}, {5, 3}
        });
    }

    @Test
    void shouldListFourMovesOn4x4Board() {
        // given
        Board board = new Board(4);
        // then
        assertThat(board.availableMoves(Mark.BLACK)).isDeepEqualTo(new int[][]{
                {0, 1}, {1, 0}, {2, 3}, {3, 2}
        });
    }

    @Test
    void shouldRejectNullAndEmptyInMoveDiscovery() {
        // given
        Board board = new Board();
        // then
        assertThat(board.availableMoves(null)).isEmpty();
        assertThat(board.availableMoves(Mark.EMPTY)).isEmpty();
    }

    @Test
    void shouldPlaceBlackAndFlipOneWhiteDisc() {
        // given
        Board board = new Board();
        // when
        boolean result = board.place(2, 3, Mark.BLACK);
        // then
        assertThat(result).isTrue();
        assertThat(board.getCell(2, 3)).isEqualTo(Mark.BLACK);
        assertThat(board.getCell(3, 3)).isEqualTo(Mark.BLACK);
        assertThat(board.count(Mark.BLACK)).isEqualTo(4);
        assertThat(board.count(Mark.WHITE)).isEqualTo(1);
    }

    @Test
    void shouldFlipBlackDiscsAfterWhiteTurn() {
        // given
        Board board = new Board();
        board.place(2, 3, Mark.BLACK);
        // when
        boolean result = board.place(2, 2, Mark.WHITE);
        // then
        assertThat(result).isTrue();
        assertThat(board.getCell(2, 2)).isEqualTo(Mark.WHITE);
        assertThat(board.getCell(3, 3)).isEqualTo(Mark.WHITE);
        assertThat(board.count(Mark.BLACK)).isEqualTo(3);
        assertThat(board.count(Mark.WHITE)).isEqualTo(3);
    }

    @Test
    void shouldRejectPlacementWhenNoOpponentDiscIsCaptured() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // when
        boolean result = board.place(0, 0, Mark.BLACK);
        // then
        assertThat(result).isFalse();
        assertThat(board.toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectPlacementOnOccupiedDisc() {
        // given
        Board board = new Board();
        // then
        assertThat(board.place(3, 3, Mark.BLACK)).isFalse();
        assertThat(board.place(3, 3, Mark.WHITE)).isFalse();
        assertThat(board.getCell(3, 3)).isEqualTo(Mark.WHITE);
    }

    @Test
    void shouldRejectNullMarkWithoutChangingBoard() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // then
        assertThat(board.place(2, 3, null)).isFalse();
        assertThat(board.toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectEmptyMarkWithoutChangingBoard() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // then
        assertThat(board.place(2, 3, Mark.EMPTY)).isFalse();
        assertThat(board.toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectNegativeCoordinates() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // then
        assertThat(board.place(-1, 3, Mark.BLACK)).isFalse();
        assertThat(board.place(3, -1, Mark.BLACK)).isFalse();
        assertThat(board.place(-1, -1, Mark.WHITE)).isFalse();
        assertThat(board.toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectCoordinatesOutsideBoard() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // then
        assertThat(board.place(8, 3, Mark.BLACK)).isFalse();
        assertThat(board.place(3, 8, Mark.BLACK)).isFalse();
        assertThat(board.place(100, 100, Mark.BLACK)).isFalse();
        assertThat(board.toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectUnclosedOpponentLine() {
        // given
        Board board = new Board(4);
        // when
        boolean result = board.place(0, 0, Mark.WHITE);
        // then
        assertThat(result).isFalse();
        assertThat(board.getCell(0, 0)).isEqualTo(Mark.EMPTY);
    }

    @Test
    void shouldFlipInAllEightDirectionsAcrossLegalGame() {
        // given
        Board board = new Board(4);
        boolean[][] found = new boolean[3][3];
        // when
        for (Move move : MULTI_DIRECTION_GAME) {
            int[][] flipped = board.flippedCells(move.row(), move.col(), move.mark());
            assertThat(flipped).isNotEmpty();
            for (int[] cell : flipped) {
                int dr = Integer.compare(cell[0], move.row());
                int dc = Integer.compare(cell[1], move.col());
                found[dr + 1][dc + 1] = true;
            }
            assertThat(board.place(move.row(), move.col(), move.mark())).isTrue();
        }
        // then
        for (int dr = 0; dr < 3; dr++) {
            for (int dc = 0; dc < 3; dc++) {
                if (dr != 1 || dc != 1) {
                    assertThat(found[dr][dc]).isTrue();
                }
            }
        }
    }

    @Test
    void shouldFlipSeveralDiscsInOneDirection() {
        // given
        Board board = new Board(4);
        for (int i = 0; i < 10; i++) {
            assertThat(board.place(MULTI_DIRECTION_GAME[i].row(),
                    MULTI_DIRECTION_GAME[i].col(), MULTI_DIRECTION_GAME[i].mark())).isTrue();
        }
        // when
        int[][] flipped = board.flippedCells(0, 1, Mark.BLACK);
        boolean result = board.place(0, 1, Mark.BLACK);
        // then
        assertThat(flipped).isDeepEqualTo(new int[][]{{1, 1}, {2, 1}});
        assertThat(result).isTrue();
        assertThat(board.getCell(1, 1)).isEqualTo(Mark.BLACK);
        assertThat(board.getCell(2, 1)).isEqualTo(Mark.BLACK);
    }

    @Test
    void shouldFlipMultipleDirectionsInOneMove() {
        // given
        Board board = new Board(4);
        for (int i = 0; i < MULTI_DIRECTION_GAME.length - 1; i++) {
            Move move = MULTI_DIRECTION_GAME[i];
            assertThat(board.place(move.row(), move.col(), move.mark())).isTrue();
        }
        // when
        int[][] flipped = board.flippedCells(0, 3, Mark.BLACK);
        boolean result = board.place(0, 3, Mark.BLACK);
        // then
        assertThat(flipped).isDeepEqualTo(new int[][]{
                {0, 2}, {1, 2}, {1, 3}, {2, 3}
        });
        assertThat(result).isTrue();
        assertThat(board.count(Mark.BLACK)).isEqualTo(14);
        assertThat(board.count(Mark.WHITE)).isEqualTo(2);
    }

    @Test
    void shouldReturnDeepCopyOfBoardArray() {
        // given
        Board board = new Board();
        Mark[][] snapshot = board.toArray();
        // when
        snapshot[3][3] = Mark.EMPTY;
        snapshot[0][0] = Mark.BLACK;
        // then
        assertThat(board.getCell(3, 3)).isEqualTo(Mark.WHITE);
        assertThat(board.getCell(0, 0)).isEqualTo(Mark.EMPTY);
    }

    @Test
    void shouldReturnIndependentArraysOfAvailableMoves() {
        // given
        Board board = new Board();
        int[][] moves = board.availableMoves(Mark.BLACK);
        // when
        moves[0][0] = -1;
        moves[0][1] = -1;
        // then
        assertThat(board.availableMoves(Mark.BLACK)[0]).containsExactly(2, 3);
    }

    @Test
    void shouldReturnIndependentArraysOfFlippedCells() {
        // given
        Board board = new Board();
        int[][] flipped = board.flippedCells(2, 3, Mark.BLACK);
        // when
        flipped[0][0] = -1;
        // then
        assertThat(board.flippedCells(2, 3, Mark.BLACK)).isDeepEqualTo(new int[][]{{3, 3}});
    }

    @Test
    void shouldCopyBoardIndependently() {
        // given
        Board original = new Board(4);
        Board copy = new Board(original);
        // when
        assertThat(copy.place(0, 1, Mark.BLACK)).isTrue();
        // then
        assertThat(original.getCell(0, 1)).isEqualTo(Mark.EMPTY);
        assertThat(copy.getCell(0, 1)).isEqualTo(Mark.BLACK);
        assertThat(original.count(Mark.BLACK)).isEqualTo(2);
    }

    @Test
    void shouldCopyBoardAfterSeveralMoves() {
        // given
        Board original = new Board(4);
        original.place(0, 1, Mark.BLACK);
        original.place(0, 0, Mark.WHITE);
        // when
        Board copy = new Board(original);
        // then
        assertThat(copy.toArray()).isDeepEqualTo(original.toArray());
        assertThat(copy.availableMoves(Mark.BLACK)).isDeepEqualTo(original.availableMoves(Mark.BLACK));
    }

    @Test
    void shouldDetectFullBoardAfterCompleteGame() {
        // given
        Board board = new Board(4);
        // when
        for (Move move : MULTI_DIRECTION_GAME) {
            assertThat(board.place(move.row(), move.col(), move.mark())).isTrue();
        }
        // then
        assertThat(board.full()).isTrue();
        assertThat(board.count(Mark.EMPTY)).isZero();
        assertThat(board.availableMoves(Mark.BLACK)).isEmpty();
        assertThat(board.availableMoves(Mark.WHITE)).isEmpty();
    }

    @Test
    void shouldNotBeFullAfterOnlyOneMove() {
        // given
        Board board = new Board();
        // then
        assertThat(board.full()).isFalse();
        board.place(2, 3, Mark.BLACK);
        assertThat(board.full()).isFalse();
    }

    @Test
    void shouldHandleNullCounts() {
        // given
        Board board = new Board();
        // then
        assertThat(board.count(null)).isZero();
    }

    @Test
    void shouldNotMutateBoardWhenCheckingLegalMoves() {
        // given
        Board board = new Board();
        Mark[][] before = board.toArray();
        // when
        board.flippedCells(2, 3, Mark.BLACK);
        board.availableMoves(Mark.BLACK);
        board.availableMoves(Mark.WHITE);
        // then
        assertThat(board.toArray()).isDeepEqualTo(before);
    }
}
