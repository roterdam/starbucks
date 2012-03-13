package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.nodes.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidVarType;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;

public class MidVisitor {

	public static MidNode visit(DecafNode node, MidSymbolTable symbolTable) {
		// TODO: replace with real logic, i.e. call visitor.visit() on all
		// children.
		return new MidNode(null);
	}

	public static MidFieldDeclNode visit(FIELD_DECLNode node,
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

	public static MidMethodDeclNode visit(METHOD_DECLNode node,
			MidSymbolTable symbolTable) { 
		
		
		return null;

	}

	public static MidNode visit(CLASSNode node, MidSymbolTable symbolTable) {
		MidNode classNode = new MidNode();
		for (FIELD_DECLNode fieldNode : node.getFieldNodes()) {
			MidFieldDeclNode midFieldNode = visit(fieldNode, symbolTable);
			symbolTable.addVar(midFieldNode.getName(), midFieldNode);
		}
		for (METHOD_DECLNode methodNode : node.getMethodNodes()) {
			MidMethodDeclNode midMethodNode = visit(methodNode, symbolTable);
			symbolTable.addMethod(midMethodNode.getName(), midMethodNode);
		}
		
		return null;
	}
}
