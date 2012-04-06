package edu.mit.compilers.opt.cse;

import java.util.HashMap;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.Expr;
import edu.mit.compilers.opt.State;
import edu.mit.compilers.opt.Temp;
import edu.mit.compilers.opt.Value;

public class CSEState implements State<CSEState> {
	
	protected HashMap<MidMemoryNode, Value> varToVal;
	protected HashMap<Expr, Temp> exprToTemp;
	protected HashMap<Expr, Value> exprToVal;

	
	public CSEState() {
		this.varToVal = new HashMap<MidMemoryNode, Value>();
		this.exprToTemp = new HashMap<Expr, Temp>();
		this.exprToVal = new HashMap<Expr, Value>();
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
	
	public void addVar(MidMemoryNode node) {
		if (!this.varToVal.containsKey(node)) {
			Value v = new Value(node);
			this.varToVal.put(node, v);
		}
	}
	
	
	
}
