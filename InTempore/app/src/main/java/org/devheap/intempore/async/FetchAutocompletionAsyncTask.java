package org.devheap.intempore.async;

import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.AutocompletePrediction;

import java.io.IOException;

/**
 * Created by mike on 10/14/17.
 */

public class FetchAutocompletionAsyncTask extends AsyncTask<Void, Void, AutocompletePrediction[]> {
    public static final String TAG = "FetchAutocompletion";
    private GeoApiContext context;
    private String query;
    private Callback callback;
    private Throwable e;

    public interface Callback {
        void onSuccess(AutocompletePrediction[] predictions);
        void onError(Throwable e);
    }

    public FetchAutocompletionAsyncTask(GeoApiContext context, String query, Callback callback) {
        this.context = context;
        this.query = query;
        this.callback = callback;
    }

    @Override
    protected AutocompletePrediction[] doInBackground(Void... voids) {
        try {
            return PlacesApi.placeAutocomplete(context, query).await();
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch autocompletion for " + query);
            e.printStackTrace();
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(AutocompletePrediction[] predictions) {
        if(predictions != null) {
            callback.onSuccess(predictions);
        } else if(e != null) {
            callback.onError(e);
        } else {
            throw new RuntimeException("Unreachable");
        }
    }
}
