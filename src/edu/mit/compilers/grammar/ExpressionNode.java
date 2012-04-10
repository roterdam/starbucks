package edu.mit.compilers.grammar;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;

@SuppressWarnings("serial")
public abstract class ExpressionNode extends DecafNode {
	private List<METHOD_CALLNode> callsBeforeExecution;
	private List<METHOD_CALLNode> callsAfterExecution;
	public ExpressionNode(){
		callsBeforeExecution = new ArrayList<METHOD_CALLNode>();
		callsAfterExecution = new ArrayList<METHOD_CALLNode>();
	}
	public abstract boolean hasMethodCalls();
	
	public List<METHOD_CALLNode> getAllCallsDuringExecution(){
		List<METHOD_CALLNode> list = new ArrayList<METHOD_CALLNode>();
		list.addAll(callsBeforeExecution);
		list.addAll(callsAfterExecution);
		return list;
	}
	public List<METHOD_CALLNode> getCallsBeforeExecution(){
		return callsBeforeExecution;
	}
	
	public List<METHOD_CALLNode> getCallsAfterExecution(){
		return callsAfterExecution;
	}
	
	public abstract VarType getReturnType(Scope scope);

	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	public abstract ExpressionNode simplify(MidSymbolTable symbolTable);
	
	public MidNodeList shortCircuit(MidSymbolTable symbolTable,
			MidLabelNode trueLabel, MidLabelNode falseLabel) {
		return MidShortCircuitVisitor
				.shortCircuit(this, symbolTable, trueLabel, falseLabel);
	}

	public abstract VarType getMidVarType(MidSymbolTable symbolTable);
	
	@Override
	public void simplifyExpressions(){
		assert false : "Expressions do not have children expressions, unless it is an IDNode and should override this method.";
	}
}