package org.devheap.intempore.route;

import android.os.AsyncTask;

import com.google.maps.PendingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RouteBuildTask extends AsyncTask<Void, Void, List<RoutePoint>> {
    private ConcurrentHashMap<String, RoutePoint> points;
    private DistanceGraph distances;
    private Callback callback;

    public interface Callback {
        void onResult(List<RoutePoint> point);
        void onError();
    }

    public RouteBuildTask(ConcurrentHashMap<String, RoutePoint> points, DistanceGraph distances) {
        this.points = points;
        this.distances = distances;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected List<RoutePoint> doInBackground(Void... voids) {
        distances.loadDistanceMatrix((RoutePoint[]) points.values().toArray());
        distances.await();

        // Here will be call to real calculation class
        return new ArrayList<>(points.values());
    }

    @Override
    protected void onPostExecute(List<RoutePoint> routePoints) {
        if(callback != null) {
            callback.onResult(routePoints);
        }
    }
}
