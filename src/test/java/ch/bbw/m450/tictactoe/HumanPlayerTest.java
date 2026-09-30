package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
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
				.contains("where to put the next CROSS? (0-8): ")
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
				+ "where to put the next " + color + "? (0-8): " + System.lineSeparator());
	}

	@Test
	@StdIo({"invalid", "", "1.5", "2147483648", "4"})
	void play_retriesMalformedInputUntilAValidMove(StdOut out) {
		var board = emptyBoard();

		assertThat(new HumanPlayer().play(board, Stone.CROSS)).isEqualTo(4);
		assertThat(board).containsOnlyNulls();
		assertThat(out.capturedString().split("where to put", -1)).hasSize(6);
		assertThat(out.capturedString().split("Please enter a whole number from 0 to 8.", -1)).hasSize(5);
	}

	@ParameterizedTest
	@EnumSource(Stone.class)
	@StdIo({"-1", "9", "2147483647", "-2147483648", "0", "1", "8"})
	void play_retriesOutOfRangeAndOccupiedPositionsForBothColors(Stone color, StdOut out) {
		var board = boardOf("XO.......");
		var before = board.clone();

		assertThat(new HumanPlayer().play(board, color)).isEqualTo(8);
		assertThat(board).containsExactly(before);
		assertThat(out.capturedString().split("Please choose a position from 0 to 8.", -1)).hasSize(5);
		assertThat(out.capturedString().split("That position is already occupied.", -1)).hasSize(3);
		assertThat(out.capturedString().split("where to put", -1)).hasSize(8);
	}

	@Test
	@StdIo(" \t4 \t")
	void play_acceptsWhitespaceAroundTheNumber(StdOut out) {
		assertThat(new HumanPlayer().play(emptyBoard(), Stone.CROSS)).isEqualTo(4);
		assertThat(out.capturedString()).doesNotContain("Please", "occupied");
	}

	@Test
	@StdIo("invalid")
	void play_doesNotRetryEndOfInputAfterAnInvalidLine(StdOut out) {
		assertThatThrownBy(() -> new HumanPlayer().play(emptyBoard(), Stone.CROSS))
				.isInstanceOf(NoSuchElementException.class);
		assertThat(out.capturedString().split("where to put", -1)).hasSize(3);
	}

	@Test
	@StdIo("4")
	void play_rejectsEndOfInput(StdOut out) {
		var player = new HumanPlayer();
		assertThat(player.play(emptyBoard(), Stone.CROSS)).isEqualTo(4);

		assertThatThrownBy(() -> player.play(emptyBoard(), Stone.CROSS))
				.isInstanceOf(NoSuchElementException.class);
		assertThat(out.capturedString()).contains("where to put the next CROSS?");
	}

	@Test
	@StdIo({"0", "2", "4", "6"})
	void main_runsTheInteractiveGameToCompletion(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(out.capturedString())
				.contains("where to put the next CROSS? (0-8): ")
				.endsWith("...and the winner is: CROSS" + System.lineSeparator());
	}
}
