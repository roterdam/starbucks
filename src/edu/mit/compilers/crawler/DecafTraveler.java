package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.grammar.BLOCKNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.METHODSNode;

public class DecafTraveler {
	Scope scope;
	Stack<DecafNode> parents;
	private Map<DecafNode, Object> propertyMap;
	boolean debug;
	public void hi(int b){
		b = 3;
	}
	public DecafTraveler(DecafNode root) {
		parents = new Stack<DecafNode>();
		parents.push(root);
		
		// Store extra information about nodes.
		propertyMap = new HashMap<DecafNode, Object>();
		debug = false;
	}

	// Set debug to false to disable JFrame.
	public DecafTraveler(DecafNode root, boolean debug) {
		this.debug = debug;
	}

	public void crawl() {
		scope = new Scope();
		DecafNode node;
		DecafNode child;
		if (debug) {
			ASTFrame frame = new ASTFrame("6.035", parents.peek());
			frame.setVisible(true);
		}
		Stack<DecafNode> tempStack = new Stack<DecafNode>();

		System.out.println("Starting crawl.");
		while (parents.size() > 0) {
			node = parents.pop();
			if (node.enterScope()) {
				scope = new Scope(scope);
			} else if (node.exitScope()) {
				scope = scope.getParent();
			}
			node.validate(scope);
			// Add the children to the stack in the correct order.
			child = node.getFirstChild(); 
			if (child == null) {
				continue;
			}
			tempStack.push(child);
			
			while ((child = child.getNextSibling()) != null) {
				tempStack.push(child);
			}
			while (tempStack.size() > 0) {
				parents.push(tempStack.pop());
			}
			tempStack.clear();
		}
	}
	
	public Map<DecafNode, Object> getPropertyMap() {
		return propertyMap;
	}
	
}
