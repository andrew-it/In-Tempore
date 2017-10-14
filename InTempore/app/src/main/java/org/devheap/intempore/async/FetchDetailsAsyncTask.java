package org.devheap.intempore.async;

import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.PlaceDetails;

import org.devheap.intempore.route.RouteBuildTask;
import org.devheap.intempore.route.RouteBuilder;

import java.io.IOException;

/**
 * Created by mike on 10/14/17.
 */

public class FetchDetailsAsyncTask extends AsyncTask<Void, Void, PlaceDetails> {
    public static final String TAG = "FetchAutocompletion";
    private GeoApiContext context;
    private String query;
    private Callback callback;
    private Throwable e;

    public interface Callback {
        void onSuccess(PlaceDetails predictions);
        void onError(Throwable e);
    }

    public FetchDetailsAsyncTask(GeoApiContext context, String query, Callback callback) {
        this.context = context;
        this.query = query;
        this.callback = callback;
    }

    @Override
    protected PlaceDetails doInBackground(Void... voids) {
        try {
            return RouteBuilder.getInstance().fetchPlaceDetails(query).await();
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch details for " + query);
            e.printStackTrace();
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlaceDetails details) {
        if(details != null) {
            callback.onSuccess(details);
        } else if(e != null) {
            callback.onError(e);
        } else {
            throw new RuntimeException("Unreachable");
        }
    }
}
