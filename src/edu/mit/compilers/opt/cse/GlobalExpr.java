package edu.mit.compilers.opt.cse;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Note that this is a *symbolic* expression needed for GLOBAL CSE, since
 * global CSE is concerned with symbolic similarities, i.e.
 * if (b) { x = 5; c = x+y; } else { d = x+y; }
 * e = x+y;
 * Should be optimized by
 * if (b) { x = 5; c = x+y; t=c; } else { d = x+y; t=d; }
 * e = t;
 */
public class GlobalExpr {

	public GlobalExpr(String nodeClass, MidMemoryNode... v) {
		
	}

}
