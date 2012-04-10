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
		Map<Canonicalization, Integer> freqs = new HashMap<Canonicalization, Integer>();
		freqs.put(this, 1);
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0);
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
	public Map<Canonicalization, Integer> getTerms() {
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
}
