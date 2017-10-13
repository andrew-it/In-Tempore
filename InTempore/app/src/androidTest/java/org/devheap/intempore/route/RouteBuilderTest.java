package org.devheap.intempore.route;

import org.devheap.intempore.algorithms.PathAlgorithm;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class RouteBuilderTest {
    @Test
    public void addPlace() throws Exception {
        RouteBuilder.mapsApiKey = "AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM";
        RouteBuilder routeBuilder = RouteBuilder.getInstance();
        routeBuilder.addPlace("ChIJYVe_3mn-W0ERom-Xu0sQdls");
        routeBuilder.await();
        RoutePoint point = routeBuilder.getRoutePoint("ChIJYVe_3mn-W0ERom-Xu0sQdls");
        assertNotNull(point);
    }

    @Test
    public void build() throws Exception {
        RouteBuilder.mapsApiKey = "AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM";
        RouteBuilder routeBuilder = RouteBuilder.getInstance();
        routeBuilder.addPlace("ChIJYVe_3mn-W0ERom-Xu0sQdls"); // Medical Center
        routeBuilder.addPlace("ChIJ0zaR5RCtXkERA7GurKB4KEs"); // IU
        routeBuilder.addPlace("ChIJ4-it80yrXkERI6GK5y1usZs"); // Verkhny Uslon
        routeBuilder.addPlace("ChIJ93cA3A-tXkERzdfOHMIOEWA"); // Koltso
        routeBuilder.await();

        RouteBuildTask task = routeBuilder.build();
        task.setCallback(new RouteBuildTask.Callback() {
            @Override
            public void onSuccess(PathAlgorithm.PathData pathData) {
                assertNotNull(pathData);
            }

            @Override
            public void onError(Throwable e) {
                assertNotNull(e);
                throw new RuntimeException(e);
            }
        });
        task.execute();

        TimeUnit.SECONDS.sleep(100);
    }
}