package edu.mit.compilers.grammar;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.PotentialCheckDivideByZeroNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.opt.algebra.Canonicalization;

@SuppressWarnings("serial")
public abstract class ExpressionNode extends DecafNode {
	private List<DecafNode> callsBeforeExecution;
	private List<DecafNode> callsAfterExecution;
	private Canonicalization canonicalization;
	public ExpressionNode(){
		callsBeforeExecution = new ArrayList<DecafNode>();
		callsAfterExecution = new ArrayList<DecafNode>();
	}
	
	public Canonicalization getCanonicalization(){
		return canonicalization;
	}
	public void setCanonicalization(Canonicalization c){
		canonicalization = c;
	}
	
	public abstract boolean hasMethodCalls();
	
	
	public List<DecafNode> getAllCallsDuringExecution(){
		List<DecafNode> list = new ArrayList<DecafNode>();
		for(DecafNode d: callsBeforeExecution){
			if(d instanceof PotentialCheckDivideByZeroNode){
				((PotentialCheckDivideByZeroNode)d).setActive(true);
			}
			list.add(d);
		}
		list.addAll(getCallsDuringExecution());
		for(DecafNode d: callsAfterExecution){
			if(d instanceof PotentialCheckDivideByZeroNode){
				((PotentialCheckDivideByZeroNode)d).setActive(true);
			}
			list.add(d);
		}
		return list;
	}
	
	public abstract List<DecafNode> getCallsDuringExecution();
	
	public List<DecafNode> getCallsBeforeExecution(){
		return callsBeforeExecution;
	}
	
	public List<DecafNode> getCallsAfterExecution(){
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
	
	@Override
	public DecafNode deepCopy(){
		ExpressionNode copyNode = (ExpressionNode) DecafNode.deepCopyHelper(this);
		if (copyNode == null) return null;
		for(DecafNode call : callsBeforeExecution){
			copyNode.getCallsBeforeExecution().add(call.deepCopy());
		}
		for(DecafNode call : callsAfterExecution){
			copyNode.getCallsAfterExecution().add(call.deepCopy());
		}
		return copyNode;
	}
}