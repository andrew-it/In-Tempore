package org.devheap.intempore.algorithms;

import org.devheap.intempore.route.RoutePoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;

public abstract class PathAlgorithm{

    protected PathGraph _graph = null;
    protected PathData _pathData = null;

    public static class PathData{
        public ArrayList<RoutePoint> pathSequence = new ArrayList<RoutePoint>();
        public DateTime startingTime = null;
        public Duration tripDuration = null;
    }

    protected abstract void _calculate();

    public PathData pathData(){
        if (_pathData==null){
            _pathData = new PathData();
            _calculate();
        }
        return _pathData;
    }
}