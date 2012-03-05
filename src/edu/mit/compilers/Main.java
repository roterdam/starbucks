package edu.mit.compilers;

import java.io.DataInputStream;
import java.io.InputStream;

import antlr.Token;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.crawler.DecafSemanticChecker;
import edu.mit.compilers.grammar.DecafParser;
import edu.mit.compilers.grammar.DecafParserTokenTypes;
import edu.mit.compilers.grammar.DecafScanner;
import edu.mit.compilers.grammar.DecafScannerTokenTypes;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;

class Main {
	public static void main(String[] args) {
		try {
			CLI.parse(args, new String[0]);
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
					|| CLI.target == Action.DEFAULT) {

				DecafScanner scanner = new DecafScanner(new DataInputStream(
						inputStream));
				DecafParser parser = new DecafParser(scanner);
				parser.setTrace(CLI.debug);
				parser.program();

				// Return a non-zero code if an error has occurred. DO NOT
				// proceed with semantic checking.
				if (parser.hasError()) {
					System.exit(1);
				}

				if (CLI.target == Action.INTER) {
					DecafSemanticChecker semanticChecker = new DecafSemanticChecker();
					semanticChecker.crawl((CLASSNode) parser.getAST());
					
					if (CLI.visual){
						// For debugging.
						System.out.println("--------------------      String Tree      -----------------------");
						AST root = parser.getAST();
						System.out.println(root.toStringTree());
						System.out.println("--------------------  Error Checking Done  -----------------------");
						ASTFrame frame = new ASTFrame("6.035", root);
						frame.setVisible(true);
					} else if (ErrorCenter.hasError()) {
						// Only exit if we're not trying to show the frame.
						System.exit(1);
					}
				}
			}
		} catch (Exception e) {
			// print the error:
			System.out.println(CLI.infile);
			e.printStackTrace();
		}
	}

}
