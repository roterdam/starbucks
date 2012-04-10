package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;

public class DMMCanonicalization extends Canonicalization {
	protected Canonicalization c1;
	protected Canonicalization c2;
	private DMMType op;
	public DMMCanonicalization(Canonicalization c1, Canonicalization c2, DMMType op){
		this.c1 = c1;
		this.c2 = c2;
		this.op = op;
	}
	@Override
	public Canonicalization add(Canonicalization x) {
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		freqs.put(this, 1L);
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0L);
			}
			freqs.put(c, freqs.get(c)+x.getTerms().get(c));
		}
		return new ComplexCanonicalization(freqs);
	}
	@Override
	public Canonicalization mult(Canonicalization x) {
		if(op == DMMType.MUL){
			return new DMMCanonicalization(c1, c2.mult(x), DMMType.MUL);
		}else{
			return new DMMCanonicalization(this, x, DMMType.MUL);
		}
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	@Override
	public Map<Canonicalization, Long> getTerms() {
		return null;
	}
	
	public enum DMMType {
		DIV, MOD, MUL;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		return op.toString()+"< "+c1.toString()+", "+c2.toString()+" >";
	}
	
}
