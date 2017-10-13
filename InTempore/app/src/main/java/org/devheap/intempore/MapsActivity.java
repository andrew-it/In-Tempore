package org.devheap.intempore;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private RouteBuilder routeBuilder;
    private boolean isAddItem = false;
    private static int width;

    public static Context baseContext;
    AutocompletePrediction[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain maps key from resources
        mapsApiKey = getResources().getString(google_maps_key);

        RouteBuilder routeBuilder = new RouteBuilder("AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM");
        routeBuilder.addPlace("ChIJYVe_3mn-W0ERom-Xu0sQdls"); // Medical Center
        routeBuilder.addPlace("ChIJ0zaR5RCtXkERA7GurKB4KEs"); // IU
        routeBuilder.addPlace("ChIJ4-it80yrXkERI6GK5y1usZs"); // Verkhny Uslon
        routeBuilder.addPlace("ChIJ93cA3A-tXkERzdfOHMIOEWA"); // Koltso
        routeBuilder.await();

        DistanceGraph graph = new DistanceGraph(routeBuilder.getGeoApiContext());
        graph.loadDistanceMatrix(routeBuilder.getRoutePoints());
        graph.await();

        for(RoutePoint from: routeBuilder.getRoutePoints()) {
            for(RoutePoint to: routeBuilder.getRoutePoints()) {
                for(int h = 0; h < 24; h++) {
                    assertNotNull(graph.getDistanceDurationPair(h, from, to));
                    assertNotNull(graph.getDistance(h, from, to));
                    assertNotNull(graph.getDuration(h, from, to));
                }
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = (Button) findViewById(R.id.buttonNewPath);

        routeBuilder = RouteBuilder.getInstance();

        initSearchView();

        baseContext = getBaseContext();

        width = getResources().getDisplayMetrics().widthPixels;
        ;
    }

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) findViewById(R.id.mySearchView);
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


    private void loadSuggestions(String query) {
        //showSuggestion();

        String[] columns = new String[]{"_id", "name"};
        Object[] temp = new Object[]{0, "default"};
        MatrixCursor cursor = new MatrixCursor(columns);
        result = getAutocomplete(query);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                temp[0] = i;
                temp[1] = result[i].description;
                cursor.addRow(temp);
            }

            SearchView search = (SearchView) findViewById(R.id.mySearchView);
            String[] from = {"name"};
            int[] to = new int[]{android.R.id.text1};

            Context inContext = getBaseContext();

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(baseContext,
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

                    openNewElement(position);
                    return false;
                }
            });
        }
    }

    private void openNewElement(final int position) {
        isAddItem = true;
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

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

        //drawPath(context, googleMap, origin, destination, waypoints);


    }

    public void drawPath(GeoApiContext context, GoogleMap googleMap, com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination, List<com.google.maps.model.LatLng> waypoints) {

        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(origin.toString())//55.799634, 49.121456 Somewhere in Kazan
                    .destination(destination.toString())//55.753495,48.742126 Somewhere in Innopolis
                    .waypoints(waypoints.get(0)).await();//55.769612, 48.976454 Somewhere in Hell, 56.146615, 48.444906 Somewhere in Park
            DirectionsRoute[] routes = result.routes;

            List<com.google.maps.model.LatLng> path = routes[0].overviewPolyline.decodePath();

            PolylineOptions line = new PolylineOptions();

            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

            for (int i = 0; i < path.size(); i++) {
                if (i == 0) {
                    MarkerOptions startMarker = new MarkerOptions()
                            .position(new LatLng(path.get(i).lat, path.get(i).lng));
                    googleMap.addMarker(startMarker);
                }

                line.add(new LatLng(path.get(i).lat, path.get(i).lng));
                latLngBuilder.include(new LatLng(path.get(i).lat, path.get(i).lng));
            }

            line.width(4f).color(R.color.colorPrimary);

            googleMap.addPolyline(line);
            int size = getResources().getDisplayMetrics().widthPixels;

            LatLngBounds latLngBounds = latLngBuilder.build();
            CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
            googleMap.moveCamera(track);

        } catch (ApiException e) {
            e.printStackTrace();

            Log.e(ERROR_TAG, "API EXCEPTION");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(ERROR_TAG, "INTERRUPTED EXCEPTION");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(ERROR_TAG, "IOEXCEPTION");
        }
    }


    private AutocompletePrediction[] getAutocomplete(String s) {
        final GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

        try {

            //GeolocationResult result = GeolocationApi.geolocate(context, new GeolocationPayload()).await();

            AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(context, s).type(PlaceAutocompleteType.ADDRESS).await();
            return predictions;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void startAutocomplete(String s) {
        final GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();
        Context outContext = getBaseContext();

        try {
            GeolocationResult result = GeolocationApi.geolocate(context, new GeolocationPayload()).await();

            PlacesApi.placeAutocomplete(context, s).type(PlaceAutocompleteType.ADDRESS).setCallback(new PendingResult.Callback<AutocompletePrediction[]>() {
                @Override
                public void onResult(AutocompletePrediction[] result) {
                    System.out.println("SOME RESULT HAS COME");
                    showSuggestion();
                }

                @Override
                public void onFailure(Throwable e) {

                }
            });
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showSuggestion() {

        String[] columns = new String[]{"_id", "name"};
        Object[] temp = new Object[]{0, "default"};
        MatrixCursor cursor = new MatrixCursor(columns);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                temp[0] = i;
                temp[1] = result[i].description;
                cursor.addRow(temp);
            }

            SearchView search = (SearchView) findViewById(R.id.mySearchView);
            String[] from = {"name"};
            int[] to = new int[]{android.R.id.text1};

            Context inContext = getBaseContext();

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(baseContext,
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

                    openNewElement(position);
                    return false;
                }
            });
        }
    }


    public static void drawOptimizePath() throws InterruptedException, ApiException, IOException {
        RouteBuilder routeBuilder = RouteBuilder.getInstance();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(mapsApiKey)
                .build();

        RoutePoint[] points = routeBuilder.getRoutePoints();

        DirectionsResult result = null;
        if (points.length > 2) {
            com.google.maps.model.LatLng[] waypoints = new com.google.maps.model.LatLng[points.length - 2];

            for (int i = 1; i < points.length - 1; i++) {
                waypoints[i - 1] = points[i].getDetails().geometry.location;
            }
            result = DirectionsApi.newRequest(context)
                    .origin(points[0].getDetails().geometry.location)//55.799634, 49.121456 Somewhere in Kazan
                    .destination(points[points.length - 1].getDetails().geometry.location)//55.753495,48.742126 Somewhere in Innopolis
                    .waypoints(waypoints).await();//55.769612, 48.976454 Somewhere in Hell, 56.146615, 48.444906 Somewhere in Park

        } else {
            result = DirectionsApi.newRequest(context)
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
