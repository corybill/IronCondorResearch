package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.control.mongoDao.VanillaOptionsDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.OptionsHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.Quote;
import code.com.corybill.model.UpdateDate;
import code.com.corybill.model.VanillaOption;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/22/13
 * Time: 8:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class VanillaOptionsDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(VanillaOptionsDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;
    private OptionsHelper dlHelper;

    private VanillaOptionsDao vanillaOptionsDao;
    private QuoteDao quoteDao;

    Calendar lengthOfTime;
    private List<String> listOfSymbols;
    private List<Quote> quotesQuery;

    @Override
    public void invoke() {
        Calendar today = calendarHelper.getMyCalendarInstance();
        lengthOfTime = calendarHelper.getMyCalendarInstance();
        lengthOfTime.add(Calendar.YEAR,-5);

        for(String symbol : listOfSymbols){
            long start = Calendar.getInstance().getTimeInMillis();

            quotesQuery =  quoteDao.getForVanillaOptionsLoad(symbol);
            getData(symbol);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug(symbol + " - " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            UpdateDate updateDate = UpdateDateDao.getUpdateDate();
            Calendar updateDateCal = calendarHelper.getCalendar(updateDate.getLastVanillaOptionUpdate());
            List<VanillaOption> saveList = new ArrayList<VanillaOption>();

            calendarHelper.popAllOfThisWeekFromList(quotesQuery);

            while(quotesQuery.size() != 0){

                //Get all quotes from this week.
                List<Quote> weeksQuotes = calendarHelper.getAllQuotesFromThisWeek(quotesQuery);

                //If week quotes size = 0 then we ran out of data for this symbol and can break out of the loop.
                if(weeksQuotes.size()==0){
                    break;
                }

                //Could be a Monday, Tuesday, or Wednesday
                Quote mondayQuote = weeksQuotes.get(weeksQuotes.size()-1);

                //Get all of the strikes we will be analyzing for this week
                List<Double> strikes = dlHelper.getWeeksStrikes(mondayQuote.getOhlc().getOpen());

                //Gets all the weeks options prices and stores them in the DAO storage.  The DAO takes care of when to save to the DB.
                dlHelper.getWeeksOptionPrices(symbol, strikes, weeksQuotes, saveList);

                weeksQuotes.clear();
            }
            vanillaOptionsDao.saveVanillaOption(saveList);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public boolean overTwoYearsAgo(Quote quote){
        if(lengthOfTime.after(calendarHelper.getCalendar(quote.getDate()))){
            return true;
        }
        return false;
    }

    public int getSmallestValue(List<Quote> list, int max){
        if(list.size() < TradingConstants.TRADING_DAYS_IN_YEAR){
            max = list.size();
        }
        return max;
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

    public VanillaOptionsDao getVanillaOptionsDao() {
        return vanillaOptionsDao;
    }
    public void setVanillaOptionsDao(VanillaOptionsDao vanillaOptionsDao) {
        this.vanillaOptionsDao = vanillaOptionsDao;
    }

    public OptionsHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(OptionsHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }
}
