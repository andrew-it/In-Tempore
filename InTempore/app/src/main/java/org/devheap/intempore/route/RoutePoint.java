package org.devheap.intempore.route;

import com.google.maps.model.PlaceDetails;

public class RoutePoint {
    private String placeId;
    private PlaceDetails details;
    private WaitTimeFunction function;

    private RoutePoint(String placeId, PlaceDetails details) {
        this.placeId = placeId;
        this.details = details;
    }

    public RoutePoint(String placeId, PlaceDetails details, WaitTimeFunction function) {
        this.placeId = placeId;
        this.details = details;
        this.function = function;
    }

    public static RoutePoint create(String placeId, PlaceDetails details) {
        RoutePoint point = new RoutePoint(placeId, details);
        point.setFunction(WaitTimeFunctionFactory.produceFunction(point));
        return point;
    }

    public String getPlaceId() {
        return placeId;
    }

    public PlaceDetails getDetails() {
        return details;
    }

    public WaitTimeFunction getFunction() {
        return function;
    }

    public void setFunction(WaitTimeFunction function) {
        this.function = function;
    }

}
