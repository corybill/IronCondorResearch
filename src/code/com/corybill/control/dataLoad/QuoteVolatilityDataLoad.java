package code.com.corybill.control.dataLoad;

import code.com.corybill.control.dataStructures.MyArrayList;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.Quote;
import code.com.corybill.model.TradingRange;
import code.com.corybill.model.UpdateDate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/24/13
 * Time: 9:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuoteVolatilityDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(QuoteVolatilityDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;

    private QuoteDao quoteDao;

    private List<String> listOfSymbols;

    private List<Quote> quotesQuery;
    UpdateDate updateDate;


    @Override
    public void invoke() {
        updateDate = UpdateDateDao.getUpdateDate();

        for(String symbol : listOfSymbols){
            long start = Calendar.getInstance().getTimeInMillis();

            quotesQuery = quoteDao.getForQuoteVolatilityDataLoad(symbol);
            getData(symbol);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug(symbol + " - " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            List<Quote> saveList = new ArrayList<Quote>();
            MyArrayList myList = null;
            boolean gotInitialAddedPercentage = false;

            for(int i=0; i<quotesQuery.size(); ){
                Quote quote = quotesQuery.get(0);

                Calendar frontDay = calendarHelper.getCalendar(quote.getDate());

                //We have reached our update date and can stop.
                if(calendarHelper.getCalendar(updateDate.getLastVolatilityUpdate()).after(frontDay)){
                    break;
                }

                //Will hold a list of at most the first 'TradingConstants.TRADING_DAYS_IN_A_YEAR' from quotesQuery in chronological order
                List<Quote> chronQuotes;

                /**
                 * First time through we need to get a list of log differences which are stored in myList.
                 * If not the first time through we will remove the date closest to present time and add one on in the past.
                 * The date closest to present time is the date we are calculating volatility and sd for.  It will be stored in that quote.
                 */
                if(!gotInitialAddedPercentage){
                    gotInitialAddedPercentage = true;

                    int quotesQueryMax = getSmallestValue(quotesQuery, TradingConstants.TRADING_DAYS_IN_YEAR);
                    chronQuotes = quotesQuery.subList(0,quotesQueryMax);

                    //Gets the initial list that will hold the summed log diff of the list.  After this we will start adding in one new value and
                    //subtracting one new value instead of calculating the entire list every time.
                    myList = mathUtil.getSummedLogDifference(chronQuotes);

                    double numQuotes = getSmallestValue(chronQuotes, TradingConstants.TRADING_DAYS_IN_YEAR);

                    myList.mean = myList.total / numQuotes;
                }else{
                    //If myList has no items left then we only have one quote left in quotesQuery.
                    //For the last quote we will just copy the prior days information and continue.
                    if(myList.size() <= 5){
                        Quote quoteToCopy = saveList.get(saveList.size()-1);

                        quote.setDailyLogDiff(quoteToCopy.getDailyLogDiff());
                        quote.setStandardDeviation(quoteToCopy.getStandardDeviation());
                        quote.setMean(quoteToCopy.getMean());
                        quote.setTradingRanges(quoteToCopy.getTradingRanges());
                        quote.setVolatility(quoteToCopy.getVolatility());

                        saveList.add(quotesQuery.remove(0));
                        continue;
                    }else if(myList.size()==5){
                        System.out.print("");
                    }

                    //Gets at most first TradingConstants.TRADING_DAYS_IN_YEAR from quotesQuery in chronological order
                    int quotesQueryMax = getSmallestValue(quotesQuery, TradingConstants.TRADING_DAYS_IN_YEAR);
                    chronQuotes = quotesQuery.subList(0,quotesQueryMax);

                    //Remove the quote from the front of myList.  This total will need to be removed from from myListTotal
                    Quote removedQuote = myList.remove(0);
                    myList.total = myList.total - removedQuote.getDailyLogDiff();

                    //If we still have a full years quotes in the db, we will need to add the quote at the end of chronQuotes
                    if(chronQuotes.size() >= TradingConstants.TRADING_DAYS_IN_YEAR){
                        Quote myListLast =  myList.get(myList.size()-1);
                        Quote chronQuotesLast = chronQuotes.get(chronQuotes.size()-1);

                        double newLogPriceDiff = Math.log( myListLast.getOhlc().getClose() / chronQuotesLast.getOhlc().getClose() );
                        chronQuotesLast.setDailyLogDiff(newLogPriceDiff);

                        myList.add(chronQuotesLast);

                        myList.total = myList.total + chronQuotesLast.getDailyLogDiff();
                    }

                    //Either way we need to update the mean because we removed one qoute.
                    myList.mean = Math.abs(myList.total / myList.size());
                }

                //Set volatility values
                if(quote.getVolatility() == null){
                    quote.setVolatility(new HashMap<Integer, Double>());
                }
                mathUtil.getVolatilityWithMean(quote.getVolatility(),myList);

                //Set StandardDeviation values
                if(quote.getStandardDeviation() == null){
                    quote.setStandardDeviation(new HashMap<Integer, Double>());
                }
                if(quote.getMean() == null){
                    quote.setMean(new HashMap<Integer, Double>());
                }
                if(quote.getTradingRanges() == null){
                    quote.setTradingRanges(new HashMap<Integer, TradingRange>());
                }
                int max = getSmallestValue(chronQuotes, TradingConstants.TRADING_DAYS_IN_YEAR);
                mathUtil.getSDAndMean(quote.getStandardDeviation(), quote.getMean(), quote.getTradingRanges(), chronQuotes.subList(0, max));

                //We now pop off of the main list and place into save list.  This makes processing quotesQuery very easy because we are always
                //processing from the front of that list.
                saveList.add(quotesQuery.remove(0));
            }
            //Save quotes to mongoDao
            quoteDao.saveQuotes(saveList);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public List<String> getListOfSymbols() {
        return listOfSymbols;
    }
    public void setListOfSymbols(List<String> listOfSymbols) {
        this.listOfSymbols = listOfSymbols;
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

    public int getSmallestValue(List<Quote> list, int max){
        if(list.size() < TradingConstants.TRADING_DAYS_IN_YEAR){
            max = list.size();
        }
        return max;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }
}
