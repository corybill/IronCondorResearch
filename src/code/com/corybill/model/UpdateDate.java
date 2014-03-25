package code.com.corybill.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import org.bson.types.ObjectId;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/24/13
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class UpdateDate {
    private static String resetDate = "1970-01-01";

    @Id
    private ObjectId id;

    private String lastICProfitLossUpdate;
    private String lastSingleQuoteUpdate;
    private String lastVanillaOptionUpdate;
    private String lastIronCondorUpdate;
    private String lastVolatilityUpdate;

    public UpdateDate(){  }

    public String getLastIronCondorUpdate() {
        return lastIronCondorUpdate;
    }
    public void setLastIronCondorUpdate(String lastIronCondorUpdate) {
        this.lastIronCondorUpdate = lastIronCondorUpdate;
    }

    public String getLastSingleQuoteUpdate() {
        return lastSingleQuoteUpdate;
    }
    public void setLastSingleQuoteUpdate(String lastSingleQuoteUpdate) {
        this.lastSingleQuoteUpdate = lastSingleQuoteUpdate;
    }

    public String getLastVanillaOptionUpdate() {
        return lastVanillaOptionUpdate;
    }
    public void setLastVanillaOptionUpdate(String lastVanillaOptionUpdate) {
        this.lastVanillaOptionUpdate = lastVanillaOptionUpdate;
    }

    public String getLastVolatilityUpdate() {
        return lastVolatilityUpdate;
    }
    public void setLastVolatilityUpdate(String lastVolatilityUpdate) {
        this.lastVolatilityUpdate = lastVolatilityUpdate;
    }

    public String getLastICProfitLossUpdate() {
        return lastICProfitLossUpdate;
    }
    public void setLastICProfitLossUpdate(String lastICProfitLossUpdate) {
        this.lastICProfitLossUpdate = lastICProfitLossUpdate;
    }

    public void resetAllDates(){
        lastIronCondorUpdate = resetDate;
        lastSingleQuoteUpdate = resetDate;
        lastVanillaOptionUpdate = resetDate;
        lastVolatilityUpdate = resetDate;
    }
}
