package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import org.junit.jupiter.api.Test;

/**
 * Five tests for the TicTacToe game logic in {@link TicTacToeMain}.
 */
class TicTacToeMainTest {

	/**
	 * Helper to build a board from a compact string.
	 * 'X' -> CROSS, 'O' -> CIRCLE, anything else -> null (empty).
	 */
	private static Stone[] boardOf(String cells) {
		var board = new Stone[TicTacToeMain.BOARD_SIZE];
		for (var i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
			var c = cells.charAt(i);
			board[i] = c == 'X' ? Stone.CROSS : c == 'O' ? Stone.CIRCLE : null;
		}
		return board;
	}

	@Test
	void isWin_detectsWinningRow() {
		// GIVEN a board with three crosses in the top row
		var board = boardOf("XXX" + "..." + "...");
		// WHEN checking for a CROSS win
		// THEN it is a win
		assertThat(TicTacToeMain.isWin(board, Stone.CROSS)).isTrue();
	}

	@Test
	void isWin_detectsWinningColumn() {
		// GIVEN a board with three circles in the left column
		var board = boardOf("O.." + "O.." + "O..");
		// WHEN checking for a CIRCLE win
		// THEN it is a win
		assertThat(TicTacToeMain.isWin(board, Stone.CIRCLE)).isTrue();
	}

	@Test
	void isWin_returnsFalseForEmptyBoard() {
		// GIVEN a completely empty board
		var board = boardOf(".........");
		// WHEN checking for any win
		// THEN neither color wins
		assertThat(TicTacToeMain.isWin(board, Stone.CROSS)).isFalse();
		assertThat(TicTacToeMain.isWin(board, Stone.CIRCLE)).isFalse();
	}

	@Test
	void play_twoGreedyPlayersResultInCrossWinner() {
		// GIVEN two greedy players (both always play the top-most free field)
		var xPlayer = new GreedyPlayer();
		var oPlayer = new GreedyPlayer();
		// WHEN a full game is played
		var winner = TicTacToeMain.play(xPlayer, oPlayer);
		// THEN the starting player (CROSS) wins
		assertThat(winner).isEqualTo(Stone.CROSS);
	}

	@Test
	void play_rejectsIdenticalPlayers() {
		// GIVEN a single player instance used for both sides
		var player = new GreedyPlayer();
		// WHEN starting a game with the same instance twice
		// THEN an IllegalArgumentException is thrown
		assertThatThrownBy(() -> TicTacToeMain.play(player, player))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("players must differ");
	}
}
