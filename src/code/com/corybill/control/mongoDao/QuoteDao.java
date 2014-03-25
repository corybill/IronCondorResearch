package code.com.corybill.control.mongoDao;

import code.com.corybill.helper.CalendarHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.Quote;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuoteDao {
    private static Logger log = Logger.getLogger(QuoteDao.class);
    public MongoDatabase mongo = MongoDatabase.getInstance();
    private CalendarHelper calendarHelper;

    public List<Quote> getForIronCondorDataLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).order("-date").asList();
    }
    public List<Quote> getForIronCondorCurrentDataLoad(String symbol){
        Calendar today = calendarHelper.getMyCalendarInstance();
        while(today.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY){
            calendarHelper.stepCalendarBusinessDay(today,-1);
        }
        String fridayDate = calendarHelper.getDateString(today);
        return mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date",fridayDate).order("-date").limit(5).asList();
    }
    public List<Quote> getForVanillaOptionsLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).order("-date").asList();
    }
    public Quote getForCurrentOptions(String symbol){
        List<Quote> list = mongo.db.createQuery(Quote.class).filter("symbol",symbol).order("-date").limit(1).asList();
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }
    public List<Quote> getForQuoteVolatilityDataLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol", symbol).order("-date").asList();
    }
    public List<Quote> getQuotesByDate(String date){
        return mongo.db.createQuery(Quote.class).filter("date", date).asList();
    }
    public void saveQuotes(List<Quote> list){
        mongo.db.save(list);
    }

    public CalendarHelper getCalendarHelper() {
        return calendarHelper;
    }
    public void setCalendarHelper(CalendarHelper calendarHelper) {
        this.calendarHelper = calendarHelper;
    }
}
