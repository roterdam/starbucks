package edu.mit.compilers;

import edu.mit.compilers.tools.CLI;

public class LogCenter {

	public static void debug(String s) {
		if (CLI.debug) {
			System.out.println(s);
		}
	}

}
