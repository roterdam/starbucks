package edu.mit.compilers.opt.regalloc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class RegisterAllocator {

	private final MidSymbolTable symbolTable;

	public RegisterAllocator(MidSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void run() {
		LivenessDoctor analyzer = new LivenessDoctor();
		Map<MidSaveNode, Set<MidLoadNode>> defUseMap = analyzer
				.analyze(symbolTable);
		WebKnitter knitter = new WebKnitter(defUseMap);
		List<Web> webs = knitter.run();
		InterferenceGenerator gen = new InterferenceGenerator(
				knitter.getWebMapDefs(), knitter.getWebMapUses());
		gen.analyze(symbolTable);
		LogCenter.debug("RA", "RA results: " + webs);
	}

	/**
	 * Debugging code for printing interference graph.
	 * @param webs
	 */
	private void printInterferenceGraph(List<Web> webs) {
		System.out.println("digraph InterferenceGraph {\n");
		for (Web web : webs) {
			LogCenter.debug("RA", web.toString());
			System.out.println(String.format("%s [label=\"%s\"];", web
					.hashCode(), web.toString()));
			for (Web inter : web.getInterferences()) {
				System.out
						.println(String.format("%s -> %s;", web.hashCode(), inter
								.hashCode()));
			}
		}
		System.out.println("}");

	}

}
