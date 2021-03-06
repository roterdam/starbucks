package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.codegen.nodes.regops.MidUnaryRegNode;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.Value;
import edu.mit.compilers.opt.cse.data.BinaryLocalExpr;
import edu.mit.compilers.opt.cse.data.LocalExpr;
import edu.mit.compilers.opt.cse.data.UnaryLocalExpr;

/**
 * Stores state information for block-level optimizations, NOT global CSE.
 */
public class CSELocalState {

	HashMap<MidMemoryNode, Value> varToVal;
	HashMap<Value, MidSaveNode> valToTemp;
	HashMap<LocalExpr, Value> exprToVal;

	public CSELocalState() {
		this.varToVal = new HashMap<MidMemoryNode, Value>();
		this.valToTemp = new HashMap<Value, MidSaveNode>();
		this.exprToVal = new HashMap<LocalExpr, Value>();
	}

	/**
	 * Numbers a new value for the loaded value if necessary. Regardless,
	 * returns the value corresponding to the load node's memory reference.
	 */
	public Value addVar(MidMemoryNode node) {
		Value v;
		if (this.varToVal.containsKey(node)) {
			v = this.varToVal.get(node);
		} else {
			v = new Value();
			this.varToVal.put(node, v);
		}
		LogCenter.debug("OPT", String
				.format("Map VAR->VAL : %s -> %s", node, v));
		return v;
	}

	/**
	 * Records a memory reference with an explicit value. Used for final
	 * assignment operations, this also stores the register used so the
	 * following temp variable can store from the same register.
	 */
	public void addVarVal(MidMemoryNode m, MidRegisterNode r, Value v) {
		LogCenter.debug("OPT", String.format("Map VAR->VAL : %s -> %s", m, v));
		this.varToVal.put(m, v);
	}

	public Value addBinaryExpr(Value v1, Value v2, MidArithmeticNode node) {
		BinaryLocalExpr e = new BinaryLocalExpr(v1, v2, node);
		Value v3;
		if (this.exprToVal.containsKey(e)) {
			v3 = this.exprToVal.get(e);
		} else {
			v3 = new Value();
			this.exprToVal.put(e, v3);
		}
		LogCenter
				.debug("OPT", String
						.format("Map EXPR->VAL: (%s,%s,%s) -> %s", node
								.getNodeClass(), v1, v2, v3));
		return v3;
	}

	public Value addUnaryExpr(Value v1, MidUnaryRegNode node) {
		LocalExpr e = new UnaryLocalExpr(v1, node);
		Value v2;
		if (this.exprToVal.containsKey(e)) {
			v2 = this.exprToVal.get(e);
		} else {
			v2 = new Value();
			this.exprToVal.put(e, v2);
		}
		LogCenter
				.debug("OPT", String
						.format("Map EXPR->VAL: (%s,%s) -> %s", node
								.getNodeClass(), v1, v2));
		return v2;
	}

	public MidSaveNode addTemp(Value v3, MidTempDeclNode destinationNode,
			MidLoadNode loadNode) {
		assert !this.valToTemp.containsKey(v3);
		MidSaveNode m = new OptSaveNode(loadNode, destinationNode);
		LogCenter.debug("OPT", "Saving temp node for later use: " + m + " ("
				+ m.hashCode() + ")");
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

	@Override
	public String toString() {
		String out = "CSELocalState:\n";
		out += "varToVal: " + HashMapUtils.toMapString(varToVal) + "\n";
		out += "valToTemp: " + HashMapUtils.toMapString(valToTemp) + "\n";
		out += "exprToVal: " + HashMapUtils.toMapString(exprToVal) + "\n";
		return out;
	}

	public void clearGlobals() {
		for (MidMemoryNode key : new ArrayList<MidMemoryNode>(varToVal.keySet())) {
			if (key instanceof MidFieldDeclNode) {
				varToVal.remove(key);
			}
		}
	}

}
