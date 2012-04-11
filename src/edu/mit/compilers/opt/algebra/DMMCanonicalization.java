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
		if(x == null) return null;
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		freqs.put(this, 1L);
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0L);
			}
			freqs.put(c, freqs.get(c)+x.getTerms().get(c));
		}
		return new LinearCombinationCanonicalization(freqs);
	}
	@Override
	public Canonicalization mult(Canonicalization x) {
		if(x == null) return null;
		if(!x.isDiscrete()){
			Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
			for (Canonicalization c : x.getTerms().keySet()) {
				Canonicalization prod = c.mult(this);
				if (!freqs.containsKey(prod)) {
					freqs.put(prod, 0L);
				}
				freqs.put(prod, freqs.get(prod) + x.getTerms().get(c));
				if (freqs.get(prod) == 0) {
					freqs.remove(prod);
				}
			}
			if (freqs.keySet().size() == 0) {
				return UnitLiteralCanonicalization.makeLiteral(0);
			} else if (freqs.keySet().size() == 1) {
				Canonicalization c = (Canonicalization) freqs.keySet()
						.toArray()[0];
				if(freqs.get(c) == 1){
					return c;
				}
				//return c.mult(UnitLiteralCanonicalization.makeLiteral(freqs.get(c)));
			}
			return new LinearCombinationCanonicalization(freqs);

		}else if(x instanceof UnitLiteralCanonicalization){
			return this;
		}else if(op == DMMType.MUL){
			return new DMMCanonicalization(c1, c2.mult(x), DMMType.MUL);
		}else{
			return new DMMCanonicalization(this, x, DMMType.MUL);
		}
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Map<Canonicalization, Long> getTerms() {
		return new HashMap<Canonicalization, Long>() {
			{
				put(DMMCanonicalization.this, 1L);
			}
		};
	}
	
	public enum DMMType {
		DIV, MOD, MUL;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DMMCanonicalization)){
			return false;
		}
		DMMCanonicalization p = (DMMCanonicalization)o;
		return c1.equals(p.c1) && c2.equals(p.c2) && op == p.op ||
		 	c1.equals(p.c2)&& c2.equals(p.c1)&& op == p.op && op == DMMType.MUL;
	}
	
	@Override
	public String toString(){
		return op.toString()+"< "+c1.toString()+", "+c2.toString()+" >";
	}
	
}
