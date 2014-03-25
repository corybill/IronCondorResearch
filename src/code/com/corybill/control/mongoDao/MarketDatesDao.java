package code.com.corybill.control.mongoDao;

import code.com.corybill.model.MarketDate;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/27/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarketDatesDao {
    private static Logger log = Logger.getLogger(MarketDatesDao.class);

    public MongoDatabase mongo = MongoDatabase.getInstance();

    public List<MarketDate> getAllDates(){
        return mongo.db.createQuery(MarketDate.class).asList();
    }
    public void saveMarketDates(Map<String,String> newMapSet){
        List<MarketDate> saveList = new ArrayList<MarketDate>();
        for(String current : newMapSet.values()){
            saveList.add(new MarketDate(current));
        }
        mongo.db.save(saveList);
    }
}
