package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class Web {

	private List<MidSaveNode> definitions;
	private List<MidLoadNode> uses;
	
	public Web() {
		definitions = new ArrayList<MidSaveNode>();
		uses = new ArrayList<MidLoadNode>();
	}
	
	public void expand(MidSaveNode newDef, List<MidLoadNode> newUses) {
		definitions.add(newDef);
		uses.addAll(newUses);
	}

}