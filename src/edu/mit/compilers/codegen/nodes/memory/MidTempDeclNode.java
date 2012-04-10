package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.MidLabelManager;

public class MidTempDeclNode extends MidLocalMemoryNode {

	static int nodeNum = 0;
	private MidTempDeclNode linkedTempDecl;

	public MidTempDeclNode() {
		// generate a random temp id
		super("t" + MidLabelManager.getNewId());
	}

	public void linkTempDecl(MidTempDeclNode node) {
		registerTempDecl(node);
		node.registerTempDecl(this);
	}

	public void registerTempDecl(MidTempDeclNode node) {
		linkedTempDecl = node;
	}

	@Override
	public void setRawLocationReference(String rawLocationReference) {
		if (linkedTempDecl != null) {
			String linkedRef = linkedTempDecl.getRawLocationReference();
			if (linkedRef == null) {
				super.setRawLocationReference(rawLocationReference);
				linkedTempDecl.setRawLocationReference(rawLocationReference);
			} else {
				super.setRawLocationReference(linkedRef);
			}
		} else {
			super.setRawLocationReference(rawLocationReference);
		}
	}

	@Override
	public String toDotSyntax() {
		if (linkedTempDecl != null) {
			String out = super.toDotSyntax() + hashCode() + " -> "
					+ linkedTempDecl.hashCode()
					+ " [style=dotted,color=blue];\n";
			return out;
		}
		return super.toDotSyntax();
	}

	@Override
	public boolean equals(Object node) {
		return linkedTempDecl == node || super.equals(node);
	}

}