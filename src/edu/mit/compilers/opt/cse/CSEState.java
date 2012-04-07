package edu.mit.compilers.opt.cse;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Expr;
import edu.mit.compilers.opt.State;
import edu.mit.compilers.opt.Value;

public class CSEState implements State<CSEState> {

	Map<MidMemoryNode, Value> varToVal;
	Map<Value, MidSaveNode> valToTemp;
	Map<Expr, Value> exprToVal;
	// Used to store the register that had the value for a value saved to
	// memory. DO NOT assume these values persist for more than instruction.
	// This is purely for temp saving that occurs immediately after saving a
	// value.
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

	/**
	 * Numbers a new value for the loaded value if necessary. Regardless,
	 * returns the value corresponding to the load node's memory reference.
	 */
	public Value addVar(MidMemoryNode node) {
		Value v;
		if (!this.varToVal.containsKey(node)) {
			v = new Value();
			this.varToVal.put(node, v);
		} else {
			v = this.varToVal.get(node);
		}
		LogCenter.debug(String.format("[OPT] Map VAR->VAL : %s -> %s", node, v));
		return v;
	}

	/**
	 * Records a memory reference with an explicit value. Used for final
	 * assignment operations, this also stores the register used so the
	 * following temp variable can store from the same register.
	 */
	public void addVarVal(MidSaveNode node, Value v) {
		LogCenter.debug(String.format("[OPT] Map VAR->VAL : %s -> %s", node, v));
		MidMemoryNode m = node.getDestinationNode();
		MidRegisterNode r = node.getRegNode();
		this.valToReg.put(v, r);
		this.varToVal.put(m, v);
	}

	public Value addExpr(Value v1, Value v2, String nodeClass) {
		Expr e = new Expr(v1, v2, nodeClass);
		Value v3;
		if (!this.exprToVal.containsKey(e)) {
			v3 = new Value();
			this.exprToVal.put(e, v3);
		} else {
			v3 = this.exprToVal.get(e);
		}
		LogCenter.debug(String.format("[OPT] Map EXPR->VAL: (%s,%s,%s) -> %s", nodeClass, v1, v2, v3));
		return v3;
	}

	public MidSaveNode addTemp(Value v3, MidTempDeclNode destinationNode) {
		assert !this.valToTemp.containsKey(v3);
		MidSaveNode m = new MidSaveNode(this.valToReg.get(v3), destinationNode);
		this.valToTemp.put(v3, m);
		return m;
	}

	/**
	 * Looks up the temp variable corresponding to a value if it already exists,
	 * null otherwise.
	 */
	public MidSaveNode getTemp(Value v) {
		return this.valToTemp.get(v);
	}

}
