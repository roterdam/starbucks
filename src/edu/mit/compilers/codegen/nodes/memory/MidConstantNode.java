package edu.mit.compilers.codegen.nodes.memory;

/**
 * A wrapper class that lets constant propagation refer to constants as
 * "memory nodes" like on other definitions.
 */
public class MidConstantNode extends MidMemoryNode {

	private long value;

	public MidConstantNode(long value) {
		super("CONS " + value);
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public String getFormattedLocationReference() {
		return Long.toString(value);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MidConstantNode)) {
			return false;
		}
		MidConstantNode consNode = (MidConstantNode) o;
		return consNode.getValue() == value;
	}

}
