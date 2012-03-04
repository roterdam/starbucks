package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.grammar.DecafNode;

/**
 * Careful, this is "3" not "int".
 * 
 * @author joshma
 * 
 */
@SuppressWarnings("serial")
public class INT_LITERALNode extends DecafNode {
	public int getValue(){
		return Integer.parseInt(this.getText());
	}
}