package code.com.corybill.control.mongoDao;

import code.com.corybill.model.IronCondor;
import org.apache.log4j.Logger;

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

    public List<IronCondor> getForICCreditDataLoad(String date){
        return mongo.db.createQuery(IronCondor.class).filter("expiration",date).asList();
    }
    public List<IronCondor> getForICProfitLossDataLoad(String date){
        return mongo.db.createQuery(IronCondor.class).filter("expiration",date).asList();
    }
    public List<IronCondor> getForICProfitLossStudyDataLoad(String date){
        return mongo.db.createQuery(IronCondor.class).filter("expiration",date).asList();
    }

    public void saveIronCondors(List<IronCondor> condors){
        mongo.db.save(condors);
    }
}
