package org.devheap.intempore.async;

import android.os.AsyncTask;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;

import org.devheap.intempore.route.RoutePoint;


public class ReverseGeocodeAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "ReverseGeocode";
    private GeoApiContext context;
    private LatLng query;
    private Callback callback;
    private Throwable e;

    public interface Callback {
        void onSuccess(String placeId);
        void onError(Throwable e);
    }

    public ReverseGeocodeAsyncTask(GeoApiContext context, LatLng query, Callback callback) {
        this.context = context;
        this.query = query;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, query).await();
            return results[0].placeId;
        } catch (Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String placeId) {
        if(placeId != null) {
            callback.onSuccess(placeId);
        } else if(e != null) {
            callback.onError(e);
        } else {
            throw new RuntimeException("Unreachable");
        }
    }
}
