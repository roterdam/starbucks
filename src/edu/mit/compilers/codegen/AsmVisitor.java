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
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidStringDeclNode;
import edu.mit.compilers.crawler.SemanticRules;

public class AsmVisitor {
	public static String PRINTF = "printf";
	public static String EXIT = "exit";

	// Static variable because Strings have to be added to it from within other
	// code.
	private static List<ASM> dataSection;
	private static List<ASM> readOnlySection;
	private static Set<String> externCalls;

	static {
		readOnlySection = createReadOnlySection();
		externCalls = new HashSet<String>();
	}

	private AsmVisitor(MidSymbolTable symbolTable) {
	}

	public static String generateText(List<ASM> asm) {

		StringBuilder out = new StringBuilder();
		for (String extern : externCalls) {
			out.append(new OpASM(OpCode.EXTERN, extern).toString());
		}
		
		for (ASM asmLine : asm) {
			out.append(asmLine.toString());
		}

		return out.toString();
	}
	
	public static List<ASM> buildASMList(MidSymbolTable symbolTable) {
		dataSection = createDataSection();

		List<ASM> asm = new ArrayList<ASM>();
		List<ASM> textSection = createTextSection();

		// Add fieldNode declarations.
		Map<String, MidMemoryNode> fieldVars = symbolTable.getLocalVars();
		for (String fieldName : fieldVars.keySet()) {
			MidFieldDeclNode fieldNode = (MidFieldDeclNode) fieldVars
					.get(fieldName);
			dataSection.add(fieldNode.getFieldLabelASM());
			dataSection.add(fieldNode.getFieldDeclarationASM());
		}

		for (String methodName : symbolTable.getMethods().keySet()) {
			textSection.addAll(symbolTable.getMethod(methodName).toASM());
		}
		for (String methodName : symbolTable.getStarbucksMethods().keySet()) {
			textSection.addAll(symbolTable.getStarbucksMethod(methodName)
					.toASM());
		}

		asm.addAll(dataSection);
		asm.addAll(readOnlySection);
		asm.addAll(textSection);
		return asm;
	}

	public static List<ASM> exitCall(int exitCode) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(String.format("Exit interrupt %d", exitCode),
				OpCode.XOR, Reg.RAX.name(), Reg.RAX.name()));
		out.add(new OpASM(OpCode.XOR, Reg.RDI.name(), Reg.RDI.name()));
		externCalls.add(EXIT);
		out.add(new OpASM(OpCode.CALL, EXIT));
		return out;
	}

	private static List<ASM> createDataSection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("data"));
		return out;
	}

	private static List<ASM> createReadOnlySection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("rodata"));
		return out;
	}

	private static List<ASM> createTextSection() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new SectionASM("text"));
		out.add(new OpASM(OpCode.GLOBAL, SemanticRules.MAIN));
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
		readOnlySection.add(new LabelASM("", labelText));
		String outputString = String.format("`%s`,0", text);
		readOnlySection
				.add(new OpASM("`" + text + "`", OpCode.DB, outputString));

		MidStringDeclNode out = new MidStringDeclNode(labelText);
		out.setRawLocationReference(labelText);
		return out;
	}

	public static Reg[] paramRegisters = new Reg[] { Reg.RDI, Reg.RSI, Reg.RDX,
			Reg.RCX, Reg.R8, Reg.R9 };

	public static List<ASM> methodCall(MidCallNode callNode,
			boolean saveValueDisabled) {
		String name = callNode.getName();

		if (callNode instanceof MidCalloutNode) {
			externCalls.add(name);
		}
		List<ASM> out = new ArrayList<ASM>();

		// Always set RAX to 0.
		out.add(new OpASM(OpCode.CALL, name));

		if (!callNode.saveValueDisabled()) {
			Reg destinationRegister = callNode.getRegister();
			// Push RAX into destinationRegister.
			out.add(new OpASM("Saving results of " + name, OpCode.MOV,
					destinationRegister.name(), Reg.RAX.name()));
		}

		return out;
	}

}
