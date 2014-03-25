package code.com.corybill.control.mySqlDao;

import code.com.corybill.helper.TradingConstants;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.Quote;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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

    public List<Quote> getForIronCondorDataLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).order("-date").asList();
    }
    public List<Quote> getForVanillaOptionsLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol",symbol).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).order("-date").asList();
    }
    public List<Quote> getForQuoteVolatilityDataLoad(String symbol){
        return mongo.db.createQuery(Quote.class).filter("symbol", symbol).order("-date").asList();
    }
    public List<Quote> getQuotesByDate(String date){
        return mongo.db.createQuery(Quote.class).filter("date", date).asList();
    }
    public List<String> getDistinctDatesFromLastFriday(){
        //We will just search using one symbol that has been around since TradingConstants.ORIGINAL_START_DATE (i.e. PG).
        List<Quote> quotes =  mongo.db.createQuery(Quote.class).filter("symbol", "PG").order("-date").asList();

        //Now we will get all of the expiration dates and add them to the list.
        List<String> dates = new ArrayList<String>();
        for(Quote quote : quotes){
            dates.add(quote.getDate());
        }
        return dates;
    }

    public void saveQuotes(List<Quote> list){
        mongo.db.save(list);
    }
}
