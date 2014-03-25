package code.com.corybill.helper;

import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.Quote;
import com.google.code.morphia.query.Query;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/20/13
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalendarHelper {
    public MongoDatabase mongo = MongoDatabase.getInstance();

    public Calendar getCalendar(String date){
        String[] vals = date.split("-");

        Calendar cal = getMyCalendarInstance();
        cal.set(Calendar.YEAR,Integer.parseInt(vals[0]));
        cal.set(Calendar.MONTH,Integer.parseInt(vals[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(vals[2]));
        return cal;
    }

    /**
     * There might be a holiday or something on friday so the market might not have been open. We can step back to Thursday to get that prior days Quote.
     * If Thursday is empty we will assume the data has ended and we will return a DBCursor of size 0.
     * @param symbol
     * @param friday
     * @return
     */
    public Query<Quote> getEndOfWeek(String symbol, String friday){
        Query<Quote> fridayQuote;

        int attempts = 1;
        do{
            String id = symbol + "_" + friday;
            fridayQuote = mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date",friday);

            if(attempts == 0){
                break;
            }else if(fridayQuote.countAll() == 0){
                friday = stepDate(friday,-1);
            }
            attempts--;
        }while(fridayQuote.countAll() == 0);
        return fridayQuote;
    }

    /**
     * There might be a holiday or something on Monday so the market might not have been open. We can step forward to Tuesday and then Wednesday to get that prior days Quote.
     * If Tuesday and Wednesday are empty we will assume the data has ended and we will return a DBCursor of size 0.
     * @param symbol
     * @param monday
     * @return
     */
    public Query<Quote> getBeginningOfWeek(String symbol, String monday){
        Query<Quote> mondayQuote;

        int attempts = 2;
        long queryCount;
        do{
            String id = symbol + "_" + monday;
            mondayQuote = mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date",monday);
            queryCount = mondayQuote.countAll();
            if(attempts == 0){
                break;
            }else if(queryCount == 0){
                monday = stepDate(monday,1);
            }
            attempts--;
        }while(queryCount == 0);
        return mondayQuote;
    }

    public String stepDate(String date,int step){
        Calendar cal = getMyCalendarInstance();
        String[] info = date.split("-");

        int year = Integer.parseInt(info[0]);
        int month = Integer.parseInt(info[1]) - 1;
        int day = Integer.parseInt(info[2]);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        cal.add(Calendar.DAY_OF_MONTH,step);

        return getDateString(cal);
    }

    /**
     * Will keep on stepping until we are on a business day (2-6) (Mon - Fri)
     * @param cal
     * @param step
     * @return
     */
    public Calendar stepCalendarBusinessDay(Calendar cal,int step){
        cal.add(Calendar.DAY_OF_MONTH,step);
        while(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            cal.add(Calendar.DAY_OF_MONTH,step);
        }
        return  cal;
    }


    public String getDateString(Calendar date){
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DAY_OF_MONTH);

        String dateString = year + "-";

        if(month >= 10){
            dateString += month + "-";
        }else{
            dateString += "0" + month + "-";
        }

        if(day >= 10){
            dateString += day;
        }else{
            dateString += "0" + day;
        }
        return dateString;
    }

    /**
     * weekQuotes is in chronological order so we need to make sure that the last quotes in this list are in the same week as the first quotes.
     * @param quotesQuery
     */
    public List<Quote> getAllQuotesFromThisWeek(List<Quote> quotesQuery){
        //Get the last quote of this week.
        Calendar endOfWeek = getCalendar(quotesQuery.get(0).getDate());

        int i=1;
        int max = getSmallestValue(quotesQuery, TradingConstants.TRADING_DAYS_IN_WEEK);
        for(; i<max; i++){
            Quote quote = quotesQuery.get(i);

            Calendar beginningOfWeek = getCalendar(quote.getDate());

            if(beginningOfWeek.get(Calendar.WEEK_OF_YEAR) != endOfWeek.get(Calendar.WEEK_OF_YEAR)){
                break;
            }
        }
        return quotesQuery.subList(0,i);
    }

    /**
     * Continually pop the front item from the chronological list until the first quote from the prior week is found.
     * @param list
     */
    public void popAllOfThisWeekFromList(List<Quote> list){
        Calendar c = getLastFriday();

        Quote frontQuote = list.get(0);
        Calendar frontQuoteCal = getCalendar(frontQuote.getDate());

        //The goal of this function is to get the last day of the week at the front of list.  So if frontQuoteCal is a friday, we will just return.
        if(frontQuoteCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            return;
        }

        int frontQuoteWeek = frontQuoteCal.get(Calendar.WEEK_OF_YEAR);

        //We need to pop quotes until the week of year changes. As soon as this happens, we know we have the last day of the prior week.
        for(int i=0; i<list.size(); ){
            Quote quote = list.get(0);
            Calendar day = getCalendar(quote.getDate());
            if(day.get(Calendar.WEEK_OF_YEAR) == frontQuoteWeek){
                list.remove(quote);
            }else{
                return;
            }
        }
    }
    /**
     * Continually movde the cursor down the chronological list until the first quote from the prior week is found.
     * @param list
     */
    public int getLastWeekFridayIndex(List<Quote> list, int currentCursor){
        Calendar c = getLastFriday();

        Quote frontQuote = list.get(currentCursor);
        Calendar frontQuoteCal = getCalendar(frontQuote.getDate());

        //The goal of this function is to get the last day of the week at the front of list.  So if frontQuoteCal is a friday, we will just return.
        if(frontQuoteCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            return currentCursor;
        }

        int frontQuoteWeek = frontQuoteCal.get(Calendar.WEEK_OF_YEAR);

        //We need to pop quotes until the week of year changes. As soon as this happens, we know we have the last day of the prior week.
        for(int i=currentCursor; i<list.size(); i++ ){
            Quote quote = list.get(i);
            Calendar day = getCalendar(quote.getDate());
            if(day.get(Calendar.WEEK_OF_YEAR) != frontQuoteWeek){
                return i-1;
            }
        }
        return -1;
    }

    /**
     * weekQuotes is in chronological order so we need to make sure that the last quotes in this list are in the same week as the first quotes.
     * @param quotesQuery
     */
    public int getThisWeekMondayIndex(List<Quote> quotesQuery,int currentCursor){
        //Get the last quote of this week.
        Calendar endOfWeek = getCalendar(quotesQuery.get(currentCursor).getDate());

        int i=currentCursor;
        int max = getSmallestValue(quotesQuery, TradingConstants.TRADING_DAYS_IN_WEEK + currentCursor) + 1;
        for(; i<max; i++){
            if(i >= quotesQuery.size()){
                return -1;
            }
            Quote quote = quotesQuery.get(i);

            Calendar beginningOfWeek = getCalendar(quote.getDate());

            if(beginningOfWeek.get(Calendar.WEEK_OF_YEAR) != endOfWeek.get(Calendar.WEEK_OF_YEAR)){
                return i;
            }
        }
        return -1;
    }

    public Calendar getNextDay(int day){
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.YEAR, getMyCalendarInstance().get(Calendar.YEAR));
        startDate.set(Calendar.MONTH, getMyCalendarInstance().get(Calendar.MONTH));
        startDate.set(Calendar.DAY_OF_MONTH, getMyCalendarInstance().get(Calendar.DAY_OF_MONTH));

        startDate.add(Calendar.DAY_OF_WEEK, 1);
        while(startDate.get(Calendar.DAY_OF_WEEK) != day){
            startDate.add(Calendar.DAY_OF_WEEK, 1);
        }

        return startDate;
    }

    private Calendar getLastFriday(){
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.YEAR, getMyCalendarInstance().get(Calendar.YEAR));
        startDate.set(Calendar.MONTH, getMyCalendarInstance().get(Calendar.MONTH));
        startDate.set(Calendar.DAY_OF_MONTH, getMyCalendarInstance().get(Calendar.DAY_OF_MONTH));

        while(startDate.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY){
            startDate.add(Calendar.DAY_OF_WEEK, -1);
        }

        return startDate;
    }
    private Calendar getLastMonday(){
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.YEAR, getMyCalendarInstance().get(Calendar.YEAR));
        startDate.set(Calendar.MONTH, getMyCalendarInstance().get(Calendar.MONTH));
        startDate.set(Calendar.DAY_OF_MONTH, getMyCalendarInstance().get(Calendar.DAY_OF_MONTH));

        while(startDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            startDate.add(Calendar.DAY_OF_WEEK, -1);
        }

        return startDate;
    }
    public void getTwoMondaysInThePast(Calendar monday){
        int dayOfWeek = monday.get(Calendar.DAY_OF_WEEK);

        //Need to rollback beyond the first Monday and to the second monday back.
        int weeks = 0;
        while(dayOfWeek != 2 && weeks != 1){
            monday.add(Calendar.DAY_OF_WEEK,-1);
            dayOfWeek = monday.get(Calendar.DAY_OF_WEEK);
        }
    }

    public boolean isBeforeTenYearsAgo(Calendar tenYearsAgo,Calendar current){
        if(current.before(tenYearsAgo)){
            return true;
        }
        return false;
    }
    public Calendar getMonthsAgo(Calendar current,int monthsAgo){
        Calendar c = getMyCalendarInstance();
        c.set(Calendar.YEAR,current.get(Calendar.YEAR));
        c.set(Calendar.MONTH,current.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.MONTH, monthsAgo);
        return c;
    }

    public Calendar getMyCalendarInstance(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR, 0);

        return c;
    }

    public int getSmallestValue(List<Quote> list, int max){
        if(list.size() < max){
            max = list.size();
        }
        return max;
    }

    public String getCurrentOptionsDateString(Calendar c){
        String year = (c.get(Calendar.YEAR) + "").substring(2);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return year + getFieldAsString(month + 1) + getFieldAsString(day);
    }

    private String getFieldAsString(int val){
        if(val < 10){
            return "0"+ val;
        }
        return val+"";
    }
}
