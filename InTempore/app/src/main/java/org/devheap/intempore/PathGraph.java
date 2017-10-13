package org.devheap.intempore;



// TODO: implement deep cloning
public class PathGraph {
    private ArrayList<Vertex> _vertexList = new ArrayList<Vertex>();
    private EdgeFunction[][] _edgeMatrix;

    //TODO: consider more suitable type for easier value change (e.g. UNIXtime)
    private DateTime _timestamp = null;
    private int[][] _auxiliaryMatrix;
    private final DateTime _initTime = null;
    private final Vertex _startingPoint;

    public PathGraph(RoutePoint[] routePoints, EdgeFunction[][] edgeMatrix, DateTime initTime, RoutePoint startingPoint){
        int count = 0
        _startingPoint = new Vertex(startingPoint, count++);
        for (RoutePoint rp: routePoints){
            _vertexList.add(new Vertex(rp, count++));
        }
        _edgeMatrix = edgeMatrix;
        _initTime = initTime;
        _timestamp = initTime;
    }

    public ArrayList<Vertex> vertices(){
        //TODO: consider making a copy
        return  _vertexList;
    }

    public DateTime timestamp(){
        //TODO: consider making a copy
        return _timestamp;
    }

    public DateTime initTime(){
        return _initTime;
    }

    public Vertex startingPoint(){
        return _startingPoint;
    }

    public void timestamp(DateTime timestamp){
        _timestamp = timestamp;
    }

    public double getEdge(Vertex from, Vertex to){
        return _edgeMatrix[from.ordering()][to.ordering()].calc(_timestamp);
    }
}