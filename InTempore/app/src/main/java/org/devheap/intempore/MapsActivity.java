package org.devheap.intempore;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeolocationApi;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeolocationPayload;
import com.google.maps.model.GeolocationResult;
import com.google.maps.model.PlaceAutocompleteType;
import com.google.maps.model.PlaceDetails;

import org.devheap.intempore.route.DistanceGraph;
import org.devheap.intempore.route.RouteBuilder;
import org.devheap.intempore.route.RoutePoint;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.devheap.intempore.R.string.google_maps_key;

import static org.devheap.intempore.R.string.google_maps_key;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String ERROR_TAG = "SomeErrorTag";
    private static final String TAG = "MySuperTag";
    private Button button;
    private static GoogleMap mMap;
    public static String mapsApiKey = "AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM";
    private static GeoApiContext geoApiContext;

    private RouteBuilder routeBuilder;
    private boolean isAddItem = false;
    private static int width;

    AutocompletePrediction[] result;

    private SearchView search;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain maps key from resources
        mapsApiKey = getResources().getString(google_maps_key);
        RouteBuilder.mapsApiKey = mapsApiKey;

        geoApiContext = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = (Button) findViewById(R.id.buttonNewPath);

        routeBuilder = RouteBuilder.getInstance();

        initSearchView();

        width = getResources().getDisplayMetrics().widthPixels;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) findViewById(R.id.mySearchView);
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                loadSuggestions(newText);
                //startAutocomplete(newText);
                return true;
            }
        });
    }

    private void loadSuggestions(final String query) {
        FetchAutocompletionAsyncTask task = new FetchAutocompletionAsyncTask(geoApiContext, query, new FetchAutocompletionAsyncTask.Callback() {
            @Override
            public void onSuccess(AutocompletePrediction[] predictions) {
                result = predictions;
                String[] columns = new String[]{"_id", "name"};
                Object[] temp = new Object[]{0, "default"};
                MatrixCursor cursor = new MatrixCursor(columns);

                for (int i = 0; i < result.length; i++) {
                    temp[0] = i;
                    temp[1] = result[i].description;
                    cursor.addRow(temp);
                }

                SearchView search = findViewById(R.id.mySearchView);
                String[] from = {"name"};
                int[] to = new int[]{android.R.id.text1};

                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                        android.R.layout.simple_list_item_1,
                        cursor, from, to,
                        CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                search.setSuggestionsAdapter(simpleCursorAdapter);
                search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                    @Override
                    public boolean onSuggestionSelect(int position) {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int position) {
                        try {
                            openNewElement(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Couldn't fetch autocompletion for " + query);
                e.printStackTrace();
            }
        });
        task.execute();
    }

    private void openNewElement(final int position) throws InterruptedException, ApiException, IOException {
        isAddItem = true;
        search.clearFocus();


        PlaceDetails results = routeBuilder.fetchPlaceDetails(result[position].placeId).await();
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(results.geometry.location.lat, results.geometry.location.lng));
        mMap.addMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(results.geometry.location.lat, results.geometry.location.lng), 15.0f);
        mMap.animateCamera(cameraUpdate);

        search.setQuery(results.name, false);

        button.setText("Добавить");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddItem) {//Добавить
                    final GeoApiContext context = new GeoApiContext.Builder()
                            .apiKey(mapsApiKey)
                            .build();

                    routeBuilder.addPlace(result[position].placeId);
                    button.setText("Выбранные");
                    isAddItem = false;
                } else {//Выбранные
                    Intent intent = new Intent(MapsActivity.this, AllPointsActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(55.799634, 49.121456);
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(55.753495, 48.742126);
        List<com.google.maps.model.LatLng> waypoints = new LinkedList<>();
        waypoints.add(new com.google.maps.model.LatLng(55.769612, 48.976454));
        waypoints.add(new com.google.maps.model.LatLng(56.146615, 48.444906));

    }

    public static void drawOptimizePath() throws InterruptedException, ApiException, IOException {
        RouteBuilder routeBuilder = RouteBuilder.getInstance();

        RoutePoint[] points = routeBuilder.getRoutePoints();

        //Markers for places
        for (int i = 0; i < points.length; i++) {
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(points[i].getDetails().geometry.location.lat, points[i].getDetails().geometry.location.lng));
            mMap.addMarker(marker);
        }

        DirectionsResult result = null;

        //if not only origin and destination
        if (points.length > 2) {
            com.google.maps.model.LatLng[] waypoints = new com.google.maps.model.LatLng[points.length - 2];

            for (int i = 1; i < points.length - 1; i++) {
                waypoints[i - 1] = points[i].getDetails().geometry.location;
            }
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(points[0].getDetails().geometry.location)//55.799634, 49.121456 Somewhere in Kazan
                    .destination(points[points.length - 1].getDetails().geometry.location)//55.753495,48.742126 Somewhere in Innopolis
                    .waypoints(waypoints).await();//55.769612, 48.976454 Somewhere in Hell, 56.146615, 48.444906 Somewhere in Park
        } else {
            result = DirectionsApi.newRequest(geoApiContext)
                    .origin(points[0].getDetails().geometry.location)//55.799634, 49.121456 Somewhere in Kazan
                    .destination(points[points.length - 1].getDetails().geometry.location)//55.753495,48.742126 Somewhere in Innopolis
                    .await();//55.769612, 48.976454 Somewhere in Hell, 56.146615, 48.444906 Somewhere in Park

        }
        DirectionsRoute[] routes = result.routes;

        List<com.google.maps.model.LatLng> path = routes[0].overviewPolyline.decodePath();

        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < path.size(); i++) {
            line.add(new LatLng(path.get(i).lat, path.get(i).lng));
            latLngBuilder.include(new LatLng(path.get(i).lat, path.get(i).lng));
        }

        line.width(4f).color(R.color.colorPrimary);

        mMap.addPolyline(line);

        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, width, 25);
        mMap.moveCamera(track);
    }


}
