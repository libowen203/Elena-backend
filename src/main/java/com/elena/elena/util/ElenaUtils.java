package com.elena.elena.util;

import com.elena.elena.model.AbstractElenaNode;
import com.elena.elena.model.AbstractElenaPath;
import com.elena.elena.routing.ElevationMode;
import com.elena.elena.routing.WeightType;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class ElenaUtils {

    public static InputStream getFileAsInputStream(String fileName) {
        InputStream res = ElenaUtils.class.getResourceAsStream("/" + fileName);

        return res;
    }

    public static AbstractElenaPath selectPath(ElevationMode mode, List<AbstractElenaPath> paths, int percentage){

        float margin = paths.get(0).getPathWeights().get(WeightType.DISTANCE) * percentage / 100;
        AbstractElenaPath selectedPath = paths.get(0);

        for(AbstractElenaPath path : paths){

            float pathDistance = path.getPathWeights().get(WeightType.DISTANCE);

            if(pathDistance <= margin && compareElevation(path, selectedPath, mode)){
                selectedPath = path;
            }
        }

        return selectedPath;
    }

    public static boolean compareElevation(AbstractElenaPath firstPath, AbstractElenaPath secondPath, ElevationMode mode){

        switch (mode){
            case MAX:
                return firstPath.getPathWeights().get(WeightType.ELEVATION) > secondPath.getPathWeights().get(WeightType.ELEVATION);
            default:
                return firstPath.getPathWeights().get(WeightType.ELEVATION) < secondPath.getPathWeights().get(WeightType.ELEVATION);
        }
    }


    public static Optional<URI> getURL(String hostName, String searchPath, String scheme, NameValuePair... searchParameters){

        URI uri = null;
        try {
            uri = new URIBuilder()
                    .setScheme(scheme)
                    .setHost(hostName)
                    .setPath("/" + searchPath)
                    .setParameters(searchParameters).build();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Optional<URI> optionalURI = Optional.ofNullable(uri);

        return optionalURI;
    }

    /**
     * This method uses haversine formula to approximate a straight line distance between two nodes.
     * Based on provided type of unit, it either returns distance in feet or meters.
     */
    public static float getDistance(float sourceLat, float sourceLon, float targetLat, float targetLon,  Units unit) {

        final double R = 6371000; //Earth radius in meters
        sourceLat =  (float) Math.toRadians(sourceLat);
        sourceLon =  (float) Math.toRadians(sourceLon);
        targetLat =  (float) Math.toRadians(targetLat);
        targetLon =  (float) Math.toRadians(targetLon);

        float latDiff = targetLat - sourceLat;
        float lonDiff = targetLon - sourceLon;

        double a = Math.pow(Math.sin(latDiff / 2) , 2) + Math.pow(Math.sin(lonDiff / 2) , 2) * Math.cos(sourceLat) * Math.cos(targetLat);
        double distance = 2 * R * Math.asin(Math.sqrt(a));

        switch (unit){
            case US:
                return (float) (distance * 3.28084);

            default:
                return (float) distance;
        }
    }

    public static float getDistance(AbstractElenaNode source, AbstractElenaNode target, Units unit){
        float targetLat = Float.parseFloat(target.getLatitude());
        float targetLon = Float.parseFloat(target.getLongitude());
        float sourceLat = Float.parseFloat(source.getLatitude());
        float sourceLon = Float.parseFloat(source.getLongitude());

        return getDistance(sourceLat, sourceLon, targetLat, targetLon, unit);
    }

}