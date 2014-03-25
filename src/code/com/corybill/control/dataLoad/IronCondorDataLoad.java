package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.IronCondorDao;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.control.mongoDao.VanillaOptionsDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.IronCondorHelper;
import code.com.corybill.helper.SymbolHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.IronCondor;
import code.com.corybill.model.Quote;
import code.com.corybill.model.VanillaOption;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/6/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(IronCondorDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;
    private IronCondorHelper dlHelper;

    private VanillaOptionsDao vanillaOptionsDao;
    private IronCondorDao ironCondorDao;
    private QuoteDao quoteDao;

    private List<String> listOfSymbols;

    private Calendar lengthOfTime;

    List<IronCondor> condors;

    //List to be saved to mongoDao
    List<Quote> quotesQuery;
    boolean first = true;

    @Override
    public void invoke() {
        Calendar today = calendarHelper.getMyCalendarInstance();

        for(String symbol : listOfSymbols){
            symbol = SymbolHelper.ironCondorDataLoadCleaner(symbol);
            long start = Calendar.getInstance().getTimeInMillis();

            quotesQuery = quoteDao.getForIronCondorDataLoad(symbol);

            condors = new ArrayList<IronCondor>();
            first = true;
            for(double deviation : TradingConstants.deviations){
                for(int day : TradingConstants.condorDays){
                    double[] doubles = {day,deviation};
                    getData(symbol,doubles);
                    first = false;
                }
            }
            ironCondorDao.saveIronCondors(condors);

            long stop = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (stop - start) / 60000.00;
            log.debug(symbol + ": " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            if(quotesQuery.size() == 0){
                System.out.println("SYMBOL: " + symbol + " never had any quotes.");
            }
            int fridayIndex = calendarHelper.getLastWeekFridayIndex(quotesQuery,0);
            int mondayIndex = calendarHelper.getThisWeekMondayIndex(quotesQuery,fridayIndex);

            while(fridayIndex >= 0 && mondayIndex > 0){

                //Get all quotes from this week.
                List<Quote> weeksQuotes = quotesQuery.subList(fridayIndex,mondayIndex);

                //If week quotes size = 0 then we ran out of data for this symbol and can break out of the loop.
                if(weeksQuotes.size()==0){
                    break;
                }

                //Could be a Monday, Tuesday, or Wednesday...we don't really care.
                Quote mondayQuote = weeksQuotes.get(weeksQuotes.size()-1);

                //If the stock price on Monday is under $5, we wont want to trade it.
                if(mondayQuote.getOhlc().getClose() < 5.00){
                    fridayIndex = mondayIndex;
                    mondayIndex = calendarHelper.getThisWeekMondayIndex(quotesQuery,fridayIndex);
                    continue;
                }

                //Could be Friday or Thursday...we don't really care.
                Quote fridayQuote = weeksQuotes.get(0);

                //Only going back to 2000
                if(calendarHelper.getCalendar(fridayQuote.getDate()).before(calendarHelper.getCalendar("2000-01-01"))){
                    break;
                }

                //daysSD will hold the key for the current standard deviation we are looking for.
                double standardDeviation = mondayQuote.getStandardDeviation().get((int)doubles[0]);

                //Gets the options from that will make up the IronCondor.
                Map<Integer,List<VanillaOption>> vanillaOptionsMap = dlHelper.createCondorsOptions(weeksQuotes,standardDeviation,doubles[1]);

                IronCondor condor = dlHelper.buildIronCondor(vanillaOptionsMap,weeksQuotes,doubles[1],(int)doubles[0]);

                condors.add(condor);

                fridayIndex = mondayIndex;
                mondayIndex = calendarHelper.getThisWeekMondayIndex(quotesQuery,fridayIndex);

                if(first){
                    IronCondorHelper.addToMap(fridayQuote.getDate());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
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

    public IronCondorHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(IronCondorHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public VanillaOptionsDao getVanillaOptionsDao() {
        return vanillaOptionsDao;
    }
    public void setVanillaOptionsDao(VanillaOptionsDao vanillaOptionsDao) {
        this.vanillaOptionsDao = vanillaOptionsDao;
    }

    public IronCondorDao getIronCondorDao() {
        return ironCondorDao;
    }
    public void setIronCondorDao(IronCondorDao ironCondorDao) {
        this.ironCondorDao = ironCondorDao;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }
}