package org.devheap.intempore.algorithms;

import org.devheap.intempore.route.RoutePoint;
import org.joda.time.Duration;

class Vertex {
    private RoutePoint _routePoint;
    private int _key;
    private Vertex _parent = null;

    public Vertex(RoutePoint routePoint) {
        _routePoint = routePoint;
    }

    public String id() {
        return _routePoint.getPlaceId();
    }

    public Duration weight(Duration duration) {
        return Duration.standardMinutes(
                _routePoint.getFunction().wait_time(
                        duration.toStandardMinutes().getMinutes() % (24 * 60)));
    }

    public int key() {
        return _key;
    }

    public RoutePoint point() {
        return _routePoint;
    }

    public Vertex parent() {
        return _parent;
    }

    public void key(int key) {
        _key = key;
    }

    public void parent(Vertex parent) {
        _parent = parent;
    }

    public void init() {
        //TODO: implement
    }

}