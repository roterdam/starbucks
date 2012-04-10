package edu.mit.compilers.opt;

import java.util.Arrays;


// This gets very complicated.
//  - Boolean expressions
//  - c*b*d/a*h*f*g --> b*c*d/a*f*g*h
//  - b[0] + a[d*c*f] --> a[c*d*f] + b[0]

public class Canonicalization {
	
	private Product[] terms;
	public Canonicalization(){

	}
	public void mult(Canonicalization c){
		
	}
	public void add(Canonicalization c){
		
	}
	public void subt(Canonicalization c){
		
	}
	public void div(Canonicalization c){
		
	}
	private class Product implements Comparable<Product> {
		protected long coeff;
		protected Var[] terms;
		public Product(){
			
		}
		public void multiply(long coeff){
			coeff *= this.coeff;
		}
		public boolean canCombine(Product p){
			return Arrays.equals(p.terms, this.terms);
		}
		
		public void add(Product p){
			if(canCombine(p)){
				this.coeff += p.coeff;
			}
		}
		public boolean isZero(){
			return terms.length == 0 || coeff == 0;
		}
		public void addTerm(Var node){
			Var[] termsNew = new Var[terms.length];
			for(int i=0; i< terms.length; i++){
				termsNew[i] = terms[i];
			}
			termsNew[terms.length] = node;
			Arrays.sort(terms);
		}
		@Override
		public int compareTo(Product p) {
			if(this.terms.length < p.terms.length){
				return -1;
			}else if(p.terms.length < this.terms.length){
				return 1;
			}else {
				for(int i=0; i < terms.length; i++){
					int comp = terms[i].compareTo(p.terms[i]);
					if(comp != 0)
						return comp;
				}
				return 0;
			}
		}
		
	}
	
	private class Var implements Comparable<Var> {

		@Override
		public int compareTo(Var o) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
