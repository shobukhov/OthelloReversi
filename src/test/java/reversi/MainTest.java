package reversi;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

    private String run(String input) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            Main.run(new BufferedReader(new StringReader(input)), out);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void shouldAskForBoardSizeBeforeStartingGame() throws Exception {
        // when
        String output = run("8\nquit\n");
        // then
        assertThat(output).contains("Введите размер поля", "Введите строку и столбец (1-8)");
        assertThat(output).contains("Отелло (Реверси)");
        assertThat(output).contains("черные(Black)>");
    }

    @Test
    void shouldShowInitialBoardScoreAndLegalMovesLegend() throws Exception {
        // when
        String output = run("8\nquit\n");
        // then
        assertThat(output).contains("B: 2  W: 2");
        assertThat(output).contains("* - допустимые ходы");
        assertThat(output).containsPattern("B: 2  W: 2\\R\\* - допустимые ходы");
    }

    @Test
    void shouldMakeValidMoveFromOneBasedCoordinates() throws Exception {
        // when
        String output = run("8\n3 4\nquit\n");
        // then
        assertThat(output).contains("B: 4  W: 1");
        assertThat(output).contains("белые(White)>");
    }

    @Test
    void shouldPrintBothPlayerPromptsInRussianAndEnglish() throws Exception {
        // when
        String output = run("8\n3 4\nquit\n");
        // then
        assertThat(output).contains("черные(Black)>", "белые(White)>");
        assertThat(output).doesNotContain("Чёрные>", "Белые>");
    }

    @Test
    void shouldUseFourByFourBoardWhenSelected() throws Exception {
        // when
        String output = run("4\n1 2\nquit\n");
        // then
        assertThat(output).contains("Введите строку и столбец (1-4)");
        assertThat(output).contains("B: 4  W: 1");
        assertThat(output).contains("белые(White)>");
    }

    @Test
    void shouldUseSelectedSizeInMovePromptForLargerBoard() throws Exception {
        // when
        String output = run("10\nquit\n");
        // then
        assertThat(output).contains("Введите строку и столбец (1-10)");
        assertThat(output).contains("B: 2  W: 2");
    }

    @Test
    void shouldRejectOddAndTooSmallBoardSizeAndRetry() throws Exception {
        // when
        String output = run("3\n5\n2\n4\nquit\n");
        // then
        assertThat(output).contains("Размер поля должен быть чётным числом не меньше 4");
        assertThat(output).contains("Введите строку и столбец (1-4)");
        assertThat(output).contains("B: 2  W: 2");
    }

    @Test
    void shouldRejectNonNumericBoardSizeAndRetry() throws Exception {
        // when
        String output = run("hello\n\n4.5\n8\nquit\n");
        // then
        assertThat(output).contains("Размер поля должен быть целым числом");
        assertThat(output).contains("Введите строку и столбец (1-8)");
        assertThat(output).contains("B: 2  W: 2");
    }

    @Test
    void shouldExitWhenQuitEnteredBeforeBoardSize() throws Exception {
        // when
        String output = run("quit\n");
        // then
        assertThat(output).contains("Введите размер поля");
        assertThat(output).doesNotContain("черные(Black)>", "B: 2  W: 2");
    }

    @Test
    void shouldExitOnEndOfInputBeforeBoardSize() throws Exception {
        // when
        String output = run("");
        // then
        assertThat(output).contains("Введите размер поля");
        assertThat(output).doesNotContain("черные(Black)>", "B: 2  W: 2");
    }

    @Test
    void shouldReportInvalidMove() throws Exception {
        // when
        String output = run("8\n1 1\nquit\n");
        // then
        assertThat(output).contains("Недопустимый ход");
        assertThat(output).contains("B: 2  W: 2");
    }

    @Test
    void shouldSupportUndoCommand() throws Exception {
        // when
        String output = run("8\n3 4\nundo\nquit\n");
        // then
        assertThat(output).contains("Последний ход отменён");
        assertThat(output).contains("B: 4  W: 1");
        assertThat(output).contains("B: 2  W: 2");
    }

    @Test
    void shouldRejectInvalidMoveInputAndContinue() throws Exception {
        // when
        String output = run("8\nhello\nabc 2\n3 4\nquit\n");
        // then
        assertThat(output).contains("Формат хода");
        assertThat(output).contains("должны быть целыми числами");
        assertThat(output).contains("B: 4  W: 1");
    }

    @Test
    void shouldExitOnEndOfInputDuringGame() throws Exception {
        // when
        String output = run("8\n");
        // then
        assertThat(output).contains("черные(Black)>");
    }
}
