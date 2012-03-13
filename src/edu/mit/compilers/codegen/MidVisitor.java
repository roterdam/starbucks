package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidParamDeclNode;
import edu.mit.compilers.codegen.nodes.MidVarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.PARAM_DECLNode;

public class MidVisitor {

	public static MidNodeList visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all
		// children.
		return new MidNodeList();
	}

	public static MidMethodDeclNode visitMethodDecl(METHOD_DECLNode node,
			MidSymbolTable symbolTable) {

		MidNodeList outputList = new MidNodeList();
		for (DecafNode statement : node.getBlockNode().getStatementNodes()) {
			System.out.println("Examining statement " + statement.toStringTree());
			outputList.addAll(statement.convertToMidLevel(symbolTable));
		}

		MidMethodDeclNode out = new MidMethodDeclNode(node.getId(), outputList);

		return out;
	}

	public static MidNodeList visit(PARAM_DECLNode node,
			MidSymbolTable symbolTable) {
		MidNodeList outputList = new MidNodeList();
		String name = node.getIDNode().getText();
		MidParamDeclNode paramNode = new MidParamDeclNode(name);
		outputList.add(paramNode);
		symbolTable.addVar(name, paramNode);
		System.out.println("ADDING PARAM NODE " + node.toStringTree());
		return outputList;
	}

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
