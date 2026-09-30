package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.StdIn;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

@Tag("e2e")
@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class TicTacToeE2ETest {

	@Test
	@StdIo({"0", "2", "4", "6"})
	void main_crossWinsFromConsoleInput(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString())
				.startsWith(TicTacToeMain.toString(emptyBoard()) + "where to put the next CROSS? (0-8): ")
				.endsWith(TicTacToeMain.toString(boardOf("XOXOXOX.."))
						+ "...and the winner is: CROSS" + System.lineSeparator());
		assertPromptCount(out, 4);
	}

	@Test
	@StdIo({"8", "7", "3"})
	void main_circleWinsFromConsoleInput(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString())
				.startsWith(TicTacToeMain.toString(emptyBoard()))
				.endsWith(TicTacToeMain.toString(boardOf("OOOX...XX"))
						+ "...and the winner is: CIRCLE" + System.lineSeparator());
		assertPromptCount(out, 3);
	}

	@Test
	@StdIo({"1", "3", "4", "6", "8"})
	void main_drawShowsTheFinalBoardAndResult(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString())
				.startsWith(TicTacToeMain.toString(emptyBoard()))
				.endsWith(TicTacToeMain.toString(boardOf("OXOXXOXOX"))
						+ "it's a draw!" + System.lineSeparator())
				.doesNotContain("the winner is:");
		assertPromptCount(out, 5);
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "abc", " ", "--1", "1e0", "1.5", "2147483648", "-2147483649"})
	@StdIo
	void main_recoversFromMalformedInput(String input, StdOut out) {
		runWithInput(input, "0", "2", "4", "6");

		assertRecoveredCrossWin(out, "Please enter a whole number from 0 to 8.", 5);
	}

	@ParameterizedTest
	@ValueSource(strings = {"-1", "9", "2147483647", "-2147483648"})
	@StdIo
	void main_recoversFromOutOfRangeConsoleMoves(String input, StdOut out) {
		runWithInput(input, "0", "2", "4", "6");

		assertRecoveredCrossWin(out, "Please choose a position from 0 to 8.", 5);
	}

	@Test
	@StdIo({"0", "1", "0", "2", "4", "6"})
	void main_recoversFromOccupiedCellsWithoutLosingTheTurn(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertRecoveredCrossWin(out, "That position is already occupied.", 6);
		assertThat(out.capturedString().split("That position is already occupied.", -1)).hasSize(3);
	}

	@ParameterizedTest(name = "malicious input case {index}")
	@ValueSource(strings = {"$(echo injected)", "0; echo injected", "<script>alert(1)</script>",
			"' OR '1'='1", "\u0000", "\033[2J", "../secret"})
	@StdIo
	void main_treatsMaliciousInputAsInvalidText(String input, StdOut out) {
		runWithInput(input, "0", "2", "4", "6");

		assertRecoveredCrossWin(out, "Please enter a whole number from 0 to 8.", 5);
		assertThat(out.capturedString()).doesNotContain(input);
	}

	@Test
	@StdIo
	void main_rejectsImmediateEndOfInput(StdOut out) {
		assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
				.isInstanceOf(NoSuchElementException.class);

		assertOnlyInitialPrompt(out);
	}

	@Test
	@StdIo("0")
	void main_rejectsDisconnectedInputDuringTheGame(StdOut out) {
		assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
				.isInstanceOf(NoSuchElementException.class);

		assertThat(out.capturedString())
				.contains(TicTacToeMain.toString(boardOf("XO.......")))
				.doesNotContain("the winner is:", "it's a draw!");
		assertPromptCount(out, 2);
	}

	@Test
	@StdIo
	void main_rejectsALargeNumericPayloadWithinTimeout(StdOut out) {
		runWithInput("9".repeat(1_048_576), "0", "2", "4", "6");

		assertRecoveredCrossWin(out, "Please enter a whole number from 0 to 8.", 5);
		assertThat(out.capturedString().length()).isLessThan(4096);
	}

	@Test
	@StdIo
	void main_recoversFromRepeatedInvalidLinesWithoutRecursiveRetries(StdOut out) {
		runWithInput("invalid\n".repeat(1000) + "0", "2", "4", "6");

		assertRecoveredCrossWin(out, "Please enter a whole number from 0 to 8.", 1004);
		assertThat(out.capturedString().split("Please enter a whole number from 0 to 8.", -1)).hasSize(1001);
		assertThat(out.capturedString().length()).isLessThan(512_000);
	}

	@Test
	@StdIo({" 0 ", "\t2", "4\t", " 6"})
	void main_acceptsWhitespaceAroundConsoleMoves(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString()).endsWith("...and the winner is: CROSS" + System.lineSeparator())
				.doesNotContain("Please", "occupied");
		assertPromptCount(out, 4);
	}

	@Test
	@StdIo({"abc", "9"})
	void main_stopsRetryingWhenTheInputEnds(StdOut out) {
		assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
				.isInstanceOf(NoSuchElementException.class);

		assertThat(out.capturedString()).contains("Please enter", "Please choose")
				.doesNotContain("the winner is:", "it's a draw!");
		assertPromptCount(out, 3);
	}

	@Test
	@StdIo({"0", "2", "4", "6", "invalid"})
	void main_doesNotReadMoreInputAfterTheGameEnds(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString()).endsWith("...and the winner is: CROSS" + System.lineSeparator());
		assertPromptCount(out, 4);
	}

	private void runWithInput(String... lines) {
		// @StdIo holds the stream lock and restores System.in after every invocation.
		System.setIn(new StdIn(lines));
		TicTacToeMain.main(new String[0]);
	}

	private void assertOnlyInitialPrompt(StdOut out) {
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(emptyBoard())
				+ "where to put the next CROSS? (0-8): " + System.lineSeparator());
	}

	private void assertPromptCount(StdOut out, int expected) {
		assertThat(out.capturedString().lines().filter(line -> line.startsWith("where to put")).count())
				.isEqualTo(expected);
	}

	private void assertRecoveredCrossWin(StdOut out, String message, int prompts) {
		assertThat(out.capturedString())
				.startsWith(TicTacToeMain.toString(emptyBoard()))
				.contains(message)
				.endsWith(TicTacToeMain.toString(boardOf("XOXOXOX.."))
						+ "...and the winner is: CROSS" + System.lineSeparator());
		assertPromptCount(out, prompts);
	}
}
