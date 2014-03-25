package code.com.corybill.control.math;

import code.com.corybill.control.dataStructures.MyArrayList;
import code.com.corybill.model.Quote;
import com.google.code.morphia.query.Query;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/25/13
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyStandardDeviation {
    public double sum (List<Double> a){
        if (a.size() > 0) {
            double sum = 0;
            for (Double i : a) {
                sum += i;
            }
            return sum;
        }
        return 0;
    }
    public double mean (List<Double> a){
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return mean;
    }
    public double sd(List<Double> a){
        double sum = 0;
        double mean = mean(a);
        for (Double i : a){
            sum += Math.pow((i - mean), 2);
        }

        return Math.sqrt( sum / ( a.size() - 1 ) ); // sample
    }

    public double getSDWithMean(MyArrayList quotes){
        double sum = 0;
        for (Quote quote : quotes){
            sum += Math.pow((quote.getDailyLogDiff() - quotes.mean), 2);
        }
        return Math.sqrt( sum / ( quotes.size() - 1 ) ); // sample
    }

    /**
     * Same static methods but overloaded to Query<Quote></Quote>
     */
    public double sum (Query<Quote> quotes){
        if (quotes.countAll() > 0) {
            double sum = 0;
            for (Quote quote : quotes.fetch()) {
                sum += quote.getOhlc().getClose();
            }
            return sum;
        }
        return 0;
    }
    public double mean (Query<Quote> quotes,long limit){
        double sum = sum(quotes);
        double mean = 0;
        mean = sum / limit;
        return mean;
    }
    public double getStandardDeviation(List<Quote> quotes,double mean,long limit){
        if(quotes.size() < limit){
            limit = quotes.size();
        }

        double sum = 0;
        for (Quote quote : quotes){
            sum += Math.pow((quote.getOhlc().getClose() - mean), 2);
        }
        return Math.sqrt( sum / ( limit - 1 ) ); // sample
    }
}
