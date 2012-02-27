package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;

public class ASTTraveler {

	Stack<AST> parents;
	ASTRunnable action;
	private Map<AST, Object> propertyMap;
	boolean debug;

	public ASTTraveler(AST root, ASTRunnable action) {
		parents = new Stack<AST>();
		parents.push(root);
		this.action = action;
		// Store extra information about nodes.
		propertyMap = new HashMap<AST, Object>();
		debug = false;
	}

	// Set debug to false to disable JFrame.
	public ASTTraveler(AST root, ASTRunnable action, boolean debug) {
		this.debug = debug;
	}

	public void crawl() {
		AST node;
		AST child;
		if (debug) {
			ASTFrame frame = new ASTFrame("6.035", parents.peek());
			frame.setVisible(true);
		}
		Stack<AST> tempStack = new Stack<AST>();

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
	
	public Map<AST, Object> getPropertyMap() {
		return propertyMap;
	}
	
}
