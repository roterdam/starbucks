package edu.mit.compilers.opt.regalloc;

import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Analyzer;

public class InterferenceGenerator extends Analyzer<WebState, WebProcessor> {

	public InterferenceGenerator(Map<MidSaveNode, Web> webDefs,
			Map<MidLoadNode, Web> webUses) {
		super(new WebState().getInitialState(), new WebProcessor());
		WebProcessor.initialize(webDefs, webUses);
	}

}
