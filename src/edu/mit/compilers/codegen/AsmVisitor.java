package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.asm.SectionASM;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidStringDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class AsmVisitor {

	// Static variable because Strings have to be added to it from within other
	// code.
	private static List<ASM> dataSection = createDataSection();;
	private static Set<String> externCalls = new HashSet<String>();

	private AsmVisitor(MidSymbolTable symbolTable) {
	}

	public static String generate(MidSymbolTable symbolTable) {

		List<ASM> asm = new ArrayList<ASM>();

		asm.addAll(createBSSSection(symbolTable));
		List<ASM> textSection = createTextSection();

		for (String methodName : symbolTable.getMethods().keySet()) {
			textSection.addAll(symbolTable.getMethod(methodName).toASM());
		}

		asm.addAll(dataSection);
		asm.addAll(textSection);

		StringBuilder out = new StringBuilder();
		for (String extern : externCalls) {
			out.append(new OpASM(OpCode.EXTERN, extern).toString());
		}
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
			out.add(fieldNode.getFieldLabelASM());
			out.add(fieldNode.getFieldDeclarationASM());
		}
		return out;
	}

	private static List<ASM> createDataSection() {
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

	private static int stringLabelCount = 0;

	private static String labelSafeString(String text) {
		String clean = text.replaceAll("[^0-9A-Za-z]", "");
		return "s_" + clean.substring(0, Math.min(8, clean.length()))
				+ stringLabelCount++;
	}

	/**
	 * Register a string literal, returns a MidFieldDeclNode to use in
	 * MidVisitor calls.
	 * 
	 * @param text
	 * @return
	 */
	public static MidStringDeclNode addStringLiteral(String text) {
		String labelText = labelSafeString(text);
		dataSection.add(new LabelASM("", labelText));
		// asciiBuilder converts all strings to comma-separated list of ascii to
		// escape all text input.
		StringBuilder asciiBuilder = new StringBuilder();
		String prefix = "";
		for (char c : text.toCharArray()) {
			asciiBuilder.append(String.format(prefix + "%d", (int) c));
			prefix = ",";
		}
		// Add newline and NULL byte.
		asciiBuilder.append(prefix + "10,0");
		dataSection.add(new OpASM("`" + text + "`", OpCode.DB, asciiBuilder
				.toString()));

		MidStringDeclNode out = new MidStringDeclNode(labelText);
		out.setRawLocationReference(labelText);
		return out;
	}

	public static List<ASM> methodCall(String name, List<MidMemoryNode> params, boolean extern) {
		if (extern) {
			externCalls.add(name);
		}
		List<ASM> out = new ArrayList<ASM>();
		// Begin calling convention, place as many nodes in registers as
		// possible.
		Reg[] paramRegisters = new Reg[] { Reg.RDI, Reg.RSI, Reg.RDX, Reg.RCX,
				Reg.R8, Reg.R9 };

		for (int i = 0; i < params.size(); i++) {
			MidLoadNode paramNode = new MidLoadNode(params.get(i));
			if (i < paramRegisters.length) {
				// Want to set the register.
				paramNode.setRegister(paramRegisters[i]);
				out.addAll(paramNode.toASM());
			} else {
				paramNode.setRegister(MemoryManager.allocTempRegister());
				out.addAll(paramNode.toASM());
				out.add(new OpASM(String.format("push param %d onto stack", i),
						OpCode.PUSH, paramNode.getRegister().name()));
			}
		}
		// Always set RAX to 0.
		out.add(new OpASM(OpCode.XOR, Reg.RAX.name(), Reg.RAX.name()));
		out.add(new OpASM(OpCode.CALL, name));
		int stackParams = params.size() - paramRegisters.length;
		if (stackParams > 0) {
			out.add(new OpASM("clean up params", OpCode.MOV, Reg.RSP.name(),
					String.format("[ %s - %d ]", Reg.RSP.name(), stackParams
							* MemoryManager.ADDRESS_SIZE)));
		}
		return out;
	}

}
