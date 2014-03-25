package code.com.corybill.control.mongoDao;

import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.Quote;
import code.com.corybill.model.UpdateDate;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/27/13
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateDateDao {
    private static Logger log = Logger.getLogger(UpdateDateDao.class);

    public static void resetAllUpdateDates(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.resetAllDates();

        mongo.getInstance().db.save(updateDate);
    }

    public static UpdateDate getUpdateDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        if(mongo.getInstance().db.createQuery(UpdateDate.class).get() == null){
            UpdateDate updateDate = new UpdateDate();
            updateDate.resetAllDates();
            mongo.getInstance().db.save(updateDate);
        }
        return mongo.getInstance().db.createQuery(UpdateDate.class).get();
    }

    /**
     * Update dates to last run cycle
     */
    public static void updateSingleQuoteDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        //Grabs any quote with the most up to date date and uses it to update db.
        Quote quote = mongo.db.createQuery(Quote.class).limit(1).order("-date").get();
        updateDate.setLastSingleQuoteUpdate(quote.getDate());
        mongo.db.save(updateDate);
    }
    public static void updateIronCondorDate(){
        //TODO:  This update date should always be the prior Friday for history collection
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        //Grabs any quote with the most up to date date and uses it to update db.
        Quote quote = mongo.db.createQuery(Quote.class).limit(1).order("-date").get();
        updateDate.setLastIronCondorUpdate(quote.getDate());
        mongo.db.save(updateDate);
    }
    public static void updateVanillaOptionDate(){
        //TODO:  This update date should always be the prior Friday for history collection
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        //Grabs any quote with the most up to date date and uses it to update db.
        Quote quote = mongo.db.createQuery(Quote.class).limit(1).order("-date").get();
        updateDate.setLastVanillaOptionUpdate(quote.getDate());
        mongo.db.save(updateDate);
    }
    public static void updateLastVolatilityDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        //Grabs any quote with the most up to date date and uses it to update db.
        Quote quote = mongo.db.createQuery(Quote.class).limit(1).order("-date").get();
        updateDate.setLastVolatilityUpdate(quote.getDate());
        mongo.db.save(updateDate);
    }
    public static void udpateICProfitLossUpdate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        //Grabs any quote with the most up to date date and uses it to update db.
        Quote quote = mongo.db.createQuery(Quote.class).limit(1).order("-date").get();
        updateDate.setLastICProfitLossUpdate(quote.getDate());
        mongo.db.save(updateDate);
    }

    /**
     * Reset quote dates back to initial starting value.
     */
    public static void resetSingleQuoteDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.setLastSingleQuoteUpdate(TradingConstants.ORIGINAL_START_DATE);
        mongo.db.save(updateDate);
    }
    public static void resetIronCondorDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.setLastIronCondorUpdate(TradingConstants.ORIGINAL_START_DATE);
        mongo.db.save(updateDate);
    }
    public static void resetVanillaOptionDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.setLastVanillaOptionUpdate(TradingConstants.OPTIONS_HISTORY_DATE);
        mongo.db.save(updateDate);
    }
    public static void resetLastVolatilityDate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.setLastVolatilityUpdate(TradingConstants.ORIGINAL_START_DATE);
        mongo.db.save(updateDate);
    }
    public static void resetICProfitLossUpdate(){
        MongoDatabase mongo = MongoDatabase.getInstance();
        UpdateDate updateDate = getUpdateDate();
        updateDate.setLastICProfitLossUpdate(TradingConstants.OPTIONS_HISTORY_DATE);
        mongo.db.save(updateDate);
    }
}
