package edu.mit.compilers.opt.forunroll;


import edu.mit.compilers.LogCenter;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.BranchNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.VarTypeNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.CONTINUENode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.GTENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.IF_CLAUSENode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.INT_TYPENode;
import edu.mit.compilers.grammar.tokens.MINUSNode;
import edu.mit.compilers.grammar.tokens.PLUS_ASSIGNNode;
import edu.mit.compilers.grammar.tokens.VAR_DECLNode;
import edu.mit.compilers.grammar.tokens.WHILENode;
import edu.mit.compilers.grammar.tokens.WHILE_TERMINATENode;

public class Unroller {
	
	static int varCounter = 0;
	
	static int LARGEST_UNROLL_LG = 1; // corresponds to 2**5 unroll.
	
	public static String getVariableName(){
		return "!unroll_"+varCounter++;
	}
	
	
	
	public static DecafNode unroll(DecafNode node){
		unrollHelper(node);
		return node;
	}
	
	public static void unrollHelper(DecafNode node){
		DecafNode childNode = node.getFirstChild();
		DecafNode prevNode = null;
		while(childNode != null){
			DecafNode unrolledChild = childNode.unroll();
			if(prevNode == null){
				node.setFirstChild(unrolledChild);
			} else {
				prevNode.setNextSibling(unrolledChild);
			}
			childNode = childNode.getNextSibling();
			
			unrolledChild.setNextSibling(childNode);
			prevNode = unrolledChild;
		}
	}
	/* Unroll completely 
	for(i=4; 10){
		print(i)
	}
	
	
	remember that this is its own block.
	
	
	for(i=a+b, c){
		
	}
	
	for(i=g(a)+2, f(x)+c){
		
	} ---> 
	
	
	{
		int i, sb_end;
		

		i = g(a)+2;
		sb_end = f(x) + c;
		
		while(end - i >= 2^3){
			unrolled 2^3 times.
		}
		if(end - i >= 2^2){
			unrolled 2^2 times.
		}
		if(end - i >= 2^1){
			unrolled 2^1 times.
		}
		if(end - i >= 2^0){
			unrolled 2^0 times.
		}
	}
	
	for(i=g(a)+2; f(x)+c){
		
	}
	
	Conditions for unrolling:
		Make sure i is not set in the loop body.
		Make sure there are no breaks/continues.
	
	*/
	
	public static DecafNode unroll(FORNode forNode){
		unrollHelper(forNode);
		
		ASSIGNNode assignNode = forNode.getForInitializeNode().getAssignNode();
		String iterVar = assignNode.getLocation().getText();
		ExpressionNode initExpr = assignNode.getExpression();
		ExpressionNode termExpr = forNode.getForTerminateNode().getExpressionNode();
		BLOCKNode blockNode = forNode.getBlockNode();
		
		// Can't unroll if variable gets modified.
		if (!blockNode.isUnrollable(iterVar, true)){
			return forNode;
		}
		
		// Do a full unroll if we are dealing with int literals.
		if(initExpr instanceof INT_LITERALNode && termExpr instanceof INT_LITERALNode){
			long initValue = ((INT_LITERALNode)initExpr).getValue();
			long termValue = ((INT_LITERALNode)termExpr).getValue();
			
			
			// Outer block init/def.
			VAR_DECLNode iterInitDeclNode = generateVarDecl(iterVar);
			DecafNode copyAssignNode = assignNode.deepCopy();
			
			
			// Create inner block
			BLOCKNode innerBlock = generateBodyWithDecls(blockNode);
			
			DecafNode beginIter = generateBody(iterVar, blockNode, termValue - initValue).getFirstChild();
			
			if(innerBlock.getFirstChild() == null){
				innerBlock.setFirstChild(beginIter);
			} else {
				innerBlock.getFirstChild().getLastSibling().setNextSibling(beginIter);
			}			
			
			// Make return block
			BLOCKNode unrolledBlock = new BLOCKNode();
			unrolledBlock.setText("OPTFOR");
			unrolledBlock.setFirstChild(iterInitDeclNode);
			iterInitDeclNode.setNextSibling(copyAssignNode);
			copyAssignNode.setNextSibling(innerBlock);
			
			LogCenter.debug("FU","Unrolled to: "+unrolledBlock);
			return unrolledBlock;
			
		}
		// Otherwise, we are dealing with at least one non-literal expression :(
		
		
		String termVar = getVariableName();
		
		// Declare itervar and termvar
		VAR_DECLNode iterInitDeclNode = generateVarDecl(iterVar);
		VAR_DECLNode termInitDeclNode = generateVarDecl(termVar);
		
		// Set the value of the itervar
		ASSIGNNode copyAssignIterNode = (ASSIGNNode) assignNode.deepCopy();
		
		// Set the value of the termvar
		ASSIGNNode copyAssignTermNode = new ASSIGNNode();
		DecafNode copyTermExpr = termExpr.deepCopy();
		IDNode copyTermVarNode = new IDNode();
		copyTermVarNode.setText(termVar);
		copyAssignTermNode.setFirstChild(copyTermVarNode);
		copyTermVarNode.setNextSibling(copyTermExpr);
		
		
		// Generate the inner block
		BLOCKNode innerNode = generateBodyWithDecls(blockNode);
		
		// ADD PARAMS HERE.
		
		DecafNode lastNode = null;
		
		long pow2 = 1;
		for(int i=0; i < LARGEST_UNROLL_LG; i++){
			pow2*=2;
		}
		for(int i=0; i <= LARGEST_UNROLL_LG; i++){
			if (i==0){
				WHILENode whileNode = generateWhile(iterVar, termVar, blockNode, pow2);
				if (innerNode.getFirstChild() == null) {
					innerNode.setFirstChild(whileNode);
				} else {
					innerNode.getFirstChild().getLastSibling().setNextSibling(whileNode);
				}
				lastNode = whileNode;
			} else {
				IFNode ifNode = generateIf(iterVar, termVar, blockNode, pow2);
				lastNode.setNextSibling(ifNode);
				lastNode = ifNode;
			}
			pow2/=2;
		}
		
		
		// Make the unrolled block
		// Add the decls/def of iter/term vars.
		BLOCKNode unrolledBlock = new BLOCKNode();
		
		unrolledBlock.setFirstChild(iterInitDeclNode);
		iterInitDeclNode.setNextSibling(termInitDeclNode);
		termInitDeclNode.setNextSibling(copyAssignIterNode);
		copyAssignIterNode.setNextSibling(copyAssignTermNode);
		copyAssignTermNode.setNextSibling(innerNode);
		
		LogCenter.debug("FU","Unrolled to: "+unrolledBlock);
		
		return unrolledBlock;
	}
	
	
	public static VAR_DECLNode generateVarDecl(String var){
		VAR_DECLNode varDeclNode = new VAR_DECLNode();
		INT_TYPENode varTypeNode = new INT_TYPENode();
		IDNode varVarNode = new IDNode();
		varVarNode.setText(var);
		varDeclNode.setFirstChild(varTypeNode);
		varTypeNode.setNextSibling(varVarNode);
		return varDeclNode;
	}
	
	public static GTENode generateClauseExpr(String iterVar, String endVar, long iterations){
		IDNode endNode = new IDNode();
		endNode.setText(endVar);
		IDNode iterNode = new IDNode();
		iterNode.setText(iterVar);
		SubtractNode subtNode = new SubtractNode();
		subtNode.setFirstChild(endNode);
		endNode.setNextSibling(iterNode);
		INT_LITERALNode boundNode = new INT_LITERALNode();
		boundNode.setText(Long.toString(iterations));
		boundNode.initializeValue();
		GTENode gteNode = new GTENode();
		gteNode.setFirstChild(subtNode);
		subtNode.setNextSibling(boundNode);
		return gteNode;
	}
	
	public static BLOCKNode generateBodyWithDecls(BLOCKNode body){
		BLOCKNode declBody = new BLOCKNode();
		DecafNode lastNode = null;
		DecafNode childNode = body.getFirstChild();
		while(childNode != null && childNode instanceof VAR_DECLNode){
			if (lastNode == null){
				declBody.setFirstChild(childNode.deepCopy());
				lastNode = declBody.getFirstChild();
			} else {
				lastNode.setNextSibling(childNode.deepCopy());
				lastNode = lastNode.getNextSibling();
			}
		}
		return declBody;
	}
	
	public static BLOCKNode generateBody(String iterVar, BLOCKNode body, long iterations){
		
		// Remove decls.
		BLOCKNode noDeclBody = (BLOCKNode) body.deepCopy();
		while(noDeclBody.getFirstChild() !=null && noDeclBody.getFirstChild() instanceof VAR_DECLNode){
			noDeclBody.setFirstChild(noDeclBody.getFirstChild().getNextSibling());
		}
		
		// Make the body
		BLOCKNode blockNode = new BLOCKNode();
		
		DecafNode lastNode = null;
		for(long i=0; i < iterations; i++){
			// Add a copy of the body
			DecafNode copyBody = noDeclBody.deepCopy();
			DecafNode firstChild = copyBody.getFirstChild();
			if(lastNode == null){
				blockNode.setFirstChild(firstChild);
			}else{
				lastNode.setNextSibling(firstChild);
			}
			if (firstChild != null){
				lastNode = firstChild.getLastSibling();
			}
			
			// Add an increment
			PLUS_ASSIGNNode incrNode = new PLUS_ASSIGNNode();
			IDNode iterNode2 = new IDNode();
			iterNode2.setText(iterVar);
			INT_LITERALNode oneNode = new INT_LITERALNode();
			oneNode.setText("1");
			oneNode.initializeValue();
			incrNode.setFirstChild(iterNode2);
			iterNode2.setNextSibling(oneNode);
			
			if(lastNode == null){
				blockNode.setFirstChild(incrNode);
			} else{
				lastNode.setNextSibling(incrNode);
			}
			lastNode = incrNode;
		}
		
		return blockNode;
	}

	public static WHILENode generateWhile(String iterVar, String endVar, BLOCKNode body, long iterations){
		
		// Termination node
		WHILE_TERMINATENode termNode = new WHILE_TERMINATENode();
		termNode.setFirstChild(generateClauseExpr(iterVar, endVar, iterations));
		
		// Make the body
		BLOCKNode blockNode = generateBody(iterVar, body, iterations);
		
		// Make the while node
		WHILENode whileNode = new WHILENode();
		whileNode.setFirstChild(termNode);
		termNode.setNextSibling(blockNode);
		
		return whileNode;
		
	}
	public static IFNode generateIf(String iterVar, String endVar, BLOCKNode body, long iterations){
		// Make the clause
		IF_CLAUSENode ifClauseNode = new IF_CLAUSENode();
		ifClauseNode.setFirstChild(generateClauseExpr(iterVar, endVar, iterations));
		
		// Make the body
		BLOCKNode blockNode = generateBody(iterVar, body, iterations);
		
		// Make the if node.
		IFNode ifNode = new IFNode();
		ifNode.setFirstChild(ifClauseNode);
		ifClauseNode.setNextSibling(blockNode);	
		
		return ifNode;
	}

	// Visitor pattern to check if a loop is unrollable.
	
	public static boolean isUnrollable(BranchNode node, String var, boolean hasLoopScope){
		return !hasLoopScope;
	}
	
	public static boolean isUnrollable(ASSIGNNode node, String var, boolean hasLoopScope){
		return !node.getLocation().getText().equals(var) && 
		isUnrollableHelper(node, var, hasLoopScope);
	}
	
	public static boolean isUnrollable(WHILENode node, String var, boolean hasLoopScope){
		return isUnrollableHelper(node, var, false);
	}
	
	public static boolean isUnrollable(FORNode node, String var, boolean hasLoopScope){
		return isUnrollableHelper(node, var, false);
	}
	
	public static boolean isUnrollable(DecafNode node, String var, boolean hasLoopScope){
		return isUnrollableHelper(node, var, hasLoopScope);
	}
	
	public static boolean isUnrollableHelper(DecafNode node, String var, boolean hasLoopScope){
		DecafNode childNode = node.getFirstChild();
		while(childNode != null){
			if(!childNode.isUnrollable(var, hasLoopScope)){
				return false;
			}
			childNode = childNode.getNextSibling();
		}
		return true;
	}
}
