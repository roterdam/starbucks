package edu.mit.compilers;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import antlr.Token;
import antlr.TokenStreamRecognitionException;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.DecafSemanticChecker;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParserTokenTypes;
import edu.mit.compilers.grammar.DecafScanner;
import edu.mit.compilers.grammar.DecafScannerTokenTypes;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.cp.CPState;
import edu.mit.compilers.opt.cp.CPTransfer;
import edu.mit.compilers.opt.cp.CPTransformer;
import edu.mit.compilers.opt.cse.CSEGlobalState;
import edu.mit.compilers.opt.cse.CSELocalAnalyzer;
import edu.mit.compilers.opt.cse.CSETransfer;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

class Main {
	private static final String OPT_CSE = "cse";
	private static final String OPT_CP = "cp";
	private static final String OPT_RA = "regalloc";
	private static final String OPT_DCE = "dce";
	private static String[] OPTS = new String[] { OPT_CSE, OPT_CP, OPT_RA,
			OPT_DCE };

	public static void main(String[] args) {
		try {
			CLI.parse(args, OPTS);
			InputStream inputStream = args.length == 0 ? System.in
					: new java.io.FileInputStream(CLI.infile);

			ErrorCenter.loadFile(CLI.infile);

			if (CLI.target == Action.SCAN) {
				DecafScanner scanner = new DecafScanner(new DataInputStream(
						inputStream));
				scanner.setTrace(CLI.debug);
				Token token;
				boolean done = false;
				while (!done) {
					try {
						for (token = scanner.nextToken(); token.getType() != DecafParserTokenTypes.EOF; token = scanner
								.nextToken()) {
							String type = "";
							String text = token.getText();
							switch (token.getType()) {
							case DecafScannerTokenTypes.ID:
								type = " IDENTIFIER";
								break;
							case DecafScannerTokenTypes.CHAR_LITERAL:
								type = " CHARLITERAL";
								break;
							case DecafScannerTokenTypes.INT_LITERAL:
								type = " INTLITERAL";
								break;
							case DecafScannerTokenTypes.TRUE:
							case DecafScannerTokenTypes.FALSE:
								type = " BOOLEANLITERAL";
								break;
							case DecafScannerTokenTypes.STRING_LITERAL:
								type = " STRINGLITERAL";
								break;
							}
							System.out.println(token.getLine() + type + " "
									+ text);
						}
						done = true;
					} catch (Exception e) {
						// print the error:
						System.out.println(CLI.infile + " " + e);
						scanner.consume();
					}
				}
			} else if (CLI.target == Action.PARSE || CLI.target == Action.INTER
					|| CLI.target == Action.LOWIR
					|| CLI.target == Action.ASSEMBLY
					|| CLI.target == Action.DEFAULT) {

				DecafScanner scanner = new DecafScanner(new DataInputStream(
						inputStream));
				DecafParser parser = new DecafParser(scanner);
				parser.setTrace(CLI.debug);
				try {
					parser.program();
				} catch (TokenStreamRecognitionException e) {
					ErrorCenter
							.reportFatalError(e.recog.line, e.recog.column, e.recog
									.getMessage());
					scanner.consume();
				}

				// Return a non-zero code if an error has occurred. DO NOT
				// proceed with semantic checking.
				if (ErrorCenter.hasError()) {
					System.exit(1);
				}

				if (CLI.target == Action.INTER || CLI.target == Action.LOWIR
						|| CLI.target == Action.ASSEMBLY) {
					DecafSemanticChecker semanticChecker = new DecafSemanticChecker();
					semanticChecker.crawl((CLASSNode) parser.getAST());

					if (CLI.optOn) {
						// Do algebraic simplifications.
						((CLASSNode) parser.getAST()).simplifyExpressions();
					}

					if (CLI.visual) {
						// For debugging.
						System.out
								.println("--------------------      String Tree      -----------------------");
						AST root = parser.getAST();
						System.out.println(root.toStringTree());
						System.out
								.println("--------------------  Error Checking Done  -----------------------");
						ASTFrame frame = new ASTFrame("6.035", root);
						frame.setVisible(true);
					} else if (ErrorCenter.hasError()) {
						// Only exit if we're not trying to show the frame.
						System.exit(1);
					}

					if (CLI.target == Action.LOWIR
							|| CLI.target == Action.ASSEMBLY) {
						MidSymbolTable symbolTable = MidVisitor
								.createMidLevelIR((CLASSNode) parser.getAST());

						// Run certain optimizations after creating a mid-level
						// IR.
						if (isEnabled(OPT_CSE)) {
							Analyzer<CSEGlobalState, CSETransfer> analyzer = new Analyzer<CSEGlobalState, CSETransfer>(
									new CSEGlobalState().getInitialState(),
									new CSETransfer());
							analyzer.analyze(symbolTable);
							CSELocalAnalyzer localAnalyzer = new CSELocalAnalyzer();
							localAnalyzer.analyze(analyzer, symbolTable);
						}

						if (isEnabled(OPT_CP)) {
							Analyzer<CPState, CPTransfer> analyzer = new Analyzer<CPState, CPTransfer>(
									new CPState().getInitialState(),
									new CPTransfer());
							analyzer.analyze(symbolTable);
							CPTransformer localAnalyzer = new CPTransformer();
							localAnalyzer.analyze(analyzer, symbolTable);
						}

//						if (isEnabled(OPT_DCE)) {
//							LivenessDoctor doctor = new LivenessDoctor();
//							BackwardsAnalyzer<LivenessState, LivenessDoctor> analyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
//									new LivenessState().getBottomState(),
//									doctor);
//							analyzer.analyze(symbolTable);
//							DeadCodeElim dce = new DeadCodeElim();
//							dce.analyze(analyzer, symbolTable);
//						}

						// if (isEnabled(OPT_RA)) {
						// RegisterAllocator allocator = new
						// RegisterAllocator(symbolTable);
						// allocator.run();
						// }

						if (CLI.dot) {
							System.out.println(symbolTable.toDotSyntax(true));
						} else if (CLI.target == Action.ASSEMBLY) {
							MemoryManager.assignStorage(symbolTable);
							writeToOutput(AsmVisitor.generate(symbolTable));
						}

					}
				}
			}
		} catch (Exception e) {
			ErrorCenter.reportError(0, 0, String
					.format("Unrecoverable error of %s\nSTACKTRACE:", e
							.getClass()));
			e.printStackTrace();
			// print the error:
			// System.out.println(CLI.infile);
			// e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Do a linear scan for the optimization and check if it's enabled in
	 * cliOpts.
	 */
	static private boolean isEnabled(String opt) {
		int optIndex = -1;
		for (int i = 0; i < OPTS.length; i++) {
			if (OPTS[i].equals(opt)) {
				optIndex = i;
				break;
			}
		}
		return CLI.opts[optIndex];
	}

	static private void writeToOutput(String text) {
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(CLI.outfile);
			outStream.write(text.getBytes());
			outStream.close();
		} catch (Exception e) {
			System.out.println(String
					.format("Could not open file %s for output.", CLI.outfile));
		}
	}

}
