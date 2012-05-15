package edu.mit.compilers.opt.as;

import java.util.Map.Entry;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.Identity;
import edu.mit.compilers.codegen.nodes.regops.MidBinaryRegNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadImmNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

public class MidAlgebraicSimplifier {

	public void analyze(MidSymbolTable symbolTable) {
		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods()
				.entrySet()) {
			for (MidNode node : entry.getValue().getNodeList()) {
				if (node instanceof MidBinaryRegNode) {
					// Check if both operands are constants.
					MidBinaryRegNode binaryNode = (MidBinaryRegNode) node;
					MidMemoryNode leftMemNode = binaryNode.getLeftOperand()
							.getMemoryNode();
					MidMemoryNode rightMemNode = binaryNode.getRightOperand()
							.getMemoryNode();

					if (leftMemNode.isConstant() && rightMemNode.isConstant()) {
						// If so, replace with a constant load.
						long leftVal = leftMemNode.getConstant();
						long rightVal = rightMemNode.getConstant();
						long simpleVal = binaryNode.applyOperation(leftVal,
								rightVal);
						LogCenter.debug("MAS", "About to replace with "
								+ simpleVal);
						binaryNode.replace(new MidLoadImmNode(simpleVal));
						continue;
					} else {
						for (Identity id : binaryNode.getIdentities()) {
							if (id.matches(binaryNode)) {
								binaryNode.replace(id.simplify(binaryNode));
								break;
							}
						}
						continue;
					}

				}

			}
		}
	}

}
