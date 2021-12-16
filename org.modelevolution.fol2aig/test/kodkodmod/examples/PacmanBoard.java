package kodkodmod.examples;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PacmanBoard {
	private Map<Integer, Set<Integer>> board;
	
	public PacmanBoard() {
		 board = new HashMap<Integer, Set<Integer>>();
	}
	
	public void addAll(Integer field, Set<Integer> neighbors) {
		Set<Integer> neighborsEntry = board.get(field);
		if(neighborsEntry != null) {
			neighborsEntry.addAll(neighbors);
		} else {
			board.put(field, neighbors);
		}
	}
	
	public void addAllBidi(Integer field, Set<Integer> neighbors) {
		addAll(field, neighbors);
		for(Integer n : neighbors) {
			add(n, field);
		}
	}

	public void add(Integer field, Integer neighbor) {
		addAll(field, Collections.singleton(neighbor));		
	}

	public void addBidi(Integer field1, Integer field2) {
		addAll(field1, Collections.singleton(field2));
		addAll(field2, Collections.singleton(field1));
	}

	public long numFields() {
		return board.size();
	}
	
	public Set<Entry<Integer,Set<Integer>>> boardStructure() {
		return board.entrySet();
	}
	
}
