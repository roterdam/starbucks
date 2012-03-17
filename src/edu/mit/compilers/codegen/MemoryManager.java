package edu.mit.compilers.codegen;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidLocalMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

/**
 * Handles traversing through code in order to assign memory and register
 * locations.
 */
public class MemoryManager {

	// How many bytes each address takes. Use for calculating offsets!
	// Note that it's 8, not 4, in 64-bit since 8*8bytes = 64 bits.
	public static final int ADDRESS_SIZE = 8;
	public static final String ADDRESS_SIZE_STRING = Integer
			.toString(ADDRESS_SIZE);
	private static int localStackSize = 0;

	@SuppressWarnings("serial")
	public static Map<Reg, Boolean> registerAvailabilityMap = new HashMap<Reg, Boolean>() {
		{
			for (Reg r : Reg.values()) {
				put(r, true);
			}
		}
	};

	/**
	 * Visits MidSymbolTable generated by MidVisitor and allocates memory
	 * storage.
	 */
	public static void assignStorage(MidSymbolTable codeRoot) {
		// Label the fields.
		Map<String, MidMemoryNode> localVars = codeRoot.getLocalVars();
		String prefix = "field";
		int count = 0;
		for (String fieldName : localVars.keySet()) {
			MidMemoryNode fieldMemoryNode = localVars.get(fieldName);
			assert fieldMemoryNode instanceof MidFieldDeclNode;
			fieldMemoryNode.setRawLocationReference(prefix + count);
			count++;
		}

		// Label nodes in each method.
		Map<String, MidMethodDeclNode> methods = codeRoot.getMethods();
		for (String methodName : methods.keySet()) {
			MidMethodDeclNode methodDeclNode = methods.get(methodName);
			// Reset the localStackSize.
			localStackSize = 0;
			for (MidNode m : methodDeclNode.getNodeList()) {
				if (m instanceof MidLocalMemoryNode) {
					localStackSize += ADDRESS_SIZE;
					((MidMemoryNode) m).setRawLocationReference(Integer
							.toString(localStackSize));
				} else if (m instanceof MidRegisterNode) {
					((MidRegisterNode) m).setRegister(allocRegister());
				} else if (m instanceof MidSaveNode) {
					if (((MidSaveNode) m).savesRegister()) {
						deallocRegister(((MidSaveNode) m).getRefNode()
								.getRegister());
					}
				}
			}
			methodDeclNode.setLocalStackSize(localStackSize);
		}
	}

	private static Reg allocRegister() {
		for (Reg r : registerAvailabilityMap.keySet()) {
			if (registerAvailabilityMap.get(r)) {
				registerAvailabilityMap.put(r, false);
				return r;
			}
		}
		// TODO: make this not possible.
		throw new RuntimeException("Ran out of registers somehow! WTF.");
	}

	private static void deallocRegister(Reg r) {
		registerAvailabilityMap.put(r, true);
	}

}