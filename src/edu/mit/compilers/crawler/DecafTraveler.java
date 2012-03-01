package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.grammar.DecafAST;

public class DecafTraveler {

	Stack<DecafAST> parents;
	DecafRunnable action;
	private Map<DecafAST, Object> propertyMap;
	boolean debug;

	public DecafTraveler(DecafAST root, DecafRunnable action) {
		parents = new Stack<DecafAST>();
		parents.push(root);
		this.action = action;
		// Store extra information about nodes.
		propertyMap = new HashMap<DecafAST, Object>();
		debug = false;
	}

	// Set debug to false to disable JFrame.
	public DecafTraveler(DecafAST root, DecafRunnable action, boolean debug) {
		this.debug = debug;
	}

	public void crawl() {
		DecafAST node;
		DecafAST child;
		if (debug) {
			ASTFrame frame = new ASTFrame("6.035", parents.peek());
			frame.setVisible(true);
		}
		Stack<DecafAST> tempStack = new Stack<DecafAST>();

		System.out.println("Starting crawl.");
		while (parents.size() > 0) {
			node = parents.pop();
			Object out = action.run(node);
			if (out != null) {
				propertyMap.put(node, out);
			}

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
	
	public Map<DecafAST, Object> getPropertyMap() {
		return propertyMap;
	}
	
}
