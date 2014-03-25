package code.com.corybill.model;

import com.google.code.morphia.annotations.Id;
import org.bson.types.ObjectId;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 3/2/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Expiration {

    @Id
    private ObjectId id;

    private String expiration;

    public Expiration(){ }

    public Expiration(String expiration){
        this.expiration = expiration;
    }

    @Override
    public boolean equals(Object strExpiration){
        if(strExpiration instanceof String){
            if(expiration.equals((String)strExpiration)){
                return true;
            }else{
                return false;
            }
        }else{
            Expiration exp = (Expiration)strExpiration;
            if(expiration.equals(exp.getExpiration())){
                return true;
            }else{
                return false;
            }
        }
    }

    public String getExpiration() {
        return expiration;
    }
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
}
