package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.FillerMidNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class MidNodeList implements List<MidNode> {

	private MidNode head;
	private MidNode tail;
	int size;

	public MidNodeList() {
		size = 0;
		add(new FillerMidNode());
	}

	/**
	 * Please do not add a node twice to the list. Breaks will shit.
	 */
	public boolean add(MidNode object) {
		assert object != null : "Don't add null to the list!";
		// this is not an all encompassing assert
		assert object.getNextNode() == null : "Don't add things that are already in lists: "
				+ object.toString()
				+ "\nalready has next="
				+ object.getNextNode().toString()
				+ "\n(list:"
				+ toString()
				+ ")";
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
		if (list == null) {
			return false;
		} else if (this.isEmpty()) { // never happens
			assert false;
			head = list.getHead();
			tail = list.getTail();
			size = list.size();
		} else if (list.isEmpty()) { // never happens
			assert false;
		} else {
			//assert list.getHead() instanceof FillerMidNode;
			//tail.setNextNode(list.getHead().getNextNode());
			//tail = list.getTail();
			//size += list.size() - 1;

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

	@Override
	public int size() {
		return size;
	}

	public int instructionSize() {
		return size - 1;
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

		class LinkedListHeadNode extends MidNode {
		}

		public LinkedListIterator() {
			currentNode = new LinkedListHeadNode();
			currentNode.setNextNode(MidNodeList.this.getHead());
		}

		@Override
		public boolean hasNext() {
			return currentNode.getNextNode() != null;
		}

		@Override
		public MidNode next() {
			currentNode = currentNode.getNextNode();
			return currentNode;
		}

		@Override
		public void remove() {
			throw new java.lang.UnsupportedOperationException();
		}

	}

	public void add(int arg0, MidNode arg1) {
		throw new java.lang.UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends MidNode> arg0) {
		throw new java.lang.UnsupportedOperationException();
	}

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

	/**
	 * Only returns the relevant part of the graph, not the entire dot file.
	 */
	public String toDotSyntax(String rootName) {
		StringBuilder out = new StringBuilder();

		if (isEmpty()) {
			return rootName + " -> " + hashCode() + ";\n" + hashCode()
					+ " [label=\"EMPTY\"];\n";
		}

		String previousNode = rootName;
		// for (int i = 0; i < size(); i++) {
		for (MidNode node : this) {
			assert node != null : "Why you got null.. iterating over " + size()
					+ " elements.";
			out.append(node.toDotSyntax());
			out.append(previousNode + " -> " + node.hashCode()
					+ " [color=blue];\n");
			previousNode = Integer.toString(node.hashCode());
		}

		return out.toString();
	}

	public MidMemoryNode getMemoryNode() {
		assert getTail() instanceof MidSaveNode : toString();
		return ((MidSaveNode) getTail()).getDestinationNode();
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		for (MidNode node : this) {
			out.addAll(node.toASM());
		}

		return out;
	}

}
