package code.com.corybill.control.mySqlDao;

import code.com.corybill.helper.TradingConstants;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.IronCondor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorDao {
    private static Logger log = Logger.getLogger(IronCondorDao.class);
    private MongoDatabase mongo = MongoDatabase.getInstance();

    public List<String> getDistinctExpirations(String afterThisDate){
        //We will just search using one symbol, one sdDays value, and one deviation.
        //This query will allow us to get all the expirations we need.
        List<IronCondor> condors =  mongo.db.createQuery(IronCondor.class).filter("symbol", "GOOG").
                filter("standardDeviationDays", TradingConstants.TRADING_DAYS_IN_WEEK).
                filter("standardDeviationInterval", TradingConstants.deviations[0]).
                filter("expiration >",afterThisDate).asList();

        //Now we will get all of the expiration dates and add them to the list.
        List<String> expirations = new ArrayList<String>();
        for(IronCondor condor : condors){
            if(expirations.size()==0){
                expirations.add(condor.getExpiration());
            }else if(!expirations.get(expirations.size()-1).equals(condor.getExpiration())){
                expirations.add(condor.getExpiration());
            }
        }
        return expirations;
    }

    public List<String> getDistinctBeginningsOfWeeks(String date){
        //We will just search using one symbol, one sdDays value, and one deviation.
        //This query will allow us to get all the expirations we need.
        List<IronCondor> condors =  mongo.db.createQuery(IronCondor.class).filter("symbol", "GOOG").
                filter("standardDeviationDays", TradingConstants.TRADING_DAYS_IN_WEEK).
                filter("standardDeviationInterval", TradingConstants.deviations[0]).
                filter("expiration >",date).asList();

        //Now we will get all of the first days of the trading week and add them to the list.
        List<String> startDates = new ArrayList<String>();
        for(IronCondor condor : condors){
            if(startDates.size()==0){
                startDates.add(condor.getStartDate());
            }else if(!startDates.get(startDates.size()-1).equals(condor.getStartDate())){
                startDates.add(condor.getStartDate());
            }
        }
        return startDates;
    }

    public List<IronCondor> getForICCreditDataLoad(String date, int day, double deviation){
        return mongo.db.createQuery(IronCondor.class).filter("expiration",date).filter("standardDeviationDays",day).filter("standardDeviationInterval",deviation).asList();
    }
    public List<IronCondor> getForICProfitAndLossDataLoad(String date, int day, double deviation){
        return mongo.db.createQuery(IronCondor.class).filter("expiration",date).filter("standardDeviationDays",day).filter("standardDeviationInterval",deviation).asList();
    }
    public void saveIronCondors(List<IronCondor> condors){
        mongo.db.save(condors);
    }
}
