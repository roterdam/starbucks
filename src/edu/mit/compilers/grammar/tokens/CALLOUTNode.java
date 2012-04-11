package edu.mit.compilers.grammar.tokens;

import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;

@SuppressWarnings("serial")
public class CALLOUTNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.INT;
	}

	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable) {
		return VarType.INT;
	}

	public String getName() {
		assert getChild(0) instanceof CALLOUT_NAMENode;
		return ((CALLOUT_NAMENode) getChild(0)).getName();
	}

	/**
	 * Returns a list of arguments, which is type Object because it can either
	 * be a String or an ExpressionNode.
	 * 
	 * @return
	 */
	public List<DecafNode> getArgs() {
		assert getChild(1) instanceof CALLOUT_ARGSNode;
		CALLOUT_ARGSNode args = (CALLOUT_ARGSNode) getChild(1);
		return args.getArgs();
	}

	public CALLOUT_ARGSNode getArgsNode(){
		return (CALLOUT_ARGSNode) getChild(1);
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	@Override
	public ExpressionNode simplify(MidSymbolTable symbolTable) {
		return AlgebraicSimplifier.simplifyExpression(this, symbolTable);
	}

	@Override
	public boolean hasMethodCalls() {
		return true;
	}
	
	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}
	
	@Override
	public List<DecafNode> getCallsDuringExecution() {
		assert false : "Never remove a callout. It changes stuff.";
		return null;
		//List<DecafNode> list = new ArrayList<DecafNode>();
		//list.addAll(getLeftOperand().getAllCallsDuringExecution());
		//list.addAll(getRightOperand().getAllCallsDuringExecution());
		//return list;
	}

}