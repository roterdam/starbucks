package edu.mit.compilers.grammar;

/**
 * This gets replaced during semantic checking with one of SubtractNode or
 * UnaryMinusNode.
 */
@SuppressWarnings("serial")
public class MINUSNode extends DecafNode {

	@Override
	public DecafNode clean() {
		super.clean();
		DecafNode cleanNode;
		
		int numChildren = getNumberOfChildren();
		assert numChildren == 1 || numChildren == 2;
		
		if (numChildren == 1) {
			DecafNode childNode = getFirstChild();
			// Remove or add the - sign as necessary.
			if (childNode instanceof INT_LITERALNode) {
				String intText = childNode.getText();
				if (intText.startsWith("-")) {
					childNode.setText(intText.substring(1));
				} else {
					childNode.setText("-" + intText);
				}
				return childNode;
			}
			cleanNode = new UnaryMinusNode();
			cleanNode.setText(getText() + " (U)");
			cleanNode.setFirstChild(childNode);
			return cleanNode;
		}
		
		cleanNode = new SubtractNode();
		cleanNode.setText(getText() + " (B)");
		cleanNode.setFirstChild(getFirstChild());
		return cleanNode;
	}

}