package edu.mit.compilers.opt.as;

import java.util.Map.Entry;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.Identity;
import edu.mit.compilers.codegen.nodes.regops.MidBinaryRegNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.codegen.nodes.regops.MidUnaryRegNode;
import edu.mit.compilers.opt.AnalyzerHelpers;

public class MidAlgebraicSimplifier {

	public void analyze(MidSymbolTable symbolTable) {
		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods()
				.entrySet()) {
			LogCenter.debug("MAS", "Old Method "+entry.getKey());
			for (MidNode node : entry.getValue().getNodeList()) {
				LogCenter.debug("MAS", node.toString());
			}
		}
		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods()
				.entrySet()) {
			for (MidNode node : entry.getValue().getNodeList()) {
				if(!(node instanceof MidSaveNode)){
					continue;
				}
				MidSaveNode saveNode = (MidSaveNode) node;
				if (!saveNode.savesRegister()){
					continue;
				}
				
				MidMemoryNode destNode = saveNode.getDestinationNode();
				MidRegisterNode regNode = saveNode.getRegNode();
				if(regNode instanceof MidBinaryRegNode) {
					
					MidBinaryRegNode binaryNode = (MidBinaryRegNode) regNode;
					
					MidMemoryNode leftMemNode = binaryNode.getLeftOperand()
							.getMemoryNode();
					MidMemoryNode rightMemNode = binaryNode.getRightOperand()
							.getMemoryNode();

					if (leftMemNode.isConstant() && rightMemNode.isConstant()) {
						// If so, replace with a constant load.

						long leftVal = ((MidConstantNode)leftMemNode).getConstant();
						long rightVal = ((MidConstantNode)rightMemNode).getConstant();
						long simpleVal = binaryNode.applyOperation(leftVal,
								rightVal);
						
						LogCenter.debug("MAS", ""+binaryNode.getLeftOperand().getMemoryNode().getClass());
						LogCenter.debug("MAS", ""+binaryNode.getRightOperand().getMemoryNode().getClass());
						LogCenter.debug("MAS", "About to replace "+node+" with "+simpleVal);
						
						MidRegisterNode newRegNode = new MidLoadNode(new MidConstantNode(simpleVal));
						
						if(binaryNode.hasRegister())
							newRegNode.setRegister(binaryNode.getRegister());
						MidSaveNode newSaveNode = new MidSaveNode(newRegNode, destNode);
						MidNodeList replList = new MidNodeList();
						replList.add(newRegNode);
						replList.add(newSaveNode);
						
						AnalyzerHelpers.completeReplaceBinary(saveNode, replList);
						continue;
					} else {
						for (Identity id : binaryNode.getIdentities()) {
							if (id.matches(binaryNode)) {

								MidRegisterNode newRegNode = id.simplify(binaryNode);
								if(binaryNode.hasRegister())
									newRegNode.setRegister(binaryNode.getRegister());
								MidSaveNode newSaveNode = new MidSaveNode(newRegNode, destNode);
								MidNodeList replList = new MidNodeList();
								replList.add(newRegNode);
								replList.add(newSaveNode);
								
								AnalyzerHelpers.completeReplaceBinary(saveNode, replList);
								LogCenter.debug("MAS", "About to replace "+binaryNode+" with "+newRegNode);
								
								break;
							}
						}
						continue;
					}


				}else if(regNode instanceof MidUnaryRegNode){
					MidUnaryRegNode unaryNode = (MidUnaryRegNode) regNode;
					MidMemoryNode memNode = unaryNode.getOperand().getMemoryNode();
					if(memNode.isConstant()){
						
						long val = memNode.getConstant();
						long simpleVal = unaryNode.applyOperation(val);
						
						LogCenter.debug("MAS", "About to replace "+node+" with "+simpleVal);
						
						MidRegisterNode newRegNode = new MidLoadNode(new MidConstantNode(simpleVal));
						
						if(unaryNode.hasRegister())
							newRegNode.setRegister(unaryNode.getRegister());
						
						MidSaveNode newSaveNode = new MidSaveNode(newRegNode, destNode);
						MidNodeList replList = new MidNodeList();
						replList.add(newRegNode);
						replList.add(newSaveNode);
						
						
						AnalyzerHelpers.completeReplaceUnary(saveNode, replList);
						continue;
						
						
					}					
				}

			}
		}
		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods()
				.entrySet()) {
			LogCenter.debug("MAS", "Method "+entry.getKey());
			for (MidNode node : entry.getValue().getNodeList()) {
				LogCenter.debug("MAS", node.toString());
			}
		}
	}
	

}
