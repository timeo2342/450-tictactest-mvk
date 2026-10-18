package ch.bbw.m450.tictactoe.players;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer;

/**
 * Simple human-player taking input from stdin.
 */
public class HumanPlayer implements TicTacToePlayer {

	private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

	@Override
	public int play(Stone[] board, Stone colorToPlay) {
		while (true) {
			System.out.println(TicTacToeMain.toString(board) + "where to put the next " + colorToPlay + "? (0-8): ");
			var input = scanner.nextLine().strip();
			int move;
			try {
				move = Integer.parseInt(input);
			} catch (NumberFormatException exception) {
				System.out.println("Please enter a whole number from 0 to 8.");
				continue;
			}
			if (move < 0 || move >= board.length) {
				System.out.println("Please choose a position from 0 to 8.");
				continue;
			}
			if (board[move] != null) {
				System.out.println("That position is already occupied. Please choose a free position.");
				continue;
			}
			return move;
		}
	}
}
