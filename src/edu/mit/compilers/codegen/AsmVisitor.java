package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.SectionASM;

public class AsmVisitor {

	private static MidSymbolTable symbolTable;
	// Static variable because Strings have to be added to it from within other
	// code.
	private static List<ASM> dataSection;

	// How many bytes each address takes. Use for calculating offsets!
	// Note that it's 8, not 4, in 64-bit since 8*8bytes = 64 bits.
	public static final int ADDRESS_SIZE = 8;
	public static final String ADDRESS_SIZE_STRING = Integer.toString(ADDRESS_SIZE);

	private AsmVisitor(MidSymbolTable symbolTable) {
	}

	public static String generate() {

		dataSection = createDataSection();

		List<ASM> textSection = createTextSection();

		for (String methodName : AsmVisitor.symbolTable.getMethods().keySet()) {
			textSection.addAll(symbolTable.getMethod(methodName).toASM());
		}

		List<ASM> asm = new ArrayList<ASM>();
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

	private static List<ASM> createDataSection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("data"));
		return out;
	}

	private static List<ASM> createTextSection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("text"));
		out.add(new OpASM(OpASM.OpCode.GLOBAL, "main"));
		return out;
	}

	public static void setSymbolTable(MidSymbolTable symbolTable) {
		AsmVisitor.symbolTable = symbolTable;
	}
}
