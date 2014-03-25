package code.com.corybill.control.mongoDao;

import code.com.corybill.model.IronCondorRanges;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorRangesDao {
    private static Logger log = Logger.getLogger(IronCondorRangesDao.class);
    private MongoDatabase mongo = MongoDatabase.getInstance();

    public List<IronCondorRanges> getForICCreditDataLoad(String date){
        return mongo.db.createQuery(IronCondorRanges.class).filter("expiration",date).asList();
    }
    public List<IronCondorRanges> getForICProfitLossDataLoad(String date){
        return mongo.db.createQuery(IronCondorRanges.class).filter("expiration",date).asList();
    }
    public List<IronCondorRanges> getForICProfitLossStudyDataLoad(String date){
        return mongo.db.createQuery(IronCondorRanges.class).filter("expiration",date).asList();
    }

    public void saveIronCondors(List<IronCondorRanges> condors){
        mongo.db.save(condors);
    }
}
