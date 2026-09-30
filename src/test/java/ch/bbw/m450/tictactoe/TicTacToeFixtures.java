package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

final class TicTacToeFixtures {

	private static final String[] WINNING_LINES = {
			"XXX......", "...XXX...", "......XXX",
			"X..X..X..", ".X..X..X.", "..X..X..X",
			"X...X...X", "..X.X.X.."
	};

	private TicTacToeFixtures() {
	}

	static Stone[] boardOf(String cells) {
		if (cells.length() != TicTacToeMain.BOARD_SIZE) {
			throw new IllegalArgumentException(
					"board string must have exactly " + TicTacToeMain.BOARD_SIZE + " characters");
		}
		var board = new Stone[TicTacToeMain.BOARD_SIZE];
		for (var i = 0; i < board.length; i++) {
			var cell = cells.charAt(i);
			board[i] = cell == 'X' ? Stone.CROSS : cell == 'O' ? Stone.CIRCLE : null;
		}
		return board;
	}

	static Stone[] emptyBoard() {
		return boardOf(".........");
	}

	static TicTacToePlayer scriptedPlayer(int... moves) {
		var remainingMoves = Arrays.stream(moves).iterator();
		return (board, color) -> remainingMoves.nextInt();
	}

	static Stream<Arguments> winningBoards() {
		return Arrays.stream(Stone.values()).flatMap(color -> Arrays.stream(WINNING_LINES)
				.map(line -> Arguments.of(forColor(line, color), color)));
	}

	static Stream<Arguments> incompleteWinningLines() {
		return Arrays.stream(Stone.values()).flatMap(color -> Arrays.stream(WINNING_LINES)
				.flatMap(line -> IntStream.range(0, line.length())
						.filter(index -> line.charAt(index) == 'X')
						.boxed()
						.flatMap(index -> Stream.of('.', 'O').map(replacement -> {
							var cells = line.toCharArray();
							cells[index] = replacement;
							return Arguments.of(forColor(new String(cells), color), color);
						}))));
	}

	private static String forColor(String cells, Stone color) {
		return color == Stone.CROSS ? cells : cells.replace('X', '_').replace('O', 'X').replace('_', 'O');
	}
}
