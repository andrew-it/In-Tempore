package org.devheap.intempore;

public abstract class PathAlgorithm{

    protected PathGraph _graph = null;
    protected PathData _pathData = null;

    public static class PathData{
        public ArrayList<RoutePoint> pathSequence = new ArrayList<RoutePoint>();
        public DateTime startingTime = null;
        public TimeDelta tripDuration = null; // TODO: move to Joda Time
    }

    protected abstract void _calculate();

    public PathData pathData(){
        if (_pathData==null){
            _calculate();
        }
        return _pathData;
    }
}