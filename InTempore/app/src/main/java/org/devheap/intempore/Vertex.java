package org.devheap.intempore;

class Vertex{
    private RoutePoint _routePoint;
    private int _key;
    private Vertex _parent = null;
    private final int _orderingNumber;

    public Vertex(RoutePoint routePoint, count){
        _routePoint = routePoint;
        _orderingNumber = count;
    }

    public String id(){
        return _routePoint.getID();
    }

    public double weight(DateTime time){
        // TODO explore the case where time Function does not know a value for this time
        return _routePoint.timeFunction(time);
    }

    public int key(){
        return _key;
    }

    public int ordering(){
        return _orderingNumber;
    }

    public Vertex parent(){
        //TODO implement a copy to avoid unintentional change
        return _parent;
    }

    public void key(int key){
        _key = key;
    }

    public void parent(Vertex parent){
        _parent = parent;
    }

    public void init(){
        //TODO: implement
    }

}