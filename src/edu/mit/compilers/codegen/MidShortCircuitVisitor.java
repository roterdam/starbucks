package edu.mit.compilers.codegen;

import java.lang.reflect.InvocationTargetException;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpEQNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpGNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpLENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpLNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNENode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidCompareNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2BoolNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;
import edu.mit.compilers.grammar.tokens.ANDNode;
import edu.mit.compilers.grammar.tokens.BANGNode;
import edu.mit.compilers.grammar.tokens.EQNode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.GTENode;
import edu.mit.compilers.grammar.tokens.GTNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.LTENode;
import edu.mit.compilers.grammar.tokens.LTNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.NEQNode;
import edu.mit.compilers.grammar.tokens.ORNode;
import edu.mit.compilers.grammar.tokens.TRUENode;

public class MidShortCircuitVisitor {
	public static MidNodeList shortCircuit(ORNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = new MidNodeList();
		MidLabelNode rightLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidNodeList rightShortList = node.getRightOperand()
				.shortCircuit(symbolTable, trueLabel, falseLabel);
		MidNodeList leftShortList = node.getLeftOperand()
				.shortCircuit(symbolTable, trueLabel, rightLabel);
		nodeList.addAll(leftShortList);
		nodeList.add(rightLabel);
		nodeList.addAll(rightShortList);
		return nodeList;
	}

	public static MidNodeList shortCircuit(ANDNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = new MidNodeList();
		MidLabelNode rightLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidNodeList rightShortList = node.getRightOperand()
				.shortCircuit(symbolTable, trueLabel, falseLabel);
		MidNodeList leftShortList = node.getLeftOperand()
				.shortCircuit(symbolTable, rightLabel, falseLabel);
		nodeList.addAll(leftShortList);
		nodeList.add(rightLabel);
		nodeList.addAll(rightShortList);
		return nodeList;
	}

	public static MidNodeList shortCircuit(BANGNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = node.getOperand()
				.shortCircuit(symbolTable, falseLabel, trueLabel);
		return nodeList;
	}

	public static MidNodeList shortCircuit(LTENode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpLENode.class);
	}

	public static MidNodeList shortCircuit(LTNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpLNode.class);
	}

	public static MidNodeList shortCircuit(GTNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpGNode.class);
	}

	public static MidNodeList shortCircuit(GTENode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitIntInt2BoolHelper(node, symbolTable, trueLabel, falseLabel, MidJumpGENode.class);
	}

	public static MidNodeList shortCircuit(EQNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitEqHelper(node, symbolTable, trueLabel, falseLabel, MidJumpEQNode.class);
	}

	public static MidNodeList shortCircuit(NEQNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		return shortCircuitEqHelper(node, symbolTable, trueLabel, falseLabel, MidJumpNENode.class);
	}

	public static MidNodeList shortCircuitEqHelper(OpSameSame2BoolNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel, Class<? extends MidJumpNode> c) {
		MidNodeList nodeList = new MidNodeList();

		ValuedMidNodeList valuedLeft = valuedHelper(node.getLeftOperand(), symbolTable);
		ValuedMidNodeList valuedRight = valuedHelper(node.getRightOperand(), symbolTable);

		MidNodeList leftInstr = valuedLeft.getList();
		MidLoadNode leftLoad = new MidLoadNode(valuedLeft.getReturnNode());
		MidNodeList rightInstr = valuedRight.getList();
		MidLoadNode rightLoad = new MidLoadNode(valuedRight.getReturnNode());

		MidCompareNode compareNode = new MidCompareNode(leftLoad, rightLoad);
		try {
			MidJumpNode jumpTrue = c.getConstructor(MidLabelNode.class)
					.newInstance(trueLabel);
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

	public static MidNodeList shortCircuitIntInt2BoolHelper(
			OpIntInt2BoolNode node, MidSymbolTable symbolTable,
			MidLabelNode trueLabel, MidLabelNode falseLabel,
			Class<? extends MidJumpNode> c) {
		MidNodeList nodeList = new MidNodeList();

		MidNodeList leftListNode = node.getLeftOperand()
				.convertToMidLevel(symbolTable);
		MidNodeList rightListNode = node.getRightOperand()
				.convertToMidLevel(symbolTable);

		MidMemoryNode leftDeclNode = leftListNode.getMemoryNode();
		MidMemoryNode rightDeclNode = rightListNode.getMemoryNode();

		MidLoadNode leftLoadNode = new MidLoadNode(leftDeclNode);
		MidLoadNode rightLoadNode = new MidLoadNode(rightDeclNode);
		MidCompareNode compareNode = new MidCompareNode(leftLoadNode,
				rightLoadNode);
		try {
			MidJumpNode jumpTrue = c.getConstructor(MidLabelNode.class)
					.newInstance(trueLabel);
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

	public static MidNodeList shortCircuit(TRUENode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = new MidNodeList();
		MidJumpNode jumpNode = new MidJumpNode(trueLabel);
		nodeList.add(jumpNode);
		return nodeList;
	}

	public static MidNodeList shortCircuit(FALSENode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = new MidNodeList();
		MidJumpNode jumpNode = new MidJumpNode(falseLabel);
		nodeList.add(jumpNode);
		return nodeList;
	}

	public static MidNodeList shortCircuit(METHOD_CALLNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		MidNodeList nodeList = new MidNodeList();
		MidNodeList methodNodeList = node.convertToMidLevel(symbolTable);

		MidMemoryNode tempNode = new MidTempDeclNode();
		MidSaveNode trueNode = new MidSaveNode(true, tempNode);

		MidLoadNode loadMethodNode = new MidLoadNode(
				methodNodeList.getMemoryNode());
		MidLoadNode loadTempNode = new MidLoadNode(tempNode);

		MidCompareNode compareNode = new MidCompareNode(loadMethodNode,
				loadTempNode);

		MidJumpEQNode jumpTrueNode = new MidJumpEQNode(trueLabel);
		MidJumpNode jumpFalseNode = new MidJumpNode(falseLabel);

		nodeList.addAll(methodNodeList);
		nodeList.add(loadMethodNode);
		nodeList.add(tempNode);
		nodeList.add(trueNode);
		nodeList.add(loadTempNode);
		nodeList.add(compareNode);
		nodeList.add(jumpTrueNode);
		nodeList.add(jumpFalseNode);

		return nodeList;
	}

	public static MidNodeList shortCircuit(IDNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {

		ValuedMidNodeList arrayDeclInstrList = MidVisitor
				.getMemoryLocation(node, symbolTable);
		MidMemoryNode declNode = arrayDeclInstrList.getReturnNode();
		// MidMemoryNode declNode = symbolTable.getVar(node.getText());
		MidLoadNode loadNode = new MidLoadNode(declNode);
		MidMemoryNode tempNode = new MidTempDeclNode();
		MidSaveNode zeroNode = new MidSaveNode(false, tempNode);
		MidLoadNode zeroLoadNode = new MidLoadNode(tempNode);
		MidCompareNode compareNode = new MidCompareNode(loadNode, zeroLoadNode);
		MidJumpEQNode jumpFalseNode = new MidJumpEQNode(falseLabel);
		MidJumpNode jumpTrueNode = new MidJumpNode(trueLabel);

		MidNodeList nodeList = new MidNodeList();

		nodeList.addAll(arrayDeclInstrList.getList());
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
	static ValuedMidNodeList valuedHelper(ExpressionNode node,
			MidSymbolTable symbolTable) {
		if (node.getMidVarType(symbolTable) == VarType.INT) {
			MidNodeList instrList = node.convertToMidLevel(symbolTable);
			MidMemoryNode memoryNode = instrList.getMemoryNode();
			return new ValuedMidNodeList(instrList, memoryNode);
		}
		MidLabelNode trueLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidLabelNode falseLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidLabelNode endLabel = MidLabelManager.getLabel(LabelType.SHORT);
		MidJumpNode jumpEndNode1 = new MidJumpNode(endLabel);
		MidJumpNode jumpEndNode2 = new MidJumpNode(endLabel);
		MidTempDeclNode declNode = new MidTempDeclNode();
		MidSaveNode saveTrueNode = new MidSaveNode(true, declNode);
		MidSaveNode saveFalseNode = new MidSaveNode(false, declNode);

		MidNodeList nodeList = new MidNodeList();
		MidNodeList branchList = node
				.shortCircuit(symbolTable, trueLabel, falseLabel);
		nodeList.add(declNode);
		nodeList.addAll(branchList);
		nodeList.add(trueLabel);
		nodeList.add(saveTrueNode);
		nodeList.add(jumpEndNode1);
		nodeList.add(falseLabel);
		nodeList.add(saveFalseNode);
		nodeList.add(jumpEndNode2);
		nodeList.add(endLabel);
		return new ValuedMidNodeList(nodeList, declNode);
	}

	public static MidNodeList shortCircuit(ExpressionNode node,
			MidSymbolTable symbolTable, MidLabelNode trueLabel,
			MidLabelNode falseLabel) {
		assert false : node.getClass() + " cannot be short circuited.";
		return null;
	}

}
