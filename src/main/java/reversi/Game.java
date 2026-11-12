package reversi;

import java.util.ArrayList;
import java.util.List;

class Game {
    private static final int DEFAULT_GAME_SIZE = 8;

    private final Board board;
    private final List<Move> moveHistory;
    private final List<int[][]> flipHistory;
    private GameState gameStatus;

    public Game() {
        this(DEFAULT_GAME_SIZE);
    }

    public Game(int size) {
        board = new Board(size);
        moveHistory = new ArrayList<>();
        flipHistory = new ArrayList<>();
        gameStatus = GameState.BLACK_TURN;
    }

    public Game(Game other) {
        board = new Board(other.board);
        moveHistory = new ArrayList<>(other.moveHistory);
        flipHistory = new ArrayList<>();
        for (int[][] flipped : other.flipHistory) {
            flipHistory.add(copyCells(flipped));
        }
        gameStatus = other.gameStatus;
    }

    public Board board() {
        return new Board(board);
    }

    public Move[] history() {
        return moveHistory.toArray(new Move[0]);
    }

    public GameState state() {
        return gameStatus;
    }

    public Mark currentPlayer() {
        return switch (gameStatus) {
            case BLACK_TURN -> Mark.BLACK;
            case WHITE_TURN -> Mark.WHITE;
            default -> Mark.EMPTY;
        };
    }

    public int[][] availableMoves() {
        return board.availableMoves(currentPlayer());
    }

    public int score(Mark mark) {
        return (mark == Mark.BLACK || mark == Mark.WHITE) ? board.count(mark) : 0;
    }

    public boolean apply(Move move) {
        if (!checkGameInProgress() || !checkMarkMatchTurn(move)) {
            return false;
        }
        int[][] flipped = board.placeAndGetFlipped(move.row(), move.col(), move.mark());
        if (flipped.length == 0) {
            return false;
        }
        moveHistory.add(move);
        flipHistory.add(flipped);
        updateGameStatus(move.mark());
        return true;
    }

    private void updateGameStatus(Mark justPlayed) {
        Mark next = opposite(justPlayed);
        if (board.availableMoves(next).length > 0) {
            gameStatus = turnOf(next);
            return;
        }
        if (board.availableMoves(justPlayed).length > 0) {
            gameStatus = turnOf(justPlayed);
            return;
        }
        int black = score(Mark.BLACK);
        int white = score(Mark.WHITE);
        if (black > white) {
            gameStatus = GameState.BLACK_WON;
        } else if (white > black) {
            gameStatus = GameState.WHITE_WON;
        } else {
            gameStatus = GameState.DRAW;
        }
    }

    private boolean checkGameInProgress() {
        return gameStatus == GameState.BLACK_TURN || gameStatus == GameState.WHITE_TURN;
    }

    private boolean checkMarkMatchTurn(Move move) {
        return move != null && move.mark() == currentPlayer();
    }

    private static GameState turnOf(Mark mark) {
        return (mark == Mark.BLACK) ? GameState.BLACK_TURN : GameState.WHITE_TURN;
    }

    private static Mark opposite(Mark mark) {
        return (mark == Mark.BLACK) ? Mark.WHITE : Mark.BLACK;
    }

    public boolean undoLast() {
        if (moveHistory.isEmpty()) {
            return false;
        }
        Move last = moveHistory.removeLast();
        int[][] flipped = flipHistory.removeLast();
        board.undo(last, flipped);
        gameStatus = turnOf(last.mark());
        return true;
    }

    public int[][] lastFlipped() {
        if (flipHistory.isEmpty()) {
            return new int[0][2];
        }
        return copyCells(flipHistory.getLast());
    }

    private static int[][] copyCells(int[][] cells) {
        int[][] copy = new int[cells.length][2];
        for (int i = 0; i < cells.length; i++) {
            copy[i][0] = cells[i][0];
            copy[i][1] = cells[i][1];
        }
        return copy;
    }
}



