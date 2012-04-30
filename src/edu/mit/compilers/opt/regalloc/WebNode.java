package edu.mit.compilers.opt.regalloc;

import java.util.HashSet;
import java.util.Set;

public class WebNode {
	
	private Web web;
	private Set<WebNode> interferences;
	
	public WebNode(Web web) {
		this.web = web;
		interferences = new HashSet<WebNode>();
	}
	
	public void addInterference(WebNode otherWebNode) {
		interferences.add(otherWebNode);
		otherWebNode.addInterference(this);
	}

}