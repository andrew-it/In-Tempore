package org.devheap.intempore.algorithms;

import org.devheap.intempore.route.DistanceGraph;
import org.devheap.intempore.route.RoutePoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.ArrayList;

// TODO: implement deep cloning?
public class PathGraph {
    private final DateTime _initTime;
    private ArrayList<Vertex> _vertexList = new ArrayList<Vertex>();
    private DistanceGraph _edgeMatrix;
    private Vertex _initial;
    private DateTime _timestamp = DateTime.now().plusHours(2);
    private int[][] _auxiliaryMatrix;

    public PathGraph(DistanceGraph edgeMatrix, DateTime initTime, RoutePoint initial) {
        _initial = new Vertex(initial);
        _vertexList.add(_initial);
        for (RoutePoint rp : edgeMatrix.getRoutePoints()) {
            if (rp.equals(initial)) {
                continue;
            }
            _vertexList.add(new Vertex(rp));
        }
        _edgeMatrix = edgeMatrix;
        _initTime = initTime;
        _timestamp = initTime;
        _auxiliaryMatrix = new int[_vertexList.size()][_vertexList.size()];
    }

    public Vertex initial(){
        return _initial;
    }

    public DistanceGraph matrix() {
        return _edgeMatrix;
    }

    public ArrayList<Vertex> vertices() {
        return _vertexList;
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
        int hours = new Interval(_initTime, _timestamp).toDuration().toStandardHours()
                .getHours() % (24);
        return Duration.standardSeconds(_edgeMatrix.getDuration(
                hours, from.id(), to.id()).inSeconds);
    }
}