package code.com.corybill.control.mongoDao;

import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.CurrentOption;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/27/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentOptionsDao {
    private static Logger log = Logger.getLogger(CurrentOptionsDao.class);

    public MongoDatabase mongo = MongoDatabase.getInstance();

    public List<CurrentOption> getForIronCondorDataLoad(String symbol){
        return mongo.db.createQuery(CurrentOption.class).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).filter("symbol", symbol).asList();
    }
    public void saveCurrentOptions(List<CurrentOption> list){
        mongo.db.save(list);
    }
}
