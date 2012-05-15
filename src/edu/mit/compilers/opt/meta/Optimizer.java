package edu.mit.compilers.opt.meta;

import java.io.FileOutputStream;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.BackwardsAnalyzer;
import edu.mit.compilers.opt.as.MidAlgebraicSimplifier;
import edu.mit.compilers.opt.cm.CMState;
import edu.mit.compilers.opt.cm.CMTransfer;
import edu.mit.compilers.opt.cp.CPState;
import edu.mit.compilers.opt.cp.CPTransfer;
import edu.mit.compilers.opt.cp.CPTransformer;
import edu.mit.compilers.opt.cse.CSEGlobalState;
import edu.mit.compilers.opt.cse.CSETransfer;
import edu.mit.compilers.opt.cse.CSETransformer;
import edu.mit.compilers.opt.dce.DeadCodeElim;
import edu.mit.compilers.opt.regalloc.LivenessDoctor;
import edu.mit.compilers.opt.regalloc.LivenessState;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;

/**
 * Let's play the meta game. Re-compiles with different parameters and
 * experiments to find the best set. This is a singleton.
 */
public class Optimizer {

	private static final int MAX_CSE_CP_DCE_TIMES = 5;
	private static Optimizer singleton;

	// Statically track whether or not we've made optimizations.
	public static boolean hasAdditionalChanges = false;
	private int x;

	private final boolean optsOn;
	private final boolean enableCSE;
	private final boolean enableCP;
	private final boolean enableDCE;
	private final boolean enableCM;
	private final boolean enableRA;

	private Optimizer(int options) {
		optsOn = (options & Options.OPTS_ON) == Options.OPTS_ON;
		enableCSE = (options & Options.CSE) == Options.CSE;
		enableCP = (options & Options.CP) == Options.CP;
		enableDCE = (options & Options.DCE) == Options.DCE;
		enableCM = (options & Options.CM) == Options.CM;
		enableRA = (options & Options.RA) == Options.RA;
	}

	public void go(MidSymbolTable symbolTable, String outputFile) {

		setHasAdditionalChanges();
		x = 0;
		// Run CSE + CP + DCE as long as there are changes,
		// since each round of CP may help the next round's
		// CSE.
		while (hasAdditionalChanges && x < MAX_CSE_CP_DCE_TIMES) {
			clearHasAdditionalChanges();

			if (enableCSE) {
				Analyzer<CSEGlobalState, CSETransfer> analyzer = new Analyzer<CSEGlobalState, CSETransfer>(
						new CSEGlobalState().getInitialState(),
						new CSETransfer());
				analyzer.analyze(symbolTable);
				CSETransformer localAnalyzer = new CSETransformer();
				localAnalyzer.analyze(analyzer, symbolTable);
			}

			if (enableCP) {
				Analyzer<CPState, CPTransfer> analyzer = new Analyzer<CPState, CPTransfer>(
						new CPState().getInitialState(), new CPTransfer());
				analyzer.analyze(symbolTable);
				CPTransformer localAnalyzer = new CPTransformer();
				localAnalyzer.analyze(analyzer, symbolTable);
			}

			if (enableDCE) {
				LivenessDoctor doctor = new LivenessDoctor();
				BackwardsAnalyzer<LivenessState, LivenessDoctor> analyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
						new LivenessState().getBottomState(), doctor);
				analyzer.analyze(symbolTable);
				DeadCodeElim dce = new DeadCodeElim();
				dce.analyze(analyzer, symbolTable);
			}

			if (enableCM) {
				Analyzer<CMState, CMTransfer> analyzer = new Analyzer<CMState, CMTransfer>(
						new CMState().getInitialState(), new CMTransfer());
				analyzer.analyze(symbolTable);
			}

			x++;
		}

		if (optsOn) {
			MidAlgebraicSimplifier simplifier = new MidAlgebraicSimplifier();
			simplifier.analyze(symbolTable);
		}

		LogCenter.debug("OPT", "Ran CSE/CP/DCE optimizations " + (x - 1)
				+ " times.");

		if (enableRA) {
			RegisterAllocator allocator = new RegisterAllocator(symbolTable);
			allocator.run();
		}

//		System.out.println(symbolTable.toDotSyntax(true));
		MemoryManager.assignStorage(symbolTable);
		writeToOutput(outputFile, AsmVisitor.generate(symbolTable));

	}

	private void writeToOutput(String outputFile, String text) {
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(outputFile);
			outStream.write(text.getBytes());
			outStream.close();
		} catch (Exception e) {
			System.out.println(String
					.format("Could not open file %s for output.", outputFile));
		}
	}

	private static void clearHasAdditionalChanges() {
		hasAdditionalChanges = false;
	}

	public static void setHasAdditionalChanges() {
		hasAdditionalChanges = true;
	}

	public static Optimizer getOptimizer(int options) {
		if (singleton == null) {
			singleton = new Optimizer(options);
		}
		return singleton;
	}

}
