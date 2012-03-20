package edu.mit.compilers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ErrorCenter {

	static String filename = "";
	static RandomAccessFile file;
	static List<Long> lineOffsets;
	static int maxLineNumberWidth;
	static boolean hasError = false;
	// This has to match with ANTLR's size, unfortunately.
	static int ANTLR_TAB_SIZE = 8;
	// This is OUR tab size. Much nicer.
	static int TAB_SIZE = 4;

	/**
	 * Reads entire file into memory for error reporting.
	 * 
	 * @param newFilename
	 * @throws IOException
	 */
	public static void loadFile(String newFilename) {
		try {
			file = new RandomAccessFile(newFilename, "r");
			filename = newFilename;
			lineOffsets = new ArrayList<Long>();
			// Line "0" doesn't exist.
			lineOffsets.add(0L);
			lineOffsets.add(file.getFilePointer());
			while (file.readLine() != null) {
				lineOffsets.add(file.getFilePointer());
			}
			maxLineNumberWidth = Integer.toString(lineOffsets.size() - 1)
					.length();
		} catch (IOException e) {
			System.out.println("Error opening " + newFilename);
		}
	}

	public static void reportFatalError(int line, int col, String message) {
		System.out.println("Fatal syntax error encountered, stopping now:");
		reportError(line, col, message);
	}

	public static void reportError(int line, int col, String message) {
		hasError = true;
		try {
			// If both line and col are 0, treat it as a "global" error with no
			// real context.
			if (line == 0 && col == 0) {
				if(!filename.equals("")){
					System.out.println("Error in " + filename + ":" + message);
				}else{
					System.out.println(message);
				}
				return;
			}
			System.out.println("Error at " + filename + ":" + line + "," + col
					+ ": " + message);
			file.seek(lineOffsets.get(line));

			// Generate the prefix before the line of code.
			StringBuilder prefixSb = new StringBuilder();
			for (int i = 0; i < maxLineNumberWidth
					- Integer.toString(line).length(); i++) {
				prefixSb.append(" ");
			}
			prefixSb.append(line);
			prefixSb.append(":");
			// Combine the prefix and the code.
			String out = prefixSb.toString() + file.readLine();

			// This is absurd.
			String spaces = "";
			for (int i = 0; i < TAB_SIZE; i++) {
				spaces += " ";
			}
			// Replace each tab with TAB_SIZE spaces, but also track how much to
			// decrement the col by. This is because col is calculated with the
			// assumption that each tab took up ANTLR_TAB_SIZE; reason why is
			// not known.
			StringBuilder outBuilder = new StringBuilder();
			char c;
			for (int i = 0; i < out.length(); i++) {
				c = out.charAt(i);
				if (c == '\t') {
					outBuilder.append(spaces);
					// Only subtract from the column index if tabs occur before it.
					if (i < col) {
						col -= (ANTLR_TAB_SIZE - TAB_SIZE);
					}
				} else {
					outBuilder.append(c);
				}
			}
			out = outBuilder.toString();
			System.out.println(out);

			// Add the number of spaces necessary for caret.
			StringBuilder caretSb = new StringBuilder();
			// col already takes TAB_SIZE into account.
			for (int i = 0; i < prefixSb.length() + col - 1; i++) {
				caretSb.append(" ");
			}
			caretSb.append("^");
			System.out.println(caretSb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean hasError() {
		return hasError;
	}

}
