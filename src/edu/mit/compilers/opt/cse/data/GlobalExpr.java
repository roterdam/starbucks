package edu.mit.compilers.opt.cse.data;

import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;

/**
 * Note that this is a *symbolic* expression needed for GLOBAL CSE, since
 * global CSE is concerned with symbolic similarities, i.e.
 * if (b) { x = 5; c = x+y; } else { d = x+y; }
 * e = x+y;
 * Should be optimized by
 * if (b) { x = 5; c = x+y; t=c; } else { d = x+y; t=d; }
 * e = t;
 */
public abstract class GlobalExpr {

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();

	public abstract List<MidMemoryNode> getMemoryNodes();

	public static GlobalExpr createUnaryExpr(MidNegNode negNode) {
		return new UnaryGlobalExpr(negNode, new LeafGlobalExpr(negNode.getOperand()
				.getMemoryNode()));
	}

}
