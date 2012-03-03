package edu.mit.compilers.crawler;

import org.junit.Test;

import edu.mit.compilers.grammar.DecafNode;

public class DecafTravelerTest {
	
	private final int DUMMY = 101;
	
	@Test
	public void testCrawlerSimple() {
		System.out.println("=== testCrawlerSimple() ===");
		DecafNode root = createAST("root");
		DecafNode A = createAST("A");
		DecafNode B = createAST("B");
		DecafNode C = createAST("C");
		root.addChild(A);
		root.addChild(B);
		root.addChild(C);
		DecafNode A_A = createAST("A->A");
		DecafNode A_B = createAST("A->B");
		DecafNode B_A = createAST("B->A");
		A.addChild(A_A);
		A.addChild(A_B);
		B.addChild(B_A);
		DecafNode A_A_A = createAST("A->A->A");
		A_A.addChild(A_A_A);
		DecafNode B_A_A = createAST("B->A->A");
		DecafNode B_A_B = createAST("B->A->B");
		DecafNode B_A_C = createAST("B->A->C");
		DecafNode B_A_D = createAST("B->A->D");
		B_A.addChild(B_A_A);
		B_A.addChild(B_A_B);
		B_A.addChild(B_A_C);
		B_A.addChild(B_A_D);
		
		DecafTraveler crawler = new DecafTraveler(root, new DecafRunnable() {
			
			int i;
			@Override
			public Object run(DecafNode root) {
				System.out.println("Crawled " + root.getText());
				i++;
				return i-1;
			}
			
		});
		crawler.crawl();
		System.out.println(crawler.getPropertyMap().toString());
		
	}
	
	@Test
	public void testCrawlerWithParser() {
		System.out.println("=== testCrawlerWithParser() ===");
		DecafNode root = null;
	}
	
	private DecafNode createAST(String text) {
		DecafNode node = new DecafNode();
		node.initialize(DUMMY, text);
		return node;
	}

}
