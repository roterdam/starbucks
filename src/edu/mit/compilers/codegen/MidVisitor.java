package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidLocalVarDeclNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.MidVarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_INITIALIZENode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.WHILENode;

public class MidVisitor {

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all
		// children.
		return new MidNodeList();
	}

	public static MidNodeList visit(PARAM_DECLNode node,
			MidSymbolTable symbolTable) {
		MidNodeList outputList = new MidNodeList();
		
		String name = node.getIDNode().getText();
		MidParamDeclNode paramNode = new MidParamDeclNode(name);
		outputList.add(paramNode);
		
		symbolTable.addVar(name, paramNode);
		
		return outputList;
	}

	/**
	 * Special method returns a MidMethodDeclNode instead of the usual List.
	 */
	public static MidMethodDeclNode visitMethodDecl(METHOD_DECLNode node,
			MidSymbolTable symbolTable) {

		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable);
		MidNodeList outputList = new MidNodeList();
		for (DecafNode statement : node.getBlockNode().getStatementNodes()) {
			outputList.addAll(statement.convertToMidLevel(newSymbolTable));
		}

		MidMethodDeclNode out = new MidMethodDeclNode(node.getId(), outputList, newSymbolTable);

		return out;
	}
	
	public static MidNodeList visit(FORNode node, MidSymbolTable symbolTable) {
		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.FOR);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ROF);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable, endLabel);
		MidNodeList outputList = new MidNodeList();
		
		// add the initialization and termination condition
		//
//		init;  
//		compute limit.
//		label for_start
//		CMP(variable, limit)
//		jge for_end
//		{statements}
//		increment variable.
//		jmp for_start
//		label for_end
		
		MidNodeList assignList = node.getAssignNode().convertToMidLevel(newSymbolTable);
		MidSaveNode assignNode = assignList.getSaveNode();
		outputList.addAll(assignList);
		MidNodeList limitList = node.getForTerminateNode().getExpressionNode().convertToMidLevel(newSymbolTable);
		MidSaveNode limitNode = limitList.getSaveNode(); 
		outputList.addAll(limitList);
		
		outputList.add(startLabel);
		
		return null;
		
	}
	
	//public static MidLocalVarDeclNode visitLocalVarDecl(FOR_INITIALIZENode node, 
	//		MidSymbolTable symbolTable) {
		
	//}
	
	public static MidNodeList visit(WHILENode node, MidSymbolTable symbolTable) {
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable);
		MidNodeList mn = new MidNodeList();
		
		return null;
		
	}
	

	/**
	 * Special method returns a MidFieldDeclNode instead of the usual List.
	 */
	public static MidFieldDeclNode visitFieldDecl(FIELD_DECLNode node,
			MidSymbolTable symbolTable) {
		String name = node.getIDNode().getText();
		switch (node.getVarType()) {
		case BOOLEAN_ARRAY:
			return new MidFieldArrayDeclNode(name, MidVarType.BOOLEAN,
					node.getArrayLength());
		case INT_ARRAY:
			return new MidFieldArrayDeclNode(name, MidVarType.INT,
					node.getArrayLength());
		case INT:
			return new MidFieldDeclNode(name, MidVarType.INT);
		case BOOLEAN:
			return new MidFieldDeclNode(name, MidVarType.BOOLEAN);
		default:
			assert false : "Unexpected varType";
			return null;
		}
	}

	public static MidSymbolTable createMidLevelIR(CLASSNode node) {
		MidSymbolTable symbolTable = new MidSymbolTable();

		for (FIELD_DECLNode fieldNode : node.getFieldNodes()) {
			MidFieldDeclNode midFieldNode = visitFieldDecl(fieldNode, symbolTable);
			symbolTable.addVar(midFieldNode.getName(), midFieldNode);
		}

		for (METHOD_DECLNode methodNode : node.getMethodNodes()) {
			MidMethodDeclNode midMethodNode = visitMethodDecl(methodNode, symbolTable);
			symbolTable.addMethod(midMethodNode.getName(), midMethodNode);
		}

		return symbolTable;
	}
}
