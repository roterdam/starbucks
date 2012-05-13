package edu.mit.compilers;

import edu.mit.compilers.tools.CLI;

public class LogCenter {

	public static void debug(String tag, String s) {
		if (tag.equals("MEM")) {
			return;
		}
		if (CLI.debug) {
			StackTraceElement[] stackTraceElements = Thread.currentThread()
					.getStackTrace();
			StackTraceElement lastElement = stackTraceElements[2];
			String[] lines = s.split("\n");
			String[] classComponents = lastElement.getClassName().split("\\.");
			for (String line : lines) {
				System.out
						.println(String
								.format("[%s] (%s:%s) %s", tag, classComponents[classComponents.length - 1], lastElement
										.getLineNumber(), line));
			}
		}
	}
}
