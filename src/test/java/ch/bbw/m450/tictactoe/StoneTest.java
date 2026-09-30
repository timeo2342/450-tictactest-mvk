package ch.bbw.m450.tictactoe;

import static org.assertj.core.api.Assertions.assertThat;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StoneTest {

	@ParameterizedTest
	@CsvSource({"CROSS, CIRCLE", "CIRCLE, CROSS"})
	void opponent_returnsTheOtherColorAndIsReversible(Stone color, Stone opponent) {
		assertThat(color.opponent()).isEqualTo(opponent).isNotEqualTo(color);
		assertThat(color.opponent().opponent()).isEqualTo(color);
	}
}
