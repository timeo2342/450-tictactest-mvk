package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class GreedyPlayerTest {

	@ParameterizedTest
	@EnumSource(Stone.class)
	void play_choosesFirstFreeCellIncludingLastSlotWithoutChangingBoard(Stone color) {
		var board = emptyBoard();
		var player = new GreedyPlayer();
		for (var expected = 0; expected < board.length; expected++) {
			var before = board.clone();
			assertThat(player.play(board, color)).isEqualTo(expected);
			assertThat(board).containsExactly(before);
			board[expected] = expected % 2 == 0 ? color : color.opponent();
		}
	}

	@ParameterizedTest
	@EnumSource(Stone.class)
	void play_rejectsFullBoard(Stone color) {
		var board = boardOf("XOXXOOOXX");
		var before = board.clone();

		assertThatThrownBy(() -> new GreedyPlayer().play(board, color))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play at all");
		assertThat(board).containsExactly(before);
	}
}
