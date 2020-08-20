package com.elena.elena.routing;

import com.elena.elena.model.AbstractElenaGraph;
import com.elena.elena.model.AbstractElenaNode;
import com.elena.elena.model.AbstractElenaEdge;
import com.elena.elena.model.AbstractElenaPath;
import com.elena.elena.model.ElenaPath;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Optional;

public class DijkstraRouter extends AbstractRouter {

	private Map<AbstractElenaNode, AbstractElenaNode> nodeAncestor;
	private Map<AbstractElenaNode, Float> nodeTentativeDistance;
	
	private class NodeWrapper {
		
		AbstractElenaNode wrappedNode;
		Float distanceWeight;
		
		// Constructor
		public NodeWrapper(AbstractElenaNode node, Float distanceWeight) {
			this.wrappedNode = node;
			this.distanceWeight = distanceWeight;
		}
	}

	@Override
	public List<AbstractElenaPath> getRoute(AbstractElenaNode from, AbstractElenaNode to, AbstractElenaGraph graph) {

		this.nodeAncestor = new HashMap<>();
		this.nodeTentativeDistance = new HashMap<>();
		
		// Initialize list to record shortest path
		List<AbstractElenaPath> shortestPaths = new ArrayList<>();
		
		// Initialize graph
		this.initializeGraph(graph, from);
		
		// Initialize min-priority queue
		PriorityQueue<NodeWrapper> nodePriorityQueue = new PriorityQueue<>((n1 , n2) -> {
			if(n1.distanceWeight > n2.distanceWeight)
				return 1;
			else if(n1.distanceWeight < n2.distanceWeight)
				return -1;
			else
				return 0;
		});

		nodePriorityQueue.add(new NodeWrapper(from, nodeTentativeDistance.get(from)));

		// Perform Dijkstra algorithm to find shortest path between specified source and destination
		while(!nodePriorityQueue.isEmpty()) {
			AbstractElenaNode candidateNode = nodePriorityQueue.poll().wrappedNode;
			// Check if the shortest path from source to destination has been found
			if(candidateNode == to) {
				// Construct the path from the destination
				AbstractElenaPath shortestPath = new ElenaPath();
				AbstractElenaNode currentNode = candidateNode;
				Optional<AbstractElenaEdge> currentEdge;
				while(this.nodeAncestor.get(currentNode) != null) {
					currentEdge = this.nodeAncestor.get(currentNode).getEdge(currentNode);
					shortestPath.addEdgeToPath(0, currentEdge.get());
					currentNode = this.nodeAncestor.get(currentNode);
				}
				// Return the shortest path
				shortestPaths.add(shortestPath);
				return shortestPaths;
			}
			// Perform relaxation if the shortest path from source to destination hasn't been found
			else {
				Collection<AbstractElenaEdge> edges = candidateNode.getOutGoingEdges();
				for(AbstractElenaEdge edge : edges) {
					AbstractElenaNode node = edge.getDestinationNode();
					this.relaxEdge(candidateNode, node, edge.getEdgeDistance(), nodePriorityQueue);
				}
			}
		}

		return shortestPaths;
	}

	private void initializeGraph(AbstractElenaGraph graph, AbstractElenaNode from) {

		// Iterate through each node in graph to initialize them
		for(AbstractElenaNode node : graph.getAllNodes()) {
			nodeTentativeDistance.put(node, Float.MAX_VALUE);
		}

		// Initialize source node
		nodeTentativeDistance.put(from, 0f);
		this.nodeAncestor.put(from, null);
	}

	private void relaxEdge(AbstractElenaNode in, AbstractElenaNode out, Float weight, PriorityQueue<NodeWrapper> nodePriorityQueue) {

		// Check if we need to relax the distance for the out node
		if(nodeTentativeDistance.get(out) > nodeTentativeDistance.get(in) + weight) {
			// Decrease distance of out node in the min-priority queue
			nodeTentativeDistance.put(out, nodeTentativeDistance.get(in) + weight);
			this.nodeAncestor.put(out, in);
			// Wrap the node to maintain order in min-priority queue
			NodeWrapper wrappedOutNode = new NodeWrapper(out, nodeTentativeDistance.get(out));
			nodePriorityQueue.add(wrappedOutNode);
		}
	}
}