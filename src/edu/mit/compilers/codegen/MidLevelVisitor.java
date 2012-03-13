package edu.mit.compilers.codegen;

import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;

public class MidLevelVisitor {
	
	public MidLevelNode visit(DecafNode node) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all children.
		return new MidLevelNode(null);
	}
	
	public MidLevelNode visit(CLASSNode node) {
		return null;
	}
	

}
