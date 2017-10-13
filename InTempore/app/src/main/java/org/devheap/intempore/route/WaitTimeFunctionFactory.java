package org.devheap.intempore.route;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class WaitTimeFunctionFactory {
    public static WaitTimeFunction produceFunction(RoutePoint point) {
        // Здесь будет маппинг из параметров RoutePoint к соответствующим функциям
        //throw new RuntimeException("Not Yet Implemented");
        return new WaitTimeFunction() {
            @Override
            public int wait_time(int minute_of_day) {
                return 0;
            }
        };
    }

    // @param pike_min -- min of the day with maximum visitors
    // @param rate -- how fast the function grows/falls before and after the pike, [0..Inf] range
    // @param noize_rate -- rate of random shift of these values, in range [0, 1]
    private double[] generic_function(int pike_min, double rate, int noize_rate){
        int minutes = 60 * 24;
        double _result[] = new double[minutes];
        double k = 1.0/minutes;
        for(int i = 0; i < minutes; i++){
            _result[i] = gaussian(pike_min*k, rate, (double)i*k, noize_rate) * rate;
        }
        return _result;
    }

    private double gaussian(double mu, double sig, double x, int noize_rate){
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

    public double[] multiple(int[] pike_mins, double rate, int noize_rate){
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
