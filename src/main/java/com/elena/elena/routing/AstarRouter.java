package com.elena.elena.routing;

import com.elena.elena.model.AbstractElenaGraph;
import com.elena.elena.model.AbstractElenaNode;
import com.elena.elena.model.AbstractElenaEdge;
import com.elena.elena.model.AbstractElenaPath;
import com.elena.elena.model.ElenaPath;
import com.elena.elena.util.ElenaUtils;
import com.elena.elena.util.Units;

import java.util.*;

public class AstarRouter extends AbstractRouter{

	private Map<AbstractElenaNode, Float> gScores; // Distance between a node and origin
	private Optional<Set<AbstractElenaEdge>> excludedEdges;

	protected AstarRouter(Set<AbstractElenaEdge> excludedEdges){

		if(excludedEdges != null && !excludedEdges.isEmpty()) {
			this.excludedEdges = Optional.of(excludedEdges);
		}
		else{
			this.excludedEdges = Optional.empty();
		}
	}

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

		Map<AbstractElenaNode, AbstractElenaNode> nodeAncestor = new HashMap<>();
		this.gScores = new HashMap<>();
		
		// Initialize list to record shortest path
		List<AbstractElenaPath> shortestPaths = new ArrayList<>();

		// Initialize graph
		this.gScores.put(from, 0f);

		// Initialize open list
		PriorityQueue<NodeWrapper> nodePriorityQueue = new PriorityQueue<>((n1 , n2) -> {
			if(n1.distanceWeight > n2.distanceWeight)
				return 1;
			else if(n1.distanceWeight < n2.distanceWeight)
				return -1;
			else
				return 0;
		});

		nodePriorityQueue.add(new NodeWrapper(from, this.getFscore(from, to)));

		while(!nodePriorityQueue.isEmpty()){
			AbstractElenaNode candidateNode = nodePriorityQueue.poll().wrappedNode;

			// Check if the shortest path from source to destination has been found
				if (candidateNode == to) {
					// Construct the path from the destination
					AbstractElenaPath shortestPath = new ElenaPath();
					AbstractElenaNode currentNode = candidateNode;
					Optional<AbstractElenaEdge> currentEdge;
					nodeAncestor.put(from, null);

					while (nodeAncestor.get(currentNode) != null) {
						currentEdge = nodeAncestor.get(currentNode).getEdge(currentNode);
						shortestPath.addEdgeToPath(0, currentEdge.get());
						currentNode = nodeAncestor.get(currentNode);
					}

					// Return the shortest path
					shortestPaths.add(shortestPath);
					return shortestPaths;
				}

				// Perform relaxation if the shortest path from source to destination hasn't been found
				else {
					Collection<AbstractElenaEdge> edges = candidateNode.getOutGoingEdges();

					for (AbstractElenaEdge edge : edges) {
						if (excludedEdges.isEmpty() || !excludedEdges.get().contains(edge)) {
							AbstractElenaNode targetNode = edge.getDestinationNode();
							if (this.gScores.getOrDefault(targetNode, Float.MAX_VALUE) > edge.getEdgeDistance() + this.gScores.get(candidateNode)) {
								this.gScores.put(targetNode, edge.getEdgeDistance() + this.gScores.get(candidateNode));
								nodePriorityQueue.add(new NodeWrapper(targetNode, this.getFscore(targetNode, to)));
								nodeAncestor.put(targetNode, candidateNode);
							}
						}
					}
				}
			}

		return shortestPaths;
	}

	private float getFscore(AbstractElenaNode origin, AbstractElenaNode destination){

		return this.gScores.get(origin) + ElenaUtils.getDistance(origin, destination, Units.METRIC);
	}
}
