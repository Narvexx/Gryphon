package kodkodmod.examples;

import java.util.List;

public interface PacmanGame {

	public abstract PacmanBoard board();
	
	public abstract long numFields();

	public abstract int numGhosts();

	public abstract List<Integer> treasureFieldIndices();
	
	public abstract Integer treasureFieldIndexOf(int fieldIndex);

	public abstract int pacmanPosition();

	public abstract List<Integer> ghostPositions();

	public abstract long numTreasures();

	public abstract boolean isTreasureField(Integer fieldIndex);

}