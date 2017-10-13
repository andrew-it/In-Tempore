package org.devheap.intempore.route;

import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
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

    // PlaceID to PlaceDetails
    private HashMap<String, PlaceDetails> places = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    // Distances
    

    public RouteBuilder(String mapsApiKey) {
        this.mapsApiKey = mapsApiKey;

        geoApiContext = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();
    }

    private void addPlace(final String placeId) {
        Log.i(TAG, "Starting retrieving place details for " + placeId);
        PlacesApi.placeDetails(geoApiContext, placeId).setCallback(
                new PendingResult.Callback<PlaceDetails>() {
                    @Override
                    public void onResult(PlaceDetails result) {
                        Log.d(TAG, "Retrieved details for place " + placeId);
                        Log.d(TAG, result.formattedAddress);
                        lock.writeLock().lock();
                        places.put(placeId, result);
                        lock.writeLock().unlock();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "Couldn't retrieve places details for " + placeId);
                    }
                }
        );
    }




}
