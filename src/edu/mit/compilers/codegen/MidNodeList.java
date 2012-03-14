package edu.mit.compilers.codegen;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;

public class MidNodeList implements List<MidNode> {
	MidNode head;
	MidNode tail;
	int size;

	public boolean add(MidNode object) {
		if (tail == null) {
			head = object;
			tail = head;
		} else {
			tail.setNextNode(object);
			tail = object;
		}
		size++;
		return true;
	}

	public boolean addAll(MidNodeList list) {
		if (this.isEmpty()) {
			head = list.getHead();
			tail = list.getTail();
			size = list.size();
		} else if (list.isEmpty()) {
			// do nothing.
		} else {
			tail.setNextNode(list.getHead());
			tail = list.getTail();
			size += list.size();
		}
		return true;
	}

	public void clear() {
		head = null;
		tail = null;
		size = 0;
	}

	public boolean contains(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public MidNode get(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public int indexOf(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Iterator<MidNode> iterator() {
		return new LinkedListIterator();
	}

	public int lastIndexOf(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public ListIterator<MidNode> listIterator() {
		throw new java.lang.UnsupportedOperationException();
	}

	public ListIterator<MidNode> listIterator(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public MidNode remove(int location) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean remove(Object object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	public MidNode set(int location, MidNode object) {
		throw new java.lang.UnsupportedOperationException();
	}

	public int size() {
		return size;
	}

	public List<MidNode> subList(int start, int end) {
		throw new java.lang.UnsupportedOperationException();
	}

	public Object[] toArray() {
		throw new java.lang.UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] array) {
		throw new java.lang.UnsupportedOperationException();
	}

	public MidNode getHead() {
		return head;
	}

	public MidNode getTail() {
		return tail;
	}

	private class LinkedListIterator implements Iterator<MidNode> {
		MidNode currentNode;

		public LinkedListIterator() {
			currentNode = new MidNode();
			currentNode.setNextNode(MidNodeList.this.getHead());
		}

		public boolean hasNext() {
			return currentNode.getNextNode() != null;
		}

		public MidNode next() {
			currentNode = currentNode.getNextNode();
			return currentNode;
		}

		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}

	}

	@Override
	public void add(int arg0, MidNode arg1) {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends MidNode> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends MidNode> arg1) {
		throw new java.lang.UnsupportedOperationException();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		String prefix = "";
		for (MidNode m : this) {
			sb.append(prefix);
			sb.append(m.toString());
			prefix = " -> ";
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String getUniqueName(String rootName, int i) {
		return rootName + i;
	}
	
	/**
	 * Only returns the relevant part of the graph, not the entire dot file.
	 */
	public String toDotSyntax(String rootName) {
		StringBuilder out = new StringBuilder();

		MidNode head = getHead();
		String prefix = rootName +  " -> ";
		for (int i = 0; i < size(); i++) {
			out.append(prefix);
			out.append(getUniqueName(rootName, i) + ";\n");
			out.append(getUniqueName(rootName, i) + "[label=\"" + head.toString() + "\"];\n");
			head = head.getNextNode();
			prefix = getUniqueName(rootName, i) + " -> ";
		}
		
		return out.toString();
	}
	
	public MidSaveNode getSaveNode() {
		assert getTail() instanceof MidSaveNode;
		return (MidSaveNode) getTail();
	}
	
}
