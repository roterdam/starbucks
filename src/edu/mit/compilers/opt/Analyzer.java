package edu.mit.compilers.opt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidNode;


public class Analyzer<S extends State<S>, T extends Transfer<S>> {
	
	private S startState;
	private T transferFunction;
	private HashMap<Block, S> outHash;
	
	public Analyzer(S s, T t){
		startState = s;
		transferFunction = t;
		outHash = new HashMap<Block, S>();
	}
	
	public S analyze(){
		List<Block> worklist = new LinkedList<Block>();
		//temporary hack
		//worklist.add(new Block("A"));
		//worklist.add(new Block("B"));
		//worklist.add(new Block("C"));
		
		// get all the blocks
		worklist = Block.getBlocks();
		
		//Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, transferFunction.getBottomState());
		}
		
		//Do the first node
		Block n0 = Block.getHead();
		outHash.put(n0,this.transferFunction.apply(n0, startState));
		worklist.remove(n0);

		while (!worklist.isEmpty()){
			Block currentBlock = worklist.removeFirst();
			S in = getInState(currentBlock);
			S out = this.transferFunction.apply(currentBlock, in);
			if (out != outHash.get(out)){
				outHash.put(currentBlock, out);
				worklist.addAll(currentBlock.getSuccessors());
			}
			//return with less perfect result if it takes a really long time?
		}
		return state;
	}
	
	public S getInState(Block b){
		S out = new S();
		foreach (m in b.getPredecessors()){
			out.append(outHash.get(m));
		}
		return out;
	}
	
	public static void main(String[] args){
		TestState ts = new TestState();
		TestTransfer tf = new TestTransfer();
		Analyzer<TestState, Transfer<TestState>> a = new Analyzer<TestState, Transfer<TestState>>(ts, tf);
		TestState out = a.analyze();
		System.out.println(out);
		
	}

}
