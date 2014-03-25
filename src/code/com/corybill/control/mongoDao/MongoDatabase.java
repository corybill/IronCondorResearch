package code.com.corybill.control.mongoDao;

import code.com.corybill.model.*;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/27/12
 * Time: 9:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class MongoDatabase {
    private static Logger log = Logger.getLogger(MongoDatabase.class);
    private static MongoDatabase instance;
    public MongoClient client;
    public Datastore db;


    private MongoDatabase(){
        try{
            client = new MongoClient( "localhost" );
            //client.dropDatabase( "ResearchA");
            db = new Morphia().map(Quote.class).
                               map(IronCondor.class).
                               map(IronCondorCurrent.class).
                               map(Expiration.class).
                               map(MarketDate.class).
                               map(VanillaOption.class).
                               map(UpdateDate.class).
                               createDatastore(client, "ResearchA");

            db.ensureIndexes();
        }catch(UnknownHostException uhe){
            uhe.printStackTrace();
            log.error(uhe.getStackTrace());
        }
    }

    public static MongoDatabase getInstance(){
        if(instance == null){
            instance = new MongoDatabase();
        }
        return instance;
    }
}
