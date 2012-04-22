package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.opt.algebra.DMMCanonicalization.DMMType;

public abstract class Canonicalization {
	
	public static final Canonicalization NEG_ONE = UnitLiteralCanonicalization.makeLiteral(-1);
	public static final Canonicalization ZERO =  UnitLiteralCanonicalization.makeLiteral(0);
	
	public static Canonicalization makeVariable(String v){
		return ProductCanonicalization.makeVariable(v);
	}
	public static Canonicalization makeArray(String v, Canonicalization i){
		return ProductCanonicalization.makeArray(v, i);
	}
	public static Canonicalization makeLiteral(long v){
		return UnitLiteralCanonicalization.makeLiteral(v);
	}
	
	public static Canonicalization add(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return null;
		return x.add(y);
	}
	
	public static Canonicalization sub(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return null;
		return x.sub(y);
	}
	
	public static Canonicalization div(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return null;
		return x.div(y);
	}
	
	public static Canonicalization mod(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return null;
		return x.mod(y);
	}
	
	public static Canonicalization mult(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return null;
		return x.mult(y);
	}
	
	public static Canonicalization inv(Canonicalization x){
		if(x == null) return null;
		return x.mult(Canonicalization.NEG_ONE);
	}
	public static boolean equals(Canonicalization x, Canonicalization y){
		if(x == null || y == null) return false;
		return x.equals(y);
	}
	
	public static void main(String[] args){
		
		Canonicalization x = ProductCanonicalization.makeVariable("a");
		Canonicalization y = ProductCanonicalization.makeVariable("b");
		Canonicalization z = ProductCanonicalization.makeVariable("c");
//		Canonicalization w = x.add(y).add(z);
		
		Canonicalization a = ProductCanonicalization.makeVariable("a");
		Canonicalization b = ProductCanonicalization.makeVariable("b");
		Canonicalization c = ProductCanonicalization.makeVariable("c");
//		Canonicalization e = ProductCanonicalization.makeVariable("e");
		Canonicalization d = a.add(c).add(b);
		
		Canonicalization asa = a.sub(a);
		System.out.println(asa);
		System.out.println(asa.equals(ZERO));
		//System.out.println(w.equals(d));
		
		
		Canonicalization t1 = x.add(y).mult(z);
		Canonicalization t2 = x.mult(z).add(z.mult(y));
		
		System.out.println(t1.equals(t2));
		System.out.println(t1);
		System.out.println(t2);
		
		Canonicalization t3 = a.div(b).mult(c).mult(d);
		Canonicalization t4 = a.div(b).mult(c.mult(d));
		
		System.out.println(t3.equals(t4));
		System.out.println(t3);
		System.out.println(t4);
		
		
		
		System.out.println("===========");
		Canonicalization xx = a.mult(b).div(c);
		System.out.println(xx.add(xx));
		System.out.println(xx.getClass().toString());
		System.out.println(xx.mult(Canonicalization.makeLiteral(2)));
		System.out.println(xx.add(xx).equals(xx.mult(Canonicalization.makeLiteral(2))));
				
		ArrayList<Integer> xo = new ArrayList<Integer>();
		xo.add(0);
		xo.add(1);
		xo.add(2);
		xo.addAll(xo);
		System.out.println(xo);
	}
	public Canonicalization add(Canonicalization x){
		if(x == null) return null;
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		for(Canonicalization c : getTerms().keySet()){
			freqs.put(c, getTerms().get(c));
		}
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0L);
			}
			freqs.put(c, freqs.get(c) + x.getTerms().get(c));
			if(freqs.get(c) == 0){
				freqs.remove(c);
			}
		}
		if(freqs.keySet().size() == 0){
			return UnitLiteralCanonicalization.makeLiteral(0);
		}else if(freqs.keySet().size() == 1){
			Canonicalization c = (Canonicalization) freqs.keySet().toArray()[0];
			return c.mult(UnitLiteralCanonicalization.makeLiteral(freqs.get(c)));
		}
		
		return new LinearCombinationCanonicalization(freqs);
	}
	
	public abstract Canonicalization mult(Canonicalization x);
	
	public Canonicalization sub(Canonicalization x){
		if(x == null) return null;
		return this.add(x.mult(UnitLiteralCanonicalization.makeLiteral(-1)));
	}
	
	public Canonicalization div(Canonicalization x) {
		if(x == null) return null;
		return new DMMCanonicalization(this, x, DMMType.DIV);
	}
	
	public Canonicalization mod(Canonicalization x) {
		if(x == null) return null;
		return new DMMCanonicalization(this, x, DMMType.MOD);
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	public abstract boolean isDiscrete();
	public abstract Map<Canonicalization, Long> getTerms();
	
	@Override
	public int hashCode(){
		// TODO, make better hash.
		return 0;
	}
}
