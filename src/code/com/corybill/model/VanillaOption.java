package code.com.corybill.model;

import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/23/13
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class VanillaOption {

    @Id
    private ObjectId id;

    @Indexed(value = IndexDirection.ASC, unique = false)
    private String symbol;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String date;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String expiration;
    private int daysToExpiration;

    @Indexed(value = IndexDirection.ASC, unique = false)
    private double strike;

    // This value will be VanillaOption.CALL or VanillaOption.PUT
    @Indexed(value = IndexDirection.ASC, unique = false)
    private String type;

    // This value will be VanillaOption.SHORT or VanillaOption.LONG
    private String side;

    @Embedded
    private OHLC ohlc;

    @Reference
    private Quote quote;

    public VanillaOption(){  }

    public VanillaOption getClone(){
        VanillaOption o = new VanillaOption();
        o.symbol = this.symbol;
        o.date = this.date;
        o.expiration = this.expiration;
        o.daysToExpiration = this.daysToExpiration;
        o.type = this.type;
        o.strike = this.strike;
        o.side = this.side;
        o.ohlc = this.ohlc;
        o.quote = this.quote;

        return o;
    }


    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getExpiration() {
        return expiration;
    }
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public int getDaysToExpiration() {
        return daysToExpiration;
    }
    public void setDaysToExpiration(int daysToExpiration) {
        this.daysToExpiration = daysToExpiration;
    }

    public Quote getQuote() {
        return quote;
    }
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public OHLC getOhlc() {
        return ohlc;
    }
    public void setOhlc(OHLC ohlc) {
        this.ohlc = ohlc;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getStrike() {
        return strike;
    }
    public void setStrike(double strike) {
        this.strike = strike;
    }

    public String getSide() {
        return side;
    }
    public void setSide(String side) {
        this.side = side;
    }
}
