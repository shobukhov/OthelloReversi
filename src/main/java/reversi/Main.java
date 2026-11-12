package reversi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        run(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    static void run(BufferedReader reader, PrintStream out) throws IOException {
        out.println("Отелло (Реверси). Чёрные ходят первыми");
        Integer size = readBoardSize(reader, out);
        if (size == null) {
            return;
        }
        Game game = new Game(size);
        out.printf("Введите строку и столбец (1-%d), undo для отмены или quit для выхода%n", size);
        while (true) {
            printBoard(game, out);
            if (game.currentPlayer() == Mark.EMPTY) {
                printResult(game, out);
                return;
            }
            out.print((game.currentPlayer() == Mark.BLACK ? "черные(Black)" : "белые(White)") + "> ");
            out.flush();
            String line = reader.readLine();
            if (line == null || line.trim().equalsIgnoreCase("quit")) {
                return;
            }
            if (line.trim().equalsIgnoreCase("undo")) {
                out.println(game.undoLast() ? "Последний ход отменён" : "История ходов пуста");
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            if (parts.length != 2) {
                out.println("Формат хода: строка столбец, например 3 4");
                continue;
            }
            try {
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                Mark player = game.currentPlayer();
                if (!game.apply(new Move(row, col, player))) {
                    out.println("Недопустимый ход. Нужно захватить хотя бы одну фишку противника");
                } else if (game.currentPlayer() == player) {
                    out.println("У соперника нет допустимых ходов: он пропускает ход");
                }
            } catch (NumberFormatException e) {
                out.println("Строка и столбец должны быть целыми числами");
            }
        }
    }

    private static Integer readBoardSize(BufferedReader reader, PrintStream out) throws IOException {
        while (true) {
            out.print("Введите размер поля (чётное число от 4, например 8) или quit для выхода: ");
            out.flush();
            String line = reader.readLine();
            if (line == null || line.trim().equalsIgnoreCase("quit")) {
                return null;
            }
            try {
                int size = Integer.parseInt(line.trim());
                if (size < 4 || size % 2 != 0) {
                    out.println("Размер поля должен быть чётным числом не меньше 4");
                    continue;
                }
                return size;
            } catch (NumberFormatException e) {
                out.println("Размер поля должен быть целым числом");
            }
        }
    }

    private static void printBoard(Game game, PrintStream out) {
        Board board = game.board();
        Mark[][] cells = board.toArray();
        int size = board.size();
        out.println();
        out.print("   ");
        for (int col = 1; col <= size; col++) {
            out.printf("%2d ", col);
        }
        out.println();
        int[][] legal = game.availableMoves();
        for (int row = 0; row < size; row++) {
            out.printf("%2d ", row + 1);
            for (int col = 0; col < size; col++) {
                char symbol = switch (cells[row][col]) {
                    case BLACK -> 'B';
                    case WHITE -> 'W';
                    case EMPTY -> isLegal(legal, row, col) ? '*' : '.';
                };
                out.printf("%2c ", symbol);
            }
            out.println();
        }
        out.printf("B: %d  W: %d%n", game.score(Mark.BLACK), game.score(Mark.WHITE));
        out.println("* - допустимые ходы");
    }

    private static boolean isLegal(int[][] moves, int row, int col) {
        for (int[] move : moves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    private static void printResult(Game game, PrintStream out) {
        switch (game.state()) {
            case BLACK_WON -> out.println("Победили чёрные");
            case WHITE_WON -> out.println("Победили белые");
            case DRAW -> out.println("Ничья");
            default -> throw new IllegalStateException("Game is not finished");
        }
    }
}

