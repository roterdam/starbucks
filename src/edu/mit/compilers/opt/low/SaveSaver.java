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
			// If we're moving into a temp register...
			if (isRegister(dest) && isTempReg(dest) && i < asmList.size() - 1) {
				ASM nextItem = asmList.get(i + 1);
				if (nextItem instanceof OpASM) {
					OpASM nextOpItem = (OpASM) nextItem;
					OpCode nextOpCode = nextOpItem.getOpCode();
					String[] nextArgs = nextOpItem.getArgs();
					// Check if the next one is another move of the same
					// register. We can save a move this way.
					if (nextOpCode == OpCode.MOV) {
						assert args.length == 2;
						String nextDest = nextArgs[0];
						String nextFrom = nextArgs[1];
						if (nextFrom.equals(dest)
								&& (isRegister(from) || isRegister(nextDest))) {
							out.add(new OpASM("SS53", OpCode.MOV, nextDest, from));
							i++;
							continue;
						}
						// In the case of something like
						// MOV R10, R1
						// MOV R11, R2
						// ADD R10, R11
						// MOV R1, R10
						// We can just do ADD R1, R2
						if (isTempReg(nextDest) && i < asmList.size() - 2) {
							ASM nextNextItem = asmList.get(i + 2);
							if (nextNextItem instanceof OpASM) {
								OpASM nextNextOpItem = (OpASM) nextNextItem;
								OpCode nextNextOpCode = nextNextOpItem
										.getOpCode();
								if (nextNextOpCode == OpCode.ADD
										|| nextNextOpCode == OpCode.IMUL
										|| nextNextOpCode == OpCode.SUB
										|| nextNextOpCode == OpCode.CMP) {
									// We know we have:
									// MOV R10, R1
									// MOV R11, R2
									// ADD R10, R11
									String[] nextNextArgs = nextNextOpItem
											.getArgs();
									String nextNextDest = nextNextArgs[0];
									String nextNextFrom = nextNextArgs[1];
									assert dest.equals(nextNextDest)
											&& nextDest.equals(nextNextFrom);
									if (nextNextOpCode == OpCode.CMP) {
										String finalFrom = from;
										if (!isRegister(finalFrom)) {
											out.add(item);
											finalFrom = dest;
										}
										out.add(new OpASM("AS84", OpCode.CMP, finalFrom, nextFrom));
										i += 2;
										continue;
									}
									// Check if the 4th instruction can be
									// removed, i.e.
									// MOV R1, 10
									if (i < asmList.size() - 3) {
										ASM nextNextNextItem = asmList
												.get(i + 3);
										assert nextNextNextItem instanceof OpASM
												&& ((OpASM) nextNextNextItem)
														.getOpCode() == OpCode.MOV;
										OpASM nextNextNextOpItem = (OpASM) nextNextNextItem;
										String[] nextNextNextArgs = nextNextNextOpItem
												.getArgs();
										String nextNextNextDest = nextNextNextArgs[0];
										String nextNextNextFrom = nextNextNextArgs[1];
										assert nextNextNextFrom
												.equals(nextNextDest);
										if (nextNextNextDest.equals(from)) {
											out.add(new OpASM(nextNextOpCode,
													from, nextFrom));
											i += 3;
											continue;
										} else if (nextNextNextDest.equals(nextFrom) && (nextNextOpCode == OpCode.IMUL || nextNextOpCode == OpCode.ADD)) {
											out.add(new OpASM("SS104", nextNextOpCode,
													nextFrom, from));
											i += 3;
											continue;
										} else if (nextNextOpCode == OpCode.ADD) {
											// Otherwise check if we can LEA
											if ((isRegister(from) || isConstant(from))
													&& (isRegister(nextFrom) || isConstant(nextFrom))) {
												// Watch out, if they're both constant then you need to check
												// that they don't sum up to something greater than allowed.
												if (isConstant(from) && isConstant(nextFrom)) {
													int fromInt = Integer.parseInt(from);
													int nextFromInt = Integer.parseInt(nextFrom);
													if ((fromInt+nextFromInt > fromInt && nextFromInt < 0)
															|| (fromInt+nextFromInt < fromInt && nextFromInt > 0)) {
														// Even though this maybe representable as a long,
														// nasm doesn't allow qword literals.
														out.add(item);
														continue;
													}
												}
												if (!isRegister(nextNextNextDest)) {
													out.add(item);
													continue;
												}
												out.add(new OpASM("SS129", OpCode.LEA,
														nextNextNextDest,
														String.format(
																"[ %s + %s ]",
																from, nextFrom)));
												i += 3;
												continue;
											}
										}
									}
									// Otherwise settle for 3, i.e. optimize as
									// MOV R10, R1
									// MUL R10, R2
									out.add(item);
									assert nextNextDest.equals(dest);
									out.add(new OpASM("SS144", nextNextOpCode,
											nextNextDest, nextFrom));
									i += 2;
									continue;
								}
							}
						}
					}
				}
			}
			out.add(item);
		}
		return out;
	}

	private static boolean isConstant(String from) {
		try {
			// Be conservative and only allow 32-bit constants to be inlined.
			Integer.parseInt(from);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
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
