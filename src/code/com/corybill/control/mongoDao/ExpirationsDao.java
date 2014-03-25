package code.com.corybill.control.mongoDao;

import code.com.corybill.model.Expiration;
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
public class ExpirationsDao {
    private static Logger log = Logger.getLogger(ExpirationsDao.class);

    public MongoDatabase mongo = MongoDatabase.getInstance();

    public List<Expiration> getAllExpirations(){
        return mongo.db.createQuery(Expiration.class).asList();
    }
    public void saveExpirations(Map<String,String> newMapSet){
        List<Expiration> saveList = new ArrayList<Expiration>();
        for(String current : newMapSet.values()){
            saveList.add(new Expiration(current));
        }
        mongo.db.save(saveList);
    }
}
