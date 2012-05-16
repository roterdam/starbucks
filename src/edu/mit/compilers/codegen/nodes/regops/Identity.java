package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class Identity {
	IdType type;
	long inValue;
	long compValue;
	OutType outType;
	
	public Identity(long inValue, long compValue, IdType type, OutType outType) {
		this.inValue = inValue;
		this.compValue = compValue;
		this.type = type;
		this.outType = outType;
	}

	public enum OutType {
		IN,    // in op other --> in 
		COMP,  // in op other --> comp
		OTHER; // in op other --> other
	}
	
	public enum IdType {
		LEFT, RIGHT, BOTH;
	}

	public boolean matches(MidBinaryRegNode binaryNode) {
		MidMemoryNode leftMemNode = binaryNode.getLeftOperand().getMemoryNode();
		MidMemoryNode rightMemNode = binaryNode.getRightOperand()
				.getMemoryNode();

		boolean left = leftMemNode.isConstant()
				&& leftMemNode.getConstant() == inValue;

		boolean right = rightMemNode.isConstant()
				&& rightMemNode.getConstant() == inValue;

		boolean both = left || right;
		switch (type) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case BOTH:
			return both;
		}
		return false;
	}

	public MidRegisterNode simplify(MidBinaryRegNode binaryNode) {
		MidMemoryNode leftMemNode = binaryNode.getLeftOperand().getMemoryNode();
		MidMemoryNode rightMemNode = binaryNode.getRightOperand()
				.getMemoryNode();
		
		LogCenter.debug("MAS", "Left is constant? "+leftMemNode.isConstant());
		LogCenter.debug("MAS", "Right is constant? "+rightMemNode.isConstant());
		
		if(type == IdType.LEFT || type == IdType.BOTH){
			if(leftMemNode.isConstant() && leftMemNode.getConstant() == inValue){
				switch(outType){
				case IN:
					return new MidLoadNode(new MidConstantNode(inValue));
				case COMP:
					return new MidLoadNode(new MidConstantNode(compValue));
				case OTHER:
					return binaryNode.getRightOperand();
				}
			}
		}
		if(type == IdType.RIGHT || type == IdType.BOTH){
			if(rightMemNode.isConstant() && rightMemNode.getConstant() == inValue){
				switch(outType){
				case IN:
					return new MidLoadNode(new MidConstantNode(inValue));
				case COMP:
					return new MidLoadNode(new MidConstantNode(compValue));
				case OTHER:
					return binaryNode.getLeftOperand();
				}
			}
		}
		return null;

	}

}
