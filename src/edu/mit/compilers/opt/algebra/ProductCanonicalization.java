package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;

public class ProductCanonicalization extends Canonicalization {
	Map<VarCanonicalization, Long> prodTerms;

	public static Canonicalization makeArray(String v, Canonicalization i){
		Map<VarCanonicalization, Long> freqs = new HashMap<VarCanonicalization, Long>();
		freqs.put(new VarCanonicalization(v, i), 1L);
		return new ProductCanonicalization(freqs);
	}
	
	public static Canonicalization makeVariable(String v){
		Map<VarCanonicalization, Long> freqs = new HashMap<VarCanonicalization, Long>();
		freqs.put(new VarCanonicalization(v), 1L);
		return new ProductCanonicalization(freqs);
	}
	
	public ProductCanonicalization(Map<VarCanonicalization, Long> prodTerms) {
		this.prodTerms = prodTerms;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ProductCanonicalization)) {
			return false;
		}
		return prodTerms.equals(((ProductCanonicalization) o).prodTerms);
	}

	@Override
	public Canonicalization mult(Canonicalization x) {
		if(x == null) return null;
		if (x.isDiscrete()) {
			if(x instanceof ProductCanonicalization){
				return ((ProductCanonicalization)x).mult(this);
			}else if(x instanceof UnitLiteralCanonicalization){
				return ((UnitLiteralCanonicalization)x).mult(this);
			}else if(x instanceof DMMCanonicalization){
				return ((DMMCanonicalization)x).mult(this);
			}else {
				assert false : x.getClass().toString()+" is unhandled";
			}
			return x.mult(this);
		} else {
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
		}
	}

	public Canonicalization mult(ProductCanonicalization x) {
		if(x == null) return null;
		Map<VarCanonicalization, Long> freqs = new HashMap<VarCanonicalization, Long>();
		for (VarCanonicalization c : prodTerms.keySet()) {
			freqs.put(c, prodTerms.get(c));
		}
		for (VarCanonicalization c : x.prodTerms.keySet()) {
			if (!freqs.containsKey(c)) {
				freqs.put(c, 0L);
			}
			freqs.put(c, freqs.get(c) + x.prodTerms.get(c));
		}
		return new ProductCanonicalization(freqs);
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
				put(ProductCanonicalization.this, 1L);
			}
		};
	}
	
	@Override
	public String toString(){
		StringBuilder x = new StringBuilder("Complex<");
		for(VarCanonicalization c : prodTerms.keySet()){
			x.append(c.toString()+"^"+prodTerms.get(c)+" ");
		}
		x.append(">");
		return x.toString();
	}
	
}
