package edu.mit.compilers.codegen;

import edu.mit.compilers.crawler.SemanticRules;

public class MidMethodNameManager {
	private static String USER_NAMESPACE = "user";
	private static String STARBUCKS_NAMESPACE = "starbucks";

	public static String sanitizeUserDefinedMethodName(String methodName) {
		if (methodName.equals(SemanticRules.MAIN)) {
			return methodName;
		}
		return String.format("%s_%s", USER_NAMESPACE, methodName);
	}

	public static String sanitizeCustomMethodName(String methodName) {
		return String.format("%s_%s", STARBUCKS_NAMESPACE, methodName);
	}
}