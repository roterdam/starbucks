package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductCanonicalization extends Canonicalization {
	Map<VarCanonicalization, Integer> prodTerms;
	
	public ProductCanonicalization() {
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ProductCanonicalization)){
			return false;
		}
		return prodTerms.equals(((ProductCanonicalization)o).prodTerms);
	}

	@Override
	public Canonicalization mult(Canonicalization x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	@Override
	public Map<Canonicalization, Integer> getTerms() {
		return new HashMap<Canonicalization, Integer>(){{
			put(ProductCanonicalization.this, 1);
		}};
	}
}
