package org.devheap.intempore.algorithms;

import android.util.Pair;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;

import org.devheap.intempore.route.DistanceGraph;
import org.devheap.intempore.route.RoutePoint;
import org.devheap.intempore.route.WaitTimeFunction;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nimfetrisa on 10/13/17.
 */
public class BruteForceTest {
    @Test
    public void _calculate() throws Exception {
        BruteForce alg = new BruteForce(new PathGraph(new Matrix(), DateTime.now().minusHours(2)));
        PathAlgorithm.PathData data = alg.pathData();
        if (org.joda.time.Duration.standardMinutes(51).isShorterThan(data.tripDuration)) {
            throw new RuntimeException("Not optimal");
        }
    }


    static class Matrix extends DistanceGraph {
        Matrix() {
            super(null);
            points = new LinkedList<RoutePoint>();
            points.add(new RoutePoint("A", null, new WaitTimeFunction() {
                @Override
                public int wait_time(int minute_of_day) {
                    return 0;
                }
            }));
            points.add(new RoutePoint("B", null, new WaitTimeFunction() {
                @Override
                public int wait_time(int minute_of_day) {
                    return 16;
                }
            }));
            points.add(new RoutePoint("C", null, new WaitTimeFunction() {
                @Override
                public int wait_time(int minute_of_day) {
                    return 7;
                }
            }));
            points.add(new RoutePoint("D", null, new WaitTimeFunction() {
                @Override
                public int wait_time(int minute_of_day) {
                    return 4;
                }
            }));

            for(RoutePoint from: points) {
                ConcurrentHashMap<String, ArrayList<Pair<Distance, Duration>>> to_map = new ConcurrentHashMap<>();
                for(RoutePoint to: points) {
                    ArrayList<Pair<Distance, Duration>> hours = new ArrayList<>();
                    for(int i = 0; i < 24; i++) {
                        hours.add(Pair.create((Distance)null, (Duration)null));
                    }
                    to_map.put(to.getPlaceId(), hours);
                }
                matrix.put(from.getPlaceId(), to_map);
            }

            for (int i = 0; i < 24; i++) {
                setDistanceDurationPair(i, "A", "B", Pair.create(DistanceT.create(42), DurationT.create(21)));
                setDistanceDurationPair(i, "B", "A", Pair.create(DistanceT.create(42), DurationT.create(13)));
                setDistanceDurationPair(i, "A", "C", Pair.create(DistanceT.create(42), DurationT.create(13)));
                setDistanceDurationPair(i, "C", "A", Pair.create(DistanceT.create(42), DurationT.create(12)));
                setDistanceDurationPair(i, "A", "D", Pair.create(DistanceT.create(42), DurationT.create(3)));
                setDistanceDurationPair(i, "D", "A", Pair.create(DistanceT.create(42), DurationT.create(3)));
                setDistanceDurationPair(i, "B", "C", Pair.create(DistanceT.create(42), DurationT.create(4)));
                setDistanceDurationPair(i, "C", "B", Pair.create(DistanceT.create(42), DurationT.create(5)));
                setDistanceDurationPair(i, "B", "D", Pair.create(DistanceT.create(42), DurationT.create(5)));
                setDistanceDurationPair(i, "D", "B", Pair.create(DistanceT.create(42), DurationT.create(5)));
                setDistanceDurationPair(i, "C", "D", Pair.create(DistanceT.create(42), DurationT.create(8)));
                setDistanceDurationPair(i, "D", "C", Pair.create(DistanceT.create(42), DurationT.create(9)));
            }

        }

        static class DurationT  {
            static Duration create(int d) {
                Duration dur = new Duration();
                dur.inSeconds = d;
                return dur;
            }
        }

        static class DistanceT {
            static Distance create(int d){
                Distance dis = new Distance();
                dis.inMeters = d;
                return dis;
            }
        }

    }
}