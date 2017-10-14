package org.devheap.intempore.route;

import android.location.Location;
import android.os.AsyncTask;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import org.devheap.intempore.MapsActivity;
import org.devheap.intempore.algorithms.BruteForce;
import org.devheap.intempore.algorithms.PathAlgorithm;
import org.devheap.intempore.algorithms.PathGraph;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RouteBuildTask extends AsyncTask<Void, Void, PathAlgorithm.PathData> {
    private DistanceGraph distances;
    private Callback callback;
    private Throwable e;

    public interface Callback {
        void onSuccess(PathAlgorithm.PathData pathData);
        void onError(Throwable e);
    }

    public RouteBuildTask(DistanceGraph distances) {
        this.distances = distances;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected PathAlgorithm.PathData doInBackground(Void... voids) {
        RouteBuilder builder = RouteBuilder.getInstance();
        String currentLocation = MapsActivity.latestLocationId;

        RoutePoint startPoint = builder.getRoutePoint(currentLocation);
        RoutePoint[] points = builder.getRoutePoints();
        distances.loadDistanceMatrix(points);

        BruteForce bruteForce = new BruteForce(new PathGraph(distances, DateTime.now(), startPoint));
        return bruteForce.pathData();
    }

    @Override
    protected void onPostExecute(PathAlgorithm.PathData pathData) {
        if(callback != null) {
            if (pathData != null) {
                callback.onSuccess(pathData);
            } else if(e != null) {
                callback.onError(e);
            } else {
                throw new RuntimeException("Unreachable");
            }
        }
    }
}
