package org.devheap.intempore.route;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceGraphTest {
    @Test
    public void fetchDistances() throws Exception {
        RouteBuilder.mapsApiKey = "AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM";
        RouteBuilder routeBuilder = RouteBuilder.getInstance();
        routeBuilder.addPlace("ChIJYVe_3mn-W0ERom-Xu0sQdls"); // Medical Center
        routeBuilder.addPlace("ChIJ0zaR5RCtXkERA7GurKB4KEs"); // IU
        routeBuilder.addPlace("ChIJ4-it80yrXkERI6GK5y1usZs"); // Verkhny Uslon
        routeBuilder.addPlace("ChIJ93cA3A-tXkERzdfOHMIOEWA"); // Koltso
        routeBuilder.await();

        DistanceGraph graph = new DistanceGraph(routeBuilder.getGeoApiContext());
        graph.loadDistanceMatrix(routeBuilder.getRoutePoints());

        for(RoutePoint from: routeBuilder.getRoutePoints()) {
            for(RoutePoint to: routeBuilder.getRoutePoints()) {
                for(int h = 0; h < 24; h++) {
                    assertNotNull(graph.getDistanceDurationPair(h, from, to));
                    assertNotNull(graph.getDistance(h, from, to));
                    assertNotNull(graph.getDuration(h, from, to));
                }
            }
        }
    }

}