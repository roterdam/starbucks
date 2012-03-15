package edu.mit.compilers.codegen;

import java.lang.reflect.InvocationTargetException;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidLocalVarDeclNode;
import edu.mit.compilers.codegen.nodes.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;
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
import edu.mit.compilers.grammar.DeclNode;
import edu.mit.compilers.grammar.ModifyAssignNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.VarTypeNode;
import edu.mit.compilers.grammar.expressions.DoubleOperandNode;
import edu.mit.compilers.grammar.expressions.SingleOperandNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_INITIALIZENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.INT_TYPENode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MINUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.TRUENode;
import edu.mit.compilers.grammar.tokens.VAR_DECLNode;
import edu.mit.compilers.grammar.tokens.WHILENode;

public class MidVisitor {

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all
		// children.
		assert false : "Implement convertToMidLevel in "+node.getClass();
		return new MidNodeList();
	}

	public static MidNodeList visit(PARAM_DECLNode node,
			MidSymbolTable symbolTable) {
		// TODO: NEED TO ADD NODE FROM PASSED IN REGISTERS
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
			
			MidLoadNode leftLoadNode = new MidLoadNode(preLists[0].getSaveNode().getDestinationNode());
			MidLoadNode rightLoadNode = new MidLoadNode(preLists[1].getSaveNode().getDestinationNode());
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
			MidLoadNode loadNode = new MidLoadNode(nodeList.getSaveNode().getDestinationNode());
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
		MidLoadNode loadNode = new MidLoadNode(rightOperandList.getSaveNode().getDestinationNode());
		rightOperandList.add(loadNode);
		
		// Save from register to memory
		MidSaveNode saveNode = new MidSaveNode(loadNode, symbolTable.getVar(node.getLocation().getText()));
		rightOperandList.add(saveNode);
		
		return rightOperandList;
	}
	
	public static MidNodeList visit(PLUS_ASSIGNNode node, MidSymbolTable symbolTable) {
		return modifyAssignHelper(node, symbolTable, MidPlusNode.class);	
	}

	public static MidNodeList visit(MINUS_ASSIGNNode node, MidSymbolTable symbolTable) {
		return modifyAssignHelper(node, symbolTable, MidMinusNode.class);
	}
	
	private static MidNodeList modifyAssignHelper(ModifyAssignNode node, MidSymbolTable symbolTable,
			Class<? extends MidBinaryRegNode> nodeClass) {
		MidNodeList newOperandList = new MidNodeList();
		try {
			MidNodeList rightOperandList = node.getExpression().convertToMidLevel(symbolTable);
			MidNodeList leftOperandList = node.getLocation().convertToMidLevel(symbolTable);
			
			assert !rightOperandList.isEmpty();		
			assert !leftOperandList.isEmpty();
			
			// Load from memory into register and add to left hand side
			MidLoadNode loadRightNode = new MidLoadNode(rightOperandList.getSaveNode().getDestinationNode());
			MidLoadNode loadLeftNode = new MidLoadNode(leftOperandList.getSaveNode().getDestinationNode());
			MidBinaryRegNode binaryRegNode = nodeClass.getConstructor(MidLoadNode.class, MidLoadNode.class).newInstance(loadLeftNode, loadRightNode);
			MidSaveNode saveRegNode = new MidSaveNode(binaryRegNode, leftOperandList.getSaveNode().getDestinationNode());
			
			// Save from register to memory
			newOperandList.addAll(rightOperandList);
			newOperandList.add(loadLeftNode);
			newOperandList.add(loadRightNode);
			newOperandList.add(binaryRegNode);
			newOperandList.add(saveRegNode);
			
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
		return newOperandList;		
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
		MidLoadNode loadNode = new MidLoadNode(symbolTable.getVar(node.getText()));
		MidTempDeclNode tempNode = new MidTempDeclNode();
		out.add(loadNode);
		out.add(tempNode);
		out.add(new MidSaveNode(loadNode, tempNode));
		return out;
	}
	
	/**
	 * Special method returns a MidMethodDeclNode instead of the usual List.
	 */
	public static MidMethodDeclNode visitMethodDecl(METHOD_DECLNode node,
			MidSymbolTable symbolTable) {
		
		MidNodeList outputList = node.getBlockNode().convertToMidLevel(symbolTable);
		MidMethodDeclNode out = new MidMethodDeclNode(node.getId(), outputList,
				symbolTable);

		return out;
	}
	
	public static MidNodeList visit(BLOCKNode node, MidSymbolTable symbolTable) {

		MidNodeList outputList = new MidNodeList();
		
		// New symbol table for the new method scope.
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable);
		for (DecafNode statement : node.getStatementNodes()) {
			outputList.addAll(statement.convertToMidLevel(newSymbolTable));
		}
		
		return outputList;
		
	}
	/**
	 * Last node must be a MidSaveNode.
	 */
	public static MidNodeList visit(FOR_INITIALIZENode node, MidSymbolTable symbolTable){
		// Treat 'a=3' as 'int a; a=3;'
		MidNodeList nodeList = new MidNodeList();
		
		// Construct a var decl for 'int a;'
		VAR_DECLNode declNode = new VAR_DECLNode();
		VarTypeNode typeNode = new INT_TYPENode();
		IDNode idNode = new IDNode();
		idNode.setText(node.getAssignNode().getLocation().getText());
		typeNode.setNextSibling(idNode);
		declNode.setFirstChild(typeNode);
		
		nodeList.addAll(declNode.convertToMidLevel(symbolTable)); // 'int a;'
		
		MidNodeList assignList = node.getAssignNode().convertToMidLevel(symbolTable);
		assignList.getSaveNode();
		nodeList.addAll(assignList); // 'a=3;'
		nodeList.getSaveNode();
		return nodeList;
		
	}

	public static MidNodeList visit(FORNode node, MidSymbolTable symbolTable) {
		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.FOR);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ROF);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable, endLabel);
		MidNodeList outputList = new MidNodeList();
		
		MidNodeList assignList = node.getForInitializeNode().convertToMidLevel(newSymbolTable);
		MidSaveNode iterVarNode = (MidSaveNode)assignList.getSaveNode();


		MidNodeList limitList = node.getForTerminateNode().getExpressionNode().convertToMidLevel(newSymbolTable);
		MidSaveNode limitNode = (MidSaveNode)limitList.getSaveNode(); 
		

		MidLoadNode iterVarLoadNode = new MidLoadNode(iterVarNode.getDestinationNode());
		MidLoadNode limitLoadNode = new MidLoadNode(limitNode.getDestinationNode());
		MidCompareNode compareNode = new MidCompareNode(iterVarLoadNode, limitLoadNode);
		MidJumpGENode jumpEndNode = new MidJumpGENode(endLabel);
		MidJumpNode jumpStartNode = new MidJumpNode(startLabel);
		
		MidNodeList statementList = node.getBlockNode().convertToMidLevel(newSymbolTable);
		
		
		INT_LITERALNode intLiteralNode = new INT_LITERALNode();
		intLiteralNode.setText("1");
		intLiteralNode.initializeValue();
		IDNode idNode = new IDNode();
		idNode.setText(node.getAssignNode().getLocation().getText());
		PLUS_ASSIGNNode incrementNode = new PLUS_ASSIGNNode();
		idNode.setNextSibling(intLiteralNode);
		incrementNode.setFirstChild(idNode);
		MidNodeList incrementList = incrementNode.convertToMidLevel(newSymbolTable);
		
		outputList.addAll(assignList);
		outputList.addAll(limitList);
		outputList.add(startLabel);
		outputList.add(iterVarLoadNode);
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
	
	public static MidNodeList visit(WHILENode node, MidSymbolTable symbolTable) {/*		
		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.WHILE);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ELIHW);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable, endLabel);
		MidNodeList outputList = new MidNodeList();
		
		MidNodeList conditionList = node.getWhileTerminateNode().getExpressionNode().convertToMidLevel(newSymbolTable);
		MidSaveNode conditionNode = (MidSaveNode) conditionList.getMemoryNode(); 
			
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
		
		return outputList;				*/
		return null;
	}
	

	public static MidNodeList visit(VAR_DECLNode node,
			MidSymbolTable symbolTable) {
		MidNodeList nodeList = new MidNodeList();
		String name = node.getIDNode().getText();
		switch (node.getVarType()) {
		case INT: case BOOLEAN :
			MidLocalVarDeclNode declNode = new MidLocalVarDeclNode(name);
			symbolTable.addVar(name, declNode);
			nodeList.add(declNode);
			break;
		default:
			assert false : "Unexpected varType: "+node.getVarType();
		}
		return nodeList;
	}
	/**
	 * Special method returns a MidLocalVarDeclNode instead of the usual List.
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
