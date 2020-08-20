package com.elena.elena.routing;

import com.elena.elena.model.*;

import java.util.*;

public class MultiRoutesAstarRouter extends AbstractRouter{

    private int percentage;

    protected MultiRoutesAstarRouter(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public List<AbstractElenaPath> getRoute(AbstractElenaNode originNode, AbstractElenaNode destinationNode, AbstractElenaGraph graph) {

        List<AbstractElenaPath> paths = new ArrayList<>();
        AbstractRouter router = new AstarRouter(null);
        AbstractElenaPath shortestPath = router.getRoute(originNode, destinationNode, graph).get(0);
        paths.add(shortestPath);
        Set<AbstractElenaEdge> excludedEdges = new HashSet<>();
        populateExcludedEdges(shortestPath, excludedEdges, this.getAllowedSize(shortestPath.getEdgesInPath().size()));
        router = new AstarRouter(excludedEdges);
        
        for(int i = 0; i < 10; i++){
            List<AbstractElenaPath> path = router.getRoute(originNode, destinationNode, graph);
            if(path.isEmpty()){
                break;
            }
            shortestPath = path.get(0);
            paths.add(shortestPath);
            populateExcludedEdges(path.get(0), excludedEdges, this.getAllowedSize(shortestPath.getEdgesInPath().size()));
        }

        return paths;
    }

    /**
     * Given a baseline path. This method inserts edges between a range in the path into an excluded
     * set. The range is determined by allowed number of edges starting from two ends (origin, destination).
     * For example, if a path has 5 edges and allowed size is 1, the third edge in the path will be added
     * to the excluded set.
     * @param path baseline path
     * @param excludedEdges a set of edges that we exclude when building alternative paths
     * @param allowedSize number of edges from the two ends (origin, destination) that are allowed
     */
    private void populateExcludedEdges(AbstractElenaPath path, Set<AbstractElenaEdge> excludedEdges, int allowedSize){

        for(int i = allowedSize + 1; i < path.getEdgesInPath().size() - allowedSize - 1; i++){
            excludedEdges.add(path.getEdgesInPath().get(i));
        }
    }

    private int getAllowedSize(int pathSize){

        double maxExlucdedEdgeSize = pathSize - 2;
        return (int) Math.ceil(maxExlucdedEdgeSize * 0.15);
    }
}
