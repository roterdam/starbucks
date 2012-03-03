package edu.mit.compilers.grammar;


@SuppressWarnings("serial")
public class BLOCKNode extends DecafNode {

	@Override
	public boolean enterScope() {
		return true;
	}
}