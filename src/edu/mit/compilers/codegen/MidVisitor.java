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
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpEQNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpLENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpLNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNENode;
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
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.ModifyAssignNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.VarTypeNode;
import edu.mit.compilers.grammar.expressions.DoubleOperandNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2BoolNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;
import edu.mit.compilers.grammar.expressions.SingleOperandNode;
import edu.mit.compilers.grammar.tokens.ANDNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BANGNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.BREAKNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.CONTINUENode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.EQNode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_INITIALIZENode;
import edu.mit.compilers.grammar.tokens.GTENode;
import edu.mit.compilers.grammar.tokens.GTNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.INT_TYPENode;
import edu.mit.compilers.grammar.tokens.LTENode;
import edu.mit.compilers.grammar.tokens.LTNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MINUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.NEQNode;
import edu.mit.compilers.grammar.tokens.ORNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.TRUENode;
import edu.mit.compilers.grammar.tokens.VAR_DECLNode;
import edu.mit.compilers.grammar.tokens.WHILENode;
import edu.mit.compilers.grammar.tokens.WHILE_TERMINATENode;

public class MidVisitor {

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
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
		assert false : "NO EXCEPTIONS ALLOWED";
		return null;
	}
	
	public static MidNodeList visit(UnaryMinusNode node, MidSymbolTable symbolTable) {

		return visitUnaryOpHelper(node, symbolTable, MidNegNode.class);
	}
	
//	public static MidNodeList visit(BANGNode node, MidSymbolTable symbolTable){
//		return visitUnaryOpHelper(node, symbolTable, MidNotNode.class);
//	}	
	
	
	public static MidNodeList visit(ASSIGNNode node, MidSymbolTable symbolTable) {
		// TODO: needs to handle boolean expressions.
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
			MidMemoryNode leftOperandNode = symbolTable.getVar(node.getLocation().getText());
			
			// Load from memory into register and add to left hand side
			MidLoadNode loadRightNode = new MidLoadNode(rightOperandList.getSaveNode().getDestinationNode());
			MidLoadNode loadLeftNode = new MidLoadNode(leftOperandNode);
			MidBinaryRegNode binaryRegNode = nodeClass.getConstructor(MidLoadNode.class, MidLoadNode.class).newInstance(loadLeftNode, loadRightNode);
			MidSaveNode saveRegNode = new MidSaveNode(binaryRegNode, leftOperandNode);
			
			// Save from register to memory
			newOperandList.addAll(rightOperandList);
			newOperandList.add(loadLeftNode);
			newOperandList.add(loadRightNode);
			newOperandList.add(binaryRegNode);
			newOperandList.add(saveRegNode);
			return newOperandList;	
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
		assert false : "NO EXCEPTIONS ALLOWED";
		return null;
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
	
	
	//FIXME: SHOULD ONLY CREATE A METHOD TABLE IF ITS ANONYMOUS.
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
	
	public static MidNodeList visit(CONTINUENode node, MidSymbolTable symbolTable) {
		MidNodeList nodeList = new MidNodeList();
		nodeList.add(new MidJumpNode(symbolTable.getContinueLabel()));
		return nodeList;
	}
	
	public static MidNodeList visit(BREAKNode node, MidSymbolTable symbolTable) {
		MidNodeList nodeList = new MidNodeList();
		nodeList.add(new MidJumpNode(symbolTable.getBreakLabel()));
		return nodeList;
	}
	
	public static MidNodeList visit(FORNode node, MidSymbolTable symbolTable) {
		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.FOR);
		MidLabelNode nextLabel = MidLabelManager.getLabel(LabelType.FOR_NEXT);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ROF);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable, nextLabel, endLabel);
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
		outputList.add(nextLabel);
		outputList.addAll(incrementList);
		outputList.add(jumpStartNode);
		outputList.add(endLabel);
		return outputList;
		
	}
	
	//public static MidLocalVarDeclNode visitLocalVarDecl(FOR_INITIALIZENode node, 
	//		MidSymbolTable symbolTable) {
		
	//}
	
	public static MidNodeList visit(WHILENode node, MidSymbolTable symbolTable) {
		MidNodeList outputList = new MidNodeList();
		
		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.WHILE);
		MidLabelNode bodyLabel = MidLabelManager.getLabel(LabelType.WHILE_BODY);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ELIHW);
		MidJumpNode loopJump = new MidJumpNode(startLabel);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable, startLabel, endLabel);
		
		ExpressionNode logicNode = node.getWhileTerminateNode().getExpressionNode();
		MidNodeList branchList = logicNode.shortCircuit(newSymbolTable, bodyLabel, endLabel);
		
		MidNodeList bodyList = node.getBlockNode().convertToMidLevel(newSymbolTable);
		
		outputList.add(startLabel);
		outputList.addAll(branchList);
		outputList.add(bodyLabel);
		outputList.addAll(bodyList);
		outputList.add(loopJump);
		outputList.add(endLabel);
		return outputList;
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
	
	public static MidNodeList visit(IFNode node, MidSymbolTable symbolTable){
		MidNodeList nodeList = new MidNodeList();
		
		MidLabelNode ifLabel = MidLabelManager.getLabel(LabelType.IF);
		MidLabelNode elseLabel = MidLabelManager.getLabel(LabelType.ELSE);
		MidLabelNode fiLabel = MidLabelManager.getLabel(LabelType.FI);
		
		MidJumpNode skipElseJumpNode = new MidJumpNode(fiLabel);
		
		MidNodeList branchList = node.getIfClauseNode().getExpressionNode().shortCircuit(symbolTable, ifLabel, elseLabel);
		MidNodeList ifList = node.getBlockNode().convertToMidLevel(symbolTable);
		MidNodeList elseList = new MidNodeList();
		if(node.getElseBlock() != null)
			elseList = node.getElseBlock().convertToMidLevel(symbolTable);
		else
			elseList = new MidNodeList();
		
		nodeList.addAll(branchList);
		nodeList.add(ifLabel);
		nodeList.addAll(ifList);
		nodeList.add(skipElseJumpNode);
		nodeList.add(elseLabel);
		nodeList.addAll(elseList);
		nodeList.add(fiLabel);
		return nodeList;
	}
	public static MidNodeList shortCircuit(ExpressionNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		assert false : node.getClass() + " cannot be short circuited.";
		return null;
	}
	public static MidNodeList shortCircuit(ORNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidNodeList nodeList = new MidNodeList();
		MidLabelNode rightLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidNodeList rightShortList = node.getRightOperand().shortCircuit(symbolTable, trueLabel, falseLabel);
		MidNodeList leftShortList = node.getLeftOperand().shortCircuit(symbolTable, trueLabel, rightLabel);
		nodeList.addAll(leftShortList);
		nodeList.add(rightLabel);
		nodeList.addAll(rightShortList);
		return rightShortList;
	}
	public static MidNodeList shortCircuit(ANDNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidNodeList nodeList = new MidNodeList();
		MidLabelNode rightLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidNodeList rightShortList = node.getRightOperand().shortCircuit(symbolTable, trueLabel, falseLabel);
		MidNodeList leftShortList = node.getLeftOperand().shortCircuit(symbolTable, rightLabel, falseLabel);
		nodeList.addAll(leftShortList);
		nodeList.add(rightLabel);
		nodeList.addAll(rightShortList);
		return rightShortList;
	}
	public static MidNodeList shortCircuit(BANGNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidNodeList nodeList = node.shortCircuit(symbolTable, falseLabel, trueLabel);
		return nodeList;
	}
	public static MidNodeList shortCircuit(LTENode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpLENode.class);
	}
	public static MidNodeList shortCircuit(LTNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpLNode.class);
	}
	public static MidNodeList shortCircuit(GTNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpGNode.class);
	}
	public static MidNodeList shortCircuit(GTENode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpGENode.class);
	}
	public static MidNodeList shortCircuit(EQNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitEqHelper(node, symbolTable, trueLabel, falseLabel, MidJumpEQNode.class);
	}
	public static MidNodeList shortCircuit(NEQNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return shortCircuitEqHelper(node, symbolTable, trueLabel, falseLabel, MidJumpNENode.class);
	}
	public static MidNodeList shortCircuitEqHelper(OpSameSame2BoolNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel,
			Class<? extends MidJumpNode> c){
		MidNodeList nodeList = new MidNodeList();
		
		MidNodeList leftInstr;
		MidNodeList rightInstr;
		MidLoadNode leftLoad;
		MidLoadNode rightLoad;
		
		if(node.getLeftOperand().getMidVarType() == VarType.INT){
			leftInstr = node.getLeftOperand().convertToMidLevel(symbolTable);
			leftLoad = new MidLoadNode(leftInstr.getSaveNode().getDestinationNode());
			rightInstr = node.getRightOperand().convertToMidLevel(symbolTable);
			rightLoad = new MidLoadNode(rightInstr.getSaveNode().getDestinationNode());
		}else {
			ValuedMidNodeList valuedLeftMidLevelNode = valuedHelper(node.getLeftOperand(), symbolTable);
			ValuedMidNodeList valuedRightMidLevelNode = valuedHelper(node.getRightOperand(), symbolTable);
			leftInstr = valuedLeftMidLevelNode.getList();
			leftLoad = new MidLoadNode(valuedLeftMidLevelNode.getReturnNode());
			rightInstr = valuedRightMidLevelNode.getList();
			rightLoad = new MidLoadNode(valuedRightMidLevelNode.getReturnNode());
		}
		MidCompareNode compareNode = new MidCompareNode(leftLoad, rightLoad);
		try {
			MidJumpNode jumpTrue = c.getConstructor(MidLabelNode.class).newInstance(trueLabel);
			MidJumpNode jumpFalse = new MidJumpNode(falseLabel);
			nodeList.addAll(leftInstr);
			nodeList.addAll(rightInstr);
			nodeList.add(leftLoad);
			nodeList.add(rightLoad);
			nodeList.add(compareNode);
			nodeList.add(jumpTrue);
			nodeList.add(jumpFalse);
			return nodeList;
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
		assert false : "NO EXCEPTIONS ALLOWED";
		return null;
	}
	public static MidNodeList shortCircuitIntInt2BoolHelper(OpIntInt2BoolNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel,
			Class<? extends MidJumpNode> c){
		MidNodeList nodeList = new MidNodeList();
		
		MidNodeList leftListNode = node.getLeftOperand().convertToMidLevel(symbolTable);
		MidNodeList rightListNode = node.getRightOperand().convertToMidLevel(symbolTable);
		
		MidMemoryNode leftDeclNode = leftListNode.getSaveNode().getDestinationNode();
		MidMemoryNode rightDeclNode = rightListNode.getSaveNode().getDestinationNode();
		
		MidLoadNode leftLoadNode = new MidLoadNode(leftDeclNode);
		MidLoadNode rightLoadNode = new MidLoadNode(rightDeclNode);
		MidCompareNode compareNode = new MidCompareNode(leftLoadNode, rightLoadNode);
		try {
			MidJumpNode jumpTrue  = c.getConstructor(MidLabelNode.class).newInstance(trueLabel);
			MidJumpNode jumpFalse = new MidJumpNode(falseLabel);			
			nodeList.addAll(leftListNode);
			nodeList.addAll(rightListNode);
			nodeList.add(leftLoadNode);
			nodeList.add(rightLoadNode);
			nodeList.add(compareNode);
			nodeList.add(jumpTrue);
			nodeList.add(jumpFalse);
			return nodeList;
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
		assert false : "NO EXCEPTIONS ALLOWED";
		return null;
	}
	public static MidNodeList shortCircuit(TRUENode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidNodeList nodeList = new MidNodeList();
		MidJumpNode jumpNode = new MidJumpNode(trueLabel);
		nodeList.add(jumpNode);
		return nodeList;
	}
	public static MidNodeList shortCircuit(FALSENode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidNodeList nodeList = new MidNodeList();
		MidJumpNode jumpNode = new MidJumpNode(falseLabel);
		nodeList.add(jumpNode);
		return nodeList;
	}
	public static MidNodeList shortCircuit(METHOD_CALLNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		assert false : "This needs to be implemented";
		return null;
	}
	
	public static MidNodeList shortCircuit(IDNode node, MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		MidMemoryNode declNode = symbolTable.getVar(node.getText());
		MidLoadNode loadNode = new MidLoadNode(declNode);
		MidMemoryNode tempNode = new MidTempDeclNode();
		MidSaveNode zeroNode = new MidSaveNode(false, tempNode);
		MidLoadNode zeroLoadNode = new MidLoadNode(tempNode);
		MidCompareNode compareNode = new MidCompareNode(loadNode, zeroLoadNode);
		MidJumpEQNode jumpFalseNode = new MidJumpEQNode(falseLabel);
		MidJumpNode jumpTrueNode = new MidJumpNode(trueLabel);
		
		MidNodeList nodeList = new MidNodeList();
		
		nodeList.add(declNode);
		nodeList.add(loadNode);
		nodeList.add(tempNode);
		nodeList.add(zeroNode);
		nodeList.add(zeroLoadNode);
		nodeList.add(compareNode);
		nodeList.add(jumpFalseNode);
		nodeList.add(jumpTrueNode);
		return nodeList;
	}
	// Returns true or false
	private static ValuedMidNodeList valuedHelper(ExpressionNode expressionNode, MidSymbolTable symbolTable){
		MidLabelNode trueLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidLabelNode falseLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidJumpNode jumpEndNode = new MidJumpNode(endLabel);
		MidTempDeclNode declNode = new MidTempDeclNode();
		MidSaveNode saveTrueNode = new MidSaveNode(true, declNode);
		MidSaveNode saveFalseNode = new MidSaveNode(false, declNode);
		
		MidNodeList nodeList = new MidNodeList();
		MidNodeList branchList = expressionNode.shortCircuit(symbolTable, trueLabel, falseLabel);
		nodeList.add(declNode);
		nodeList.addAll(branchList);
		nodeList.add(trueLabel);
		nodeList.add(saveTrueNode);
		nodeList.add(jumpEndNode);
		nodeList.add(falseLabel);
		nodeList.add(saveFalseNode);
		nodeList.add(jumpEndNode);
		return new ValuedMidNodeList(nodeList, declNode);
	}
	
}
