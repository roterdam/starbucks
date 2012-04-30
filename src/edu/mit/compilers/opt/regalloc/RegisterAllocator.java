package edu.mit.compilers.opt.regalloc;

import java.util.List;
import java.util.Map;

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
		Map<MidSaveNode, List<MidLoadNode>> defUseMap = analyzer
				.analyze(symbolTable);
		WebKnitter knitter = new WebKnitter(defUseMap);
		knitter.run();
		InterferenceGenerator gen = new InterferenceGenerator(
				knitter.getWebMapDefs(), knitter.getWebMapUses());
		gen.run();
	}

}
