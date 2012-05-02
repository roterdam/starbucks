package edu.mit.compilers.opt.regalloc;

import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class WebProcessor implements Transfer<WebState> {

	private static Map<MidSaveNode, Web> webDefs;
	private static Map<MidLoadNode, Web> webUses;

	@Override
	public WebState apply(Block b, WebState s) {
		assert (webDefs != null && webUses != null) : "WebProcessor function apply() called before initialize().";
		LogCenter.debug("RA", "Processing " + b);
		WebState out = s.clone();
		for (MidNode node : b) {
			if (node instanceof MidSaveNode) {
				if (!webDefs.containsKey(node)) {
					LogCenter.debug("RA", "Warning: saveNode: " + node
							+ " doesn't belong to a web.");
					continue;
				}
				Web web = webDefs.get(node);
				s.birthWeb(web);
				s.interfereWith(web);
			} else if (node instanceof MidLoadNode) {
				// It's possible that this is dead code and doesn't belong to a
				// web.
				if (!webUses.containsKey(node)) {
					LogCenter.debug("RA", "Warning: loadNode " + node
							+ " doesn't belong to a web.");
					continue;
				}
				Web web = webUses.get(node);
				web.markUsed(node);
				if (web.isDead()) {
					s.killWeb(web);
				}
			}
		}
		return out;
	}

	public static void initialize(Map<MidSaveNode, Web> webDefs,
			Map<MidLoadNode, Web> webUses) {
		assert (webDefs != null && webUses != null) : "WebProcessor function initialized() called with null arguments.";
		WebProcessor.webDefs = webDefs;
		WebProcessor.webUses = webUses;
	}

}
