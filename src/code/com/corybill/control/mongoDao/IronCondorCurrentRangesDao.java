package code.com.corybill.control.mongoDao;

import code.com.corybill.model.IronCondorCurrentRanges;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorCurrentRangesDao {
    private static Logger log = Logger.getLogger(IronCondorCurrentRangesDao.class);
    private MongoDatabase mongo = MongoDatabase.getInstance();

    public List<IronCondorCurrentRanges> getForICCreditDataLoad(){
        return mongo.db.createQuery(IronCondorCurrentRanges.class).asList();
    }

    public void saveIronCondors(List<IronCondorCurrentRanges> condors){
        mongo.db.save(condors);
    }
}
