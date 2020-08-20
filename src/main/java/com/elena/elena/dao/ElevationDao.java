package com.elena.elena.dao;

import java.io.Closeable;
import java.util.Collection;
import java.util.Set;

public interface ElevationDao extends Closeable {

    int insert(Set<ElevationData> elevationData);

    int delete(Set<ElevationData> elevationData);

    Collection<ElevationData> get(Set<ElevationData> elevationData);

    int update(Set<ElevationData> elevationData);
}
