package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LocAndASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.nodes.MidMemoryNode;

public class MemoryManager {


	
	private static Map<String, String> variables = new HashMap<String, String>();
	//int stores 0 if free, 1 if used. I'm thinking of doing ref counting, so that's why it's not a bool?
	private static Map<String, Integer> registers = new HashMap<String, Integer>() {
		private static final long serialVersionUID = -6299866066217043832L;
		{put("r10", 0); put("r11", 0);}
		//{put("r10", 0); put("r11", 0);put("r12", 0);put("r13", 0);put("r14", 0);put("r15", 0);put("r16", 0);}
		};
	
	
	public MemoryManager(){
	}
	
	public static void LoadArgs(){
		
	}
	
	public static List<ASM> LoadDecl(MidMemoryNode node){

		String location = Integer.toString((variables.size()+1) * AsmVisitor.ADDRESS_SIZE);
		location = "rbp - " + location;
		variables.put(node.getName(), location );
		
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM("Loading a declaration: " + node.getName(), OpASM.OpCode.MOV, "rsp", "[ rsp - 8 ]"));
		
		return out;
	}
	
	public static List<ASM> LoadDecls(MidMemoryNode... nodes){
		List<ASM> out = new ArrayList<ASM>();
		for (MidMemoryNode node : nodes){
			out.addAll(LoadDecl(node));
		}
		return out;
	}
	
	public static LocAndASM getRegister(String var){
		for (String reg : registers.keySet()){
			if (registers.get(reg) == 0) {
				
				//got a register
				registers.put(reg, 1);
				//TODO: load the var into the register
				String location = variables.get(var);
				
				List<ASM> asm = new ArrayList<ASM>();
				asm.add(new OpASM("Geting " + var + " Prob called by MidLoadNode", OpASM.OpCode.MOV, reg, location));
				
				return new LocAndASM(reg, asm);
			}
		}
		assert false : "Uh oh. Ran out of registers!";
		return null;
	}
	
	public static void returnRegister(String reg){
		registers.put(reg, 0);
	}
	
	public static String getVarLocation(String var){
		return variables.get(var);
	}
	
	
}
