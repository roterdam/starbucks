package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class WebKnitter {

	private Map<MidLoadNode, Web> webMapUses;
	private Map<MidSaveNode, Web> webMapDefs;
	private final Map<MidSaveNode, List<MidLoadNode>> defUseMap;

	public WebKnitter(Map<MidSaveNode, List<MidLoadNode>> defUseMap) {
		this.defUseMap = defUseMap;
		webMapUses = new HashMap<MidLoadNode, Web>();
		webMapDefs = new HashMap<MidSaveNode, Web>();
	}
	
	public void run() {
		List<Web> output = new ArrayList<Web>();
		for (Entry<MidSaveNode, List<MidLoadNode>> entry : defUseMap.entrySet()) {
			List<MidLoadNode> uses = entry.getValue();
			MidSaveNode def = entry.getKey();
			Web targetWeb = null;
			for (MidLoadNode use : uses) {
				targetWeb = getWebMapUses().get(use);
				if (targetWeb != null) {
					break;
				}
			}
			if (targetWeb == null) {
				targetWeb = new Web();
				output.add(targetWeb);
			}
			targetWeb.expand(def, uses);
			for (MidLoadNode use : uses) {
				getWebMapUses().put(use, targetWeb);
			}
			getWebMapDefs().put(def, targetWeb);
		}
	}

	public Map<MidLoadNode, Web> getWebMapUses() {
		return webMapUses;
	}

	public Map<MidSaveNode, Web> getWebMapDefs() {
		return webMapDefs;
	}

}
