package edu.mit.compilers.codegen;

import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;

public class MidLevelVisitor {
	
	public static MidLevelNode visit(DecafNode node, MidLevelSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all children.
		return new MidLevelNode(null);
	}
	
	public static MidLevelNode visit(CLASSNode node, MidLevelSymbolTable symbolTable) {
		return null;
	}
}
