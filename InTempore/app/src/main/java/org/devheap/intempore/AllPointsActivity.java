package org.devheap.intempore;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.errors.ApiException;

import org.devheap.intempore.route.RouteBuilder;
import org.devheap.intempore.route.RoutePoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ekaterina on 10/13/17.
 */

public class AllPointsActivity extends Activity {
    List<RoutePoint> points;
    RVAdapter rvAdapter;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_points);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        rv = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        points = Arrays.asList(RouteBuilder.getInstance().getRoutePoints());
        rvAdapter = new RVAdapter(points);

        rv.setAdapter(rvAdapter);
    }


    public void onDrawPath(View view) throws InterruptedException, ApiException, IOException {
        MapsActivity.drawOptimizePath();
        this.finish();
    }

}
