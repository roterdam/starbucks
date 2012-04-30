package edu.mit.compilers.opt.regalloc;

import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class InterferenceGenerator {

	private final Map<MidSaveNode, Web> webMapDefs;
	private final Map<MidLoadNode, Web> webMapUses;

	public InterferenceGenerator(Map<MidSaveNode, Web> webMapDefs,
			Map<MidLoadNode, Web> webMapUses) {
		this.webMapDefs = webMapDefs;
		this.webMapUses = webMapUses;
	}

	public void run() {
	}

}
