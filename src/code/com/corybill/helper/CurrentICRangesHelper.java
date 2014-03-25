package code.com.corybill.helper;

import code.com.corybill.control.dataStructures.MutableDouble;
import code.com.corybill.control.math.BlackScholes;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentICRangesHelper {
    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;

    private double getLowerBound(double stockPrice, double sd,double strikeInterval){
        double lowerBound = stockPrice - mathUtil.round(sd,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double change = mathUtil.round(lowerBound % strikeInterval,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        lowerBound = mathUtil.round(lowerBound - change,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);

        return lowerBound;
    }
    private double getUpperBound(double stockPrice, double sd,double strikeInterval){
        double upperBound = stockPrice + mathUtil.round(sd,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double change = mathUtil.round(upperBound % strikeInterval,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        upperBound = mathUtil.round((upperBound + (strikeInterval - change)),TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        return upperBound;
    }

    /**
     * 1. Get the first strike above Stock Price
     * 2. Get that value + (10*strike interval)
     * 3. if callPrice is greater than this number, then it must be a multiple of (2*strikeInterval) instead of (1*strikeInterval)
     * @param stockPrice
     * @param shortCallStrike
     * @param longCallStrike
     * @param strikeInterval
     * @return
     */
    private void verifyCorrectCall(Double stockPrice, MutableDouble shortCallStrike,MutableDouble longCallStrike,Double strikeInterval){
        if(strikeInterval == 5){
            return;
        }
        double firstStrike = stockPrice - mathUtil.round((stockPrice % strikeInterval),TradingConstants.STOCK_PRICING_ROUNDING_PRECISION) + strikeInterval;
        double topLevelBeforeIntervalIncrease = firstStrike + (10*strikeInterval);

        //If this is true then we need to make sure shortCallStrike is a multiple of strikeInterval*2
        if(shortCallStrike.getValue() > topLevelBeforeIntervalIncrease){
            if(shortCallStrike.getValue() % (2*strikeInterval) != 0){
                shortCallStrike.setValue(shortCallStrike.getValue() + strikeInterval);
                longCallStrike.setValue(shortCallStrike.getValue() + 2*strikeInterval);
            }else{
                //short call is okay, lets just reset the long call strike to make sure.
                longCallStrike.setValue(shortCallStrike.getValue() + 2*strikeInterval);
            }
        }else if(longCallStrike.getValue() > topLevelBeforeIntervalIncrease){
            if(longCallStrike.getValue() % (2*strikeInterval) != 0){
                longCallStrike.setValue(longCallStrike.getValue() + strikeInterval);
            }
        }
    }
    private void verifyCorrectPut(Double stockPrice, MutableDouble shortPutStrike,MutableDouble longPutStrike,Double strikeInterval){
        if(strikeInterval == 5){
            return;
        }

        double firstStrike = stockPrice - mathUtil.round((stockPrice % strikeInterval), TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double topLevelBeforeIntervalIncrease = firstStrike - (10*strikeInterval);

        //If this is true then we need to make sure shortCallStrike is a multiple of strikeInterval*2
        if(shortPutStrike.getValue() < topLevelBeforeIntervalIncrease){
            if(shortPutStrike.getValue() % (2*strikeInterval) != 0){
                shortPutStrike.setValue(shortPutStrike.getValue() - strikeInterval);
                longPutStrike.setValue(shortPutStrike.getValue() - 2*strikeInterval);
            }else{
                //short call is okay, lets just reset the long call strike to make sure.
                longPutStrike.setValue(shortPutStrike.getValue() - 2*strikeInterval);
            }
        }else if(longPutStrike.getValue() < topLevelBeforeIntervalIncrease){
            if(longPutStrike.getValue() % (2*strikeInterval) != 0){
                longPutStrike.setValue(longPutStrike.getValue() - strikeInterval);
            }
        }
    }

    private double getStrikeInterval(double price){
        if(price < 50){
            return .5;
        }else if(price < 100){
            return 2.50;
        }else{
            return 5;
        }
    }

    /**
     * First it gets the strike interval, upper bound, and then lower bound.  With this information it will find the options for this Iron Condor and store them in the map.
     * The map will have a Key of a day of the week (i.e. Calendar.MONDAY)
     * @param quote
     * @param rangeDays
     * @return
     */
    public List<VanillaOption> createCondorsOptions(Quote quote,int rangeDays){
        List<VanillaOption> list = new ArrayList<VanillaOption>();

        TradingRange tradingRange = quote.getTradingRanges().get(rangeDays);

        double upperLimit = tradingRange.getHigh() - quote.getOhlc().getOpen();
        double lowerLimit = quote.getOhlc().getOpen() - tradingRange.getLow();
        double strikeInterval = getStrikeInterval(quote.getOhlc().getClose());
        MutableDouble shortPutStrike = new MutableDouble( getLowerBound(quote.getOhlc().getClose(),lowerLimit,strikeInterval)  );
        MutableDouble shortCallStrike = new MutableDouble( getUpperBound(quote.getOhlc().getClose(),upperLimit,strikeInterval) );
        MutableDouble longPutStrike = new MutableDouble(shortPutStrike.getValue() - strikeInterval);
        MutableDouble longCallStrike = new MutableDouble(shortCallStrike.getValue() + strikeInterval);

        verifyCorrectCall(quote.getOhlc().getClose(),shortCallStrike,longCallStrike,strikeInterval);
        verifyCorrectPut(quote.getOhlc().getClose(), shortPutStrike, longPutStrike, strikeInterval);

        Calendar expirationCal = calendarHelper.getNextDay(Calendar.FRIDAY);
        String expiration = calendarHelper.getDateString(expirationCal);

        VanillaOption longCall = createSingleOption(quote.getSymbol(),longCallStrike.getValue(),quote,5,expiration,TradingConstants.LONG,TradingConstants.CALL);
        VanillaOption shortCall = createSingleOption(quote.getSymbol(),shortCallStrike.getValue(),quote,5,expiration,TradingConstants.SHORT,TradingConstants.CALL);
        VanillaOption shortPut = createSingleOption(quote.getSymbol(),shortPutStrike.getValue(),quote,5,expiration,TradingConstants.SHORT,TradingConstants.PUT);
        VanillaOption longPut = createSingleOption(quote.getSymbol(),longPutStrike.getValue(),quote,5,expiration,TradingConstants.LONG,TradingConstants.PUT);

        list.add(longCall);
        list.add(shortCall);
        list.add(shortPut);
        list.add(longPut);

        return list;
    }

    /**
     * Gets all of the weeks options ohlc prices for both puts and calls for all strikes passed in through @strikes.
     * It stores all of these strikes in vanillaOptions which is a reference to a list in VanillaOptionsDataLoad.
     * @param symbol
     * @param strike
     * @param quote
     * @param daysToExpiration
     * @param expiration
     * @param side
     * @param type
     * @return
     */
    private VanillaOption createSingleOption(String symbol,Double strike,Quote quote,int daysToExpiration,String expiration,String side, String type){
        MongoDatabase mongo = MongoDatabase.getInstance();

        double openTime =  mathUtil.getTime(daysToExpiration);

        OHLC ohlc = quote.getOhlc();

        VanillaOption option = new VanillaOption();
        OHLC optionOHLC = new OHLC();

        double openPrice = 0.0;

        //Grabs yearly vol from the quote.
        double volatility = quote.getVolatility().get(TradingConstants.NUM_VOL_CALC_DAYS);
        if(type.equals(TradingConstants.CALL)){
            openPrice = BlackScholes.getCallPrice(mathUtil, ohlc.getClose(), strike, volatility, openTime,side);
        }else if(type.equals(TradingConstants.PUT)){
            openPrice = BlackScholes.getPutPrice(mathUtil, ohlc.getClose(), strike, volatility, openTime,side);
        }

        option.setSymbol(symbol);
        option.setDaysToExpiration(daysToExpiration);
        option.setDate(quote.getDate());
        option.setExpiration(expiration);
        option.setQuote(quote);
        option.setOhlc(optionOHLC);
        option.setType(type);
        option.setSide(side);
        option.setStrike(strike);
        optionOHLC.setOpen(openPrice);

        return option;
    }

    public IronCondorCurrentRanges buildIronCondor(List<VanillaOption> options,Quote quote,double boundMultipleForSD, int daysForSD){
        OHLC condorOHLC = new OHLC();

        IronCondorCurrentRanges condor = new IronCondorCurrentRanges();
        condor.setWeeklyOHLC(condorOHLC);

        int executable = getOpen(options,condor);

        //This is an assumption that the expiration is on a friday.  Will have to tackle this on a week by week basis.
        Calendar expirationCal = calendarHelper.getNextDay(Calendar.FRIDAY);
        String expiration = calendarHelper.getDateString(expirationCal);
        Calendar startDateCal = calendarHelper.getNextDay(Calendar.MONDAY);
        String startDate = calendarHelper.getDateString(startDateCal);

        //Initialize Condor
        condor.setSymbol(quote.getSymbol());
        condor.setQuote(quote);
        condor.setStartDate(startDate);
        condor.setExpiration(expiration);
        condor.setStandardDeviationInterval(boundMultipleForSD);
        condor.setStandardDeviationDays(daysForSD);
        condor.setOptions(options);
        condor.calculateCreditForCondor();

        return condor;
    }

    /**
     * Return 0 We will not execute this trade.  Set open and close to 0
     * Return -1 We will only execute Bull Put side of trade.
     * Return 1 We will only execute Bear Call side of trade.
     * Return 2 We will execute both sides of the trade.
     * @param shortCall
     * @param longCall
     * @param shortPut
     * @param longPut
     * @return
     */
    private int getIronCondorExecutable(double shortCall, double longCall, double shortPut, double longPut){
        if(shortCall - longCall <= 0 && shortPut - longPut <=0){ //Do not execute trade.
            return 0;
        }else if(shortCall - longCall <=0){ //Only execute Bull Put side of trade
            return -1;
        }else if(shortPut - longPut <=0){ //Only execute Bear Call side of trade.
            return 1;
        }else{ // Execute both sides of trade.
            return 2;
        }
    }
    private double getCondorValue(int executable, double shortCall, double longCall, double shortPut, double longPut){
        if(executable == 0){
            return 0.0;
        }else if(executable == -1){
            return (shortPut - longPut);
        }else if(executable == 1){
            return (shortCall - longCall);
        }else{
            return (shortCall - longCall) + (shortPut - longPut);
        }
    }

    private int getOpen(List<VanillaOption> list,IronCondorCurrentRanges condor){
        try{
            double shortCall = IronCondor.getShortCall(list).getOhlc().getOpen();
            double longCall = IronCondor.getLongCall(list).getOhlc().getOpen();
            double shortPut = IronCondor.getShortPut(list).getOhlc().getOpen();
            double longPut = IronCondor.getLongPut(list).getOhlc().getOpen();

            int executable =  getIronCondorExecutable(shortCall, longCall, shortPut, longPut);

            //Condors are over valued so we will subtract $50 from all of them and call this the loss from the bid/ask spread
            double openValue = getCondorValue(executable, shortCall, longCall, shortPut, longPut);

            condor.getWeeklyOHLC().setOpen(openValue);
            return executable;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public MathUtil getMathUtil() {
        return mathUtil;
    }
    public void setMathUtil(MathUtil mathUtil) {
        this.mathUtil = mathUtil;
    }

    public CalendarHelper getCalendarHelper() {
        return calendarHelper;
    }
    public void setCalendarHelper(CalendarHelper calendarHelper) {
        this.calendarHelper = calendarHelper;
    }
}
