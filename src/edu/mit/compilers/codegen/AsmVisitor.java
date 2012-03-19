package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.LabeledOpASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.asm.SectionASM;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidStringDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class AsmVisitor {
	private static int interruptCounter = 0;

	private static String SYS_EXIT_CODE = "1";
	private static String SYS_INTERRUPT_CODE = "0x80";

	// Static variable because Strings have to be added to it from within other
	// code.
	private static List<ASM> dataSection = createDataSection();
	private static Set<String> externCalls = new HashSet<String>();

	private AsmVisitor(MidSymbolTable symbolTable) {
	}

	public static String generate(MidSymbolTable symbolTable) {

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

		// Error handler
		List<ASM> divZero = addInterrupt(MidLabelManager.getDivideByZeroLabel(), "*** RUNTIME ERROR ***: Divide by zero in method.");
		List<ASM> indexBounds = addInterrupt(MidLabelManager.getArrayIndexOutOfBoundsLabel(), "*** RUNTIME ERROR ***: Array out of bounds access in method.");

		
		asm.addAll(dataSection);
		asm.addAll(textSection);
		asm.addAll(divZero);
		asm.addAll(indexBounds);

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
	 * Appends helper text to end of assembly for error handling.
	 */
	private static List<ASM> addInterrupt(MidLabelNode labelNode, String msg) {
		interruptCounter++;

		List<ASM> out = new ArrayList<ASM>();

		String msgLabel = "msg" + interruptCounter;
		String lenLabel = "len" + interruptCounter;
		// Add string to header.
		String outputMessage = String.format("`%s`,0", msg);
		dataSection.add(new LabeledOpASM(msgLabel, OpCode.DB, outputMessage));
		dataSection.add(new LabeledOpASM(lenLabel, OpCode.EQU, String
				.format("$-%s", msgLabel)));
		out.addAll(labelNode.toASM());
		out.add(new OpASM(OpCode.MOV, Reg.RDX.name(), lenLabel));
		out.add(new OpASM(OpCode.MOV, Reg.RCX.name(), msgLabel));
		out.add(new OpASM(OpCode.MOV, Reg.RBX.name(), "1"));
		out.add(new OpASM(OpCode.MOV, Reg.RAX.name(), "4"));
		out.add(new OpASM(OpCode.INT, SYS_INTERRUPT_CODE));
		out.addAll(exitCall(1));
		return out;
	}

	private static List<ASM> exitCall(int exitCode) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(String.format("Exit interrupt %d", exitCode),
				OpCode.MOV, Reg.RAX.name(), SYS_EXIT_CODE));
		out.add(new OpASM(OpCode.MOV, Reg.RBX.name(), Integer
				.toString(exitCode)));
		out.add(new OpASM(OpCode.INT, SYS_INTERRUPT_CODE));
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
		String outputString = String.format("`%s`,0", text);
		dataSection.add(new OpASM("`" + text + "`", OpCode.DB, outputString));

		MidStringDeclNode out = new MidStringDeclNode(labelText);
		out.setRawLocationReference(labelText);
		return out;
	}

	public static Reg[] paramRegisters = new Reg[] { Reg.RDI, Reg.RSI, Reg.RDX,
			Reg.RCX, Reg.R8, Reg.R9 };

	public static List<ASM> methodCall(String name, List<MidMemoryNode> params,
			Reg destinationRegister, boolean extern) {
		if (extern) {
			externCalls.add(name);
		}
		List<ASM> out = new ArrayList<ASM>();

		// Begin calling convention, place as many nodes in registers as
		// possible.

		List<ASM> pushStack = new ArrayList<ASM>();
		for (int i = 0; i < params.size(); i++) {
			MidLoadNode paramNode = new MidLoadNode(params.get(i));
			if (i < paramRegisters.length) {
				// Want to set the register.
				paramNode.setRegister(paramRegisters[i]);
				out.addAll(paramNode.toASM());
			} else {
				// Push the parameters in reverse order to a list
				Reg temp = MemoryManager.allocTempRegister();
				paramNode.setRegister(temp);
				List<ASM> pushIt = new ArrayList<ASM>();
				pushIt.addAll(paramNode.toASM());
				pushIt.add(new OpASM(String
						.format("push param %d onto stack", i), OpCode.PUSH,
						paramNode.getRegister().name()));
				pushStack.addAll(0, pushIt);

				// FIXME: is this bad to do? (made deallocTempRegister public)
				MemoryManager.deallocTempRegister(temp);
			}
		}
		// Add the push parameters in reverse order
		out.addAll(pushStack);

		// Always set RAX to 0.
		out.add(new OpASM(OpCode.XOR, Reg.RAX.name(), Reg.RAX.name()));
		out.add(new OpASM(OpCode.CALL, name));

		// Remove pointers from stack.
		int stackParams = params.size() - paramRegisters.length;
		if (stackParams > 0) {
			out.add(new OpASM("clean up params", OpCode.ADD, Reg.RSP.name(),
					Integer.toString(stackParams * MemoryManager.ADDRESS_SIZE)));
		}

		// Push RAX into destinationRegister.
		out.add(new OpASM("Saving results of " + name, OpCode.MOV,
				destinationRegister.name(), Reg.RAX.name()));

		// out.add(new OpASM("Leaving!", OpCode.LEAVE));
		return out;
	}

}
