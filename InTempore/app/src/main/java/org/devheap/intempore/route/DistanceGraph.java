package org.devheap.intempore.route;

import android.util.Log;
import android.util.Pair;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Duration;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DistanceGraph {
    public static final String TAG = "DistanceGraph";

    private GeoApiContext context;
    private LinkedList<RoutePoint> points;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, ArrayList<Pair<Distance, Duration>>>> matrix
            = new ConcurrentHashMap<>();

    private AtomicInteger fetchedCount = new AtomicInteger(0);

    public DistanceGraph(GeoApiContext context) {
        this.context = context;
    }

    public Distance getDistance(int departure_hour, RoutePoint from, RoutePoint to) {
        return getDistanceDurationPair(departure_hour, from, to).first;
    }

    public Distance getDistance(int departure_hour, String from_id, String to_id) {
        return getDistanceDurationPair(departure_hour, from_id, to_id).first;
    }

    public Duration getDuration(int departure_hour, RoutePoint from, RoutePoint to) {
        return getDistanceDurationPair(departure_hour, from, to).second;
    }

    public Duration getDuration(int departure_hour, String from_id, String to_if) {
        return getDistanceDurationPair(departure_hour, from_id, to_if).second;
    }

    public Pair<Distance, Duration> getDistanceDurationPair(int departure_hour, RoutePoint from, RoutePoint to) {
        return getDistanceDurationPair(departure_hour, from.getPlaceId(), to.getPlaceId());
    }

    public Pair<Distance, Duration> getDistanceDurationPair(int departure_hour, String from_id, String to_id) {
        return matrix.get(from_id).get(to_id).get(departure_hour);
    }

    private void setDistanceDurationPair(int departure_hour, RoutePoint from, RoutePoint to, Pair<Distance, Duration> dist_duration) {
        setDistanceDurationPair(departure_hour, from.getPlaceId(), to.getPlaceId(), dist_duration);
    }

    private void setDistanceDurationPair(int departure_hour, String from_id, String to_id, Pair<Distance, Duration> dist_duration) {
        matrix.get(from_id).get(to_id).set(departure_hour, dist_duration);
    }

    public void fetchDistances(RoutePoint... points) {
        // Prepare container
        matrix.clear();
        fetchedCount.set(0);

        // Initialize matrix
        for(RoutePoint from: points) {
            ConcurrentHashMap<String, ArrayList<Pair<Distance, Duration>>> to_map = new ConcurrentHashMap<>();
            for(RoutePoint to: points) {
                ArrayList<Pair<Distance, Duration>> hours = new ArrayList<>();
                for(int i = 0; i < 24; i++) {
                    hours.add(Pair.create((Distance) null, (Duration) null));
                }
                to_map.put(to.getPlaceId(), hours);
            }
            matrix.put(from.getPlaceId(), to_map);
        }

        Log.d(TAG, "Started fetching Distance Matrices");
        for(int i = 0; i < 24; i++) {
            fetchDistanceMatrix(i, points);
        }
    }

    private void fetchDistanceMatrix(final int departure_hour, final RoutePoint... points) {
        DateTime dateTime = DateTime.now(DateTimeZone.UTC);
        dateTime.plusHours(departure_hour);

        // Prepare place_id queries
        final String[] place_queries = new String[points.length];
        for(int i = 0; i < points.length; i++) {
            place_queries[i] = "place_id:" + points[i].getPlaceId();
        }

        Log.d(TAG, "Started fetching Distance Matrix for T+" + departure_hour);
        DistanceMatrixApi.getDistanceMatrix(context, place_queries, place_queries)
                .departureTime(dateTime)
                .setCallback(new PendingResult.Callback<DistanceMatrix>() {
                    @Override
                    public void onResult(DistanceMatrix result) {
                        Log.i(TAG, "Fetched the Distance Matrix for T+" + departure_hour);
                        for(int i = 0; i < place_queries.length; i++) {
                            for(int j = 0; j < place_queries.length; j++) {
                                Distance dist = result.rows[i].elements[j].distance;
                                Duration duration = result.rows[i].elements[j].durationInTraffic;
                                setDistanceDurationPair(departure_hour, points[i], points[j], Pair.create(dist, duration));
                            }
                        }
                        fetchedCount.addAndGet(1);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "Failed to fetch Distance Matrix for T+" + departure_hour);
                    }
                });

    }

    public boolean isFinishedFetchingMatrix() {
        return fetchedCount.get() == 24;
    }

    public void await() {
        while(!isFinishedFetchingMatrix()) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}