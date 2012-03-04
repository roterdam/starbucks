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
			lineOffsets.add(-1L);
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

			StringBuilder prefixSb = new StringBuilder();
			for (int i = 0; i < maxLineNumberWidth
					- Integer.toString(line).length(); i++) {
				prefixSb.append(" ");
			}
			prefixSb.append(line);
			prefixSb.append(":");
			String out = prefixSb.toString() + file.readLine();

			// This is absurd.
			String spaces = "";
			for (int i = 0; i < TAB_SIZE; i++) {
				spaces += " ";
			}
			out = out.replace("\t", spaces);
			System.out.println(out);

			StringBuilder caretSb = new StringBuilder();
			int end = sizeWithTabs(out, TAB_SIZE, col + prefixSb.length() - 1);
			for (int i = 0; i < end; i++) {
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
	private static int sizeWithTabs(String in, int tabsize, int col) {
		int size = 0;
		for (int i = 0; i < col; i++) {
			char c = in.charAt(i);
			if (c == '\t') {
				size += tabsize;
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
