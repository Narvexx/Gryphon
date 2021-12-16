package kodkodmod.examples;

import java.util.Arrays;
import java.util.List;

public final class PacmanGameData implements PacmanGame {
	private final PacmanBoard board;
	private final List<Integer> treasureFieldIndices;
	private final int pacmanPosition;
	private final List<Integer> ghostPositions;

	
	public static PacmanGame createPacmanGame(PacmanBoard board, int numGhosts,
			Integer[] treasureFieldIndices, int pacmanPosition,
			Integer[] ghostPositions) {
		if (numGhosts != ghostPositions.length)
			throw new IllegalArgumentException(
					"Not all ghosts are assigned to a field. Reason: numGhosts != ghostPositions.size()");

		return new PacmanGameData(board, treasureFieldIndices, pacmanPosition, ghostPositions);
	}

	protected PacmanGameData(PacmanBoard board,
			Integer[] treasureFieldIndices, int pacmanPosition,
			Integer[] ghostPositions) {
		this.board = board;
		this.treasureFieldIndices = Arrays.asList(treasureFieldIndices);
		this.pacmanPosition = pacmanPosition;
		this.ghostPositions = Arrays.asList(ghostPositions);
	}

	/* (non-Javadoc)
	 * @see examples.PacmanGame#board()
	 */
	@Override
	public final PacmanBoard board() {
		return board;
	}	
	
	/* (non-Javadoc)
	 * @see examples.PacmanGame#board()
	 */
	@Override
	public final long numFields() {
		return board.numFields();
	}

	/* (non-Javadoc)
	 * @see examples.PacmanGame#numGhosts()
	 */
	@Override
	public final int numGhosts() {
		return ghostPositions().size();
	}

	/* (non-Javadoc)
	 * @see examples.PacmanGame#treasureFieldIndices()
	 */
	@Override
	public final List<Integer> treasureFieldIndices() {
		return treasureFieldIndices;
	}

	/* (non-Javadoc)
	 * @see examples.PacmanGame#pacmanPosition()
	 */
	@Override
	public final int pacmanPosition() {
		return pacmanPosition;
	}

	/* (non-Javadoc)
	 * @see examples.PacmanGame#ghostPositions()
	 */
	@Override
	public final List<Integer> ghostPositions() {
		return ghostPositions;
	}

	@Override
	public long numTreasures() {
		return treasureFieldIndices.size();
	}

	@Override
	public boolean isTreasureField(Integer i) {
		if(treasureFieldIndices().indexOf(i) == -1)
			return false;
		else
			return true;
	}
	
	public Integer treasureFieldIndexOf(int fieldIndex) {
//		if(fieldIndex < 0 || fieldIndex > treasureFieldIndices.size())
//			throw new IllegalArgumentException();
		return treasureFieldIndices.indexOf(fieldIndex);
	}
}