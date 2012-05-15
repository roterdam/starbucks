package edu.mit.compilers.opt.regalloc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.BackwardsAnalyzer;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.regalloc.nodes.Allocatable;
import edu.mit.compilers.opt.regalloc.nodes.LiveWebsActivist;

public class RegisterAllocator {

	private final MidSymbolTable symbolTable;
	// MidMethodDeclNode honors CALLEE_SAVED_REGISTERS.
	public final static Reg[] CALLEE_SAVED_REGISTERS = { Reg.RBX, Reg.R12,
			Reg.R13, Reg.R14, Reg.R15 };
	public final static Reg[] CALLER_SAVED_REGISTERS = { Reg.RCX, Reg.RDX,
			Reg.RSI, Reg.RDI, Reg.R8, Reg.R9 };
	// Sorted in "good idea to use" to "fine, use if necessary."
	public final static Reg[] USABLE_REGISTERS = {
			// Callee saved.
			Reg.RBX, Reg.R12, Reg.R13, Reg.R14, Reg.R15,
			// Caller saved.
			Reg.RCX, Reg.RSI, Reg.RDI, Reg.R8, Reg.R9,
			// Caller saved, but also used in DIV and MOD.
			Reg.RDX };

	public RegisterAllocator(MidSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void run() {
		LivenessDoctor doctor = new LivenessDoctor();
		BackwardsAnalyzer<LivenessState, LivenessDoctor> analyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
				new LivenessState().getBottomState(), doctor);
		analyzer.analyze(symbolTable);

		Map<MidSaveNode, Set<MidUseNode>> defUseMap = doctor.getDefUseMap();
		WebKnitter knitter = new WebKnitter(defUseMap);
		List<Web> webs = knitter.run();

		WebProcessor.initialize(knitter.getWebMapDefs(), knitter
				.getWebMapUses());
		BackwardsAnalyzer<WebState, WebProcessor> interferenceAnalyzer = new BackwardsAnalyzer<WebState, WebProcessor>(
				new WebState().getBottomState(), new WebProcessor());
		interferenceAnalyzer.analyze(symbolTable);

		LogCenter.debug("RA", "Webs created:");
		for (Web w : webs) {
			LogCenter.debug("RA", String.format("%s: %s", w, w
					.getInterferences()));
		}
		GraphColorer crayola = new GraphColorer(USABLE_REGISTERS);
		Map<Web, Reg> mapping = crayola.color(webs);
		LogCenter.debug("RA", "Coloring results: "
				+ HashMapUtils.toMapString(mapping));

		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods()
				.entrySet()) {
			applyAllocations(entry.getValue(), mapping, knitter);
		}
	}

	private void applyAllocations(MidMethodDeclNode methodDeclNode,
			Map<Web, Reg> mapping, WebKnitter knitter) {
		for (MidNode node : methodDeclNode.getNodeList()) {
			if (node instanceof Allocatable) {
				Allocatable allocatedNode = (Allocatable) node;
				Web web = knitter.lookupWeb(allocatedNode);
				Reg allocatedReg = mapping.get(web);
				if (allocatedReg != null) {
					LogCenter.debug("RA", "Allocating " + allocatedReg.name()
							+ " (" + web + ") to " + node);
					allocatedNode.allocateRegister(allocatedReg);
				}
				continue;
			}
			if (node instanceof LiveWebsActivist) {
				if (node instanceof MidCallNode) {
					LogCenter.debug("CALL", "Checking out "
							+ ((MidCallNode) node).getName());
				}
				((LiveWebsActivist) node).applyAllocatedMapping(mapping);
			}
		}
	}

}
