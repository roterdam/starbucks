package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Transfer function to analyze web liveness in order to generate interference
 * graphs.
 * 
 * @author joshma
 * 
 */
public class WebProcessor implements Transfer<WebState> {

	private static Map<MidSaveNode, Web> webDefs;
	private static Map<MidRegisterNode, Web> webUses;

	public WebState apply(Block b, WebState s) {
		assert (webDefs != null && webUses != null) : "WebProcessor function apply() called before initialize().";
		LogCenter.debug("RA", "\n########\nProcessing " + b);
		WebState out = s.clone();
		LogCenter.debug("RA", "Live webs: " + out.getLiveWebs());
		for (MidNode node : b.reverse()) {
			LogCenter.debug("RA", "Processing " + node);
			if (node instanceof MidSaveNode) {
				if (!webDefs.containsKey(node)) {
					LogCenter.debug("RA", "Warning: saveNode: " + node
							+ " doesn't belong to a web.");
					continue;
				}
				Web web = webDefs.get(node);
				out.killWeb(web);
				continue;
			}
			if (node instanceof MidLoadNode) {
				// It's possible that this is dead code and doesn't belong to a
				// web.
				if (!webUses.containsKey(node)) {
					LogCenter.debug("RA", "Warning: loadNode " + node
							+ " doesn't belong to a web.");
					continue;
				}
				Web web = webUses.get(node);
				out.birthWeb(web);
				out.interfereWith(web);
				continue;
			}
			if (node instanceof MidCallNode){
				for (MidLoadNode loadNode : ((MidCallNode)node).getParamNodes() ){
					if (!webUses.containsKey(node)) {
						LogCenter.debug("RA", "Warning: loadNode " + node
								+ " doesn't belong to a web.");
						continue;
					}
					Web web = webUses.get(node);
					out.birthWeb(web);
					out.interfereWith(web);
				}
				
			}
			if (node instanceof LiveWebsActivist) {
				((LiveWebsActivist) node).setLiveWebs(new ArrayList<Web>(out
						.getLiveWebs()));
			}
		}
		LogCenter.debug("RA", "Live webs: " + out.getLiveWebs() + "\n#\n#");
		return out;
	}

	public static void initialize(Map<MidSaveNode, Web> webDefs,
			Map<MidRegisterNode, Web> webUses) {
		assert (webDefs != null && webUses != null) : "WebProcessor function initialized() called with null arguments.";
		WebProcessor.webDefs = webDefs;
		WebProcessor.webUses = webUses;
	}

}
