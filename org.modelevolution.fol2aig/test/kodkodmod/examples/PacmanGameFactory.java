package kodkodmod.examples;

import java.util.Arrays;
import java.util.HashSet;

public final class PacmanGameFactory {
	public static final PacmanGame _2x2_PacmanAt1_GhostAt0_TreasureAt3() {
	  return PacmanGameData.createPacmanGame(twoTimesTwoBoard(), 1,
				new Integer[] { 3 }, 1, new Integer[] { 0 });
	}
	
	public static final PacmanGame _2x2_PacmanAt1_2GhostsAt0_GhostAt2_TreasureAt3() {
    return PacmanGameData.createPacmanGame(twoTimesTwoBoard(), 3,
        new Integer[] { 3 }, 1, new Integer[] { 0, 0, 2 });
  }

	public static final PacmanGame satTwoTimesTwo() {
		return PacmanGameData.createPacmanGame(twoTimesTwoBoard(), 1,
				new Integer[] { 3 }, 1, new Integer[] { 2 });
	}

	/**
	 * Simple 1x2 board with a Pacman and no ghosts. Treasure field is located
	 * at field (1,2), Pacman starts at (1,1).
	 * 
	 * @return a super simple Pacman game
	 */
	public static final PacmanGame satSuperSimple() {
		return PacmanGameData.createPacmanGame(oneTimesTwoBoard(), 0, new Integer[] {1}, 0, new Integer[]{});
	}

	private static PacmanBoard oneTimesTwoBoard() {
		PacmanBoard board = new PacmanBoard();
		board.addBidi(0, 1);
		return board;
	}

	/**
	 * Field 0 is in the left-lower corner
	 * Board layout with neighbor-relations
	 * 1 --- 2
	 * |     |
	 * |     |
	 * 0 --- 3
	 * 
	 * @return a 2x2 game board
	 */
	private static PacmanBoard twoTimesTwoBoard() {
		PacmanBoard board = new PacmanBoard();
		
		board.addAll(0, new HashSet<Integer>(Arrays.asList(new Integer[] { 1, 3 })));
		board.addAll(1, new HashSet<Integer>(Arrays.asList(new Integer[] { 0, 2 })));
		board.addAll(2, new HashSet<Integer>(Arrays.asList(new Integer[] { 1, 3 })));
		board.addAll(3, new HashSet<Integer>(Arrays.asList(new Integer[] { 0, 2 })));

		return board;
	}

	public static final PacmanGame jotThreeTimesThree() {
//		List<Integer[]> board = new ArrayList<Integer[]>(9);
//		Integer[] neighbors0 = new Integer[] { 3 };
//		Integer[] neighbors1 = new Integer[] { 4 };
//		Integer[] neighbors2 = new Integer[] { 5 };
//		Integer[] neighbors3 = new Integer[] { 0, 6 };
//		Integer[] neighbors4 = new Integer[] { 1, 7 };
//		Integer[] neighbors5 = new Integer[] { 2, 8 };
//		Integer[] neighbors6 = new Integer[] { 3, 7 };
//		Integer[] neighbors7 = new Integer[] { 4, 6, 8 };
//		Integer[] neighbors8 = new Integer[] { 5, 7 };
//		board.add(neighbors0);
//		board.add(neighbors1);
//		board.add(neighbors2);
//		board.add(neighbors3);
//		board.add(neighbors4);
//		board.add(neighbors5);
//		board.add(neighbors6);
//		board.add(neighbors7);
//		board.add(neighbors8);
		PacmanBoard board = new PacmanBoard();
		board.addBidi(0, 3);
		board.addBidi(1, 4);
		board.addBidi(2, 5);
		board.addBidi(3, 6);
		board.addBidi(4, 7);
		board.addBidi(5, 8);
		board.addBidi(6, 7);
		board.addBidi(7, 8);
		return PacmanGameData.createPacmanGame(board, 2, new Integer[] { 1 }, 0,
				new Integer[] { 4, 8 });
	}
}
