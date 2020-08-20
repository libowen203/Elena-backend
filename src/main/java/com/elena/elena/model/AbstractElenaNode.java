package com.elena.elena.model;


import lombok.Setter;

import java.util.Collection;
import java.util.Optional;


public abstract class AbstractElenaNode {


    @Setter protected Float elevationWeight = 0f;

    public abstract String getId();

    public abstract Float getElevationWeight();

    public abstract Collection<AbstractElenaNode> getNeighbors();

    public abstract Collection<AbstractElenaEdge> getOutGoingEdges();

    public abstract Collection<AbstractElenaEdge> getInComingEdges();

    public abstract String getLatitude();

    public abstract String getLongitude();

    public abstract Optional<AbstractElenaEdge> getEdge(AbstractElenaNode destinationNode);

}


