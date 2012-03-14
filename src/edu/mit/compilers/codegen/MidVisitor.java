package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidJumpNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.MidVarType;
import edu.mit.compilers.codegen.nodes.regops.MidCompareNode;
import edu.mit.compilers.codegen.nodes.regops.MidDivideNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.expressions.DoubleOperandNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.WHILENode;

public class MidVisitor {

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all
		// children.
		return new MidNodeList();
	}

	public static MidNodeList visit(PARAM_DECLNode node,
			MidSymbolTable symbolTable) {
		// TODO: THIS IS WRONG. SHOULD BE A SAVE?
		MidNodeList outputList = new MidNodeList();

		String name = node.getIDNode().getText();
		MidParamDeclNode paramNode = new MidParamDeclNode(name);
		outputList.add(paramNode);

		symbolTable.addVar(name, paramNode);

		return outputList;
	}

	public static MidNodeList[] partialVisit(DoubleOperandNode node,
			MidSymbolTable symbolTable) {
		return new MidNodeList[] {
				node.getLeftOperand().convertToMidLevel(symbolTable),
				node.getRightOperand().convertToMidLevel(symbolTable) };
	}

	public static MidNodeList visit(DIVIDENode node, MidSymbolTable symbolTable) {
		MidNodeList[] preLists = partialVisit(node, symbolTable);
		assert preLists.length == 2;
		
		MidLoadNode leftLoadNode = new MidLoadNode(preLists[0].getSaveNode());
		MidLoadNode rightLoadNode = new MidLoadNode(preLists[1].getSaveNode());
		MidDivideNode divideNode = new MidDivideNode(leftLoadNode, rightLoadNode);
		
		MidNodeList out = preLists[0];
		out.addAll(preLists[1]);
		out.add(leftLoadNode);
		out.add(rightLoadNode);
		out.add(divideNode);
		out.add(new MidSaveNode(divideNode));
		return out;
	}
	
	public static MidNodeList visit(ASSIGNNode node, MidSymbolTable symbolTable) {
		MidNodeList rightOperandList = node.getExpression().convertToMidLevel(symbolTable);
		
		// Load from memory into register
		MidLoadNode loadNode = new MidLoadNode(rightOperandList.getSaveNode());
		rightOperandList.add(loadNode);
		
		// Save from register to memory
		MidSaveNode saveNode = new MidSaveNode(loadNode);
		rightOperandList.add(saveNode);
		
		return rightOperandList;
	}
	
	public static MidNodeList visit(INT_LITERALNode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		out.add(new MidSaveNode(node.getValue()));
		return out;
	}
	
	/**
	 * Special method returns a MidMethodDeclNode instead of the usual List.
	 */
	public static MidMethodDeclNode visitMethodDecl(METHOD_DECLNode node,
			MidSymbolTable symbolTable) {

		// New symbol table for the new method scope.
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable);
		MidNodeList outputList = new MidNodeList();
		for (DecafNode statement : node.getBlockNode().getStatementNodes()) {
			outputList.addAll(statement.convertToMidLevel(newSymbolTable));
		}

		MidMethodDeclNode out = new MidMethodDeclNode(node.getId(), outputList,
				newSymbolTable);

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
		
		MidNodeList limitList = node.getForTerminateNode().getExpressionNode().convertToMidLevel(newSymbolTable);
		MidSaveNode limitNode = limitList.getSaveNode(); 
		
		MidLoadNode assignLoadNode = new MidLoadNode(assignNode);
		MidLoadNode limitLoadNode = new MidLoadNode(limitNode);
		MidCompareNode compareNode = new MidCompareNode(assignLoadNode, limitLoadNode);
		MidJumpNode jumpEndNode = new MidJumpNode(endLabel);
		MidJumpNode jumpStartNode = new MidJumpNode(startLabel);
		
		MidNodeList statementList = node.getBlockNode().convertToMidLevel(newSymbolTable);
		
		INT_LITERALNode intLiteralNode = new INT_LITERALNode();
		intLiteralNode.setText("1");
		IDNode idNode = new IDNode();
		idNode.setText(node.getAssignNode().getLocation().getText());
		PLUS_ASSIGNNode incrementNode = new PLUS_ASSIGNNode();
		idNode.setNextSibling(intLiteralNode);
		incrementNode.setFirstChild(idNode);
		MidNodeList incrementList = incrementNode.convertToMidLevel(newSymbolTable);
		
		outputList.addAll(assignList);
		outputList.addAll(limitList);
		outputList.add(startLabel);
		outputList.add(assignLoadNode);
		outputList.add(limitLoadNode);
		outputList.add(compareNode);
		outputList.add(jumpEndNode);
		outputList.addAll(statementList);
		outputList.addAll(incrementList);
		outputList.add(jumpStartNode);
		
		return outputList;
		
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
