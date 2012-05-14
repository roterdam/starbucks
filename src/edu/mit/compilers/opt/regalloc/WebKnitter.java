package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;

/**
 * Given a mapping from definitions to uses, stitches together the webs that
 * group together references to the same memory node.
 * 
 * @author joshma
 * 
 */
public class WebKnitter {

	private Map<MidUseNode, Web> webMapUses;
	private Map<MidSaveNode, Web> webMapDefs;
	private Map<MidSaveNode, Set<MidUseNode>> defUseMap;

	public WebKnitter(Map<MidSaveNode, Set<MidUseNode>> defUseMap) {
		this.defUseMap = defUseMap;
		webMapUses = new HashMap<MidUseNode, Web>();
		webMapDefs = new HashMap<MidSaveNode, Web>();
	}

	public List<Web> run() {
		List<Web> output = new ArrayList<Web>();
		for (Entry<MidSaveNode, Set<MidUseNode>> entry : defUseMap.entrySet()) {
			Set<MidUseNode> uses = entry.getValue();
			MidSaveNode def = entry.getKey();
			// We skip web generation for field nodes. Can potentially improve
			// this in the future by splitting the web at method calls instead
			// of not doing it at all.
			if (def.getDestinationNode() instanceof MidFieldDeclNode) {
				continue;
			}
			Web targetWeb = null;
			for (MidUseNode use : uses) {
				targetWeb = webMapUses.get(use);
				if (targetWeb != null) {
					break;
				}
			}
			if (targetWeb == null) {
				targetWeb = new Web();
				output.add(targetWeb);
			}
			targetWeb.expand(def, uses);
			for (MidUseNode use : uses) {
				webMapUses.put(use, targetWeb);
			}
			webMapDefs.put(def, targetWeb);
		}
		return output;
	}

	public Map<MidUseNode, Web> getWebMapUses() {
		return webMapUses;
	}

	public Map<MidSaveNode, Web> getWebMapDefs() {
		return webMapDefs;
	}

	public Web lookupWeb(Allocatable node) {
		if (node instanceof MidUseNode) {
			return webMapUses.get(node);
		}
		return webMapDefs.get(node);
	}

}
