package org.devheap.intempore.route;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andrey Ermak on 13/10/2017.
 */
public class WaitTimeFunctionFactoryTest {
    @Test
    public void produceFunctionXui() throws Exception {
        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(RoutePoint.create("xui", null));
        assertTrue(f.wait_time(567) >= 0);
    }

    @Test
    public void produceFunction() throws Exception {
        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(RoutePoint.create("bank", null));
        assertTrue(f.wait_time(567) >= 0);
    }

}