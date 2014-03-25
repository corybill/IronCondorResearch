package code.com.corybill.control.math;

import code.com.corybill.control.dataStructures.MyArrayList;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.Quote;
import code.com.corybill.model.TradingRange;
import com.google.code.morphia.query.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/19/13
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathUtil {
    private MyStandardDeviation myStandardDeviation;

    public double getMeanOfList(List<Double> list){
        return 0;
    }

    public MyArrayList getSummedLogDifference(List<Quote> quotes){
        double yesterdaysPrice = 0.0;
        MyArrayList list = new MyArrayList();

        int count = 0;
        for(Quote quote : quotes){
            if(yesterdaysPrice == 0.0){
                yesterdaysPrice = quote.getOhlc().getClose();
                continue;
            }
            double todaysPrice = quote.getOhlc().getClose();
            quote.setDailyLogDiff(Math.log(yesterdaysPrice / todaysPrice));

            list.add(quote);

            list.total += quote.getDailyLogDiff();
            yesterdaysPrice = quote.getOhlc().getClose();
            count++;
        }
        return list;
    }

    /**
     * Gets the percentage change on a day by day basis and stores this value into an array.
     * We will get the 30 day historical volatility and turn that into the yearly volatility.
     * Yearly Vol = STDEV (From day 1 to n)[ ln(day1 / day2) ] * SQRT(TRADING_DAYS_IN_YEAR)
     *
     * @param quotes
     * @param map
     * @return
     */
    public void getVolatility(Map<Integer,Double> map, Query<Quote> quotes) {
        //Count is how many of the quotes we have gone through thus far.

        double yesterdaysPrice = 0.0;
        List<Double> list = new ArrayList<Double>();
        for(Quote quote : quotes.fetch()){
            if(yesterdaysPrice == 0.0){
                yesterdaysPrice = quote.getOhlc().getClose();
                continue;
            }
            double todaysPrice = quote.getOhlc().getClose();
            double percentChange = Math.log(yesterdaysPrice / todaysPrice);
            list.add(percentChange);
            yesterdaysPrice = quote.getOhlc().getClose();
        }
        double sd = myStandardDeviation.sd(list);

        map.put(TradingConstants.TRADING_DAYS_IN_WEEK ,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_WEEK));
        map.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS ,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_2_WEEKS));
        map.put(TradingConstants.TRADING_DAYS_IN_MONTH,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_MONTH));
        map.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR ,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_HALF_YEAR));
        map.put(TradingConstants.TRADING_DAYS_IN_YEAR,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_YEAR));
    }

    public void getVolatilityWithMean(Map<Integer,Double> map, MyArrayList quotes) {
        double sd = myStandardDeviation.getSDWithMean(quotes);

        map.put(TradingConstants.TRADING_DAYS_IN_WEEK ,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_WEEK));
        map.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_2_WEEKS));
        map.put(TradingConstants.TRADING_DAYS_IN_MONTH,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_MONTH));
        map.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_HALF_YEAR));
        map.put(TradingConstants.TRADING_DAYS_IN_YEAR,sd * Math.sqrt(TradingConstants.TRADING_DAYS_IN_YEAR));
    }

    public void getSDAndMean(Map<Integer,Double> sdMap, Map<Integer,Double> meanMap, Map<Integer,TradingRange> rangeMap, List<Quote> yearOfQuotes){
        setBeginningTradingRanges(rangeMap);

        int count = 0;
        double weekSum=0.0;
        double twoWeekSum=0.0;
        double monthSum=0.0;
        double halfYearSum=0.0;
        double yearSum=0.0;
        int weekCount=0;
        int twoWeekCount=0;
        int monthCount = 0;
        int halfYearCount = 0;
        int yearCount = 0;

        for(Quote quote : yearOfQuotes){
            if(count< TradingConstants.TRADING_DAYS_IN_WEEK){
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_WEEK, rangeMap, quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_2_WEEKS, rangeMap, quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_MONTH, rangeMap, quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_HALF_YEAR, rangeMap, quote);

                weekCount++; twoWeekCount++; monthCount++; halfYearCount++;
                weekSum += quote.getOhlc().getClose();
                twoWeekSum += quote.getOhlc().getClose();
                monthSum += quote.getOhlc().getClose();
                halfYearSum += quote.getOhlc().getClose();
            }else if(count< TradingConstants.TRADING_DAYS_IN_2_WEEKS){
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_2_WEEKS,rangeMap,quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_MONTH, rangeMap, quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_HALF_YEAR, rangeMap, quote);

                twoWeekCount++; monthCount++; halfYearCount++;
                twoWeekSum += quote.getOhlc().getClose();
                monthSum += quote.getOhlc().getClose();
                halfYearSum += quote.getOhlc().getClose();
            }else if(count< TradingConstants.TRADING_DAYS_IN_MONTH){
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_MONTH,rangeMap,quote);
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_HALF_YEAR, rangeMap, quote);

                monthCount++; halfYearCount++;
                monthSum += quote.getOhlc().getClose();
                halfYearSum += quote.getOhlc().getClose();
            }else if(count< TradingConstants.TRADING_DAYS_IN_HALF_YEAR){
                setTradingRanges(TradingConstants.TRADING_DAYS_IN_HALF_YEAR,rangeMap,quote);
                halfYearCount++;
                halfYearSum += quote.getOhlc().getClose();
            }
            yearCount++;
            setTradingRanges(TradingConstants.TRADING_DAYS_IN_YEAR,rangeMap,quote);
            yearSum += quote.getOhlc().getClose();
            count++;
        }

        double weekMean = weekSum / weekCount;
        double twoWeekMean =twoWeekSum / twoWeekCount;
        double monthMean = monthSum / monthCount;
        double halfYearMean = halfYearSum / halfYearCount;
        double yearMean = yearSum / yearCount;

        meanMap.put(TradingConstants.TRADING_DAYS_IN_YEAR , yearMean);
        meanMap.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR , halfYearMean);
        meanMap.put(TradingConstants.TRADING_DAYS_IN_MONTH , monthMean);
        meanMap.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS , twoWeekMean);
        meanMap.put(TradingConstants.TRADING_DAYS_IN_WEEK , weekMean);

        sdMap.put(TradingConstants.TRADING_DAYS_IN_YEAR , myStandardDeviation.getStandardDeviation(yearOfQuotes,yearMean, TradingConstants.TRADING_DAYS_IN_YEAR));
        sdMap.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR , myStandardDeviation.getStandardDeviation(yearOfQuotes.subList(0,halfYearCount),halfYearMean, TradingConstants.TRADING_DAYS_IN_HALF_YEAR));
        sdMap.put(TradingConstants.TRADING_DAYS_IN_MONTH , myStandardDeviation.getStandardDeviation(yearOfQuotes.subList(0,monthCount),monthMean, TradingConstants.TRADING_DAYS_IN_MONTH));
        sdMap.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS , myStandardDeviation.getStandardDeviation(yearOfQuotes.subList(0,twoWeekCount),twoWeekMean, TradingConstants.TRADING_DAYS_IN_2_WEEKS));
        sdMap.put(TradingConstants.TRADING_DAYS_IN_WEEK , myStandardDeviation.getStandardDeviation(yearOfQuotes.subList(0,weekCount),weekMean, TradingConstants.TRADING_DAYS_IN_WEEK));
    }

    public double getTime(int daysToExpiration) {
        return daysToExpiration / TradingConstants.DAYS_IN_A_YEAR;
    }
    public double getTime(double daysToExpiration) {
        return daysToExpiration / TradingConstants.DAYS_IN_A_YEAR;
    }
    public double getTime(Calendar startDate, Calendar expiration) {
        int days = (int)round((expiration.getTimeInMillis() - startDate.getTimeInMillis()) / 86400000.00, 0);
        return getTime(days + 1);
    }

    private void setBeginningTradingRanges(Map<Integer,TradingRange> rangeMap){
        rangeMap.put(TradingConstants.TRADING_DAYS_IN_WEEK,new TradingRange());
        rangeMap.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS,new TradingRange());
        rangeMap.put(TradingConstants.TRADING_DAYS_IN_MONTH,new TradingRange());
        rangeMap.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR,new TradingRange());
        rangeMap.put(TradingConstants.TRADING_DAYS_IN_YEAR,new TradingRange());
    }
    private void setTradingRanges(Integer key,Map<Integer,TradingRange> rangeMap,Quote quote){
        TradingRange range = rangeMap.get(key);
        if(range.getLow() == -1 || range.getLow() > quote.getOhlc().getLow()){
            range.setLow(quote.getOhlc().getLow());
        }
        if(range.getHigh() < quote.getOhlc().getHigh()){
            range.setHigh(quote.getOhlc().getHigh());
        }
    }

    /**
     * Round a double value to a specified number of decimal
     * places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public double round(double val, int places) {
        long factor = (long)Math.pow(10,places);
        val = val * factor;
        long tmp = Math.round(val);
        return (double)tmp / factor;
    }

    public MyStandardDeviation getMyStandardDeviation() {
        return myStandardDeviation;
    }
    public void setMyStandardDeviation(MyStandardDeviation myStandardDeviation) {
        this.myStandardDeviation = myStandardDeviation;
    }
}
