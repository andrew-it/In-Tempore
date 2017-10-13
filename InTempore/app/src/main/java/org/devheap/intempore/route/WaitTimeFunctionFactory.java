package org.devheap.intempore.route;

public class WaitTimeFunctionFactory {
    public static WaitTimeFunction produceFunction(RoutePoint point) {
        // Здесь будет маппинг из параметров RoutePoint к соответствующим функциям
        throw new RuntimeException("Not Yet Implemented");
    }

    // @param pike_minutes -- minutes of the day with maximum visitors
    // @param grow_rate -- how fast the function grows/falls before and after the pike
    // @param noize_rate -- rate of random shift of these values
    private int generic_function(int[] pike_minutes, int rate, int noize_rate) {
        throw new RuntimeException("Not Yet Implemented");
    }
}
