package com.elena.elena.model;

import com.elena.elena.routing.WeightType;

import java.util.List;
import java.util.Map;

public abstract class AbstractElenaPath{

    public abstract List<AbstractElenaEdge> getEdgesInPath();

    public abstract Map<WeightType, Float> getPathWeights();

    public abstract void addEdgeToPath(int position, AbstractElenaEdge edge);
}