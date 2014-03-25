package code.com.corybill.control.dataLoad;

import code.com.corybill.control.dataStructures.MutableDouble;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.CurrentOptionsHelper;
import code.com.corybill.helper.SymbolHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.IronCondorInvest;
import code.com.corybill.model.Quote;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/6/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentOptionsDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(CurrentOptionsDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;
    private CurrentOptionsHelper dlHelper;

    private QuoteDao quoteDao;

    private List<String> listOfSymbols;
    private Map<Double,List<IronCondorInvest>> condors = new HashMap<Double, List<IronCondorInvest>>();

    //List to be saved to mongoDao
    Quote quote;

    String expirationDay;

    @Override
    public void invoke() {
        Calendar today = calendarHelper.getMyCalendarInstance();

        while(today.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY){
            calendarHelper.stepCalendarBusinessDay(today,1);
        }

        expirationDay = calendarHelper.getCurrentOptionsDateString(today);

        for(String symbol : listOfSymbols){
            long start = Calendar.getInstance().getTimeInMillis();

            quote = quoteDao.getForCurrentOptions(symbol);
            if(quote == null){
                continue;
            }

            String symbolToUse = SymbolHelper.cleanSymbols(symbol);

            for(double deviation : TradingConstants.deviations){
                getData(symbol,deviation);
            }

            long stop = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (stop - start) / 60000.00;
            log.debug(symbol + ": " + totalTimeInMinutes);
        }

        for(double deviation : TradingConstants.deviations){
            List<IronCondorInvest> orderedList = dlHelper.orderCreditHighToLow(condors.get(deviation));
            condors.put(deviation,orderedList);
        }
        dlHelper.writeToFile(condors);

    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            double close = quote.getOhlc().getClose();
            double bound = doubles[0];
            Double standardDeviation = quote.getStandardDeviation().get(21);

            double lowerLimits = standardDeviation * bound;
            double strikeInterval = dlHelper.getStrikeInterval(close);
            MutableDouble shortPutStrike = new MutableDouble( dlHelper.getLowerBound(close,lowerLimits,strikeInterval)  );
            MutableDouble shortCallStrike = new MutableDouble( dlHelper.getUpperBound(close,lowerLimits,strikeInterval) );
            MutableDouble longPutStrike = new MutableDouble(shortPutStrike.getValue() - strikeInterval);
            MutableDouble longCallStrike = new MutableDouble(shortCallStrike.getValue() + strikeInterval);

            dlHelper.verifyCorrectCall(close,shortCallStrike,longCallStrike,strikeInterval);
            dlHelper.verifyCorrectPut(close, shortPutStrike, longPutStrike, strikeInterval);

            String longCallId = dlHelper.prepareOptionId(symbol,expirationDay,longCallStrike.getValue(),"C");
            String shortCallId = dlHelper.prepareOptionId(symbol,expirationDay,shortCallStrike.getValue(),"C");
            String shortPutId = dlHelper.prepareOptionId(symbol,expirationDay,shortPutStrike.getValue(),"P");
            String longPutId = dlHelper.prepareOptionId(symbol,expirationDay,longPutStrike.getValue(),"P");

            String longCallHtml = dlHelper.getOptionHtml(longCallId);
            String shortCallHtml = dlHelper.getOptionHtml(shortCallId);
            String shortPutHtml = dlHelper.getOptionHtml(shortPutId);
            String longPutHtml = dlHelper.getOptionHtml(longPutId);

            String bidSplit = "Bid:";
            String askSplit = "Ask:";

            double longCallBid = dlHelper.getBidAskPrice(longCallHtml,bidSplit,longCallId);
            double longCallAsk = dlHelper.getBidAskPrice(longCallHtml,askSplit,longCallId);

            double shortCallBid = dlHelper.getBidAskPrice(shortCallHtml,bidSplit,shortCallId);
            double shortCallAsk = dlHelper.getBidAskPrice(shortCallHtml,askSplit,shortCallId);

            double shortPutBid = dlHelper.getBidAskPrice(shortPutHtml,bidSplit,shortPutId);
            double shortPutAsk = dlHelper.getBidAskPrice(shortPutHtml,askSplit,shortPutId);

            double longPutBid = dlHelper.getBidAskPrice(longPutHtml,bidSplit,longPutId);
            double longPutAsk = dlHelper.getBidAskPrice(longPutHtml,askSplit,longPutId);

            if(longCallAsk == 0){
                shortCallBid = 0;
            }
            if(longPutAsk == 0){
                shortPutBid = 0;
            }

            IronCondorInvest condor = new IronCondorInvest();
            condor.setSymbol(symbol);
            condor.setQuote(quote);
            condor.setStandardDeviationDays(21);
            condor.setStandardDeviationInterval(bound);
            condor.setStrikeInterval(strikeInterval);
            condor.setLongCallStrike(longCallStrike.getValue());
            condor.setShortCallStrike(shortCallStrike.getValue());
            condor.setShortPutStrike(shortPutStrike.getValue());
            condor.setLongPutStrike(longPutStrike.getValue());
            condor.setLongCallBid(longCallBid);
            condor.setLongCallAsk(longCallAsk);
            condor.setShortCallBid(shortCallBid);
            condor.setShortCallAsk(shortCallAsk);
            condor.setShortPutBid(shortPutBid);
            condor.setShortPutAsk(shortPutAsk);
            condor.setLongPutBid(longPutBid);
            condor.setLongPutAsk(longPutAsk);
            condor.calculateCreditForCondor();

            List<IronCondorInvest> list = condors.get(bound);
            if(list == null){
                list = new ArrayList<IronCondorInvest>();
                condors.put(bound,list);
            }
            list.add(condor);

        }catch(Exception e){
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

    public CurrentOptionsHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(CurrentOptionsHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }
}