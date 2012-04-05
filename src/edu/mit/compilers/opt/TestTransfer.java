package edu.mit.compilers.opt;

public class TestTransfer implements Transfer<TestState> {

	public TestState apply(Block b, TestState s) {
		return s;
	}

}
