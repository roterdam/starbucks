package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;

@SuppressWarnings("serial")
public class WHILENode extends DecafNode {
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 2;
		assert getChild(0) instanceof WHILE_TERMINATENode;
		assert getChild(1) instanceof BLOCKNode;
		getChild(0).validate(scope);
		((BLOCKNode) getChild(1)).validate(scope, BlockType.WHILE);
	}
	
	public WHILE_TERMINATENode getWhileTerminateNode() {
		assert getChild(0) instanceof WHILE_TERMINATENode;
		return (WHILE_TERMINATENode) getChild(0);
	}
	
	public BLOCKNode getBlockNode() {
		assert getChild(1) instanceof BLOCKNode;
		return (BLOCKNode) getChild(1);
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}
}