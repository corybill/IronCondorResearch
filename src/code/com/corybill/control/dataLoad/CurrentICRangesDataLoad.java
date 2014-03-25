package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.IronCondorCurrentRangesDao;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.CurrentICRangesHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.IronCondorCurrentRanges;
import code.com.corybill.model.Quote;
import code.com.corybill.model.VanillaOption;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/6/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentICRangesDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(CurrentICRangesDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;
    private CurrentICRangesHelper dlHelper;

    private IronCondorCurrentRangesDao icCurrentRangesDao;
    private QuoteDao quoteDao;

    private List<String> listOfSymbols;

    private Calendar lengthOfTime;

    List<IronCondorCurrentRanges> condors;

    //List to be saved to mongoDao
    List<Quote> quotesQuery;

    @Override
    public void invoke() {
        Calendar today = calendarHelper.getMyCalendarInstance();

        for(String symbol : listOfSymbols){
            long start = Calendar.getInstance().getTimeInMillis();

            quotesQuery = quoteDao.getForIronCondorCurrentDataLoad(symbol);

            condors = new ArrayList<IronCondorCurrentRanges>();
            for(double deviation : TradingConstants.deviations){
                for(int day : TradingConstants.condorDays){
                    double[] doubles = {day,deviation};
                    getData(symbol,doubles);
                }
            }
            icCurrentRangesDao.saveIronCondors(condors);

            long stop = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (stop - start) / 60000.00;
            log.debug(symbol + ": " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            if(quotesQuery.size() > 0){

                Quote quote = quotesQuery.get(0);

                //If the stock price is under $5, we wont want to trade it.
                if(quote.getOhlc().getClose() < 5.00){
                    return;
                }

                //daysSD will hold the key for the current standard deviation we are looking for.
                double standardDeviation = quote.getStandardDeviation().get((int)doubles[0]);

                //Gets the options that will make up the IronCondor.
                List<VanillaOption> options = dlHelper.createCondorsOptions(quote, (int)doubles[0]);

                IronCondorCurrentRanges condor = dlHelper.buildIronCondor(options, quote, doubles[1], (int) doubles[0]);

                condors.add(condor);
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

    public CurrentICRangesHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(CurrentICRangesHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public IronCondorCurrentRangesDao getIcCurrentRangesDao() {
        return icCurrentRangesDao;
    }
    public void setIcCurrentRangesDao(IronCondorCurrentRangesDao icCurrentRangesDao) {
        this.icCurrentRangesDao = icCurrentRangesDao;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }
}