package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;

public class MidVisitor {
	
	public static MidNode visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all children.
		return new MidNode(null);
	}
	
	public static MidNode visit(CLASSNode node, MidSymbolTable symbolTable) {
		return null;
	}
}
