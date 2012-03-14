package edu.mit.compilers.grammar;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public abstract class ExpressionNode extends DecafNode {
	public abstract VarType getReturnType(Scope scope);
	
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		//System.out.println("EXPRESSION NODE'S CONVERT IS CALLED.");
		return MidVisitor.visit(this, symbolTable);
	}
}