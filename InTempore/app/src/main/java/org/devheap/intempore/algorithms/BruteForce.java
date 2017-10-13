package org.devheap.intempore.algorithms;

import org.devheap.intempore.route.RoutePoint;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;

public class BruteForce extends PathAlgorithm {
    public BruteForce(PathGraph graph) {
        _graph = graph;
    }

    protected void _calculate() {
        ArrayList<PathGraph> graphs = new ArrayList<>();
        graphs.add(_graph);
        for (int i = 1; i < 24; i++) {
            graphs.add(
                    new PathGraph(
                            _graph.matrix(), _graph.initTime().plus(Duration.standardHours(i))));
        }

        ArrayList<ArrayList<Integer>> permutations = _permute();
        ArrayList<Vertex> vertices = _graph.vertices();
        Duration currentMinDuration = Duration.standardDays(365);
        ArrayList<Integer> currentBestPath = null;
        DateTime currentOptimalDepartureTime = null;

        for (PathGraph graph : graphs) {
            for (ArrayList<Integer> l : permutations) {
                Duration totalDuration = new Duration(0);
                _graph.timestamp(_graph.initTime());
                for (int i = 0; i < l.size() - 1; i++) {
                    //add travel time
                    totalDuration = totalDuration.plus(
                            _graph.getEdge(vertices.get(l.get(i)), vertices.get(l.get(i + 1))));

                    _graph.timestamp(
                            _graph.timestamp().plus(
                                    _graph.getEdge(
                                            vertices.get(l.get(i)), vertices.get(l.get(i + 1)))));


                    //add waiting time
                    totalDuration = totalDuration.plus(
                            vertices.get(l.get(i + 1)).weight(
                                    new Duration(_graph.initTime(), _graph.timestamp())));

                    _graph.timestamp(
                            _graph.timestamp().plus(
                                    vertices.get(l.get(i + 1)).weight(
                                            new Duration(_graph.initTime(), _graph.timestamp()))));
                }
                if (totalDuration.compareTo(currentMinDuration) < 0) {
                    currentMinDuration = totalDuration;
                    currentBestPath = l;
                    currentOptimalDepartureTime = _graph.initTime();
                }
            }
        }

        ArrayList<RoutePoint> route = new ArrayList<>();
        for (Integer i : currentBestPath) {
            _pathData.pathSequence.add(vertices.get(i).point());
        }
        _pathData.startingTime = currentOptimalDepartureTime;
        _pathData.tripDuration = currentMinDuration;
        //calculate the path length
        //compare to current minima
        //construct a PathData instance
    }


    private ArrayList<ArrayList<Integer>> _permute() {

        ArrayList<Integer> vertices = new ArrayList<>();
        for (int i = 1; i < _graph.vertices().size(); i++) {
            vertices.add(new Integer(i));
        }

        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        //main part
        result.add(new ArrayList<Integer>());

        for (int i = 0; i < vertices.size(); i++) {
            //list of list in current iteration of the array num
            ArrayList<ArrayList<Integer>> current = new ArrayList<ArrayList<Integer>>();

            for (ArrayList<Integer> l : result) {
                // # of locations to insert is largest index + 1
                for (int j = 0; j < l.size() + 1; j++) {
                    // + add num[i] to different locations
                    l.add(j, vertices.get(i));

                    ArrayList<Integer> temp = new ArrayList<Integer>(l);
                    current.add(temp);

                    l.remove(j);
                }
            }

            result = new ArrayList<ArrayList<Integer>>(current);
        }

        for (ArrayList<Integer> l : result) {
            l.add(0, 0);
        }

        return result;
    }
}