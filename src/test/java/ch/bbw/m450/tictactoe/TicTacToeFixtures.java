package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

/**
 * Shared test helpers and fixtures for the TicTacToe tests.
 *
 * <p>Helpers build board states from a compact string notation, while the
 * fixtures expose ready-to-use board constants for common scenarios so the
 * individual tests stay short and readable.</p>
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
}
