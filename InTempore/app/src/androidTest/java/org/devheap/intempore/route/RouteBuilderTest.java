package org.devheap.intempore.route;

import org.junit.Test;

import static org.junit.Assert.*;

public class RouteBuilderTest {
    @Test
    public void addPlace() throws Exception {
        RouteBuilder routeBuilder = new RouteBuilder("AIzaSyBwProiHgcHKPpBwxZzGpjNfPFzC3Rl3SM");
        routeBuilder.addPlace("ChIJYVe_3mn-W0ERom-Xu0sQdls");
        routeBuilder.await();
        RoutePoint point = routeBuilder.getRoutePoint("ChIJYVe_3mn-W0ERom-Xu0sQdls");
        assertNotNull(point);
    }
}