package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.PerfectPlayer;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class PerfectPlayerTest {

	@ParameterizedTest
	@EnumSource(Stone.class)
	void play_neverLosesAgainstAnyLegalOpponentSequence(Stone color) {
		var terminalGames = verifyAllOpponentSequences(
				new PerfectPlayer(), emptyBoard(), Stone.CROSS, color);

		assertThat(terminalGames).isPositive();
	}

	@ParameterizedTest
	@CsvSource({
			"OO.XX...., CROSS, 5",
			"XX.OO.X.., CIRCLE, 5"
	})
	void play_prefersImmediateWinOverBlockingOrWinningLater(String cells, Stone color, int expectedMove) {
		var board = boardOf(cells);

		var move = new PerfectPlayer().play(board, color);

		assertThat(move).isEqualTo(expectedMove);
		board[move] = color;
		assertThat(TicTacToeMain.isWin(board, color)).isTrue();
	}

	@ParameterizedTest
	@CsvSource({
			"OO.X...X., CROSS, 2",
			"XX..O...., CIRCLE, 2"
	})
	void play_blocksImmediateOpponentWin(String cells, Stone color, int expectedMove) {
		assertThat(new PerfectPlayer().play(boardOf(cells), color)).isEqualTo(expectedMove);
	}

	@ParameterizedTest
	@CsvSource({
			"XX.XOO.O., CROSS",
			"OO.OXX.XX, CIRCLE"
	})
	void play_deterministicallyChoosesLowestIndexAmongEqualWins(String cells, Stone color) {
		var board = boardOf(cells);
		var player = new PerfectPlayer();

		assertThat(player.play(board, color)).isEqualTo(2);
		assertThat(player.play(board, color)).isEqualTo(2);
		assertThat(new PerfectPlayer().play(board, color)).isEqualTo(2);
	}

	@ParameterizedTest
	@CsvSource({
			"OO.XX...., CROSS",
			"XX..O...., CIRCLE",
			"XOXXOOOX., CROSS"
	})
	void play_leavesBoardUnchanged(String cells, Stone color) {
		var board = boardOf(cells);
		var before = board.clone();

		var move = new PerfectPlayer().play(board, color);

		assertThat(move).isBetween(0, board.length - 1);
		assertThat(before[move]).isNull();
		assertThat(board).containsExactly(before);
	}

	@ParameterizedTest
	@EnumSource(Stone.class)
	void play_rejectsFullBoardWithoutChangingIt(Stone color) {
		var board = boardOf("XOXXOOOXX");
		var before = board.clone();

		assertThatThrownBy(() -> new PerfectPlayer().play(board, color))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play at all");
		assertThat(board).containsExactly(before);
	}

	private int verifyAllOpponentSequences(PerfectPlayer player, Stone[] board, Stone turn, Stone color) {
		assertThat(TicTacToeMain.isWin(board, color.opponent()))
				.as("opponent must not win against %s on %s", color, Arrays.toString(board))
				.isFalse();
		if (TicTacToeMain.isWin(board, color)) {
			return 1;
		}
		var hasEmptyCell = false;
		for (var cell : board) {
			if (cell == null) {
				hasEmptyCell = true;
			}
		}
		if (!hasEmptyCell) {
			return 1;
		}
		if (turn == color) {
			var before = board.clone();
			var move = player.play(board, color);
			assertThat(board).containsExactly(before);
			assertThat(move).isBetween(0, board.length - 1);
			assertThat(board[move]).isNull();
			board[move] = color;
			var terminalGames = verifyAllOpponentSequences(player, board, turn.opponent(), color);
			board[move] = null;
			return terminalGames;
		}
		var terminalGames = 0;
		for (var i = 0; i < board.length; i++) {
			if (board[i] == null) {
				board[i] = turn;
				terminalGames += verifyAllOpponentSequences(player, board, turn.opponent(), color);
				board[i] = null;
			}
		}
		return terminalGames;
	}
}
