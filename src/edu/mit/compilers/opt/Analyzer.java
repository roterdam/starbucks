package edu.mit.compilers.opt;

import java.util.HashMap;
import java.util.LinkedList;


public class Analyzer<S extends State<S>, T extends Transfer<S>> {
	
	private S state;
	private T transferFunction;
	private HashMap<Block, S> stateHash;
	
	public Analyzer(S s, T t){
		state = s;
		transferFunction = t;
		stateHash = new HashMap<Block, S>();
	}
	
	public S analyze(){
		LinkedList<Block> worklist = new LinkedList<Block>();
		//populate worklist = block.getBlocks()
		
		while (!worklist.isEmpty()){
			Block currentBlock = worklist.removeFirst();
			S currentState = stateHash.get(currentBlock);
			S out = this.transferFunction.apply(currentBlock, currentState);
			if (out.equals(currentState)){
				stateHash.put(currentBlock, out);
				worklist.addLast(currentBlock);
			}
			//return with less perfect result if it takes a really long time?
		}
		return state;
	}
	
	public static void main(String[] args){
		TestState ts = new TestState();
		TestTransfer tf = new TestTransfer();
		Analyzer a = new Analyzer<TestState, Transfer<TestState>>(ts, tf);
	}

}
