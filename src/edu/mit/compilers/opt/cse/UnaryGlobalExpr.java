package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class UnaryGlobalExpr extends GlobalExpr {
	protected MidNode node;
	protected GlobalExpr expr;
	public UnaryGlobalExpr(MidNode node, GlobalExpr expr) {
		this.node = node;
		this.expr = expr;
	}
	
	@Override
	public String toString(){
		return node.toString() + ":" + expr.toString();
	}
	
	@Override
	public List<MidMemoryNode> getMemoryNodes() {
		List<MidMemoryNode> nodes = new ArrayList<MidMemoryNode>();
		nodes.addAll(expr.getMemoryNodes());
		return nodes;
	}

	@Override
	public int hashCode() {
		return node.hashCode() + expr.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnaryGlobalExpr)){
			return false;
		}
		if(((UnaryGlobalExpr) o).node.getClass().toString().equals(node.getClass().toString())){
			if(expr.equals(((UnaryGlobalExpr)o).expr)){
				return true;
			}
		}
		return false;
	}
}
