package edu.mit.compilers.crawler;

import org.junit.Test;

import antlr.CommonAST;
import antlr.collections.AST;

public class ASTTravelerTest {
	
	private final int DUMMY = 101;
	
	@Test
	public void testCrawlerSimple() {
		
		AST root = createAST("root");
		AST A = createAST("A");
		AST B = createAST("B");
		AST C = createAST("C");
		root.addChild(A);
		root.addChild(B);
		root.addChild(C);
		AST A_A = createAST("A->A");
		AST A_B = createAST("A->B");
		AST B_A = createAST("B->A");
		A.addChild(A_A);
		A.addChild(A_B);
		B.addChild(B_A);
		AST A_A_A = createAST("A->A->A");
		A_A.addChild(A_A_A);
		AST B_A_A = createAST("B->A->A");
		AST B_A_B = createAST("B->A->B");
		AST B_A_C = createAST("B->A->C");
		AST B_A_D = createAST("B->A->D");
		B_A.addChild(B_A_A);
		B_A.addChild(B_A_B);
		B_A.addChild(B_A_C);
		B_A.addChild(B_A_D);
		
		ASTTraveler crawler = new ASTTraveler(root, new ASTRunnable() {
			
			int i;
			@Override
			public Object run(AST root) {
				System.out.println("Crawled " + root.getText());
				i++;
				return i-1;
			}
			
		});
		crawler.crawl();
		System.out.println(crawler.getPropertyMap().toString());
		
	}
	
	private AST createAST(String text) {
		CommonAST node = new CommonAST();
		node.initialize(DUMMY, text);
		return node;
	}

}
