package edu.mit.compilers.codegen;

public class AsmVisitor {

	private MidSymbolTable symbolTable;
	
	public AsmVisitor(MidSymbolTable symbolTable){
		
		this.symbolTable = symbolTable;

	}
	
	public String generate() {
		StringBuilder out = new StringBuilder();
		
		for ( String methodName : this.symbolTable.getMethods().keySet() ){
			out.append(symbolTable.getMethod(methodName).toASM());
		}	
		
		return out.toString();
	}
}
