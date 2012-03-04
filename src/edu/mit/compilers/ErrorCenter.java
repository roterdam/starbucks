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
			maxLineNumberWidth = Integer.toString(lineOffsets.size() - 1).length();
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
			for (int i = 0; i < maxLineNumberWidth - Integer.toString(line).length(); i++) {
				prefixSb.append(" ");
			}
			prefixSb.append(line);
			prefixSb.append(":");
			String prefix = prefixSb.toString();
			// TODO: REPLACE TABS WITH SPACES FOR PRINTING.
			System.out.println(prefix + file.readLine());
			StringBuilder caretSb = new StringBuilder();
			for (int i = 0; i < prefix.length() + col - 1; i++) {
				caretSb.append(" ");
			}
			caretSb.append("^");
			System.out.println(caretSb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean hasError(){
		return hasError;
	}

}
