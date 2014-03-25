package code.com.corybill.helper;

import code.com.corybill.model.Quote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/7/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class VolRankingHelper {
    private Map<Integer,List<Quote>> sdMap = new HashMap<Integer, List<Quote>>();
    private Map<Integer,List<Quote>> volMap = new HashMap<Integer, List<Quote>>();

    /**
     * Orders Items in the list by profit assuming that we always hold the Condor to expiration.
     * @param quote
     */
    public void addToOrderedVolatilityMap(Quote quote,int key){
        List<Quote> list = volMap.get(key);
        if(list.size() == 0){
            list.add(quote);
            return;
        }
        for(int i=0; i<list.size(); i++){
            Quote listQ = list.get(i);
            if(quote.getVolatility().get(key) > listQ.getVolatility().get(key)){
                list.add(i,quote);
                return;
            }
        }
        list.add(quote);
    }
    public void addToOrderedSdMap(Quote quote,int key){
        List<Quote> list = sdMap.get(key);
        if(list.size() == 0){
            list.add(quote);
            return;
        }
        for(int i=0; i<list.size(); i++){
            Quote listQ = list.get(i);
            double percentSd = quote.getStandardDeviation().get(key) / quote.getOhlc().getOpen();
            double percentSdQ = listQ.getStandardDeviation().get(key) / listQ.getOhlc().getOpen();

            if(percentSd > percentSdQ){
                list.add(i,quote);
                return;
            }
        }
        list.add(quote);
    }
    public void setRanks(){
        for(Integer time : TradingConstants.allDays){
            List<Quote> sdList = sdMap.get(time);
            List<Quote> volList = volMap.get(time);

            for(int i=0; i<sdList.size(); i++){
                Quote sdQuote = sdList.get(i);
                Quote volQuote = volList.get(i);

                sdQuote.getSdRank().put(time,i+1);
                volQuote.getVolRank().put(time,i+1);
            }
        }
    }

    public void setAllQuoteVolRanks(Quote quote){
        addToOrderedVolatilityMap(quote, TradingConstants.TRADING_DAYS_IN_YEAR);
        addToOrderedVolatilityMap(quote, TradingConstants.TRADING_DAYS_IN_HALF_YEAR);
        addToOrderedVolatilityMap(quote, TradingConstants.TRADING_DAYS_IN_MONTH);
        addToOrderedVolatilityMap(quote, TradingConstants.TRADING_DAYS_IN_2_WEEKS);
        addToOrderedVolatilityMap(quote, TradingConstants.TRADING_DAYS_IN_WEEK);

        addToOrderedSdMap(quote, TradingConstants.TRADING_DAYS_IN_YEAR);
        addToOrderedSdMap(quote, TradingConstants.TRADING_DAYS_IN_HALF_YEAR);
        addToOrderedSdMap(quote, TradingConstants.TRADING_DAYS_IN_MONTH);
        addToOrderedSdMap(quote, TradingConstants.TRADING_DAYS_IN_2_WEEKS);
        addToOrderedSdMap(quote, TradingConstants.TRADING_DAYS_IN_WEEK);
    }

    public void resetSaveMaps(){
        volMap = new HashMap<Integer, List<Quote>>();
        sdMap = new HashMap<Integer, List<Quote>>();

        List<Quote> yearVol = new ArrayList<Quote>();
        List<Quote> halfYearVol = new ArrayList<Quote>();
        List<Quote> monthVol = new ArrayList<Quote>();
        List<Quote> twoWeeksVol = new ArrayList<Quote>();
        List<Quote> weekVol = new ArrayList<Quote>();

        List<Quote> yearSd = new ArrayList<Quote>();
        List<Quote> halfYearSd = new ArrayList<Quote>();
        List<Quote> monthSd = new ArrayList<Quote>();
        List<Quote> twoWeeksSd = new ArrayList<Quote>();
        List<Quote> weekSd = new ArrayList<Quote>();

        volMap.put(TradingConstants.TRADING_DAYS_IN_WEEK,weekVol);
        volMap.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS,twoWeeksVol);
        volMap.put(TradingConstants.TRADING_DAYS_IN_MONTH,monthVol);
        volMap.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR,halfYearVol);
        volMap.put(TradingConstants.TRADING_DAYS_IN_YEAR,yearVol);

        sdMap.put(TradingConstants.TRADING_DAYS_IN_WEEK,weekSd);
        sdMap.put(TradingConstants.TRADING_DAYS_IN_2_WEEKS,twoWeeksSd);
        sdMap.put(TradingConstants.TRADING_DAYS_IN_MONTH,monthSd);
        sdMap.put(TradingConstants.TRADING_DAYS_IN_HALF_YEAR,halfYearSd);
        sdMap.put(TradingConstants.TRADING_DAYS_IN_YEAR,yearSd);
    }
}
