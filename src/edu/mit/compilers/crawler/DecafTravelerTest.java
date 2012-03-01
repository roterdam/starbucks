package edu.mit.compilers.crawler;

import org.junit.Test;

import edu.mit.compilers.grammar.DecafAST;

public class DecafTravelerTest {
	
	private final int DUMMY = 101;
	
	@Test
	public void testCrawlerSimple() {
		
		DecafAST root = createAST("root");
		DecafAST A = createAST("A");
		DecafAST B = createAST("B");
		DecafAST C = createAST("C");
		root.addChild(A);
		root.addChild(B);
		root.addChild(C);
		DecafAST A_A = createAST("A->A");
		DecafAST A_B = createAST("A->B");
		DecafAST B_A = createAST("B->A");
		A.addChild(A_A);
		A.addChild(A_B);
		B.addChild(B_A);
		DecafAST A_A_A = createAST("A->A->A");
		A_A.addChild(A_A_A);
		DecafAST B_A_A = createAST("B->A->A");
		DecafAST B_A_B = createAST("B->A->B");
		DecafAST B_A_C = createAST("B->A->C");
		DecafAST B_A_D = createAST("B->A->D");
		B_A.addChild(B_A_A);
		B_A.addChild(B_A_B);
		B_A.addChild(B_A_C);
		B_A.addChild(B_A_D);
		
		DecafTraveler crawler = new DecafTraveler(root, new DecafRunnable() {
			
			int i;
			@Override
			public Object run(DecafAST root) {
				System.out.println("Crawled " + root.getText());
				i++;
				return i-1;
			}
			
		});
		crawler.crawl();
		System.out.println(crawler.getPropertyMap().toString());
		
	}
	
	private DecafAST createAST(String text) {
		DecafAST node = new DecafAST();
		node.initialize(DUMMY, text);
		return node;
	}

}
