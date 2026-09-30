package ch.bbw.m450.tictactoe;

import static ch.bbw.m450.tictactoe.TicTacToeFixtures.boardOf;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.emptyBoard;
import static ch.bbw.m450.tictactoe.TicTacToeFixtures.scriptedPlayer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

class TicTacToeMainTest {

	@ParameterizedTest(name = "{0} wins for {1}")
	@MethodSource("ch.bbw.m450.tictactoe.TicTacToeFixtures#winningBoards")
	void isWin_detectsEveryLineForBothColors(String cells, Stone color) {
		assertThat(TicTacToeMain.isWin(boardOf(cells), color)).isTrue();
		assertThat(TicTacToeMain.isWin(boardOf(cells), color.opponent())).isFalse();
	}

	@ParameterizedTest(name = "{0} does not win for {1}")
	@MethodSource("ch.bbw.m450.tictactoe.TicTacToeFixtures#incompleteWinningLines")
	void isWin_requiresAllThreeMatchingStones(String cells, Stone color) {
		assertThat(TicTacToeMain.isWin(boardOf(cells), color)).isFalse();
	}

	@ParameterizedTest
	@EnumSource(Stone.class)
	void isWin_rejectsEmptyAndDrawBoards(Stone color) {
		assertThat(TicTacToeMain.isWin(emptyBoard(), color)).isFalse();
		assertThat(TicTacToeMain.isWin(boardOf("XOXXOOOXX"), color)).isFalse();
	}

	@Test
	@StdIo
	void play_twoGreedyPlayersResultInCrossWinner(StdOut out) {
		assertThat(TicTacToeMain.play(new GreedyPlayer(), new GreedyPlayer())).isEqualTo(Stone.CROSS);
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(boardOf("XOXOXOX.."))
				+ "...and the winner is: CROSS" + System.lineSeparator());
	}

	@Test
	@StdIo
	void play_circleCanWinAndStopsImmediately(StdOut out) {
		assertThat(TicTacToeMain.play(scriptedPlayer(0, 1, 8), scriptedPlayer(3, 4, 5)))
				.isEqualTo(Stone.CIRCLE);
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(boardOf("XX.OOO..X"))
				+ "...and the winner is: CIRCLE" + System.lineSeparator());
	}

	@Test
	@StdIo
	void play_drawUsesAllNineMoves(StdOut out) {
		assertThat(TicTacToeMain.play(scriptedPlayer(0, 2, 3, 7, 8), scriptedPlayer(1, 4, 5, 6)))
				.isNull();
		assertThat(out.capturedString()).isEqualTo("it's a draw!" + System.lineSeparator());
	}

	@Test
	@StdIo
	void play_aWinOnTheLastMoveIsNotADraw(StdOut out) {
		assertThat(TicTacToeMain.play(scriptedPlayer(0, 2, 3, 7, 6), scriptedPlayer(1, 4, 5, 8)))
				.isEqualTo(Stone.CROSS);
		assertThat(out.capturedString()).endsWith("...and the winner is: CROSS" + System.lineSeparator())
				.doesNotContain("draw");
	}

	@Test
	void play_rejectsIdenticalPlayers() {
		var player = new GreedyPlayer();
		assertThatThrownBy(() -> TicTacToeMain.play(player, player))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("players must differ");
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 9})
	@StdIo
	void play_rejectsOutOfBoundsMoves(int move, StdOut out) {
		assertThatThrownBy(() -> TicTacToeMain.play(scriptedPlayer(move), new GreedyPlayer()))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play to position " + move);
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(emptyBoard()) + System.lineSeparator());
	}

	@Test
	@StdIo
	void play_rejectsOccupiedMoves(StdOut out) {
		assertThatThrownBy(() -> TicTacToeMain.play(scriptedPlayer(0), scriptedPlayer(0)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play to position 0");
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(boardOf("X........"))
				+ System.lineSeparator());
	}

	@Test
	@StdIo
	void play_givesEachPlayerAFreshSnapshotAndProtectsTheBoard(StdOut out) {
		var snapshots = new String[] {".........", "X........", "X..O.....", "XX.O.....", "XX.OO...."};
		var moves = new int[] {0, 3, 1, 4, 2};
		var round = new AtomicInteger();
		TicTacToePlayer corruptingPlayer = (board, color) -> {
			var turn = round.getAndIncrement();
			assertThat(board).containsExactly(boardOf(snapshots[turn]));
			assertThat(color).isEqualTo(turn % 2 == 0 ? Stone.CROSS : Stone.CIRCLE);
			Arrays.fill(board, color.opponent());
			return moves[turn];
		};
		TicTacToePlayer otherPlayer = (board, color) -> corruptingPlayer.play(board, color);

		assertThat(TicTacToeMain.play(corruptingPlayer, otherPlayer)).isEqualTo(Stone.CROSS);
		assertThat(round.get()).isEqualTo(5);
		assertThat(out.capturedString()).isEqualTo(TicTacToeMain.toString(boardOf("XXXOO...."))
				+ "...and the winner is: CROSS" + System.lineSeparator());
	}

	@Test
	void toString_formatsStonesFreeIndicesAndRows() {
		assertThat(TicTacToeMain.toString(boardOf("XO......."))).isEqualTo(
				"\033[1mX\033[0m  \033[1mO\033[0m  \033[37m2\033[0m  \n"
						+ "\033[37m3\033[0m  \033[37m4\033[0m  \033[37m5\033[0m  \n"
						+ "\033[37m6\033[0m  \033[37m7\033[0m  \033[37m8\033[0m  \n");
	}
}
