package edu.mit.compilers.codegen;

import edu.mit.compilers.crawler.SemanticRules;

public class MidMethodNameManager {
	public static String sanitizeUserDefinedMethodName(String methodName){
		if(methodName.equals(SemanticRules.MAIN)){
			return methodName;
		}
		return "user_"+methodName;
	}
	public static String sanitizeCustomMethodName(String methodName){
		return "sys_"+methodName;
	}
}
