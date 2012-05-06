package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.Reg;

public class GraphColorer {

	private final Set<Reg> usableRegisters;
	private Map<Web, Reg> out;

	public GraphColorer(Reg[] usableRegisters) {
		this.usableRegisters = new LinkedHashSet<Reg>(
				Arrays.asList(usableRegisters));
	}

	public Map<Web, Reg> color(List<Web> webs) {
		out = new HashMap<Web, Reg>();
		// Initialize all webs to no register at first.
		for (Web web : webs) {
			out.put(web, null);
		}
		// An approximation to NP-complete coloring with usableRegisters.
		WebGraph graph = new WebGraph(webs);
		Stack<Web> workingStack = new Stack<Web>();
		int N = usableRegisters.size();
		while (!graph.isEmpty()) {
			// Duplicate list so as to avoid concurrent modification issues.
			for (Web web : new ArrayList<Web>(graph.getVertices())) {
				// Remove nodes with degree < N.
				if (graph.getDegree(web) < N) {
					// Push removed nodes onto stack.
					workingStack.push(web);
					graph.removeVertex(web);
				}
			}
			if (graph.isEmpty()) {
				break;
			}
			// Remove a remaining nodes with degree >= N. (We are spilling it by
			// not giving it a register.)
			Web removed = graph.removeAnyVertex();
			LogCenter.debug("RA", "Could not color all nodes, spilling "
					+ removed);
		}
		while (!workingStack.isEmpty()) {
			// When all nodes are removed, start to color.
			// Pop a node from the stack back and pick a valid color.
			Web poppedWeb = workingStack.pop();
			Set<Reg> validColors = new HashSet<Reg>(usableRegisters);
			graph.insertVertex(poppedWeb);
			for (Web neighbor : graph.getNeighbors(poppedWeb)) {
				validColors.remove(getColor(neighbor));
			}
			assert validColors.size() > 0 : "Should be colorable since we removed degrees >= N.";
			assignColor(poppedWeb, validColors.iterator().next());
		}
		return out;
	}

	private void assignColor(Web web, Reg color) {
		assert out.get(web) == null : "Web was already colored!";
		out.put(web, color);
	}

	private Reg getColor(Web web) {
		return out.get(web);
	}

}
