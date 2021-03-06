package edu.mit.compilers.codegen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.MidExitNode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidMoveSPNode;
import edu.mit.compilers.codegen.nodes.MidPushNode;
import edu.mit.compilers.codegen.nodes.MidReturnNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.MidZeroRegNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpEQNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpLNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidLocalVarDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidBinaryRegNode;
import edu.mit.compilers.codegen.nodes.regops.MidCompareNode;
import edu.mit.compilers.codegen.nodes.regops.MidDivideNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidMinusNode;
import edu.mit.compilers.codegen.nodes.regops.MidModNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidParamLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidPlusNode;
import edu.mit.compilers.codegen.nodes.regops.MidTimesNode;
import edu.mit.compilers.codegen.nodes.regops.MidUnaryRegNode;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.ModifyAssignNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.VarTypeNode;
import edu.mit.compilers.grammar.expressions.CallNode;
import edu.mit.compilers.grammar.expressions.DoubleOperandNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.BREAKNode;
import edu.mit.compilers.grammar.tokens.CALLOUTNode;
import edu.mit.compilers.grammar.tokens.CHAR_LITERALNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.CONTINUENode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_INITIALIZENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.INT_TYPENode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MINUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.RETURNNode;
import edu.mit.compilers.grammar.tokens.STRING_LITERALNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.VAR_DECLNode;
import edu.mit.compilers.grammar.tokens.WHILENode;
import edu.mit.compilers.opt.regalloc.nodes.MidPreserveParamsNode;
import edu.mit.compilers.opt.regalloc.nodes.MidRestoreRegLaterNode;
import edu.mit.compilers.opt.regalloc.nodes.MidSaveRegLaterNode;
import edu.mit.compilers.opt.regalloc.nodes.MidUndoPreserveParamsNode;

public class MidVisitor {

	public static final String OUT_OF_BOUNDS_METHOD_NAME = "outOfBounds";
	public static final String DIVIDE_BY_ZERO_NAME = "divideByZero";
	public static final String OUT_OF_BOUNDS_ERROR = "*** RUNTIME ERROR ***: Array out of Bounds access in method \"%s\"\\n";
	public static final String DIVIDE_BY_ZERO_ERROR = "*** RUNTIME ERROR ***: Divide by zero in method \"%s\"\\n";

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
		assert false : "Implement convertToMidLevel in " + node.getClass();
		return new MidNodeList();
	}

	private static String stripQuotes(String text) {
		return text.substring(1, text.length() - 1);
	}

	public static MidNodeList getPreCalls(ExpressionNode expr,
			MidSymbolTable symbolTable) {
		MidNodeList instrList = new MidNodeList();
		for (DecafNode callNode : expr.getCallsBeforeExecution())
			instrList.addAll(callNode.convertToMidLevel(symbolTable));
		return instrList;
	}

	public static MidNodeList getPostCalls(ExpressionNode expr,
			MidSymbolTable symbolTable) {
		MidNodeList instrList = new MidNodeList();
		for (DecafNode callNode : expr.getCallsAfterExecution())
			instrList.addAll(callNode.convertToMidLevel(symbolTable));
		return instrList;
	}

	public static MidNodeList visit(CALLOUTNode node, MidSymbolTable symbolTable) {
		return makeMethodCall(node, symbolTable);
	}

	public static MidNodeList visit(RETURNNode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		MidMemoryNode returnMemoryNode = null;
		if (node.hasReturnExpression()) {
			ValuedMidNodeList returnValuedExpressionList = MidShortCircuitVisitor
					.valuedHelper(node.getReturnExpression(), symbolTable);
			out.addAll(returnValuedExpressionList.getList());
			returnMemoryNode = returnValuedExpressionList.getReturnNode();
		}
		out.add(new MidReturnNode(returnMemoryNode));

		return out;
	}

	public static MidNodeList makeMethodCall(MidCallNode methodNode,
			MidNodeList paramExpr, MidNodeList preCalls, MidNodeList postCalls,
			List<MidMemoryNode> paramMemoryNodes) {
		MidNodeList out = new MidNodeList();
		out.addAll(preCalls);

		MidNodeList paramLoadNodes = new MidNodeList();
		MidNodeList paramPushStack = new MidNodeList();

		List<MidParamLoadNode> preservationList = new ArrayList<MidParamLoadNode>();

		for (int i = 0; i < paramMemoryNodes.size(); i++) {
			MidMemoryNode midMemNode = paramMemoryNodes.get(i);
			MidParamLoadNode paramLoadNode = new MidParamLoadNode(midMemNode);
			if (i < AsmVisitor.paramRegisters.length) {
				// Want to set the register.
				paramLoadNode.setRegister(AsmVisitor.paramRegisters[i]);
				paramLoadNodes.add(paramLoadNode);
			} else {
				// Push the remaining parameters in reverse order
				MidNodeList pushIt = new MidNodeList();
				pushIt.add(paramLoadNode);
				pushIt.add(new MidPushNode(paramLoadNode));
				pushIt.addAll(paramPushStack);
				paramPushStack = pushIt;
			}
			preservationList.add(paramLoadNode);
		}

		out.addAll(postCalls);
		out.addAll(paramExpr);

		// Push caller-saved.
		out.add(new MidSaveRegLaterNode(methodNode));
		MidPreserveParamsNode preserveParamsNode = new MidPreserveParamsNode(
				preservationList);
		out.add(preserveParamsNode);
		out.addAll(paramLoadNodes);
		out.addAll(paramPushStack);

		// zero RAX.
		out.add(new MidZeroRegNode(Reg.RAX));
		out.add(methodNode);

		int stackParams = paramMemoryNodes.size()
				- AsmVisitor.paramRegisters.length;
		if (stackParams > 0) {
			out.add(new MidMoveSPNode(stackParams));
		}

		// Pop preserved params.
		out.add(new MidUndoPreserveParamsNode(preserveParamsNode));
		// Pop caller-saved.
		out.add(new MidRestoreRegLaterNode(methodNode));

		// Save output.
		MidTempDeclNode tempDeclNode = new MidTempDeclNode();
		out.add(tempDeclNode);
		out.add(new MidSaveNode(methodNode, tempDeclNode));

		return out;
	}

	public static MidNodeList makeMethodCall(CallNode node,
			MidSymbolTable symbolTable) {
		MidCallNode methodNode;
		int paramCount = node.getParameters().size();
		if (node instanceof METHOD_CALLNode) {
			methodNode = new MidMethodCallNode(symbolTable.getMethod(node
					.getName()), paramCount);
		} else {
			String methodName = stripQuotes(node.getName());
			methodNode = new MidCalloutNode(methodName, paramCount);
		}
		MidNodeList exprParamList = new MidNodeList();
		List<MidMemoryNode> paramMemoryNodes = new ArrayList<MidMemoryNode>();

		List<DecafNode> paramNodes = node.getParameters();
		for (int i = 0; i < paramNodes.size(); i++) {
			DecafNode paramRoot = paramNodes.get(i);
			if (paramRoot instanceof STRING_LITERALNode) {
				MidFieldDeclNode stringDeclNode = AsmVisitor
						.addStringLiteral(stripQuotes(paramRoot.getText()));
				paramMemoryNodes.add(stringDeclNode);
			} else if (paramRoot instanceof ExpressionNode) {
				ValuedMidNodeList expList = MidShortCircuitVisitor
						.valuedHelper((ExpressionNode) paramRoot, symbolTable);
				exprParamList.addAll(expList.getList());
				paramMemoryNodes.add(expList.getReturnNode());
			}
		}

		return makeMethodCall(methodNode, exprParamList, getPreCalls(node, symbolTable), getPostCalls(node, symbolTable), paramMemoryNodes);
	}

	public static MidNodeList visit(METHOD_CALLNode node,
			MidSymbolTable symbolTable) {
		return makeMethodCall(node, symbolTable);
	}

	public static MidNodeList visitParam(PARAM_DECLNode node,
			MidSymbolTable symbolTable, int paramOffset) {
		MidNodeList outputList = new MidNodeList();

		String name = node.getIDNode().getText();
		MidParamDeclNode paramNode = new MidParamDeclNode(name, paramOffset);
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

	public static MidNodeList visit(PotentialCheckDivideByZeroNode node,
			MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();
		if (node.isActive()) {
			MidNodeList exprList = node.getExpression()
					.convertToMidLevel(symbolTable);
			out.addAll(exprList);
			out.addAll(checkDivideByZeroError(exprList.getMemoryNode(), symbolTable));
		}
		return out;
	}

	public static MidNodeList checkDivideByZeroError(MidMemoryNode operandNode,
			MidSymbolTable symbolTable) {

		MidLoadNode loadOperandNode = new MidLoadNode(operandNode);

		MidTempDeclNode zeroNode = new MidTempDeclNode();
		MidNodeList saveInstrList = MidSaveNode.storeValueInMemory(0, zeroNode);
		MidLoadNode loadNode = new MidLoadNode(zeroNode);

		MidCompareNode compareNode = new MidCompareNode(loadOperandNode,
				loadNode);
		MidLabelNode errorLabelNode = MidLabelManager.getLabel(LabelType.SKIP);
		MidJumpNode jumpNode = new MidJumpEQNode(errorLabelNode);

		MidLabelNode skipErrorEnd = MidLabelManager.getLabel(LabelType.SKIP);
		MidJumpNode skipErrorNode = new MidJumpNode(skipErrorEnd);

		MidMemoryNode paramDeclNode = symbolTable.getCurrentMethodNameNode();
		List<MidMemoryNode> divideByZeroParams = new ArrayList<MidMemoryNode>();
		divideByZeroParams.add(paramDeclNode);
		MidMethodCallNode divideByZeroCall = new MidMethodCallNode(
				symbolTable.getStarbucksMethod(DIVIDE_BY_ZERO_NAME),
				divideByZeroParams.size(), true);

		// Set any old register.
		divideByZeroCall.setRegister(Reg.RAX);

		MidNodeList instrList = new MidNodeList();

		instrList.add(loadOperandNode);
		instrList.add(zeroNode);
		instrList.addAll(saveInstrList);
		instrList.add(loadNode);
		instrList.add(compareNode);
		instrList.add(jumpNode);

		instrList.add(skipErrorNode);
		instrList.add(errorLabelNode);
		instrList
				.addAll(makeMethodCall(divideByZeroCall, new MidNodeList(), new MidNodeList(), new MidNodeList(), divideByZeroParams));
		instrList.add(skipErrorEnd);
		return instrList;
	}

	public static MidNodeList checkArrayIndexOutOfBoundsError(
			MidFieldArrayDeclNode arrayNode, MidMemoryNode indexNode,
			MidSymbolTable symbolTable, boolean checkZeroBound,
			boolean checkLenBound) {
		// Check for zero.
		MidLoadNode loadIndexNode1 = new MidLoadNode(indexNode);
		MidTempDeclNode zeroNode = new MidTempDeclNode();
		MidNodeList zeroSaveInstrList = MidSaveNode
				.storeValueInMemory(0, zeroNode);
		MidLoadNode zeroLoadNode = new MidLoadNode(zeroNode);
		MidCompareNode zeroCompareNode = new MidCompareNode(loadIndexNode1,
				zeroLoadNode);

		MidLabelNode errorLabelNode = MidLabelManager.getLabel(LabelType.SKIP);
		MidJumpNode zeroJumpNode = new MidJumpLNode(errorLabelNode);

		// Check for greater than length.
		MidLoadNode loadIndexNode2 = new MidLoadNode(indexNode);
		MidTempDeclNode lengthNode = new MidTempDeclNode();

		MidNodeList lengthSaveInstrList = MidSaveNode
				.storeValueInMemory(arrayNode.getSize(), lengthNode);
		MidLoadNode lengthLoadNode = new MidLoadNode(lengthNode);
		MidCompareNode lengthCompareNode = new MidCompareNode(loadIndexNode2,
				lengthLoadNode);
		MidJumpNode lengthJumpNode = new MidJumpGENode(errorLabelNode);

		MidLabelNode skipErrorEnd = MidLabelManager.getLabel(LabelType.SKIP);
		MidJumpNode skipErrorNode = new MidJumpNode(skipErrorEnd);

		MidMemoryNode paramDeclNode = symbolTable.getCurrentMethodNameNode();
		List<MidMemoryNode> outOfBoundsParams = new ArrayList<MidMemoryNode>();
		outOfBoundsParams.add(paramDeclNode);
		MidMethodCallNode outOfBoundsCall = new MidMethodCallNode(
				symbolTable.getStarbucksMethod(OUT_OF_BOUNDS_METHOD_NAME),
				outOfBoundsParams.size(), true);
		// Set any old register.
		outOfBoundsCall.setRegister(Reg.RAX);

		MidNodeList instrList = new MidNodeList();

		int count = 0;
		if (checkZeroBound) {
			count++;
			instrList.add(loadIndexNode1);
			instrList.add(zeroNode);
			instrList.addAll(zeroSaveInstrList);
			instrList.add(zeroLoadNode);
			instrList.add(zeroCompareNode);
			instrList.add(zeroJumpNode);
		}
		if (checkLenBound) {
			count++;
			instrList.add(loadIndexNode2);
			instrList.add(lengthNode);
			instrList.addAll(lengthSaveInstrList);
			instrList.add(lengthLoadNode);
			instrList.add(lengthCompareNode);
			instrList.add(lengthJumpNode);
		}
		LogCenter.debug("REBC", "SKIPPED " + (2 - count) + " bound checks!");
		if (checkZeroBound || checkLenBound) {
			instrList.add(skipErrorNode);
			instrList.add(errorLabelNode);
			instrList
					.addAll(makeMethodCall(outOfBoundsCall, new MidNodeList(), new MidNodeList(), new MidNodeList(), outOfBoundsParams));
			instrList.add(skipErrorEnd);
		}
		return instrList;
	}

	// Should be DoubleOperandNodes that do not operate on booleans.
	// Happens to be enforced by overloading for the ones that do
	// operate on booleans.

	public static MidNodeList visitBinaryOpHelper(DoubleOperandNode node,
			MidSymbolTable symbolTable, Class<? extends MidBinaryRegNode> c) {

		try {
			MidNodeList leftList = node.getLeftOperand()
					.convertToMidLevel(symbolTable);
			MidNodeList rightList = node.getRightOperand()
					.convertToMidLevel(symbolTable);

			MidMemoryNode leftNode = leftList.getMemoryNode();
			MidMemoryNode rightNode = rightList.getMemoryNode();

			MidNodeList errorList = new MidNodeList();
			if (c == MidDivideNode.class || c == MidModNode.class) {
				errorList
						.addAll(checkDivideByZeroError(rightNode, symbolTable));
			}

			MidLoadNode leftLoadNode = new MidLoadNode(leftNode);
			MidLoadNode rightLoadNode = new MidLoadNode(rightNode);

			MidBinaryRegNode binNode;
			binNode = c.getConstructor(MidLoadNode.class, MidLoadNode.class)
					.newInstance(leftLoadNode, rightLoadNode);
			MidTempDeclNode dest = new MidTempDeclNode();
			MidSaveNode saveNode = new MidSaveNode(binNode, dest);

			MidNodeList instrList = new MidNodeList();
			instrList.addAll(getPreCalls(node, symbolTable));
			instrList.addAll(rightList);
			instrList.addAll(errorList);
			instrList.addAll(leftList);
			instrList.addAll(getPostCalls(node, symbolTable));
			instrList.add(leftLoadNode);
			instrList.add(rightLoadNode);
			instrList.add(binNode);
			instrList.add(dest);
			instrList.add(saveNode);
			return instrList;
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

	public static MidNodeList visit(SubtractNode node,
			MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidMinusNode.class);
	}

	public static MidNodeList visit(PLUSNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidPlusNode.class);
	}

	public static MidNodeList visit(MODNode node, MidSymbolTable symbolTable) {
		return visitBinaryOpHelper(node, symbolTable, MidModNode.class);
	}

	public static MidNodeList visit(UnaryMinusNode node,
			MidSymbolTable symbolTable) {
		MidNodeList nodeList = node.getOperand().convertToMidLevel(symbolTable);
		assert nodeList.size >= 1;
		MidLoadNode loadNode = new MidLoadNode(nodeList.getMemoryNode());
		MidUnaryRegNode unaryNode;
		unaryNode = new MidNegNode(loadNode);

		MidNodeList out = new MidNodeList();
		out.addAll(getPreCalls(node, symbolTable));
		out.addAll(nodeList);
		out.addAll(getPostCalls(node, symbolTable));

		out.add(loadNode);
		out.add(unaryNode);

		MidTempDeclNode dest = new MidTempDeclNode();
		out.add(dest);
		out.add(new MidSaveNode(unaryNode, dest));
		return out;
	}

	// public static MidNodeList visit(BANGNode node, MidSymbolTable
	// symbolTable){
	// return visitUnaryOpHelper(node, symbolTable, MidNotNode.class);
	// }

	public static MidNodeList visit(ASSIGNNode node, MidSymbolTable symbolTable) {
		ValuedMidNodeList rightEvalList = MidShortCircuitVisitor
				.valuedHelper(node.getExpression(), symbolTable);

		MidLoadNode loadNode = new MidLoadNode(rightEvalList.getReturnNode());

		ValuedMidNodeList leftEvalList = getMemoryLocation(node.getLocation(), symbolTable);
		MidSaveNode saveNode = new MidSaveNode(loadNode,
				leftEvalList.getReturnNode());

		MidNodeList nodeList = new MidNodeList();

		nodeList.addAll(rightEvalList.getList());
		nodeList.addAll(leftEvalList.getList());
		nodeList.add(loadNode);
		nodeList.add(saveNode);
		return nodeList;
	}

	public static MidNodeList visit(PLUS_ASSIGNNode node,
			MidSymbolTable symbolTable) {
		return modifyAssignHelper(node, symbolTable, MidPlusNode.class);
	}

	public static MidNodeList visit(MINUS_ASSIGNNode node,
			MidSymbolTable symbolTable) {
		return modifyAssignHelper(node, symbolTable, MidMinusNode.class);
	}

	private static MidNodeList modifyAssignHelper(ModifyAssignNode node,
			MidSymbolTable symbolTable,
			Class<? extends MidBinaryRegNode> nodeClass) {
		MidNodeList newOperandList = new MidNodeList();
		try {
			MidNodeList rightOperandList = node.getExpression()
					.convertToMidLevel(symbolTable);
			ValuedMidNodeList leftEvalList = getMemoryLocation(node.getLocation(), symbolTable);
			MidMemoryNode leftOperandNode = leftEvalList.getReturnNode();

			// Load from memory into register and add to left hand side
			MidLoadNode loadRightNode = new MidLoadNode(
					rightOperandList.getMemoryNode());

			MidLoadNode loadLeftNode = new MidLoadNode(leftOperandNode);

			MidBinaryRegNode binaryRegNode = nodeClass
					.getConstructor(MidLoadNode.class, MidLoadNode.class)
					.newInstance(loadLeftNode, loadRightNode);

			MidTempDeclNode tempNode = new MidTempDeclNode();
			MidSaveNode tempSaveNode = new MidSaveNode(binaryRegNode, tempNode);

			// Save from register to memory
			newOperandList.addAll(rightOperandList);
			newOperandList.addAll(leftEvalList.getList());
			newOperandList.add(loadLeftNode);
			newOperandList.add(loadRightNode);
			newOperandList.add(binaryRegNode);

			// Save result to temp.
			newOperandList.add(tempNode);
			newOperandList.add(tempSaveNode);

			MidMemoryNode finalSaveLoc;
			if (leftOperandNode instanceof MidArrayElementNode) {
				MidArrayElementNode originalArrayElemNode = (MidArrayElementNode) leftOperandNode;

				MidLoadNode indexLoadNode = originalArrayElemNode.getLoadNode();
				// this is where the index is stored.
				MidMemoryNode indexSaveLoc = indexLoadNode.getMemoryNode();

				MidLoadNode newIndexLoadNode = new MidLoadNode(indexSaveLoc);

				newOperandList.add(newIndexLoadNode);

				finalSaveLoc = new MidArrayElementNode(
						originalArrayElemNode.getArrayDecl(), newIndexLoadNode);
			} else {
				finalSaveLoc = leftOperandNode;
			}

			MidLoadNode bringBackValNode = new MidLoadNode(tempNode);
			MidSaveNode saveRegNode = new MidSaveNode(bringBackValNode,
					finalSaveLoc);
			newOperandList.add(bringBackValNode);
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

	public static MidNodeList visit(INT_LITERALNode node,
			MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();

		out.addAll(getPreCalls(node, symbolTable));
		out.addAll(getPostCalls(node, symbolTable));

		MidTempDeclNode dest = new MidTempDeclNode();
		MidNodeList saveInstrList = MidSaveNode.storeValueInMemory(node
				.getValue(), dest);
		out.add(dest);
		out.addAll(saveInstrList);
		return out;
	}

	public static MidNodeList visit(CHAR_LITERALNode node,
			MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();

		out.addAll(getPreCalls(node, symbolTable));
		out.addAll(getPostCalls(node, symbolTable));

		MidTempDeclNode dest = new MidTempDeclNode();
		MidNodeList saveInstrList = MidSaveNode.storeValueInMemory(node
				.getValue(), dest);
		out.add(dest);
		out.addAll(saveInstrList);
		return out;
	}

	/*
	 * 
	 * public static MidNodeList visit(TRUENode node, MidSymbolTable
	 * symbolTable) { MidNodeList out = new MidNodeList(); MidTempDeclNode dest
	 * = new MidTempDeclNode(); out.add(dest); out.add(new MidSaveNode(true,
	 * dest)); return out; }
	 * 
	 * public static MidNodeList visit(FALSENode node, MidSymbolTable
	 * symbolTable) { MidNodeList out = new MidNodeList(); MidTempDeclNode dest
	 * = new MidTempDeclNode(); out.add(dest); out.add(new MidSaveNode(false,
	 * dest)); return out; }
	 */
	public static MidNodeList visit(IDNode node, MidSymbolTable symbolTable) {
		MidNodeList out = new MidNodeList();

		ValuedMidNodeList memGetList = getMemoryLocation(node, symbolTable);
		MidLoadNode loadNode = new MidLoadNode(memGetList.getReturnNode());
		MidTempDeclNode tempNode = new MidTempDeclNode();

		out.addAll(getPreCalls(node, symbolTable));
		out.addAll(memGetList.getList());
		out.addAll(getPostCalls(node, symbolTable));
		out.add(loadNode);
		out.add(tempNode);
		out.add(new MidSaveNode(loadNode, tempNode));

		return out;
	}

	/**
	 * This method returns the location in memory specified by the IDNode.
	 * However, for array access, this method allocates a register.
	 * 
	 * The register does NOT get deallocated until the return's getReturnNode()
	 * is used. Therefore, after adding the return's getList() to instructions,
	 * you should be using getReturnNode() immediately.
	 * 
	 * This necessity is a result of dynamic memory access using a register as a
	 * pointer.
	 */
	public static ValuedMidNodeList getMemoryLocation(IDNode node,
			MidSymbolTable symbolTable) {
		MidNodeList instrList = new MidNodeList();
		MidMemoryNode locNode;
		if (node.isArray()) {
			assert symbolTable.getVar(node.getText()) instanceof MidFieldArrayDeclNode : node
					.getText() + " is an array, but it's not stored as one.";

			// Get array decl and compute the index
			MidFieldArrayDeclNode arrayNode = (MidFieldArrayDeclNode) symbolTable
					.getVar(node.getText());
			ValuedMidNodeList exprList = MidShortCircuitVisitor
					.valuedHelper(node.getExpressionNode(), symbolTable);
			MidMemoryNode exprNode = exprList.getReturnNode();

			LogCenter.debug("REBC", "Working with array " + node.getText());
			boolean mustCheckZeroBound = true;
			boolean mustCheckLenBound = true;
			// REBC
			LogCenter.debug("REBC", "p1");
			if (node.getExpressionNode() instanceof IDNode) {
				LogCenter.debug("REBC", "p2");
				IDNode idNode = (IDNode) node.getExpressionNode();
				LogCenter.debug("REBC", "Loop variable is " + idNode.getText());
				MidMemoryNode memNode = symbolTable.getVar(idNode.getText());
				LogCenter.debug("REBC", "Mem node is " + memNode.getClass());
				if (memNode instanceof MidLocalVarDeclNode) {
					LogCenter.debug("REBC", "p3");
					MidLocalVarDeclNode iterDeclNode = (MidLocalVarDeclNode) memNode;
					if (iterDeclNode.isInLoop()) {
						LogCenter.debug("REBC", "p4");
						String iterVar = idNode.getText();
						FORNode forNode = iterDeclNode.getForNode();
						if (forNode.getBlockNode().isUnrollable(iterVar, true)) {
							LogCenter.debug("REBC", "p5");
							long arraySize = arrayNode.getLength();
							ExpressionNode initExpr = forNode
									.getForInitializeNode().getAssignNode()
									.getExpression();
							ExpressionNode termExpr = forNode
									.getForTerminateNode().getExpressionNode();
							if (initExpr instanceof INT_LITERALNode) {
								LogCenter.debug("REBC", "p6");
								long val = ((INT_LITERALNode) initExpr)
										.getValue();
								if (val >= 0) {
									LogCenter.debug("REBC", "p7");
									mustCheckZeroBound = false;
									mustCheckLenBound = false;
								}
							}
							if (termExpr instanceof INT_LITERALNode) {
								LogCenter.debug("REBC", "p8");
								long val = ((INT_LITERALNode) termExpr)
										.getValue();
								if (val < arraySize) {
									LogCenter.debug("REBC", "p9");
									mustCheckZeroBound = false;
									mustCheckLenBound = false;
								}
							}
						}
					}
				}
			}

			// Make sure the index is not out of bounds
			MidNodeList errorList = checkArrayIndexOutOfBoundsError(arrayNode, exprNode, symbolTable, mustCheckZeroBound, mustCheckLenBound);

			MidLoadNode indexLoadNode = new MidLoadNode(
					exprList.getReturnNode());
			locNode = new MidArrayElementNode(arrayNode, indexLoadNode);

			instrList.addAll(exprList.getList());
			instrList.addAll(errorList);
			instrList.add(indexLoadNode);
		} else {
			locNode = symbolTable.getVar(node.getText());
		}
		return new ValuedMidNodeList(instrList, locNode);
	}

	public static MidNodeList visit(BLOCKNode node, MidSymbolTable symbolTable,
			boolean needsNewScope, String methodName) {
		MidSymbolTable blockSymbolTable = new MidSymbolTable(symbolTable);
		blockSymbolTable.setCurrentMethodNameNode(AsmVisitor
				.addStringLiteral(methodName));
		// Calls the rest of the code, but doesn't require creating a new scope
		// since we did it here.
		return visit(node, blockSymbolTable, false);
	}

	public static MidNodeList visit(BLOCKNode node, MidSymbolTable symbolTable,
			boolean needsNewScope) {
		MidNodeList outputList = new MidNodeList();
		// New symbol table for the new method scope.
		MidSymbolTable blockSymbolTable = needsNewScope ? new MidSymbolTable(
				symbolTable) : symbolTable;

		int paramOffset = 0;
		for (DecafNode statement : node.getStatementNodes()) {
			// PARAM_DECLNode needs paramOffset so memory access patterns can be
			// determined later when the method the params belong to is called
			// from somewhere else. If a better place for tracking this is
			// found, be my guest.
			if (statement instanceof PARAM_DECLNode) {
				outputList.addAll(((PARAM_DECLNode) statement)
						.convertToMidLevel(blockSymbolTable, paramOffset));
				paramOffset++;
			} else {
				outputList
						.addAll(statement.convertToMidLevel(blockSymbolTable));
			}

		}

		return outputList;

	}

	/**
	 * Last node must be a MidSaveNode.
	 */
	public static MidNodeList visit(FOR_INITIALIZENode node,
			MidSymbolTable symbolTable) {
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

		MidNodeList assignList = node.getAssignNode()
				.convertToMidLevel(symbolTable);
		assignList.getMemoryNode();
		nodeList.addAll(assignList); // 'a=3;'
		nodeList.getMemoryNode();
		return nodeList;

	}

	public static MidNodeList visit(CONTINUENode node,
			MidSymbolTable symbolTable) {
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
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable,
				nextLabel, endLabel);
		MidNodeList outputList = new MidNodeList();

		MidNodeList assignList = node.getForInitializeNode()
				.convertToMidLevel(newSymbolTable);
		MidMemoryNode iterVarNode = assignList.getMemoryNode();

		MidNodeList limitList = node.getForTerminateNode().getExpressionNode()
				.convertToMidLevel(newSymbolTable);
		MidMemoryNode limitNode = limitList.getMemoryNode();

		MidLoadNode iterVarLoadNode = new MidLoadNode(iterVarNode);
		MidLoadNode limitLoadNode = new MidLoadNode(limitNode);
		MidCompareNode compareNode = new MidCompareNode(iterVarLoadNode,
				limitLoadNode);
		MidJumpGENode jumpEndNode = new MidJumpGENode(endLabel);
		MidJumpNode jumpStartNode = new MidJumpNode(startLabel);

		// ADD META DATA FOR FOR LOOP (REBC)
		String iterVar = node.getAssignNode().getLocation().getText();
		LogCenter.debug("REBC", "iter variable for for loop is: " + iterVar);
		MidLocalVarDeclNode iterDeclNode = (MidLocalVarDeclNode) newSymbolTable
				.getVar(iterVar);
		iterDeclNode.setForNode(node);

		MidNodeList statementList = node.getBlockNode()
				.convertToMidLevelSpecial(newSymbolTable);

		INT_LITERALNode intLiteralNode = new INT_LITERALNode();
		intLiteralNode.setText("1");
		intLiteralNode.initializeValue();

		IDNode idNode = new IDNode();
		idNode.setText(node.getAssignNode().getLocation().getText());
		PLUS_ASSIGNNode incrementNode = new PLUS_ASSIGNNode();
		idNode.setNextSibling(intLiteralNode);
		incrementNode.setFirstChild(idNode);
		MidNodeList incrementList = incrementNode
				.convertToMidLevel(newSymbolTable);

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

	// public static MidLocalVarDeclNode visitLocalVarDecl(FOR_INITIALIZENode
	// node,
	// MidSymbolTable symbolTable) {

	// }

	public static MidNodeList visit(WHILENode node, MidSymbolTable symbolTable) {
		MidNodeList outputList = new MidNodeList();

		MidLabelNode startLabel = MidLabelManager.getLabel(LabelType.WHILE);
		MidLabelNode bodyLabel = MidLabelManager.getLabel(LabelType.WHILE_BODY);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.ELIHW);
		MidJumpNode loopJump = new MidJumpNode(startLabel);
		MidSymbolTable newSymbolTable = new MidSymbolTable(symbolTable,
				startLabel, endLabel);

		ExpressionNode logicNode = node.getWhileTerminateNode()
				.getExpressionNode();
		MidNodeList branchList = logicNode
				.shortCircuit(newSymbolTable, bodyLabel, endLabel);

		MidNodeList bodyList = node.getBlockNode()
				.convertToMidLevelSpecial(newSymbolTable);

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
		case INT:
		case BOOLEAN:
			MidLocalVarDeclNode declNode = new MidLocalVarDeclNode(name);
			symbolTable.addVar(name, declNode);
			nodeList.add(declNode);
			// Initialize variable to 0.
			nodeList.addAll(MidSaveNode.storeValueInMemory(0, declNode));
			break;
		default:
			assert false : "Unexpected varType: " + node.getVarType();
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

	public static MidNodeList visit(IFNode node, MidSymbolTable symbolTable) {
		MidNodeList nodeList = new MidNodeList();

		MidLabelNode ifLabel = MidLabelManager.getLabel(LabelType.IF);
		MidLabelNode elseLabel = MidLabelManager.getLabel(LabelType.ELSE);
		MidLabelNode fiLabel = MidLabelManager.getLabel(LabelType.FI);

		MidJumpNode skipElseJumpNode = new MidJumpNode(fiLabel);

		MidNodeList branchList = node.getIfClauseNode().getExpressionNode()
				.shortCircuit(symbolTable, ifLabel, elseLabel);
		MidNodeList ifList = node.getBlockNode().convertToMidLevel(symbolTable);
		MidNodeList elseList = new MidNodeList();
		if (node.getElseBlock() != null)
			elseList = node.getElseBlock().getBlockNode()
					.convertToMidLevel(symbolTable);
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

	/**
	 * Special method sets the output list on the MidMethodDeclNode.
	 */
	public static void visitMethodDecl(MidMethodDeclNode node, BLOCKNode block,
			MidSymbolTable symbolTable) {

		MidNodeList outputList = block.convertToMidLevel(symbolTable, node
				.getUserDefinedName());
		if (!(outputList.getTail() instanceof MidReturnNode)) {
			outputList.add(new MidReturnNode(null));
		}
		node.setNodeList(outputList);
	}

	public static MidSymbolTable createMidLevelIR(CLASSNode node) {
		MidSymbolTable symbolTable = new MidSymbolTable();

		for (FIELD_DECLNode fieldNode : node.getFieldNodes()) {
			MidFieldDeclNode midFieldNode = visitFieldDecl(fieldNode, symbolTable);
			symbolTable.addVar(midFieldNode.getName(), midFieldNode);
		}

		// Manually add in methods to handle DBZ and OOB.
		String sanitizedOutOfBounds = MidMethodNameManager
				.sanitizeCustomMethodName(OUT_OF_BOUNDS_METHOD_NAME);
		MidMethodDeclNode outOfBoundsMethodDecl = new MidMethodDeclNode(
				sanitizedOutOfBounds, OUT_OF_BOUNDS_METHOD_NAME, VarType.VOID);
		symbolTable
				.addStarbucksMethod(OUT_OF_BOUNDS_METHOD_NAME, outOfBoundsMethodDecl);
		outOfBoundsMethodDecl.setNodeList(generateOutOfBoundsMethod());

		String sanitizedDivideByZero = MidMethodNameManager
				.sanitizeCustomMethodName(DIVIDE_BY_ZERO_NAME);

		MidMethodDeclNode divideByZeroMethodDecl = new MidMethodDeclNode(
				sanitizedDivideByZero, DIVIDE_BY_ZERO_NAME, VarType.VOID);
		symbolTable
				.addStarbucksMethod(DIVIDE_BY_ZERO_NAME, divideByZeroMethodDecl);
		divideByZeroMethodDecl.setNodeList(generateDivideByZeroMethod());

		for (METHOD_DECLNode methodNode : node.getMethodNodes()) {
			String originalMethodName = methodNode.getId();
			String sanitizedMethodName = MidMethodNameManager
					.sanitizeUserDefinedMethodName(originalMethodName);

			MidMethodDeclNode midMethodNode = new MidMethodDeclNode(
					sanitizedMethodName, originalMethodName,
					methodNode.getReturnType());
			// Map original name
			symbolTable.addMethod(originalMethodName, midMethodNode);
			visitMethodDecl(midMethodNode, methodNode.getBlockNode(), symbolTable);
		}

		return symbolTable;
	}

	private static MidNodeList generateOutOfBoundsMethod() {
		return generateHelper(OUT_OF_BOUNDS_ERROR);
	}

	private static MidNodeList generateDivideByZeroMethod() {
		return generateHelper(DIVIDE_BY_ZERO_ERROR);
	}

	private static MidNodeList generateHelper(String errorText) {
		MidNodeList instrList = new MidNodeList();
		// Name isn't really necessary, avoiding declaring unnecessary
		// constants. 0 refers to the offset, specifying method name.
		MidParamDeclNode methodParam = new MidParamDeclNode("", 0);
		MidMemoryNode errorStringLocation = AsmVisitor
				.addStringLiteral(errorText);

		List<MidMemoryNode> params = new ArrayList<MidMemoryNode>();

		params.add(errorStringLocation);
		params.add(methodParam);

		MidNodeList printInstrList = MidVisitor
				.makeMethodCall(new MidCalloutNode(AsmVisitor.PRINTF, params
						.size()), new MidNodeList(), new MidNodeList(), new MidNodeList(), params);
		instrList.add(methodParam);
		instrList.addAll(printInstrList);
		instrList.add(new MidExitNode(1));
		return instrList;
	}
}
