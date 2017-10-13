package org.devheap.intempore.route;

import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceDetails;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RouteBuilder {
    public static final String TAG = "RouteBuilder";

    private String mapsApiKey;
    private GeoApiContext geoApiContext;

    private AtomicInteger runningFetchindDetails = new AtomicInteger(0);

    // PlaceID to PlaceDetails
    private ConcurrentHashMap<String, RoutePoint> places = new ConcurrentHashMap<>();

    DistanceGraph distances;


    public RouteBuilder(String mapsApiKey) {
        this.mapsApiKey = mapsApiKey;

        geoApiContext = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();
        distances = new DistanceGraph(geoApiContext);
    }

    public void addPlace(final String placeId) {
        Log.i(TAG, "Starting retrieving place details for " + placeId);
        runningFetchindDetails.addAndGet(1);
        fetchPlaceDetails(placeId).setCallback(

                new PendingResult.Callback<PlaceDetails>() {
                    @Override
                    public void onResult(PlaceDetails result) {
                        Log.d(TAG, "Retrieved details for place " + placeId);
                        Log.d(TAG, result.formattedAddress);
                        RoutePoint point = RoutePoint.create(placeId, result);
                        places.put(placeId, point);
                        runningFetchindDetails.decrementAndGet();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "Couldn't retrieve places details for " + placeId);
                        e.printStackTrace();
                        runningFetchindDetails.decrementAndGet();
                    }
                }
        );
    }

    public PendingResult<PlaceDetails> fetchPlaceDetails(String placeId) {
        return PlacesApi.placeDetails(geoApiContext, placeId);
    }

    public void removePlace(final String placeId) {
        places.remove(placeId);
    }

    public RoutePoint getRoutePoint(final String placeId) {
        return places.get(placeId);
    }

    public GeoApiContext getGeoApiContext() {
        return geoApiContext;
    }

    public RoutePoint[] getRoutePoints() {
        Collection<RoutePoint> points = places.values();
        return points.toArray(new RoutePoint[points.size()]);
    }

    public RouteBuildTask build() {
        return new RouteBuildTask(places, distances);
    }

    public boolean isFinishedFetchingDetails() {
        return runningFetchindDetails.get() == 0;
    }

    public void await() {
        while(!isFinishedFetchingDetails()) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
