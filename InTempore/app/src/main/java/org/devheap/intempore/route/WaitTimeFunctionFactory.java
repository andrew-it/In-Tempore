package org.devheap.intempore.route;

import com.google.maps.model.OpeningHours;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class WaitTimeFunctionFactory {
    private static HashMap<String, int[]> types_map = null;
    private static HashMap<String, double[]> function_result_map = new HashMap<>();

    private static void init_types_map(){
        types_map = new HashMap<>();
        types_map.put("bank", new int[]{11, 15});
        types_map.put("store", new int[]{19, 14});
        types_map.put("post_office", new int[]{19, 10});
        types_map.put("cafe", new int[]{21, 10});
        types_map.put("hospital", new int[]{7, 11});
        types_map.put("doctor", new int[]{7, 11});
        types_map.put("gym", new int[]{13, 19});
        types_map.put("hair_care", new int[]{17});
        types_map.put("mosque", new int[]{13});
        types_map.put("library", new int[]{18});
        types_map.put("park", new int[]{18});
        types_map.put("gas_station", new int[]{9, 23});
        types_map.put("night_club", new int[]{23, 4});
        types_map.put("shopping_mall", new int[]{19, 15, 12});
        types_map.put("dentist", new int[]{17, 12, 9});
        types_map.put("default", new int[]{19, 12});
    }

    public static WaitTimeFunction produceFunction(final RoutePoint point) {
        if(types_map == null)
            init_types_map();

        int _params[] = types_map.get(point.getPlaceId());

        String def = "default";
        if(_params != null)
            def = point.getPlaceId();

        if (function_result_map.get(def) == null)
            function_result_map.put(def, multiple(types_map.get(def), 0.15, 0));

        final String definition = def;

        return new WaitTimeFunction() {
            @Override
            public int wait_time(int minute_of_day) {
                Calendar calendar = Calendar.getInstance();
                int current_day = calendar.get(Calendar.DAY_OF_WEEK);
                OpeningHours.Period openClose = point.getDetails().openingHours.periods[current_day-1];

                int close_mins = openClose.close.time.getHourOfDay() * 60 + openClose.close.time.getMinuteOfHour();
                int open_mins = openClose.open.time.getHourOfDay() * 60 + openClose.open.time.getMinuteOfHour();

                if(minute_of_day < open_mins)
                    return open_mins - minute_of_day;
                if(minute_of_day > close_mins)
                    return 24*60 - minute_of_day + open_mins;
                return (int)(function_result_map.get(definition)[minute_of_day%(60*24)]*50);
            }
        };
    }

    // @param pike_min -- min of the day with maximum visitors
    // @param rate -- how fast the function grows/falls before and after the pike, [0..Inf] range
    // @param noize_rate -- rate of random shift of these values, in range [0, 1]
    private static double[] generic_function(int pike_min, double rate, int noize_rate){
        int minutes = 60 * 24;
        double _result[] = new double[minutes];
        double k = 1.0/minutes;
        for(int i = 0; i < minutes; i++){
            _result[i] = gaussian(pike_min*k, rate, (double)i*k, noize_rate) * rate;
        }
        double _tmp[] = _result.clone();
        Arrays.sort(_tmp);
        double median = _tmp[60 * 18];
        for(int i = 0; i < _result.length; i++)
            if(_result[i] - median > 0)
                _result[i] -= median;
            else
                _result[i] = 0;
        return _result;
    }

    private static double gaussian(double mu, double sig, double x, int noize_rate){
        Random random = new Random();
        double E = 2.7182818285;
        double PI = 3.1415926536;

        double _const = 1 / (sig * sqrt(2*PI));
        double _expr = pow(E, -pow(x - mu, 2) / (2 * sig * sig));
        double result = _const * _expr  -  (double)noize_rate * random.nextDouble() * 0.1;
        if(result < 0)
            return 0;
        else
            return result;
    }

    private static double[] multiple(int[] pike_mins, double rate, int noize_rate){
        int minutes = 60 * 24;
        double _result[] = new double[minutes];

        double _arr[][] = new double[minutes][pike_mins.length];

        for(int i = 0; i < pike_mins.length; i++){
            _arr[i] = generic_function(pike_mins[i], rate+i*0.3, noize_rate);
        }

        for(int i = 0; i < _result.length; i++){
            double score = 0;
            for(int j = 0; j < pike_mins.length; j++){
                score += _arr[j][i];
            }
            _result[i] = score / (double)pike_mins.length;
        }
        return _result;
    }
}
