package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.rule.DecafRule;
import edu.mit.compilers.rule.Scope;

public class DecafTraveler {
	Scope globalScope;
	Stack<DecafNode> parents;
	DecafRule action;
	private Map<DecafNode, Object> propertyMap;
	boolean debug;
	public void hi(int b){
		b = 3;
	}
	public DecafTraveler(DecafNode root, DecafRule rule) {
		parents = new Stack<DecafNode>();
		parents.push(root);
		action = rule;
		
		// Store extra information about nodes.
		propertyMap = new HashMap<DecafNode, Object>();
		debug = false;
	}

	// Set debug to false to disable JFrame.
	public DecafTraveler(DecafNode root, DecafRule action, boolean debug) {
		this.debug = debug;
	}

	public void crawl() {
		globalScope = new Scope();
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
			Object out = action.run(node);
			if (out != null) {
				propertyMap.put(node, out);
			}

			child = node.getFirstChild(); 
			if (child == null) {
				continue;
			}
			tempStack.push(child);
			
			// Add the children to the stack in the correct order.
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
