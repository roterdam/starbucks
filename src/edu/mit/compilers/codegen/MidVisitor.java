package edu.mit.compilers.codegen;

import java.lang.reflect.InvocationTargetException;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidJumpNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidBinaryRegNode;
import edu.mit.compilers.codegen.nodes.regops.MidCompareNode;
import edu.mit.compilers.codegen.nodes.regops.MidDivideNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidMinusNode;
import edu.mit.compilers.codegen.nodes.regops.MidModNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidPlusNode;
import edu.mit.compilers.codegen.nodes.regops.MidTimesNode;
import edu.mit.compilers.codegen.nodes.regops.MidUnaryRegNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.expressions.DoubleOperandNode;
import edu.mit.compilers.grammar.expressions.SingleOperandNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.TRUENode;
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

	public static MidNodeList visitBinaryOpHelper(DoubleOperandNode node, MidSymbolTable symbolTable, Class<? extends MidBinaryRegNode> c ){

		try {
			MidNodeList[] preLists = partialVisit(node, symbolTable);
			assert preLists.length == 2;
			
			MidLoadNode leftLoadNode = new MidLoadNode(preLists[0].getMemoryNode());
			MidLoadNode rightLoadNode = new MidLoadNode(preLists[1].getMemoryNode());
			MidBinaryRegNode binNode;
			binNode = c.getConstructor(MidLoadNode.class, MidLoadNode.class).newInstance(leftLoadNode, rightLoadNode);
			MidNodeList out = preLists[0];
			out.addAll(preLists[1]);
			out.add(leftLoadNode);
			out.add(rightLoadNode);
			out.add(binNode);
			MidTempDeclNode dest = new MidTempDeclNode();
			out.add(dest);
			out.add(new MidSaveNode(binNode, dest));

			return out;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidNodeList visit(DIVIDENode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidDivideNode.class);
	}
	
	public static MidNodeList visit(TIMESNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidTimesNode.class);
	}
	
	public static MidNodeList visit(SubtractNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidMinusNode.class);
	}
	
	public static MidNodeList visit(PLUSNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidPlusNode.class);
	}
	
	public static MidNodeList visit(MODNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidModNode.class);
	}
	
	
	public static MidNodeList visitUnaryOpHelper(SingleOperandNode node, MidSymbolTable symbolTable, Class<? extends MidUnaryRegNode> c ){

		try {
			MidNodeList nodeList = node.getOperand().convertToMidLevel(symbolTable);
			assert nodeList.size >= 1;
			MidLoadNode loadNode = new MidLoadNode(nodeList.getMemoryNode());
			MidUnaryRegNode unaryNode;
			unaryNode = c.getConstructor(MidLoadNode.class).newInstance(loadNode);

			MidNodeList out = new MidNodeList();
			out.addAll(nodeList);
			out.add(loadNode);
			out.add(unaryNode);
			MidTempDeclNode dest = new MidTempDeclNode();
			out.add(dest);
			out.add(new MidSaveNode(unaryNode, dest));
			return out;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static MidNodeList visit(UnaryMinusNode node, MidSymbolTable symbolTable) {

		return visitUnaryOpHelper(node, symbolTable, MidNegNode.class);
	}
	
//	public static MidNodeList visit(BANGNode node, MidSymbolTable symbolTable){
//		return visitUnaryOpHelper(node, symbolTable, MidNotNode.class);
//	}	
	
	
	public static MidNodeList visit(ASSIGNNode node, MidSymbolTable symbolTable) {
		MidNodeList rightOperandList = node.getExpression().convertToMidLevel(symbolTable);
		assert rightOperandList.size >= 1;
		
		// Load from memory into register
		MidLoadNode loadNode = new MidLoadNode(rightOperandList.getMemoryNode());
		rightOperandList.add(loadNode);
		
		// Save from register to memory
		MidSaveNode saveNode = new MidSaveNode(loadNode, symbolTable.getVar(node.getLocation().getText()));
		rightOperandList.add(saveNode);
		
		return rightOperandList;
	}
	
	public static MidNodeList visit(INT_LITERALNode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		MidTempDeclNode dest = new MidTempDeclNode();
		out.add(dest);
		out.add(new MidSaveNode(node.getValue(), dest));
		return out;
	}
	
	public static MidNodeList visit(TRUENode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		MidTempDeclNode dest = new MidTempDeclNode();
		out.add(dest);
		out.add(new MidSaveNode(true, dest));
		return out;
	}
	
	public static MidNodeList visit(FALSENode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		MidTempDeclNode dest = new MidTempDeclNode();
		out.add(dest);
		out.add(new MidSaveNode(false, dest));
		return out;
	}
	
	public static MidNodeList visit(IDNode node, MidSymbolTable symbolTable){
		MidNodeList out = new MidNodeList();
		out.add(symbolTable.getVar(node.getText()));
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
		MidSaveNode assignNode = (MidSaveNode)assignList.getMemoryNode();
		
		MidNodeList limitList = node.getForTerminateNode().getExpressionNode().convertToMidLevel(newSymbolTable);
		MidSaveNode limitNode = (MidSaveNode)limitList.getMemoryNode(); 
		
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
		//MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable);
		//MidNodeList mn = new MidNodeList();
		
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
			return new MidFieldArrayDeclNode(name, node.getArrayLength());
		case INT_ARRAY:
			return new MidFieldArrayDeclNode(name, node.getArrayLength());
		case INT:
			return new MidFieldDeclNode(name);
		case BOOLEAN:
			return new MidFieldDeclNode(name);
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
