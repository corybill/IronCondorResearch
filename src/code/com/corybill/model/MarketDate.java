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
public class MarketDate {

    @Id
    private ObjectId id;

    private String marketDate;

    public MarketDate(){ }

    public MarketDate(String marketDate){
        this.marketDate = marketDate;
    }

    @Override
    public boolean equals(Object strExpiration){
        if(strExpiration instanceof String){
            if(marketDate.equals((String)strExpiration)){
                return true;
            }else{
                return false;
            }
        }else{
            MarketDate exp = (MarketDate)strExpiration;
            if(marketDate.equals(exp.getMarketDate())){
                return true;
            }else{
                return false;
            }
        }
    }

    public String getMarketDate() {
        return marketDate;
    }
    public void setMarketDate(String marketDate) {
        this.marketDate = marketDate;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
}
