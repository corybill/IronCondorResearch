package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.SingleQuoteHelper;
import code.com.corybill.helper.SymbolHelper;
import code.com.corybill.model.Quote;
import code.com.corybill.model.UpdateDate;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/22/12
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class SingleQuoteDataLoad implements IDataLoad  {
    private static Logger log = Logger.getLogger(SingleQuoteDataLoad.class);

    private MathUtil mathUtil;
    private CalendarHelper calendarHelper;
    private SingleQuoteHelper dlHelper;

    private QuoteDao quoteDao;

    private static final String originalPast = 0 + "&" + 1 + "&" + 1970 + "&";
    private static final String YAHOO_FINANCE = "http://ichart.finance.yahoo.com/table.csv?";

    public String currentURL;

    //Will hold a list of quotes with symbols only at first.
    public List<String> quotes;

    public void invoke(){
        UpdateDate updateDate = UpdateDateDao.getUpdateDate();

        String urlEnd = "&g=d&ignore=.csv&s=";

        Calendar today = calendarHelper.getMyCalendarInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        //TODO: Verify that adding one to the day of month is correct here. Maybe it should be, if past and today equal, then add one????
        Calendar past = calendarHelper.getCalendar(updateDate.getLastSingleQuoteUpdate());
        past.add(Calendar.DAY_OF_MONTH,1);

        int pastYear = past.get(Calendar.YEAR);
        int pastMonth = past.get(Calendar.MONTH);
        int pastDay = past.get(Calendar.DAY_OF_MONTH);

        String a = "a="+pastMonth;
        String b = "b="+pastDay;
        String c = "c="+pastYear;

        String d = "d="+month;
        String e = "e="+day;
        String f = "f="+year;

        for(String symbol : quotes) {
            long start = Calendar.getInstance().getTimeInMillis();

            symbol = SymbolHelper.cleanSymbols(symbol);
            String pastURL =  a + "&" + b + "&" + c + "&";
            String todayURL = d + "&" + e + "&" + f + "&";
            currentURL = YAHOO_FINANCE + pastURL + todayURL + urlEnd + symbol;

            getData(symbol);

            symbol = SymbolHelper.unCleanSymbols(symbol);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug(symbol + " - " + totalTimeInMinutes);
        }
    }

    public void getData(String symbol,double... doubles){
        try{
            URL url = new URL(currentURL);
            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );

            String inputLine = in.readLine();
            List<Quote> list = new ArrayList<Quote>();
            while ((inputLine = in.readLine()) != null){
                String[] vals = inputLine.split(",");
                symbol = SymbolHelper.unCleanSymbols(symbol);
                Quote quote = new Quote(symbol, vals);
                list.add(quote);
                SingleQuoteHelper.addToMap(quote.getDate());
            }
            quoteDao.saveQuotes(list);
            in.close();
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
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

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    public SingleQuoteHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(SingleQuoteHelper dlHelper) {
        this.dlHelper = dlHelper;
    }
}
