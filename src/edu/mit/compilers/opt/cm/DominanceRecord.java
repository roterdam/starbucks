package edu.mit.compilers.opt.cm;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.Block;

public class DominanceRecord {

	private Map<Block, Set<Block>> dominanceMap;

	public DominanceRecord(Analyzer<DomState, DomTransfer> dominatorAnalyzer) {
		dominanceMap = new HashMap<Block, Set<Block>>();
		for (Block b : dominatorAnalyzer.getProcessedBlocks()) {
			DomState domState = dominatorAnalyzer.getAnalyzedState(b);
			Set<Block> domSet;
			if (domState == null) {
				// Corresponds to entry block.
				domSet = new LinkedHashSet<Block>();
				domSet.add(b);
			} else {
				domSet = domState.getDomSet();
			}
			dominanceMap.put(b, domSet);
		}
	}

	public Set<Block> getBlocks() {
		return dominanceMap.keySet();
	}
	
	public Set<Block> getBlocks(Block b) {
		return dominanceMap.get(b);
	}

}
