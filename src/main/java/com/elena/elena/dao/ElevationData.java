package com.elena.elena.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class ElevationData {

    @Setter @Getter private String id;
    @Getter private String latitude;
    @Getter private String longitude;
    @Setter @Getter private float elevation;

    public ElevationData(@NonNull String id,@NonNull String latitude, @NonNull String longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ElevationData(@NonNull String id, float elevation){
        this.id = id;
        this.elevation = elevation;
    }

    @Override
    public boolean equals(Object o) {

        if(o == this){
            return true;
        }
        ElevationData data = (ElevationData) o;
        return this.id.equals(data.id);
    }

    @Override
    public int hashCode(){
        return this.id.hashCode();
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(id).append(",").append(elevation)
                .append(")");
        return stringBuilder.toString();
    }
}
