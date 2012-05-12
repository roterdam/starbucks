package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

public class Web {

	private List<MidSaveNode> definitions;
	private List<MidRegisterNode> uses;
	private Set<Web> interferences;

	public Web() {
		definitions = new ArrayList<MidSaveNode>();
		uses = new ArrayList<MidRegisterNode>();
		interferences = new HashSet<Web>();
	}

	public void expand(MidSaveNode newDef, Set<MidRegisterNode> uses) {
		definitions.add(newDef);
		uses.addAll(uses);
	}

	public void addInterference(Web otherWeb) {
		// It's going to want to interfere with itself, so ignore that.
		if (interferences.contains(otherWeb) || otherWeb == this) {
			return;
		}
		interferences.add(otherWeb);
		otherWeb.addInterference(this);
	}

	@Override
	public String toString() {
		return String.format("Web [%s]", definitions.get(0)
				.getDestinationNode().getName());
	}

	public Set<Web> getInterferences() {
		return interferences;
	}

}