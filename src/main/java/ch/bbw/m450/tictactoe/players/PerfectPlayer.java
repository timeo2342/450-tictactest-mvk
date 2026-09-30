package ch.bbw.m450.tictactoe.players;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer;

/**
 * Perfect Minimax player, preferring faster wins and the lowest index on ties.
 */
public class PerfectPlayer implements TicTacToePlayer {

	@Override
	public int play(Stone[] board, Stone colorToPlay) {
		var position = board.clone();
		var bestMove = -1;
		var bestScore = -10;
		for (var i = 0; i < position.length; i++) {
			if (position[i] == null) {
				position[i] = colorToPlay;
				var score = -minimax(position, colorToPlay.opponent(), 1);
				position[i] = null;
				if (score > bestScore) {
					bestScore = score;
					bestMove = i;
				}
			}
		}
		if (bestMove == -1) {
			throw new IllegalStateException("cannot play at all");
		}
		return bestMove;
	}

	private int minimax(Stone[] board, Stone color, int depth) {
		if (TicTacToeMain.isWin(board, color.opponent())) {
			return depth - 10;
		}
		var bestScore = -10;
		for (var i = 0; i < board.length; i++) {
			if (board[i] == null) {
				board[i] = color;
				// Negamax: the opponent's best score is our worst score.
				var score = -minimax(board, color.opponent(), depth + 1);
				board[i] = null;
				bestScore = Math.max(bestScore, score);
			}
		}
		return bestScore == -10 ? 0 : bestScore;
	}
}
