package code.com.corybill.control.mySqlDao;

import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.model.ICStudyResult;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICStudyResultDao {
    private static Logger log = Logger.getLogger(ICStudyResultDao.class);
    private MongoDatabase mongo = MongoDatabase.getInstance();

    public void saveICStudyResult(List<ICStudyResult> studyResults){
        mongo.db.save(studyResults);
    }
}
