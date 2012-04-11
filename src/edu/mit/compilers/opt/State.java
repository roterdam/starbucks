package edu.mit.compilers.opt;

public interface State<T> {

	/**
	 * The initial state in the analysis. Note that this can either be at the
	 * start of the program or the end, depending on whether it's forward or
	 * backwards.
	 */
	public T getInitialState();
	
	public T getBottomState();

	public T join(T s);
		
}
