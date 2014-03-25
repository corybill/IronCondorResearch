package code.com.corybill.helper;

import code.com.corybill.control.dataStructures.MutableDouble;
import code.com.corybill.control.math.BlackScholes;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorHelper {
    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;

    private static Map<String,String> map = new HashMap<String,String>();

    public static synchronized void addToMap(String s){
        map.put(s,s);
    }
    public static Map<String,String> getMap(){
        return map;
    }

    public void addToExpirations(String str,List<Expiration> expirations){
        for(Expiration expiration : expirations){
            if(expiration.equals(str)){
                return;
            }
        }
        expirations.add(new Expiration(str));
    }

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

        //TODO:  Verify if this needs to be > or >= for both statements

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


        //TODO:  Verify if this needs to be < or <= for both statements

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
     * @param quotes
     * @param standardDeviation
     * @param bound
     * @return
     */
    public Map<Integer,List<VanillaOption>> createCondorsOptions(List<Quote> quotes,double standardDeviation,double bound){
        Map<Integer,List<VanillaOption>> map = new HashMap<Integer,List<VanillaOption>>();
        try{

            //Double fridayClose = quotes.get(0).getOhlc().getOpen();
            Double mondayOpen = quotes.get(quotes.size()-1).getOhlc().getOpen();

            double lowerLimits = standardDeviation * bound;
            double strikeInterval = getStrikeInterval(mondayOpen);
            MutableDouble shortPutStrike = new MutableDouble( getLowerBound(mondayOpen,lowerLimits,strikeInterval)  );
            MutableDouble shortCallStrike = new MutableDouble( getUpperBound(mondayOpen,lowerLimits,strikeInterval) );
            MutableDouble longPutStrike = new MutableDouble(shortPutStrike.getValue() - strikeInterval);
            MutableDouble longCallStrike = new MutableDouble(shortCallStrike.getValue() + strikeInterval);

            verifyCorrectCall(mondayOpen,shortCallStrike,longCallStrike,strikeInterval);
            verifyCorrectPut(mondayOpen, shortPutStrike, longPutStrike, strikeInterval);

            Calendar expirationCal = calendarHelper.getCalendar(quotes.get(0).getDate());
            String expiration = calendarHelper.getDateString(expirationCal);

            for(Quote quote : quotes){
                Calendar c = calendarHelper.getCalendar(quote.getDate());
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int daysTillExpiration = Calendar.FRIDAY - dayOfWeek + 1;

                VanillaOption longCall = createSingleOption(quote.getSymbol(),longCallStrike.getValue(),quote,daysTillExpiration,expiration,TradingConstants.LONG,TradingConstants.CALL);
                VanillaOption shortCall = createSingleOption(quote.getSymbol(),shortCallStrike.getValue(),quote,daysTillExpiration,expiration,TradingConstants.SHORT,TradingConstants.CALL);
                VanillaOption shortPut = createSingleOption(quote.getSymbol(),shortPutStrike.getValue(),quote,daysTillExpiration,expiration,TradingConstants.SHORT,TradingConstants.PUT);
                VanillaOption longPut = createSingleOption(quote.getSymbol(),longPutStrike.getValue(),quote,daysTillExpiration,expiration,TradingConstants.LONG,TradingConstants.PUT);

                List<VanillaOption> list = new ArrayList<VanillaOption>();
                list.add(longCall);
                list.add(shortCall);
                list.add(shortPut);
                list.add(longPut);

                map.put(dayOfWeek,list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return map;
    }

    private VanillaOption createSingleOption(String symbol,Double strike,Quote quote,int daysToExpiration,String expiration,String side, String type){
        MongoDatabase mongo = MongoDatabase.getInstance();

        double openTime =  mathUtil.getTime(daysToExpiration);
        double highTime =  mathUtil.getTime(daysToExpiration-.5);
        double lowTime =  mathUtil.getTime(daysToExpiration-.5);
        double closeTime =  mathUtil.getTime(daysToExpiration-1);

        //Grabs average vol from 1 week, 2 week, 1 month, 1/2 year, and 1 year from the quote.
        double volatility = quote.getVolatility().get(TradingConstants.NUM_VOL_CALC_DAYS);

        OHLC ohlc = quote.getOhlc();

        VanillaOption option = new VanillaOption();
        OHLC optionOHLC = new OHLC();

        double openPrice = 0.0;
        double highPrice = 0.0;
        double lowPrice = 0.0;
        double closePrice = 0.0;
        if(type.equals(TradingConstants.CALL)){
            openPrice = BlackScholes.getCallPrice(mathUtil, ohlc.getOpen(), strike, volatility, openTime,side);
            highPrice = BlackScholes.getCallPrice(mathUtil, ohlc.getHigh(), strike, volatility, highTime,side);
            lowPrice = BlackScholes.getCallPrice(mathUtil, ohlc.getLow(), strike, volatility, lowTime,side);
            closePrice = BlackScholes.getCallPrice(mathUtil, ohlc.getClose(), strike, volatility, closeTime,side);
        }else if(type.equals(TradingConstants.PUT)){
            openPrice = BlackScholes.getPutPrice(mathUtil, ohlc.getOpen(), strike, volatility, openTime,side);
            highPrice = BlackScholes.getPutPrice(mathUtil, ohlc.getLow(), strike, volatility, highTime,side);
            lowPrice = BlackScholes.getPutPrice(mathUtil, ohlc.getHigh(), strike, volatility, lowTime,side);
            closePrice = BlackScholes.getPutPrice(mathUtil, ohlc.getClose(), strike, volatility, closeTime,side);
        }

        //if(closeTime == 0 && type.equals(TradingConstants.CALL) && ohlc.get)

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
        optionOHLC.setHigh(highPrice);
        optionOHLC.setLow(lowPrice);
        optionOHLC.setClose(closePrice);

        return option;
    }

    public IronCondor buildIronCondor(Map<Integer,List<VanillaOption>> map,List<Quote> weeksQuotes,double boundMultipleForSD, int daysForSD){
        OHLC condorOHLC = new OHLC();
        //set the low for OHLC as -1 so we know if a value has been set or not.  All other values won't need to know this.
        condorOHLC.setLow(-1);

        String symbol = weeksQuotes.get(0).getSymbol();
        IronCondor condor = new IronCondor();
        condor.setWeeklyOHLC(condorOHLC);

        int executable = 0;

        Quote mondayQuote = null;
        Quote fridayQuote = null;

        boolean needToLoadLow = true;
        List<VanillaOption> list = null;
        for(int i=Calendar.MONDAY; i<=Calendar.FRIDAY; i++){
            if(map.get(i) == null){
                continue;
            }
            list = map.get(i);
            String currentDate;

            //Since we don't know which day is the expiration, we will update this value at every iteration.
            fridayQuote = list.get(0).getQuote();
            currentDate = list.get(0).getDate();

            if(needToLoadLow){ //setOpen()
                needToLoadLow = false;
                mondayQuote = list.get(0).getQuote();
                executable = getOpen(list,condor);
            }
            getHigh(list,condor,executable,currentDate);
            getLow(list,condor,executable,currentDate);
        }

        getClose(list,condor,executable,fridayQuote);

        //Initialize Condor
        condor.setSymbol(symbol);
        condor.setStartDate(mondayQuote.getDate());
        condor.setExpiration(fridayQuote.getDate());
        condor.setStandardDeviationInterval(boundMultipleForSD);
        condor.setStandardDeviationDays(daysForSD);
        condor.setOptionsByDay(map);
        condor.setWeeksQuotes(weeksQuotes);

        return condor;
    }

    /**
     * Return 0 We will not execute this trade.  Set open and close to 0
     * Return -1 We will only execute Bull Put side of trade.
     * Return 1 We will only execute Bear Call side of trade.
     * Return 2 We will execute both sides of the tarde.
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
        double value = 0.0;
        if(executable == 0){
            value = 0.0;
        }else if(executable == -1){
            value = (shortPut - longPut);
        }else if(executable == 1){
            value = (shortCall - longCall);
        }else{
            value = (shortCall - longCall) + (shortPut - longPut);
        }

        return (value < 0) ? 0 : value;
    }
    /*private double getCondorCloseValue(int executable, VanillaOption shortCall, VanillaOption longCall, VanillaOption shortPut, VanillaOption longPut, Quote fridayQuote){
        double closingPrice = 0.0;
        if(executable == 0){
            return 0.0;
        }else if(executable == -1){
            if(fridayQuote.getOhlc().getClose() > shortPut.getStrike()){
                closingPrice = 0;
            }else if(fridayQuote.getOhlc().getClose() < shortPut.getStrike()){
                double stockDiff = shortPut.getStrike() - fridayQuote.getOhlc().getClose();
                double strikeDiff = shortPut.getStrike() - longPut.getStrike();
                closingPrice = (stockDiff > strikeDiff) ? strikeDiff : stockDiff;
            }
        }else if(executable == 1){
            if(fridayQuote.getOhlc().getClose() < shortCall.getStrike()){
                closingPrice = 0;
            }else if(fridayQuote.getOhlc().getClose() > shortCall.getStrike()){
                double stockDiff = fridayQuote.getOhlc().getClose() - shortCall.getStrike();
                double strikeDiff = longCall.getStrike() - shortCall.getStrike();
                closingPrice = (stockDiff > strikeDiff) ? strikeDiff : stockDiff;
            }
        }else{
            if(fridayQuote.getOhlc().getClose() > shortPut.getStrike() && fridayQuote.getOhlc().getClose() < shortCall.getStrike()){
                closingPrice = 0;
            }else if(fridayQuote.getOhlc().getClose() < shortPut.getStrike()){
                double stockDiff = shortPut.getStrike() - fridayQuote.getOhlc().getClose();
                double strikeDiff = shortPut.getStrike() - longPut.getStrike();
                closingPrice = (stockDiff > strikeDiff) ? strikeDiff : stockDiff;
            }else if(fridayQuote.getOhlc().getClose() > shortCall.getStrike()){
                double stockDiff = fridayQuote.getOhlc().getClose() - shortCall.getStrike();
                double strikeDiff = longCall.getStrike() - shortCall.getStrike();
                closingPrice = (stockDiff > strikeDiff) ? strikeDiff : stockDiff;
            }
        }
        return closingPrice;
    }*/

    private int getOpen(List<VanillaOption> list,IronCondor condor){
        try{
            double shortCall = IronCondor.getShortCall(list).getOhlc().getOpen();
            double longCall = IronCondor.getLongCall(list).getOhlc().getOpen();
            double shortPut = IronCondor.getShortPut(list).getOhlc().getOpen();
            double longPut = IronCondor.getLongPut(list).getOhlc().getOpen();

            int executable =  getIronCondorExecutable(shortCall, longCall, shortPut, longPut);

            double openValue = getCondorValue(executable, shortCall, longCall, shortPut, longPut);

            condor.getWeeklyOHLC().setOpen(openValue);
            return executable;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    private void getHigh(List<VanillaOption> list,IronCondor condor,int executable,String currentDate){
        double shortCall = IronCondor.getShortCall(list).getOhlc().getHigh();
        double longCall = IronCondor.getLongCall(list).getOhlc().getHigh();
        double shortPut = IronCondor.getShortPut(list).getOhlc().getHigh();
        double longPut = IronCondor.getLongPut(list).getOhlc().getHigh();

        double highValue = getCondorValue(executable, shortCall, longCall, shortPut, longPut);

        if(highValue > condor.getWeeklyOHLC().getHigh()){
            condor.getWeeklyOHLC().setDateOfHighValue(currentDate);
            condor.getWeeklyOHLC().setHigh(highValue);
        }
    }
    private void getLow(List<VanillaOption> list,IronCondor condor,int executable,String currentDate){
        double shortCall = IronCondor.getShortCall(list).getOhlc().getLow();
        double longCall = IronCondor.getLongCall(list).getOhlc().getLow();
        double shortPut = IronCondor.getShortPut(list).getOhlc().getLow();
        double longPut = IronCondor.getLongPut(list).getOhlc().getLow();

        double lowValue = getCondorValue(executable, shortCall, longCall, shortPut, longPut);

        if(lowValue < condor.getWeeklyOHLC().getLow() || condor.getWeeklyOHLC().getLow() == -1 ){
            condor.getWeeklyOHLC().setDateOfLowValue(currentDate);
            condor.getWeeklyOHLC().setLow(lowValue);
        }
    }
    private void getClose(List<VanillaOption> list,IronCondor condor, int executable,Quote fridayQuote){
        double shortCall = IronCondor.getShortCall(list).getOhlc().getClose();
        double longCall = IronCondor.getLongCall(list).getOhlc().getClose();
        double shortPut = IronCondor.getShortPut(list).getOhlc().getClose();
        double longPut = IronCondor.getLongPut(list).getOhlc().getClose();

        double closeValue = getCondorValue(executable, shortCall, longCall, shortPut, longPut);
        condor.getWeeklyOHLC().setClose(closeValue);
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
