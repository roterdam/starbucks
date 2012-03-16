package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.asm.SectionASM;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class AsmVisitor {

	// Static variable because Strings have to be added to it from within other
	// code.
	private static List<ASM> dataSection;

	private AsmVisitor(MidSymbolTable symbolTable) {
	}

	public static String generate(MidSymbolTable symbolTable) {

		List<ASM> asm = new ArrayList<ASM>();
		
		asm.addAll(createBSSSection(symbolTable));
		dataSection = createDataSection(symbolTable);
		List<ASM> textSection = createTextSection();

		for (String methodName : symbolTable.getMethods().keySet()) {
			textSection.addAll(symbolTable.getMethod(methodName).toASM());
		}

		asm.addAll(dataSection);
		asm.addAll(textSection);

		StringBuilder out = new StringBuilder();
		for (ASM asmLine : asm) {
			out.append(asmLine.toString());
		}

		return out.toString();
	}

	public void addToDataSection(ASM asm) {
		dataSection.add(asm);
	}

	/**
	 * Initializes .bss section with fields. Note that .bss is meant for
	 * UNINITIALIZED data, as opposed to the .data section.
	 * 
	 * @return
	 */
	private static List<ASM> createBSSSection(MidSymbolTable symbolTable) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("bss"));
		Map<String, MidMemoryNode> fieldVars = symbolTable.getLocalVars();
		for (String fieldName : fieldVars.keySet()) {
			MidFieldDeclNode fieldNode = (MidFieldDeclNode) fieldVars
					.get(fieldName);
			out.add(fieldNode.getFieldLabel());
			out.add(fieldNode.getFieldDeclaration());
		}
		return out;
	}
	
	private static List<ASM> createDataSection(MidSymbolTable symbolTable) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("data"));
		return out;
	}

	private static List<ASM> createTextSection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("text"));
		out.add(new OpASM(OpCode.GLOBAL, "main"));
		return out;
	}
}
