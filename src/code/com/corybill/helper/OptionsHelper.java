package code.com.corybill.helper;

import code.com.corybill.control.math.BlackScholes;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.OHLC;
import code.com.corybill.model.Quote;
import code.com.corybill.model.VanillaOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/22/13
 * Time: 10:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class OptionsHelper {
    private MathUtil mathUtil;

    /**
     * Sorts a collection in order from lowest to highest.
     */
    public class MyDoubleComparable implements Comparator<Double> {
        @Override
        public int compare(Double o1, Double o2) {
            return (o1<o2 ? -1 : (o1==o2 ? 0 : 1));
        }
    }

    public List<Double> getWeeksStrikes(double stockPrice){
        double strikeInterval = getStrikeInterval(stockPrice);

        double change = stockPrice % strikeInterval;
        double lastDown = stockPrice - change;
        double lastUp = stockPrice + (strikeInterval - change);

        int initialIterations = 10;
        if(stockPrice > 2000){
            initialIterations = 40;
        }else if(stockPrice > 1000){
            initialIterations = 20;
        }else if(stockPrice > 500){
            initialIterations = 15;
        }else if(stockPrice > 100){
            initialIterations = 12;
        }

        List<Double> strikes = new ArrayList<Double>();
        for(int i=0; i<10; i++){
            strikes.add(lastDown);
            strikes.add(lastUp);
            lastDown = lastDown - strikeInterval;
            lastUp = lastUp + strikeInterval;
        }
        strikes.add(lastDown);
        strikes.add(lastUp);

        //Gets the strike interval for deep call & puts.
        strikeInterval = getExtendedStrikeInterval(stockPrice);

        if(lastUp % strikeInterval != 0){
            lastUp += strikeInterval/2;
            strikes.add(lastUp);
        }
        if(lastDown % strikeInterval != 0){
            lastDown -= strikeInterval/2;
            strikes.add(lastDown);
        }

        for(int i=0; i<4; i++){
            lastDown = lastDown - strikeInterval;
            lastUp = lastUp + strikeInterval;
            strikes.add(lastDown);
            strikes.add(lastUp);
        }

        //Sorts from lowest to highest
        Collections.sort(strikes, new MyDoubleComparable());
        return strikes;
    }

    /**
     * Gets all of the weeks options ohlc prices for both puts and calls for all strikes passed in through @strikes.
     * It stores all of these strikes in vanillaOptions which is a reference to a list in VanillaOptionsDataLoad.
     * @param symbol
     * @param strikes
     * @param weekQuotes
     * @return
     */
    public void getWeeksOptionPrices(String symbol,List<Double> strikes,List<Quote> weekQuotes,List<VanillaOption> list){
        MongoDatabase mongo = MongoDatabase.getInstance();
        try{
            //Used to keep track of the days remaining until expiration
            int daysToExpiration = 1;

            //The first quote in weekQuotes will hold the expiration for these options
            String expiration = weekQuotes.get(0).getDate();

            for(Quote quote : weekQuotes){
                double openTime =  mathUtil.getTime(daysToExpiration);
                double midDayTime = mathUtil.getTime(daysToExpiration - .5);
                double closeTime = mathUtil.getTime(daysToExpiration-1);

                double volatility=0.0;

                //Grabs yearly vol from the quote.
                volatility = quote.getVolatility().get(TradingConstants.TRADING_DAYS_IN_YEAR);

                for(Double strike : strikes){
                    OHLC ohlc = quote.getOhlc();

                    VanillaOption call = new VanillaOption();
                    OHLC callOHLC = new OHLC();

                    call.setSymbol(symbol);
                    call.setDaysToExpiration(daysToExpiration);
                    call.setDate(quote.getDate());
                    call.setExpiration(expiration);
                    call.setQuote(quote);
                    call.setOhlc(callOHLC);
                    call.setType(TradingConstants.CALL);
                    call.setStrike(strike);
                    callOHLC.setOpen(BlackScholes.getCallPrice(mathUtil,ohlc.getOpen(), strike, volatility, openTime,TradingConstants.LONG));
                    callOHLC.setHigh(BlackScholes.getCallPrice(mathUtil,ohlc.getHigh(), strike, volatility, midDayTime,TradingConstants.LONG));
                    callOHLC.setLow(BlackScholes.getCallPrice(mathUtil,ohlc.getLow(), strike, volatility, midDayTime,TradingConstants.LONG));
                    callOHLC.setClose(BlackScholes.getCallPrice(mathUtil,ohlc.getClose(), strike, volatility, closeTime,TradingConstants.LONG));

                    VanillaOption put = new VanillaOption();
                    OHLC putOHLC = new OHLC();
                    put.setSymbol(symbol);
                    put.setDaysToExpiration(daysToExpiration);
                    put.setDate(quote.getDate());
                    put.setExpiration(expiration);
                    put.setQuote(quote);
                    put.setOhlc(putOHLC);
                    put.setType(TradingConstants.PUT);
                    put.setStrike(strike);
                    putOHLC.setOpen(BlackScholes.getPutPrice(mathUtil,ohlc.getOpen(), strike, volatility, openTime,TradingConstants.LONG));
                    putOHLC.setHigh(BlackScholes.getPutPrice(mathUtil,ohlc.getLow(), strike, volatility, midDayTime,TradingConstants.LONG));
                    putOHLC.setLow(BlackScholes.getPutPrice(mathUtil,ohlc.getHigh(), strike, volatility, midDayTime,TradingConstants.LONG));
                    putOHLC.setClose(BlackScholes.getPutPrice(mathUtil,ohlc.getClose(), strike, volatility, closeTime,TradingConstants.LONG));

                    list.add(call);
                    list.add(put);
                }
                daysToExpiration++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getStrikeInterval(double price){
        if(price < 50){
            return .5;
        }else if(price < 100){
            return 2.50;
        }else{
            return 5.00;
        }
    }
    public double getExtendedStrikeInterval(double price){
        if(price < 50){
            return 1.00;
        }else if(price < 100){
            return 5.00;
        }else{
            return 5.00;
        }
    }

    public double getNextStrikeDown(double stockPrice, double strikeInterval){
        while(stockPrice % strikeInterval != 0){
            stockPrice = mathUtil.round((stockPrice - .01), TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        }
        return stockPrice;
    }
    public double getNextStrikeUp(double stockPrice, double strikeInterval){
        while(stockPrice % strikeInterval != 0){
            stockPrice = mathUtil.round((stockPrice + .01),TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        }
        return stockPrice;
    }

    public MathUtil getMathUtil() {
        return mathUtil;
    }
    public void setMathUtil(MathUtil mathUtil) {
        this.mathUtil = mathUtil;
    }
}
