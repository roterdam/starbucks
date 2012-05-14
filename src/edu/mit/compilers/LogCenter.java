package edu.mit.compilers;

import edu.mit.compilers.tools.CLI;

public class LogCenter {

	public static void debug(String tag, String s) {
		if (!CLI.debug) {
			return;
		}

		if (CLI.tags != null) {
			boolean print = false;
			for (int i = 0; i < CLI.tags.length; i++) {
				if (CLI.tags[i].equals(tag)) {
					print = true;
				}
			}
			if (!print) {
				return;
			}
		}

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
