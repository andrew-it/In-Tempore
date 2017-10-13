package org.devheap.intempore.route;

import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import com.google.maps.model.OpeningHours;
import com.google.maps.model.PlaceDetails;

import org.joda.time.LocalTime;
import org.junit.Test;

import static org.devheap.intempore.route.RouteBuilder.TAG;
import static org.junit.Assert.*;

/**
 * Created by Andrey Ermak on 13/10/2017.
 */
public class WaitTimeFunctionFactoryTest {
    public PlaceDetails getDetails(){
        PlaceDetails details = new PlaceDetails();
        details.openingHours = new OpeningHours();
        details.openingHours.periods = new OpeningHours.Period[7];
        for(int i = 0; i < 7; i++) {
            OpeningHours.Period per = new OpeningHours.Period();
            per.close = new OpeningHours.Period.OpenClose();
            per.close.time = LocalTime.MIDNIGHT.plusHours(21);
            per.open = new OpeningHours.Period.OpenClose();
            per.open.time = LocalTime.MIDNIGHT.plusHours(9);
            details.openingHours.periods[i] = per;
        }
        return details;
    }

    @Test
    public void produceFunctionDefault() throws Exception {


        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(
                RoutePoint.create("test", getDetails())
        );
        assertTrue(f.wait_time(567) >= 0);
    }

    @Test
    public void produceFunction() throws Exception {
        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(
                RoutePoint.create("bank", getDetails())
        );
        assertTrue(f.wait_time(567) >= 0);
    }

    @Test
    public void produceFunctionClosed() throws Exception {
        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(
                RoutePoint.create("bank", getDetails())
        );
        assertTrue(f.wait_time(540) == 0);
    }

    @Test
    public void produceFunctionOpen() throws Exception {
        WaitTimeFunction f = WaitTimeFunctionFactory.produceFunction(
                RoutePoint.create("bank", getDetails())
        );
        assertTrue(f.wait_time(1261) == 719);
    }
}