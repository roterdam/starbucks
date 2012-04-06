package edu.mit.compilers.opt.cse;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Expr;
import edu.mit.compilers.opt.State;
import edu.mit.compilers.opt.Temp;
import edu.mit.compilers.opt.Value;

public class CSEState implements State<CSEState> {
	
	Map<MidMemoryNode, Value> varToVal;
	Map<Value, MidSaveNode> valToTemp;
	Map<Expr, Value> exprToVal;
	Map<Value, MidRegisterNode> valToReg;

	
	public CSEState() {
		this.varToVal = new HashMap<MidMemoryNode, Value>();
		this.valToTemp = new HashMap<Value, MidSaveNode>();
		this.exprToVal = new HashMap<Expr, Value>();
		this.valToReg = new HashMap<Value, MidRegisterNode>();
	}

	@Override
	public CSEState getInitialState() {
		return new CSEState();
	}

	@Override
	public CSEState getBottomState() {
		return new CSEState();
	}

	@Override
	public CSEState join(CSEState s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Value addVar(MidLoadNode node) {
		MidMemoryNode m = node.getMemoryNode();
		if (!this.varToVal.containsKey(m)) {
			Value v = new Value();
			this.varToVal.put(m, v);
		}
		return this.varToVal.get(m);
	}
	
	public void addVarVal(MidSaveNode node, Value v) {
		MidMemoryNode m = node.getDestinationNode();
		MidRegisterNode r = node.getRegNode(); 
		this.valToReg.put(v, r);
		this.varToVal.put(m, v);
	}
	
	public Value addExpr(Value v1, Value v2, String nodeClass) {
		Expr e = new Expr(v1, v2, nodeClass);
		if (!this.exprToVal.containsKey(e)) {
			Value v = new Value();
			this.exprToVal.put(e, v);
		}
		return this.exprToVal.get(e);
	}

	public MidSaveNode addTemp(Value v3, MidMemoryNode destinationNode) {
		assert !this.valToTemp.containsKey(v3); 
		MidSaveNode m = new MidSaveNode(this.valToReg.get(v3), destinationNode);
		this.valToTemp.put(v3, m);
		return this.valToTemp.get(v3);
	}
	
}
