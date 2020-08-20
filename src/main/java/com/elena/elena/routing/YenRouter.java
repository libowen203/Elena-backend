package com.elena.elena.routing;

import com.elena.elena.model.AbstractElenaGraph;
import com.elena.elena.model.AbstractElenaNode;
import com.elena.elena.model.AbstractElenaPath;
import com.elena.elena.model.AbstractElenaEdge;
import com.elena.elena.model.ElenaPath;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

public class YenRouter extends AbstractRouter{
	
	private int numOfRoute;
	private AbstractRouter router;
	private List<AbstractElenaPath> shortestPaths= new ArrayList<>();
	private Map<AbstractElenaEdge, Float> restoreMap = new HashMap<>();
	
	// Constructor
	protected YenRouter(int numOfRoute, AbstractRouter router) {
		
		// Set number of routes that Yen's needs to return
		this.numOfRoute = numOfRoute;
		this.router = router;
	}
	
    @Override
    public List<AbstractElenaPath> getRoute(AbstractElenaNode originNodeId, AbstractElenaNode destinationNodeId, AbstractElenaGraph graph) {
        
    	// Get the shortest path at first
    	this.shortestPaths.add(this.router.getRoute(originNodeId, destinationNodeId, graph).get(0));

    	PriorityQueue<AbstractElenaPath> pathPriorityQueue = new PriorityQueue<>((p1, p2)->{
			if(p1.getPathWeights().get(WeightType.DISTANCE) > p2.getPathWeights().get(WeightType.DISTANCE))
				return 1;
			else if(p1.getPathWeights().get(WeightType.DISTANCE) < p2.getPathWeights().get(WeightType.DISTANCE))
				return -1;
			else
				return 0;
		});
    	
    	// Compute top k shortest path with Yen's algorithm
    	for(int k = 1; k < this.numOfRoute; k++) {
    		int previousPathNodeNum = this.shortestPaths.get(k-1).getEdgesInPath().size() + 1;
    		for(int i = 0; i < previousPathNodeNum-1; i++) {
    			AbstractElenaNode spurNode = this.shortestPaths.get(k-1).getEdgesInPath().get(i).getOriginNode();
    			List<AbstractElenaEdge> rootPath = (i==0) ? new ArrayList<>() : this.shortestPaths.get(k-1).getEdgesInPath().subList(0, i);
    			// Remove edge from the graph if root path has appeared in previous shortest path
    			for(AbstractElenaPath path : this.shortestPaths) {
    				List<AbstractElenaEdge> previousPath = path.getEdgesInPath();
    				this.removeEdgeIfRootAppear(rootPath, previousPath);
    			}
    			// Compute spur path
    			List<AbstractElenaPath> spurPaths = this.router.getRoute(spurNode, destinationNodeId, graph);
    			List<AbstractElenaEdge> spurPath = (spurPaths.isEmpty()) ? new ArrayList<>() : spurPaths.get(0).getEdgesInPath();
    			// Combine root path and spur path when spur path exists
    			if(!spurPath.isEmpty()) {
    				AbstractElenaPath totalPath = new ElenaPath();
        			for(int j = 0; j < rootPath.size(); j++)
        				totalPath.addEdgeToPath(totalPath.getEdgesInPath().size(), rootPath.get(j));
        			for(int j = 0; j < spurPath.size(); j++)
        				totalPath.addEdgeToPath(totalPath.getEdgesInPath().size(), spurPath.get(j));
        			// Add total path with ith node as spur node into priority queue to be a candidate path
        			pathPriorityQueue.add(totalPath);
    			}
    			// Restore edges
    			this.restoreEdges();
    		}
    		// Check if there is a candidate path
			if(pathPriorityQueue.isEmpty())
				break;
			else {
				shortestPaths.add(pathPriorityQueue.poll());
				pathPriorityQueue.clear();
			}
    	}
    	
    	return this.shortestPaths;
    }
    
    // Remove edge from the graph temporarily if root path has appeared in previous shortest path
    public void removeEdgeIfRootAppear(List<AbstractElenaEdge> rootPath, List<AbstractElenaEdge> previousPath) {
    	
    	// Check if root path has appeared in previous shortest path
    	boolean appearance = true;
    	if(rootPath.size() < previousPath.size()) {
    		for(int i = 0; i < rootPath.size(); i++) {
    			if(rootPath.get(i).getId() != previousPath.get(i).getId()) {
    				appearance = false;
    				break;
    			}
    		}
    		if(appearance) {
    			AbstractElenaEdge edge = previousPath.get(rootPath.size());
    			// Avoid storing distance of the same edge twice
				this.restoreMap.putIfAbsent(edge, edge.getEdgeDistance());
    			edge.setEdgeDistance(Float.MAX_VALUE);
    		}
    	}
    }
    
    // Restore edge in the graph after each iteration
    public void restoreEdges() {
    	
    	for(AbstractElenaEdge edge : this.restoreMap.keySet()) {
    		edge.setEdgeDistance(this.restoreMap.get(edge));
    	}
    	this.restoreMap.clear();
    }
}