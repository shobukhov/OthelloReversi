package reversi;

import java.util.ArrayList;
import java.util.List;

class Board {
    private static final int DEFAULT_SIZE = 8;
    private static final int[][] DIRS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    private final int size;
    private final Mark[][] boardState;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        if (size < 4 || size % 2 != 0) {
            throw new IllegalArgumentException("Board size must be even and at least 4 and divided by two without a remainder");
        }
        this.size = size;
        boardState = new Mark[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boardState[i][j] = Mark.EMPTY;
            }
        }
        int mid = size / 2;
        boardState[mid - 1][mid - 1] = Mark.WHITE;
        boardState[mid][mid] = Mark.WHITE;
        boardState[mid - 1][mid] = Mark.BLACK;
        boardState[mid][mid - 1] = Mark.BLACK;
    }

    public Board(Board other) {
        size = other.size;
        boardState = new Mark[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boardState[i][j] = other.boardState[i][j];
            }
        }
    }

    public int size() {
        return size;
    }

    public Mark[][] toArray() {
        Mark[][] copy = new Mark[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy[i][j] = boardState[i][j];
            }
        }
        return copy;
    }

    public boolean place(int row, int col, Mark mark) {
        return placeAndGetFlipped(row, col, mark).length > 0;
    }

    int[][] placeAndGetFlipped(int row, int col, Mark mark) {
        int[][] flipped = flippedCells(row, col, mark);
        if (flipped.length == 0) {
            return flipped;
        }
        boardState[row][col] = mark;
        for (int[] cell : flipped) {
            boardState[cell[0]][cell[1]] = mark;
        }
        return flipped;
    }

    int[][] flippedCells(int row, int col, Mark mark) {
        if (mark == null || mark == Mark.EMPTY || !isWithin(row, col) || boardState[row][col] != Mark.EMPTY) {
            return new int[0][2];
        }
        Mark opponent = (mark == Mark.BLACK) ? Mark.WHITE : Mark.BLACK;
        List<int[]> flipped = new ArrayList<>();
        for (int[] dir : DIRS) {
            int r = row + dir[0];
            int c = col + dir[1];
            int length = 0;
            //сначала должна идти хотя бы одна фишка соперника
            while (isWithin(r, c) && boardState[r][c] == opponent) {
                length++;
                r += dir[0];
                c += dir[1];
            }
            //захват происходит только если цепочка замкнута своей фишкой
            if (length > 0 && isWithin(r, c) && boardState[r][c] == mark) {
                for (int i = 1; i <= length; i++) {
                    flipped.add(new int[]{row + dir[0] * i, col + dir[1] * i});
                }
            }
        }
        return flipped.toArray(new int[0][]);
    }

    public int[][] availableMoves(Mark mark) {
        if (mark == null || mark == Mark.EMPTY) {
            return new int[0][2];
        }
        List<int[]> moves = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (flippedCells(row, col, mark).length > 0) {
                    moves.add(new int[]{row, col});
                }
            }
        }
        return moves.toArray(new int[0][]);
    }

    public boolean full() {
        return count(Mark.EMPTY) == 0;
    }

    public int count(Mark mark) {
        if (mark == null) {
            return 0;
        }
        int result = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (boardState[row][col] == mark) {
                    result++;
                }
            }
        }
        return result;
    }

    void undo(Move move, int[][] flipped) {
        boardState[move.row()][move.col()] = Mark.EMPTY;
        Mark opponent = (move.mark() == Mark.BLACK) ? Mark.WHITE : Mark.BLACK;
        for (int[] cell : flipped) {
            boardState[cell[0]][cell[1]] = opponent;
        }
    }

    Mark getCell(int row, int col) {
        return boardState[row][col];
    }

    private boolean isWithin(int row, int col) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }
}

