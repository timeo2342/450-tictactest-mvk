package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

/**
 * Shared test helpers and fixtures for the TicTacToe tests.
 *
 * <p>Helpers build board states from a compact string notation, while the
 * fixtures expose ready-to-use board constants and argument providers for
 * common scenarios so the individual tests stay short and readable.</p>
 */
final class TicTacToeFixtures {

	private TicTacToeFixtures() {
		// utility class, no instances
	}

	// --- Helpers ---------------------------------------------------------

	/**
	 * Builds a board from a compact 9-character string.
	 * <ul>
	 *     <li>'X' -&gt; {@link Stone#CROSS}</li>
	 *     <li>'O' -&gt; {@link Stone#CIRCLE}</li>
	 *     <li>any other char (e.g. '.') -&gt; {@code null} (empty field)</li>
	 * </ul>
	 *
	 * @param cells a string of length {@link TicTacToeMain#BOARD_SIZE}
	 * @return the parsed board
	 */
	static Stone[] boardOf(String cells) {
		if (cells.length() != TicTacToeMain.BOARD_SIZE) {
			throw new IllegalArgumentException(
					"board string must have exactly " + TicTacToeMain.BOARD_SIZE + " characters");
		}
		var board = new Stone[TicTacToeMain.BOARD_SIZE];
		for (var i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
			var c = cells.charAt(i);
			board[i] = c == 'X' ? Stone.CROSS : c == 'O' ? Stone.CIRCLE : null;
		}
		return board;
	}

	// --- Fixtures --------------------------------------------------------

	/** A completely empty board. */
	static Stone[] emptyBoard() {
		return boardOf(".........");
	}

	/** Three crosses in the top row (0, 1, 2). */
	static Stone[] crossWinningRow() {
		return boardOf("XXX" + "..." + "...");
	}

	/** Three circles in the left column (0, 3, 6). */
	static Stone[] circleWinningColumn() {
		return boardOf("O.." + "O.." + "O..");
	}

	// --- Argument providers for parameterized tests ----------------------

	/**
	 * Winning boards for {@link Stone#CROSS}: every row, column and diagonal.
	 * Each argument is (boardString, expectedWinner).
	 */
	static Stream<Arguments> winningBoardsForCross() {
		return Stream.of(
				Arguments.of("XXX......", Stone.CROSS), // top row
				Arguments.of("...XXX...", Stone.CROSS), // middle row
				Arguments.of("......XXX", Stone.CROSS), // bottom row
				Arguments.of("X..X..X..", Stone.CROSS), // left column
				Arguments.of(".X..X..X.", Stone.CROSS), // middle column
				Arguments.of("..X..X..X", Stone.CROSS), // right column
				Arguments.of("X...X...X", Stone.CROSS), // main diagonal
				Arguments.of("..X.X.X..", Stone.CROSS)  // anti diagonal
		);
	}

	/**
	 * Boards that are NOT a win for the given color.
	 * Each argument is (boardString, colorToCheck).
	 */
	static Stream<Arguments> nonWinningBoards() {
		return Stream.of(
				Arguments.of(".........", Stone.CROSS),  // empty
				Arguments.of("XX.......", Stone.CROSS),  // only two in a row
				Arguments.of("XOXOXOOXO", Stone.CROSS),  // full board, no cross line
				Arguments.of("XXX......", Stone.CIRCLE), // cross wins, but we check circle
				Arguments.of("XO.XO.O.X", Stone.CIRCLE)  // scattered, no circle line
		);
	}
}
