package edu.mit.compilers.crawler;

import java.util.List;

public class MethodSignature {

	private VarType returnType;
	private String id;
	private List<VarType> params;

	public MethodSignature(VarType returnType, String id, List<VarType> params) {
		this.returnType = returnType;
		this.id = id;
		this.params = params;
	}

	public VarType getReturnType() {
		return returnType;
	}

	public String getId() {
		return id;
	}

	public List<VarType> getParams() {
		return params;
	}

}
