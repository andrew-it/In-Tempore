package org.devheap.intempore.route;

import org.junit.Test;

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
}