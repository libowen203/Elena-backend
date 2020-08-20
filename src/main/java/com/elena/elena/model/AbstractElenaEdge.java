package com.elena.elena.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This AbstractEdge class represents a uni-directional edge
 */
public abstract class AbstractElenaEdge {

    public abstract String getId();

    public abstract float getEdgeDistance();
    
    public abstract void setEdgeDistance(float distance);

    public abstract float getEdgeElevation();

    public abstract AbstractElenaNode getOriginNode();

    public abstract AbstractElenaNode getDestinationNode();

    public abstract Map<String, String> getProperties();

    public List<AbstractElenaNode> getNodes(){
        List<AbstractElenaNode> nodes = new ArrayList<>();
        nodes.add(this.getOriginNode());
        nodes.add(this.getDestinationNode());
        return nodes;
    }

}
