package edu.mit.compilers.opt.low;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;

public class SaveSaver {

	/**
	 * Removes duplicated save efforts, i.e. MOV R11 0 followed by MOV RBX R11,
	 * and replaces them with a cleaner MOV 0 RBX.
	 * 
	 * @param asmList
	 * @return
	 */
	public static List<ASM> pruneList(List<ASM> asmList) {
		List<ASM> out = new ArrayList<ASM>();
		for (int i = 0; i < asmList.size(); i++) {
			ASM item = asmList.get(i);
			if (!(item instanceof OpASM)) {
				out.add(item);
				continue;
			}
			OpASM opItem = (OpASM) item;
			if (opItem.getOpCode() != OpCode.MOV) {
				out.add(item);
				continue;
			}
			String[] args = opItem.getArgs();
			assert args.length == 2;
			String dest = args[0];
			String from = args[1];
			if (isRegister(dest) && isTempReg(dest) && i < asmList.size() - 1) {
				ASM nextItem = asmList.get(i + 1);
				if (nextItem instanceof OpASM
						&& ((OpASM) nextItem).getOpCode() == OpCode.MOV) {
					OpASM nextOpItem = (OpASM) nextItem;
					String[] nextArgs = nextOpItem.getArgs();
					assert args.length == 2;
					String nextDest = nextArgs[0];
					String nextFrom = nextArgs[1];
					if (nextFrom.equals(dest)
							&& (isRegister(from) || isRegister(nextDest))) {
						out.add(new OpASM(OpCode.MOV, nextDest, from));
						i++;
						continue;
					}
				}
			}
			out.add(item);
		}
		return out;
	}

	private static boolean isTempReg(String memString) {
		for (Reg r : RegisterAllocator.TEMP_REGISTERS) {
			if (memString.equals(r.name())) {
				return true;
			}
		}
		return false;
	}

	private static boolean isRegister(String memString) {
		for (Reg r : RegisterAllocator.USABLE_REGISTERS) {
			if (memString.equals(r.name())) {
				return true;
			}
		}
		return isTempReg(memString);
	}

}
