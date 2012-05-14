package edu.mit.compilers.opt.cm;

public class Depth {
	
	int value;
	
	public Depth() {
		this.value = 0;
	}
	
	public void increase() {
		this.value++;
	}
	
	public int getDepth() {
		return this.value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Depth)) {
			return false;
		}
		Depth d = (Depth) o;
		return d.getDepth() == this.getDepth();
	}
	
}
