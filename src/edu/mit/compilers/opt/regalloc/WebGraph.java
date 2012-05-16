package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebGraph {

	private Map<Web, List<Web>> graphData;

	public WebGraph(List<Web> webs) {
		graphData = new HashMap<Web, List<Web>>();
		for (Web web : webs) {
			graphData.put(web, new ArrayList<Web>(web.getInterferences()));
		}
	}

	public Set<Web> getVertices() {
		return graphData.keySet();
	}

	public void insertVertex(Web newWeb) {
		// Can't assume all interfering webs are in the graph.
		List<Web> neighbors = new ArrayList<Web>();
		for (Web potentialNeighbor : newWeb.getInterferences()) {
			List<Web> neighborNeighbors = graphData.get(potentialNeighbor);
			if (neighborNeighbors != null) {
				neighborNeighbors.add(newWeb);
				neighbors.add(potentialNeighbor);
			}
		}
		graphData.put(newWeb, neighbors);
	}

	public Web removeVertex(Web web) {
		for (Web neighbor : graphData.get(web)) {
			graphData.get(neighbor).remove(web);
		}
		graphData.remove(web);
		return web;
	}

	public Web removeMostConstrainedVertex() {
		assert !graphData.isEmpty();
		Web removed = null;
		int maxDegree = -1;
		for (Web vertex : graphData.keySet()) {
			int n = getDegree(vertex);
			if (n > maxDegree) {
				maxDegree = n;
				removed = vertex;
			}
		}
		return removeVertex(removed);
	}

	public int getDegree(Web web) {
		return graphData.get(web).size();
	}

	public boolean isEmpty() {
		return graphData.size() == 0;
	}

	public List<Web> getNeighbors(Web poppedWeb) {
		return graphData.get(poppedWeb);
	}

}
