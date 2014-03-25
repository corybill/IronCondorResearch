package code.com.corybill.control.mongoDao;

import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.VanillaOption;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/27/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class VanillaOptionsDao {
    private static Logger log = Logger.getLogger(VanillaOptionsDao.class);

    public MongoDatabase mongo = MongoDatabase.getInstance();

    public List<VanillaOption> getForIronCondorDataLoad(String symbol){
        return mongo.db.createQuery(VanillaOption.class).filter("date >", TradingConstants.OPTIONS_HISTORY_DATE).filter("symbol", symbol).asList();
    }
    public List<VanillaOption> getForIronCondorCurrentDataLoad(String symbol){
        return mongo.db.createQuery(VanillaOption.class).filter("symbol", symbol).order("-expiration").limit(1).asList();
    }
    public void saveVanillaOption(List<VanillaOption> list){
        mongo.db.save(list);
    }
}
