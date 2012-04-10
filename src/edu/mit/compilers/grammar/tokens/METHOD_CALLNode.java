package edu.mit.compilers.grammar.tokens;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.crawler.MethodDecl;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.opt.AlgebraicSimplifier;

@SuppressWarnings("serial")
public class METHOD_CALLNode extends ExpressionNode {
	public String getMethodName() {
		assert getFirstChild() instanceof METHOD_IDNode;
		return ((METHOD_IDNode) getFirstChild()).getText();
	}
	
	public METHOD_IDNode getMethodIdNode() {
		assert getFirstChild() instanceof METHOD_IDNode;
		return (METHOD_IDNode) getFirstChild();
	}
	
	public List<ExpressionNode> getParamNodes() {
		List<ExpressionNode> output = new ArrayList<ExpressionNode>();
		for (int i = 1; i < getNumberOfChildren(); i++) {
			assert getChild(i) instanceof ExpressionNode;
			output.add((ExpressionNode) getChild(i));
		}
		return output;
	}
	
	@Override
	public VarType getReturnType(Scope scope) {
		assert scope.getMethods().containsKey(getMethodName());
		MethodDecl method = scope.getMethods().get(getMethodName());
		return method.getReturnType();
	}
	
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return symbolTable.getMethod(getMethodName()).getMidVarType();
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	@Override
	public MidNodeList shortCircuit(MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return MidShortCircuitVisitor.shortCircuit(this, symbolTable, trueLabel, falseLabel);
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

}