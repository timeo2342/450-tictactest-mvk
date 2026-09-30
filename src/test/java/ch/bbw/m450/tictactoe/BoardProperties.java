package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
import static org.assertj.core.api.Assertions.assertThat;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class BoardProperties {

	@Property(tries = 1000, seed = "4502026")
	void winDetectionAgreesWithIndependentGeometricOracle(@ForAll("boards") String cells) {
		var board = boardOf(cells);
		var before = board.clone();
		for (var color : Stone.values()) {
			assertThat(TicTacToeMain.isWin(board, color))
					.as("winner %s on board %s", color, cells)
					.isEqualTo(hasLine(cells, color == Stone.CROSS ? 'X' : 'O'));
		}
		assertThat(board).containsExactly(before);
	}

	@Provide
	Arbitrary<String> boards() {
		return Arbitraries.strings().withChars('.', 'X', 'O').ofLength(9);
	}

	private static boolean hasLine(String cells, char stone) {
		for (var index = 0; index < 3; index++) {
			if (cells.substring(index * 3, index * 3 + 3).equals(String.valueOf(stone).repeat(3))) {
				return true;
			}
			if (cells.charAt(index) == stone && cells.charAt(index + 3) == stone
					&& cells.charAt(index + 6) == stone) {
				return true;
			}
		}
		return cells.charAt(4) == stone
				&& ((cells.charAt(0) == stone && cells.charAt(8) == stone)
				|| (cells.charAt(2) == stone && cells.charAt(6) == stone));
	}
}
