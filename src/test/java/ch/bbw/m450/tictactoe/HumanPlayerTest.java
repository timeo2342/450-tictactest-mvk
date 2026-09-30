package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import ch.bbw.m450.tictactoe.players.HumanPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

class HumanPlayerTest {

	@Test
	@StdIo({"0", "2", "4", "6"})
	void play_preservesBufferedMovesAcrossTurns(StdOut out) {
		var winner = TicTacToeMain.play(new HumanPlayer(), new GreedyPlayer());

		assertThat(winner).isEqualTo(Stone.CROSS);
		assertThat(out.capturedString())
				.contains("where to to put the next CROSS? (0-8): ")
				.endsWith("...and the winner is: CROSS" + System.lineSeparator());
	}

	@ParameterizedTest
	@EnumSource(Stone.class)
	@StdIo("8")
	void play_readsMoveAndPrintsBoardAndColor(Stone color, StdOut out) {
		var board = emptyBoard();

		assertThat(new HumanPlayer().play(board, color)).isEqualTo(8);
		assertThat(board).containsOnlyNulls();
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(board)
				+ "where to to put the next " + color + "? (0-8): " + System.lineSeparator());
	}

	@Test
	@StdIo({"invalid", "", " 4", "2147483648", "4"})
	void play_rejectsMalformedInputWithoutRetrying(StdOut out) {
		var player = new HumanPlayer();
		for (var attempt = 0; attempt < 4; attempt++) {
			assertThatThrownBy(() -> player.play(emptyBoard(), Stone.CROSS))
					.isInstanceOf(NumberFormatException.class);
		}
		assertThat(player.play(emptyBoard(), Stone.CROSS)).isEqualTo(4);
		assertThat(out.capturedString().split("where to to put", -1)).hasSize(6);
	}

	@Test
	@StdIo("4")
	void play_rejectsEndOfInput(StdOut out) {
		var player = new HumanPlayer();
		assertThat(player.play(emptyBoard(), Stone.CROSS)).isEqualTo(4);

		assertThatThrownBy(() -> player.play(emptyBoard(), Stone.CROSS))
				.isInstanceOf(NoSuchElementException.class);
		assertThat(out.capturedString()).contains("where to to put the next CROSS?");
	}

	@Test
	@StdIo({"0", "2", "4", "6"})
	void main_runsTheInteractiveGameToCompletion(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString())
				.contains("where to to put the next CROSS? (0-8): ")
				.endsWith("...and the winner is: CROSS" + System.lineSeparator());
	}
}
