package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class Web {

	private List<MidSaveNode> definitions;
	private List<MidLoadNode> uses;
	private Set<MidLoadNode> unusedUses;
	private Set<Web> interferences;

	public Web() {
		definitions = new ArrayList<MidSaveNode>();
		uses = new ArrayList<MidLoadNode>();
		unusedUses = new HashSet<MidLoadNode>();
		interferences = new HashSet<Web>();
	}

	public void expand(MidSaveNode newDef, Set<MidLoadNode> newUses) {
		definitions.add(newDef);
		uses.addAll(newUses);
		unusedUses.addAll(newUses);
	}

	public void markUsed(MidNode node) {
		unusedUses.remove(node);
	}

	public boolean isDead() {
		return unusedUses.isEmpty();
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
		String out = "Web: [";
		for (MidSaveNode saveNode : definitions) {
			out += saveNode;
		}
		out += "] => [";
		for (MidLoadNode loadNode : uses) {
			out += loadNode;
		}
		out += "]";
		return out;
	}

	public Set<Web> getInterferences() {
		return interferences;
	}

}