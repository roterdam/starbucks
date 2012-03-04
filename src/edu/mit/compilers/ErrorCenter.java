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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void reportError(int line, int col, String message) {
		hasError = true;
		try {
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
					col -= (ANTLR_TAB_SIZE - TAB_SIZE);
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

	/**
	 * Calculates the length of a string, assuming a tab size.
	 * 
	 * @param in
	 * @param tabsize
	 * @return
	 */
	private static int sizeWithTabs(String in, int col) {
		System.out.println("col is " + col);
		System.out
				.println("WORKING WITH `" + in + "` of length " + in.length());
		int size = 0;
		for (int i = 0; i < col; i++) {
			char c = in.charAt(i);
			if (c == '\t') {
				size += TAB_SIZE;
			} else {
				size += 1;
			}
		}
		return size;
	}

	public static boolean hasError() {
		return hasError;
	}

}
