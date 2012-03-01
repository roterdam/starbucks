package edu.mit.compilers.grammar;

import antlr.CommonAST;
import antlr.Token;

public class DefactorAST extends CommonAST {
	private int line = 0;
	private int col = 0;
	
	@Override
	public void initialize(Token t){
		super.initialize(t);
		line = t.getLine();
		col = t.getColumn();
	}
	@Override
	public int getLine(){
		return line;
	}
	@Override
	public int getColumn(){
		return col;
	}
}
