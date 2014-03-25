package code.com.corybill.control.mongoDao;

import code.com.corybill.model.IronCondorCurrent;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorCurrentDao {
    private static Logger log = Logger.getLogger(IronCondorCurrentDao.class);
    private MongoDatabase mongo = MongoDatabase.getInstance();

    public List<IronCondorCurrent> getForICCreditDataLoad(){
        return mongo.db.createQuery(IronCondorCurrent.class).asList();
    }

    public void saveIronCondors(List<IronCondorCurrent> condors){
        mongo.db.save(condors);
    }
}
