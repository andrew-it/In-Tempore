package org.devheap.intempore.algorithms;

import org.devheap.intempore.route.DistanceGraph;
import org.devheap.intempore.route.RoutePoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;

// TODO: implement deep cloning
public class PathGraph {
    private final DateTime _initTime;
    private ArrayList<Vertex> _vertexList = new ArrayList<Vertex>();
    private DistanceGraph _edgeMatrix;
    private DateTime _timestamp = null;
    private int[][] _auxiliaryMatrix;

    public PathGraph(DistanceGraph edgeMatrix, DateTime initTime) {
        int count = 0;
        for (RoutePoint rp : edgeMatrix.getRoutePoints()) {
            _vertexList.add(new Vertex(rp, count++));
        }
        _edgeMatrix = edgeMatrix;
        _initTime = initTime;
        _timestamp = initTime;
        _auxiliaryMatrix = new int[_vertexList.size()][_vertexList.size()];
    }

    public DistanceGraph matrix() {
        return _edgeMatrix;
    }

    public ArrayList<Vertex> vertices() {
        return (ArrayList<Vertex>) _vertexList.clone();
    }

    public DateTime timestamp() {
        return _timestamp;
    }

    public DateTime initTime() {
        return _initTime;
    }

    public Vertex startingPoint() {
        return _vertexList.get(0);
    }

    public void timestamp(DateTime timestamp) {
        _timestamp = timestamp;
    }

    public Duration getEdge(Vertex from, Vertex to) {
        int minutes = new Interval(_timestamp, _initTime).toDuration().toStandardMinutes()
                .getMinutes() % (24 * 60);
        return Duration.standardSeconds(_edgeMatrix.getDuration(
                minutes, from.id(), to.id()).inSeconds);
    }
}