package reversi;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {

    private static final Move[] BLACK_PASS_GAME = {
            new Move(3, 2, Mark.BLACK), new Move(3, 1, Mark.WHITE),
            new Move(0, 0, Mark.BLACK), new Move(1, 3, Mark.WHITE),
            new Move(3, 3, Mark.BLACK), new Move(2, 3, Mark.WHITE),
            new Move(1, 0, Mark.BLACK), new Move(0, 2, Mark.WHITE),
            new Move(3, 0, Mark.BLACK), new Move(2, 0, Mark.WHITE),
            new Move(0, 1, Mark.BLACK), new Move(0, 3, Mark.BLACK)
    };

    private static final Move[] BLACK_EARLY_GAME = {
            new Move(2, 3, Mark.BLACK), new Move(1, 3, Mark.WHITE),
            new Move(0, 3, Mark.BLACK), new Move(3, 1, Mark.WHITE),
            new Move(0, 0, Mark.BLACK), new Move(0, 1, Mark.WHITE),
            new Move(2, 0, Mark.BLACK), new Move(3, 3, Mark.WHITE),
            new Move(3, 2, Mark.BLACK), new Move(0, 2, Mark.BLACK),
            new Move(3, 0, Mark.BLACK)
    };

    private static final Move[] WHITE_EARLY_GAME = {
            new Move(1, 0, Mark.BLACK), new Move(0, 2, Mark.WHITE),
            new Move(3, 3, Mark.BLACK), new Move(2, 0, Mark.WHITE),
            new Move(0, 1, Mark.BLACK), new Move(2, 3, Mark.WHITE),
            new Move(3, 1, Mark.BLACK), new Move(0, 0, Mark.WHITE),
            new Move(1, 3, Mark.BLACK), new Move(3, 2, Mark.WHITE),
            new Move(3, 0, Mark.WHITE)
    };

    private static final Move[] DRAW_GAME = {
            new Move(1, 0, Mark.BLACK), new Move(2, 0, Mark.WHITE),
            new Move(3, 0, Mark.BLACK), new Move(0, 2, Mark.WHITE),
            new Move(0, 3, Mark.BLACK), new Move(0, 0, Mark.WHITE),
            new Move(2, 3, Mark.BLACK), new Move(3, 3, Mark.WHITE),
            new Move(3, 2, Mark.BLACK), new Move(1, 3, Mark.WHITE),
            new Move(0, 1, Mark.BLACK), new Move(3, 1, Mark.WHITE)
    };

    private static void play(Game game, Move... moves) {
        for (Move move : moves) {
            assertThat(game.apply(move)).isTrue();
        }
    }

    private static void playFirst(Game game, Move[] moves, int length) {
        for (int i = 0; i < length; i++) {
            assertThat(game.apply(moves[i])).isTrue();
        }
    }

    @Test
    void shouldCreateDefaultGameWithBlackTurn() {
        // when
        Game game = new Game();
        // then
        assertThat(game.board().size()).isEqualTo(8);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.currentPlayer()).isEqualTo(Mark.BLACK);
        assertThat(game.history()).isEmpty();
        assertThat(game.lastFlipped()).isEmpty();
    }

    @Test
    void shouldCreateGameWithCustomEvenBoardSize() {
        // when
        Game game4 = new Game(4);
        Game game6 = new Game(6);
        // then
        assertThat(game4.board().size()).isEqualTo(4);
        assertThat(game6.board().size()).isEqualTo(6);
        assertThat(game4.state()).isEqualTo(GameState.BLACK_TURN);
    }

    @Test
    void shouldRejectInvalidGameSize() {
        assertThrows(IllegalArgumentException.class, () -> new Game(3));
        assertThrows(IllegalArgumentException.class, () -> new Game(5));
    }

    @Test
    void shouldReturnInitialLegalMovesForBlack() {
        // given
        Game game = new Game();
        // then
        assertThat(game.availableMoves()).isDeepEqualTo(new int[][]{
                {2, 3}, {3, 2}, {4, 5}, {5, 4}
        });
    }

    @Test
    void shouldSwitchTurnAfterBlackMove() {
        // given
        Game game = new Game();
        // when
        boolean result = game.apply(new Move(2, 3, Mark.BLACK));
        // then
        assertThat(result).isTrue();
        assertThat(game.state()).isEqualTo(GameState.WHITE_TURN);
        assertThat(game.currentPlayer()).isEqualTo(Mark.WHITE);
        assertThat(game.history()).containsExactly(new Move(2, 3, Mark.BLACK));
    }

    @Test
    void shouldSwitchTurnAfterWhiteMove() {
        // given
        Game game = new Game();
        // when
        play(game, new Move(2, 3, Mark.BLACK), new Move(2, 2, Mark.WHITE));
        // then
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.history()).hasSize(2);
    }

    @Test
    void shouldOnlyShowMovesForCurrentPlayer() {
        // given
        Game game = new Game(4);
        // when
        play(game, new Move(1, 0, Mark.BLACK));
        // then
        assertThat(game.availableMoves()).isDeepEqualTo(game.board().availableMoves(Mark.WHITE));
        assertThat(game.availableMoves()).isNotEmpty();
    }

    @Test
    void shouldRejectWrongTurnWithoutChangingGame() {
        // given
        Game game = new Game();
        Mark[][] before = game.board().toArray();
        // when
        boolean result = game.apply(new Move(2, 4, Mark.WHITE));
        // then
        assertThat(result).isFalse();
        assertThat(game.board().toArray()).isDeepEqualTo(before);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldRejectNullMove() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(null)).isFalse();
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldRejectNullMark() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(new Move(2, 3, null))).isFalse();
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
    }

    @Test
    void shouldRejectEmptyMark() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(new Move(2, 3, Mark.EMPTY))).isFalse();
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldRejectNonCapturingMove() {
        // given
        Game game = new Game();
        Mark[][] before = game.board().toArray();
        // then
        assertThat(game.apply(new Move(0, 0, Mark.BLACK))).isFalse();
        assertThat(game.board().toArray()).isDeepEqualTo(before);
    }

    @Test
    void shouldRejectOccupiedCell() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(new Move(3, 3, Mark.BLACK))).isFalse();
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldRejectNegativeCoordinates() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(new Move(-1, 3, Mark.BLACK))).isFalse();
        assertThat(game.apply(new Move(2, -1, Mark.BLACK))).isFalse();
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldRejectOutOfBoundsCoordinates() {
        // given
        Game game = new Game();
        // then
        assertThat(game.apply(new Move(8, 3, Mark.BLACK))).isFalse();
        assertThat(game.apply(new Move(2, 8, Mark.BLACK))).isFalse();
        assertThat(game.history()).isEmpty();
    }

    @Test
    void shouldPreserveTurnAfterRejectedMove() {
        // given
        Game game = new Game();
        // when
        game.apply(new Move(0, 0, Mark.BLACK));
        game.apply(new Move(3, 3, Mark.BLACK));
        // then
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.apply(new Move(2, 3, Mark.BLACK))).isTrue();
        assertThat(game.state()).isEqualTo(GameState.WHITE_TURN);
    }

    @Test
    void shouldCountInitialScore() {
        // given
        Game game = new Game();
        // then
        assertThat(game.score(Mark.BLACK)).isEqualTo(2);
        assertThat(game.score(Mark.WHITE)).isEqualTo(2);
        assertThat(game.score(Mark.EMPTY)).isZero();
        assertThat(game.score(null)).isZero();
    }

    @Test
    void shouldUpdateScoreAfterCapturing() {
        // given
        Game game = new Game();
        // when
        play(game, new Move(2, 3, Mark.BLACK));
        // then
        assertThat(game.score(Mark.BLACK)).isEqualTo(4);
        assertThat(game.score(Mark.WHITE)).isEqualTo(1);
    }

    @Test
    void shouldUpdateScoreAfterOppositePlayerCaptures() {
        // given
        Game game = new Game();
        // when
        play(game, new Move(2, 3, Mark.BLACK), new Move(2, 2, Mark.WHITE));
        // then
        assertThat(game.score(Mark.BLACK)).isEqualTo(3);
        assertThat(game.score(Mark.WHITE)).isEqualTo(3);
    }

    @Test
    void shouldReturnFirstFlippedDisc() {
        // given
        Game game = new Game();
        // when
        game.apply(new Move(2, 3, Mark.BLACK));
        // then
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{{3, 3}});
    }

    @Test
    void shouldReturnFlipsFromLastMoveOnly() {
        // given
        Game game = new Game();
        // when
        play(game, new Move(2, 3, Mark.BLACK), new Move(2, 2, Mark.WHITE));
        // then
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{{3, 3}});
        assertThat(game.board().getCell(3, 3)).isEqualTo(Mark.WHITE);
    }

    @Test
    void shouldReturnMultipleFlippedDiscs() {
        // given
        Game game = new Game(4);
        // when
        play(game, BLACK_PASS_GAME);
        // then
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{
                {0, 2}, {1, 2}, {1, 3}, {2, 3}
        });
        assertThat(game.score(Mark.BLACK)).isEqualTo(14);
        assertThat(game.score(Mark.WHITE)).isEqualTo(2);
    }

    @Test
    void shouldReturnIndependentLastFlippedArray() {
        // given
        Game game = new Game();
        play(game, new Move(2, 3, Mark.BLACK));
        // when
        int[][] captured = game.lastFlipped();
        captured[0][0] = -100;
        // then
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{{3, 3}});
    }

    @Test
    void shouldReturnDefensiveBoardCopy() {
        // given
        Game game = new Game();
        Board snapshot = game.board();
        // when
        assertThat(snapshot.place(2, 3, Mark.BLACK)).isTrue();
        // then
        assertThat(game.board().toArray()[2][3]).isEqualTo(Mark.EMPTY);
        assertThat(game.history()).isEmpty();
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
    }

    @Test
    void shouldReturnDefensiveHistoryCopy() {
        // given
        Game game = new Game();
        Move first = new Move(2, 3, Mark.BLACK);
        play(game, first);
        // when
        Move[] history = game.history();
        history[0] = new Move(0, 0, Mark.WHITE);
        // then
        assertThat(game.history()).containsExactly(first);
    }

    @Test
    void shouldRecordOnlySuccessfulMoves() {
        // given
        Game game = new Game();
        Move first = new Move(2, 3, Mark.BLACK);
        // when
        assertThat(game.apply(new Move(0, 0, Mark.BLACK))).isFalse();
        assertThat(game.apply(first)).isTrue();
        assertThat(game.apply(first)).isFalse();
        // then
        assertThat(game.history()).containsExactly(first);
    }

    @Test
    void shouldAutomaticallyPassWhenOpponentHasNoMoves() {
        // given
        Game game = new Game(4);
        // when
        playFirst(game, BLACK_PASS_GAME, 11);
        // then
        assertThat(game.history()).hasSize(11);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.board().availableMoves(Mark.WHITE)).isEmpty();
        assertThat(game.availableMoves()).isDeepEqualTo(new int[][]{{0, 3}});
    }

    @Test
    void shouldAllowSamePlayerToMoveAfterAutomaticPass() {
        // given
        Game game = new Game(4);
        playFirst(game, BLACK_PASS_GAME, 11);
        // when
        boolean result = game.apply(new Move(0, 3, Mark.BLACK));
        // then
        assertThat(result).isTrue();
        assertThat(game.history()).hasSize(12);
        assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
    }

    @Test
    void shouldPassBlackWhenBlackHasNoMoves() {
        // given
        Game game = new Game(4);
        // when
        playFirst(game, WHITE_EARLY_GAME, 10);
        // then
        assertThat(game.state()).isEqualTo(GameState.WHITE_TURN);
        assertThat(game.board().availableMoves(Mark.BLACK)).isEmpty();
        assertThat(game.availableMoves()).isDeepEqualTo(new int[][]{{3, 0}});
    }

    @Test
    void shouldEndWithBlackWinOnFullBoard() {
        // given
        Game game = new Game(4);
        // when
        play(game, BLACK_PASS_GAME);
        // then
        assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
        assertThat(game.board().full()).isTrue();
        assertThat(game.score(Mark.BLACK)).isEqualTo(14);
        assertThat(game.score(Mark.WHITE)).isEqualTo(2);
    }

    @Test
    void shouldEndWithBlackWinOnNonFullBoard() {
        // given
        Game game = new Game(4);
        // when
        play(game, BLACK_EARLY_GAME);
        // then
        assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
        assertThat(game.board().full()).isFalse();
        assertThat(game.board().count(Mark.EMPTY)).isEqualTo(1);
        assertThat(game.score(Mark.BLACK)).isEqualTo(14);
        assertThat(game.score(Mark.WHITE)).isEqualTo(1);
    }

    @Test
    void shouldEndWithWhiteWinOnNonFullBoard() {
        // given
        Game game = new Game(4);
        // when
        play(game, WHITE_EARLY_GAME);
        // then
        assertThat(game.state()).isEqualTo(GameState.WHITE_WON);
        assertThat(game.board().full()).isFalse();
        assertThat(game.board().count(Mark.EMPTY)).isEqualTo(1);
        assertThat(game.score(Mark.BLACK)).isEqualTo(3);
        assertThat(game.score(Mark.WHITE)).isEqualTo(12);
    }

    @Test
    void shouldDetectDrawByEqualDiscCounts() {
        // given
        Game game = new Game(4);
        // when
        play(game, DRAW_GAME);
        // then
        assertThat(game.state()).isEqualTo(GameState.DRAW);
        assertThat(game.board().full()).isTrue();
        assertThat(game.score(Mark.BLACK)).isEqualTo(8);
        assertThat(game.score(Mark.WHITE)).isEqualTo(8);
    }

    @Test
    void shouldHaveNoCurrentPlayerAfterGameEnds() {
        // given
        Game game = new Game(4);
        // when
        play(game, DRAW_GAME);
        // then
        assertThat(game.currentPlayer()).isEqualTo(Mark.EMPTY);
        assertThat(game.availableMoves()).isEmpty();
    }

    @Test
    void shouldRejectAnyMoveAfterGameIsOver() {
        // given
        Game game = new Game(4);
        play(game, BLACK_EARLY_GAME);
        Mark[][] before = game.board().toArray();
        Move[] history = game.history();
        // when
        boolean result = game.apply(new Move(1, 0, Mark.WHITE));
        // then
        assertThat(result).isFalse();
        assertThat(game.board().toArray()).isDeepEqualTo(before);
        assertThat(game.history()).containsExactly(history);
        assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
    }

    @Test
    void shouldNotUndoEmptyHistory() {
        // given
        Game game = new Game();
        // then
        assertThat(game.undoLast()).isFalse();
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.board().count(Mark.EMPTY)).isEqualTo(60);
    }

    @Test
    void shouldUndoFirstMoveAndRestoreInitialState() {
        // given
        Game game = new Game();
        Mark[][] before = game.board().toArray();
        play(game, new Move(2, 3, Mark.BLACK));
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.board().toArray()).isDeepEqualTo(before);
        assertThat(game.history()).isEmpty();
        assertThat(game.lastFlipped()).isEmpty();
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.score(Mark.BLACK)).isEqualTo(2);
        assertThat(game.score(Mark.WHITE)).isEqualTo(2);
    }

    @Test
    void shouldUndoWhiteMoveAndRestoreLastFlips() {
        // given
        Game game = new Game();
        play(game, new Move(2, 3, Mark.BLACK), new Move(2, 2, Mark.WHITE));
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.state()).isEqualTo(GameState.WHITE_TURN);
        assertThat(game.score(Mark.BLACK)).isEqualTo(4);
        assertThat(game.score(Mark.WHITE)).isEqualTo(1);
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{{3, 3}});
        assertThat(game.history()).hasSize(1);
    }

    @Test
    void shouldUndoMultiDirectionCapture() {
        // given
        Game game = new Game(4);
        playFirst(game, BLACK_PASS_GAME, 11);
        Mark[][] before = game.board().toArray();
        play(game, BLACK_PASS_GAME[11]);
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.board().toArray()).isDeepEqualTo(before);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.lastFlipped()).isDeepEqualTo(new int[][]{{1, 1}, {2, 1}});
    }

    @Test
    void shouldUndoMoveAfterAutomaticPass() {
        // given
        Game game = new Game(4);
        playFirst(game, BLACK_PASS_GAME, 11);
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.history()).hasSize(10);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.apply(BLACK_PASS_GAME[10])).isTrue();
    }

    @Test
    void shouldUndoWinningMoveAndReopenGame() {
        // given
        Game game = new Game(4);
        play(game, BLACK_EARLY_GAME);
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.currentPlayer()).isEqualTo(Mark.BLACK);
        assertThat(game.availableMoves()).isNotEmpty();
        assertThat(game.apply(BLACK_EARLY_GAME[10])).isTrue();
        assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
    }

    @Test
    void shouldUndoDrawAndReopenGame() {
        // given
        Game game = new Game(4);
        play(game, DRAW_GAME);
        // when
        boolean result = game.undoLast();
        // then
        assertThat(result).isTrue();
        assertThat(game.state()).isEqualTo(GameState.WHITE_TURN);
        assertThat(game.apply(DRAW_GAME[11])).isTrue();
        assertThat(game.state()).isEqualTo(GameState.DRAW);
    }

    @Test
    void shouldUndoEntireGameAndRestoreInitialBoard() {
        // given
        Game game = new Game(4);
        Mark[][] initial = game.board().toArray();
        play(game, DRAW_GAME);
        // when
        for (int i = 0; i < DRAW_GAME.length; i++) {
            assertThat(game.undoLast()).isTrue();
        }
        // then
        assertThat(game.undoLast()).isFalse();
        assertThat(game.board().toArray()).isDeepEqualTo(initial);
        assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(game.history()).isEmpty();
        assertThat(game.lastFlipped()).isEmpty();
    }

    @Test
    void shouldCopyInitialGameIndependently() {
        // given
        Game original = new Game();
        Game copy = new Game(original);
        // when
        assertThat(copy.apply(new Move(2, 3, Mark.BLACK))).isTrue();
        // then
        assertThat(original.history()).isEmpty();
        assertThat(original.state()).isEqualTo(GameState.BLACK_TURN);
        assertThat(original.board().getCell(2, 3)).isEqualTo(Mark.EMPTY);
        assertThat(copy.history()).hasSize(1);
    }

    @Test
    void shouldCopyNonEmptyGameIndependently() {
        // given
        Game original = new Game(4);
        playFirst(original, BLACK_PASS_GAME, 4);
        Game copy = new Game(original);
        Mark[][] before = original.board().toArray();
        // when
        assertThat(copy.apply(BLACK_PASS_GAME[4])).isTrue();
        // then
        assertThat(copy.history()).hasSize(5);
        assertThat(original.history()).hasSize(4);
        assertThat(original.board().toArray()).isDeepEqualTo(before);
        assertThat(copy.state()).isEqualTo(GameState.WHITE_TURN);
    }

    @Test
    void shouldCopyFlipsForIndependentUndo() {
        // given
        Game original = new Game(4);
        playFirst(original, BLACK_PASS_GAME, 11);
        Game copy = new Game(original);
        Mark[][] originalBefore = original.board().toArray();
        // when
        assertThat(copy.undoLast()).isTrue();
        // then
        assertThat(original.board().toArray()).isDeepEqualTo(originalBefore);
        assertThat(original.history()).hasSize(11);
        assertThat(copy.history()).hasSize(10);
        assertThat(original.lastFlipped()).isDeepEqualTo(new int[][]{{1, 1}, {2, 1}});
    }

    @Test
    void shouldCopyFinishedGameAndReopenOnlyCopy() {
        // given
        Game original = new Game(4);
        play(original, DRAW_GAME);
        Game copy = new Game(original);
        // when
        assertThat(copy.undoLast()).isTrue();
        // then
        assertThat(copy.state()).isEqualTo(GameState.WHITE_TURN);
        assertThat(original.state()).isEqualTo(GameState.DRAW);
        assertThat(original.board().full()).isTrue();
    }

    @Test
    void shouldApplyAllLegalMovesWithoutBreakingDiscInvariants() {
        // given
        Random random = new Random(0x5EED);
        // when & then
        for (int size : new int[]{4, 6, 8}) {
            for (int run = 0; run < 12; run++) {
                Game game = new Game(size);
                while (game.currentPlayer() != Mark.EMPTY) {
                    int[][] legal = game.availableMoves();
                    assertThat(legal).isNotEmpty();
                    Mark mark = game.currentPlayer();
                    int[] coords = legal[random.nextInt(legal.length)];
                    assertThat(game.apply(new Move(coords[0], coords[1], mark))).isTrue();
                    int occupied = game.score(Mark.BLACK) + game.score(Mark.WHITE);
                    assertThat(occupied).isEqualTo(game.history().length + 4);
                    assertThat(occupied + game.board().count(Mark.EMPTY)).isEqualTo(size * size);
                }
                assertThat(game.board().availableMoves(Mark.BLACK)).isEmpty();
                assertThat(game.board().availableMoves(Mark.WHITE)).isEmpty();
                int black = game.score(Mark.BLACK);
                int white = game.score(Mark.WHITE);
                if (black > white) {
                    assertThat(game.state()).isEqualTo(GameState.BLACK_WON);
                } else if (white > black) {
                    assertThat(game.state()).isEqualTo(GameState.WHITE_WON);
                } else {
                    assertThat(game.state()).isEqualTo(GameState.DRAW);
                }
            }
        }
    }

    @Test
    void shouldReplayRandomGamesAfterUndoingAllMoves() {
        // given
        Random random = new Random(20261112);
        // when & then
        for (int run = 0; run < 20; run++) {
            Game game = new Game(run % 2 == 0 ? 4 : 6);
            Mark[][] initial = game.board().toArray();
            List<Move> moves = new ArrayList<>();
            while (game.currentPlayer() != Mark.EMPTY) {
                int[][] legal = game.availableMoves();
                int[] coord = legal[random.nextInt(legal.length)];
                Move move = new Move(coord[0], coord[1], game.currentPlayer());
                assertThat(game.apply(move)).isTrue();
                moves.add(move);
            }
            Mark[][] result = game.board().toArray();
            GameState finish = game.state();
            for (int i = 0; i < moves.size(); i++) {
                assertThat(game.undoLast()).isTrue();
            }
            assertThat(game.board().toArray()).isDeepEqualTo(initial);
            assertThat(game.state()).isEqualTo(GameState.BLACK_TURN);
            play(game, moves.toArray(new Move[0]));
            assertThat(game.board().toArray()).isDeepEqualTo(result);
            assertThat(game.state()).isEqualTo(finish);
        }
    }
    @Test
    void shouldCompleteStandard8x8GameWithDrawAfterSixtyMoves() {
        // given: заранее известная корректная партия на стандартной доске
        int[][] coordinates = {
                {2, 3}, {4, 2}, {5, 5}, {2, 4}, {2, 5}, {6, 6},
                {5, 2}, {1, 5}, {5, 4}, {2, 2}, {4, 1}, {4, 5},
                {4, 6}, {5, 6}, {3, 6}, {2, 6}, {1, 4}, {3, 5},
                {2, 1}, {2, 0}, {1, 7}, {2, 7}, {7, 7}, {6, 4},
                {5, 3}, {0, 5}, {1, 1}, {6, 1}, {6, 3}, {7, 3},
                {3, 1}, {0, 7}, {0, 4}, {0, 2}, {1, 2}, {4, 0},
                {0, 0}, {0, 1}, {3, 0}, {1, 3}, {4, 7}, {5, 1},
                {6, 5}, {5, 7}, {6, 7}, {0, 3}, {1, 0}, {7, 6},
                {7, 5}, {7, 4}, {6, 0}, {3, 2}, {7, 2}, {5, 0},
                {6, 2}, {7, 0}, {3, 7}, {7, 1}, {0, 6}, {1, 6}
        };
        Game game = new Game();
        // when
        for (int i = 0; i < coordinates.length; i++) {
            Mark mark = (i % 2 == 0) ? Mark.BLACK : Mark.WHITE;
            assertThat(game.currentPlayer()).isEqualTo(mark);
            assertThat(game.apply(new Move(coordinates[i][0], coordinates[i][1], mark))).isTrue();
        }
        // then
        assertThat(game.history()).hasSize(60);
        assertThat(game.board().full()).isTrue();
        assertThat(game.score(Mark.BLACK)).isEqualTo(32);
        assertThat(game.score(Mark.WHITE)).isEqualTo(32);
        assertThat(game.state()).isEqualTo(GameState.DRAW);
    }

}
