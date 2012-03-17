package edu.mit.compilers.grammar;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public abstract class ExpressionNode extends DecafNode {
	
	public abstract VarType getReturnType(Scope scope);

	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}

	public MidNodeList shortCircuit(MidSymbolTable symbolTable,
			MidLabelNode trueLabel, MidLabelNode falseLabel) {
		return MidShortCircuitVisitor
				.shortCircuit(this, symbolTable, trueLabel, falseLabel);
	}

	public abstract VarType getMidVarType(MidSymbolTable symbolTable);
}