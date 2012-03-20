package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class CHAR_LITERALNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		// Cast to INT!
		return VarType.INT;
	}
	
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return VarType.INT;
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	public long getValue(){
		char[] chars = getText().toCharArray();
		switch(chars.length){
		case 3:
			return chars[1];
		case 4:
			return chars[2];
		default:
			assert false : "Character should be of the form 'x' or '\\x'.";
			return 0;
		}
	}

}