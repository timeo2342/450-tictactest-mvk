package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.circleWinningColumn;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.crossWinningRow;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Five tests for the TicTacToe game logic in {@link TicTacToeMain}.
 *
 * <p>Board states are provided via {@link TicTacToeFixtures}, player
 * instances via the {@link #setUp()} fixture, so each test stays focused on
 * the GIVEN-WHEN-THEN it verifies.</p>
 */
class TicTacToeMainTest {

	// player fixtures, freshly created before each test
	private TicTacToePlayer xPlayer;
	private TicTacToePlayer oPlayer;

	@BeforeEach
	void setUp() {
		xPlayer = new GreedyPlayer();
		oPlayer = new GreedyPlayer();
	}

	@Test
	void isWin_detectsWinningRow() {
		// GIVEN a board with three crosses in the top row
		var board = crossWinningRow();
		// WHEN checking for a CROSS win
		// THEN it is a win
		assertThat(TicTacToeMain.isWin(board, Stone.CROSS)).isTrue();
	}

	@Test
	void isWin_detectsWinningColumn() {
		// GIVEN a board with three circles in the left column
		var board = circleWinningColumn();
		// WHEN checking for a CIRCLE win
		// THEN it is a win
		assertThat(TicTacToeMain.isWin(board, Stone.CIRCLE)).isTrue();
	}

	@Test
	void isWin_returnsFalseForEmptyBoard() {
		// GIVEN a completely empty board
		var board = emptyBoard();
		// WHEN checking for any win
		// THEN neither color wins
		assertThat(TicTacToeMain.isWin(board, Stone.CROSS)).isFalse();
		assertThat(TicTacToeMain.isWin(board, Stone.CIRCLE)).isFalse();
	}

	@Test
	void play_twoGreedyPlayersResultInCrossWinner() {
		// GIVEN two greedy players (both always play the top-most free field)
		// WHEN a full game is played
		var winner = TicTacToeMain.play(xPlayer, oPlayer);
		// THEN the starting player (CROSS) wins
		assertThat(winner).isEqualTo(Stone.CROSS);
	}

	@Test
	void play_rejectsIdenticalPlayers() {
		// GIVEN a single player instance used for both sides
		// WHEN starting a game with the same instance twice
		// THEN an IllegalArgumentException is thrown
		assertThatThrownBy(() -> TicTacToeMain.play(xPlayer, xPlayer))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("players must differ");
	}
}
