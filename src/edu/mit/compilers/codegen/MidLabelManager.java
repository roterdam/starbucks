package edu.mit.compilers.codegen;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidLabelNode;

public class MidLabelManager {
	@SuppressWarnings("serial")
	private static Map<LabelType, Integer> tracker = new HashMap<LabelType, Integer>(){{
		for (LabelType type : LabelType.values()) {
			this.put(type, 0);
		}
	}};
	
	public enum LabelType {
		FOR, ROF, WHILE, ELIHW;
	}
	
	public static MidLabelNode getLabel(LabelType type) {
		tracker.put(type, tracker.get(type)+1);
		return new MidLabelNode(type.toString().toLowerCase() + tracker.get(type));
	}
	
}
