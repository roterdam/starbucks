package edu.mit.compilers.opt.meta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.common.io.Files;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.BackwardsAnalyzer;
import edu.mit.compilers.opt.as.MidAlgebraicSimplifier;
import edu.mit.compilers.opt.cm.CodeHoister;
import edu.mit.compilers.opt.cm.DomState;
import edu.mit.compilers.opt.cm.DomTransfer;
import edu.mit.compilers.opt.cm.DominanceRecord;
import edu.mit.compilers.opt.cm.LoopGenerator;
import edu.mit.compilers.opt.cp.CPState;
import edu.mit.compilers.opt.cp.CPTransfer;
import edu.mit.compilers.opt.cp.CPTransformer;
import edu.mit.compilers.opt.cse.CSEGlobalState;
import edu.mit.compilers.opt.cse.CSETransfer;
import edu.mit.compilers.opt.cse.CSETransformer;
import edu.mit.compilers.opt.dce.DeadCodeElim;
import edu.mit.compilers.opt.low.SaveSaver;
import edu.mit.compilers.opt.regalloc.LivenessDoctor;
import edu.mit.compilers.opt.regalloc.LivenessState;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;

/**
 * Let's play the meta game. Re-compiles with different parameters and
 * experiments to find the best set. This is a singleton.
 */
public class Optimizer {

	private static int iterID = -1;

	private static final int MAX_CSE_CP_DCE_TIMES = 2;
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

	public void ventureForth(MidSymbolTable symbolTable, String outputFile) {

		setHasAdditionalChanges();
		x = 0;
		// Run CSE + CP + DCE as long as there are changes,
		// since each round of CP may help the next round's
		// CSE.
		while (hasAdditionalChanges && x < MAX_CSE_CP_DCE_TIMES) {
			clearHasAdditionalChanges();

			if (enableCSE) {
				LogCenter.debug("SB", "STARTING CSE.");
				Analyzer<CSEGlobalState, CSETransfer> analyzer = new Analyzer<CSEGlobalState, CSETransfer>(
						new CSEGlobalState(), new CSETransfer());
				analyzer.analyze(symbolTable);
				CSETransformer localAnalyzer = new CSETransformer();
				localAnalyzer.analyze(analyzer, symbolTable);
			}

			if (enableCP) {
				LogCenter.debug("SB", "STARTING CP.");
				Analyzer<CPState, CPTransfer> analyzer = new Analyzer<CPState, CPTransfer>(
						new CPState(), new CPTransfer());
				analyzer.analyze(symbolTable);
				CPTransformer localAnalyzer = new CPTransformer();
				localAnalyzer.analyze(analyzer, symbolTable);
			}

			if (enableDCE) {
				LogCenter.debug("SB", "STARTING DCE.");
				LivenessDoctor doctor = new LivenessDoctor();
				BackwardsAnalyzer<LivenessState, LivenessDoctor> analyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
						new LivenessState().getBottomState(), doctor);
				analyzer.analyze(symbolTable);
				DeadCodeElim dce = new DeadCodeElim();
				dce.analyze(analyzer, symbolTable);
			}

			if (optsOn) {
				LogCenter.debug("SB", "STARTING AS.");
				MidAlgebraicSimplifier simplifier = new MidAlgebraicSimplifier();
				simplifier.analyze(symbolTable);
			}

			x++;
		}

		if (enableCM) {
			for (int i = 0; i < 2; i++) {
				LogCenter.debug("SB", "STARTING CM.");
				Analyzer<DomState, DomTransfer> dominatorAnalyzer = new Analyzer<DomState, DomTransfer>(
						new DomState(), new DomTransfer());
				dominatorAnalyzer.analyze(symbolTable);
				DominanceRecord record = new DominanceRecord(dominatorAnalyzer);
				// Perform DFS through CFGs to build list of loops (IDed by loop
				// end).
				LoopGenerator generator = new LoopGenerator(record);
				generator.run();

				LivenessDoctor doctor = new LivenessDoctor();
				BackwardsAnalyzer<LivenessState, LivenessDoctor> livenessAnalyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
						new LivenessState().getBottomState(), doctor);
				livenessAnalyzer.analyze(symbolTable);

				CodeHoister hoister = new CodeHoister(generator, doctor);
				hoister.hoist();
			}
		}

		LogCenter.debug("OPT", "Ran CSE/CP/DCE optimizations " + (x - 1)
				+ " times.");

		File testDir = null;
		File finalFile = new File(outputFile);
		if (enableRA) {
			File testFile = null;
			while (true) {
				LogCenter.debug("SB", "STARTING RA.");
				iterID++;
				RegisterAllocator allocator = new RegisterAllocator(symbolTable);
				allocator.run();

				// Create a test folder
				try {
					testDir = Files.createTempDir();
					LogCenter.debug("META", "Created temp folder: " + testDir);
				} catch (IllegalStateException e) {
					abort("Could not create folder for testing binaries.");
				}

				// Try a test file.
				testFile = new File(testDir,
						String.format("starbucks%d.s", iterID));
				MemoryManager.assignStorage(symbolTable);

				List<ASM> asmList = AsmVisitor.buildASMList(symbolTable);
				asmList = SaveSaver.pruneList(asmList);

				writeToOutput(testFile.getAbsolutePath(), AsmVisitor.generateText(asmList));
				LogCenter.debug("META", "Wrote to "
						+ testFile.getAbsolutePath());

				// try {
				// long time = TestBench.testFile(testFile);
				// LogCenter.debug("META", "Expecting the binary to take " +
				// time + "ms.");
				// } catch (IOException e) {
				// abort("Could not create directory and files to test binaries.");
				// }

				break;
			}

			LogCenter.debug("SB", "WRITING FINAL FILE.");
			// Write to the final file.
			try {
				Files.copy(testFile, finalFile);
			} catch (IOException e) {
				abort("Could not write to output file " + outputFile);
			}
		} else {
			// If no optimizations, go straight to writing the final file.
			MemoryManager.assignStorage(symbolTable);
			writeToOutput(finalFile.getAbsolutePath(), AsmVisitor.generateText(AsmVisitor
					.buildASMList(symbolTable)));
		}

		// Clean up temp files if necessary.
		if (testDir != null) {
			testDir.delete();
		}

	}

	private void abort(String error) {
		System.out.println(error);
		System.exit(1);
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

	public static int getIterID() {
		return iterID;
	}

}
