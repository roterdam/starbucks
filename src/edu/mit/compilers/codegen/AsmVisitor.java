package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;

public class AsmVisitor {

	private static MidSymbolTable symbolTable;
	
	private AsmVisitor(MidSymbolTable symbolTable){


	}
	
	public static String generate() {
		
		List<ASM> asm = new ArrayList<ASM>();

		for ( String methodName : AsmVisitor.symbolTable.getMethods().keySet() ){
			asm.addAll(symbolTable.getMethod(methodName).toASM());
		}	
		
		StringBuilder out = new StringBuilder();
		out.append("Generating\n");
		
		for ( ASM asmLine : asm ){
			out.append(asmLine.toString());
		}
		
		return out.toString();
	}

	public static void setSymbolTable(MidSymbolTable symbolTable) {
		AsmVisitor.symbolTable = symbolTable;
	}
}
