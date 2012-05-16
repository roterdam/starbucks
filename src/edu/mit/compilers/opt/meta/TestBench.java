package edu.mit.compilers.opt.meta;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import edu.mit.compilers.LogCenter;

public class TestBench {

	private final static String NASM_CMD = "nasm -f elf64 -o %s %s";
	private final static String GCC_CMD = "gcc -o %s %s";

	/**
	 * Takes an ASM text file, compiles it, runs it, and returns how long it
	 * took (in milliseconds).
	 * 
	 * @throws IOException
	 */
	public static long testFile(File asmFile) throws IOException {
		File tempDir = Files.createTempDir();
		String tempPath = tempDir.getAbsolutePath() + "/";
		Runtime runtime = Runtime.getRuntime();

		String asmFileName = asmFile.getName();
		int dotPos = asmFileName.lastIndexOf('.');
		if (dotPos != -1) {
			asmFileName = asmFileName.substring(0, dotPos);
		}
		String oFileName = tempPath + asmFileName + ".o";
		String exeFileName = tempPath + asmFileName;

		String nasmCmd = String.format(NASM_CMD, oFileName,
				asmFile.getAbsolutePath());
		String gccCmd = String.format(GCC_CMD, exeFileName, oFileName);

		long elapsedTime = -1;

		LogCenter.debug("META", "Test bench a go with starting command: "
				+ nasmCmd);
		try {
			String[] buildCmd = { "/bin/sh", "-c",
					nasmCmd + "; " + gccCmd + "; exit" };
			Process p1 = runtime.exec(buildCmd);
			p1.waitFor();

			long curTime = System.currentTimeMillis();
			String[] runCmd = { "/bin/sh", "-c", exeFileName + "; exit" };
			Process p2 = runtime.exec(runCmd);
			p2.waitFor();
			elapsedTime = System.currentTimeMillis() - curTime;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		tempDir.delete();
		return elapsedTime;
	}

	public static void main(String[] args) {
		try {
			testFile(new File(
					"/afs/athena.mit.edu/user/j/o/joshma/starbucks/tmp/sample.s"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
