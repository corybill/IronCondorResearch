package code.com.corybill.model;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.helper.TradingConstants;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/24/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorInvest {

    @Id
    private ObjectId id;

    private Quote quote;

    @Indexed(value = IndexDirection.ASC, unique = false)
    private String symbol;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String startDate;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String expiration;

    //This will be 1 if we are testing 1 standard deviation.  Values accepted will be any real numbers between 1 and 2.
    @Indexed(value = IndexDirection.ASC, unique = false)
    private double standardDeviationInterval;

    //The number of days back that was used for the standard deviation levels to create the upper and lower bounds on the shorts.
    @Indexed(value = IndexDirection.ASC, unique = false)
    private int standardDeviationDays;

    private double credit;
    private int creditRank;

    private double strikeInterval;

    private double longCallStrike;
    private double shortCallStrike;
    private double shortPutStrike;
    private double longPutStrike;

    private double longCallBid;
    private double longCallAsk;

    private double shortCallBid;
    private double shortCallAsk;

    private double shortPutBid;
    private double shortPutAsk;

    private double longPutBid;
    private double longPutAsk;


    public IronCondorInvest(){   }

    public String getKey(){
        return standardDeviationInterval + "_" + standardDeviationDays;
    }

    public double calculateCreditForCondor(){
        MathUtil mathUtil = new MathUtil();
        double multiplier = mathUtil.round(((TradingConstants.MAX_RISK / 100) / strikeInterval),TradingConstants.ZERO_ROUNDING_PRECISION);

        double credit = (shortCallBid - longCallAsk) + (shortPutBid - longPutAsk);
        double middle = ((longCallAsk - longCallBid) + (shortCallAsk - shortCallBid) + (shortPutAsk - shortPutBid) + (longPutAsk - longPutBid)) * .4;
        middle = mathUtil.round(middle,TradingConstants.OPTION_PRICING_ROUNDING_PRECISION);

        this.credit = (credit + middle) * 100 * multiplier * 2;

        return credit;
    }

    public double getStandardDeviationInterval() {
        return standardDeviationInterval;
    }
    public void setStandardDeviationInterval(double standardDeviationInterval) {
        this.standardDeviationInterval = standardDeviationInterval;
    }

    public int getStandardDeviationDays() {
        return standardDeviationDays;
    }
    public void setStandardDeviationDays(int standardDeviationDays) {
        this.standardDeviationDays = standardDeviationDays;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpiration() {
        return expiration;
    }
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public double getCredit() {
        return credit;
    }
    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getCreditRank() {
        return creditRank;
    }
    public void setCreditRank(int creditRank) {
        this.creditRank = creditRank;
    }

    public Quote getQuote() {
        return quote;
    }
    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public double getLongCallBid() {
        return longCallBid;
    }
    public void setLongCallBid(double longCallBid) {
        this.longCallBid = longCallBid;
    }

    public double getLongCallAsk() {
        return longCallAsk;
    }
    public void setLongCallAsk(double longCallAsk) {
        this.longCallAsk = longCallAsk;
    }

    public double getShortCallBid() {
        return shortCallBid;
    }
    public void setShortCallBid(double shortCallBid) {
        this.shortCallBid = shortCallBid;
    }

    public double getShortCallAsk() {
        return shortCallAsk;
    }
    public void setShortCallAsk(double shortCallAsk) {
        this.shortCallAsk = shortCallAsk;
    }

    public double getShortPutBid() {
        return shortPutBid;
    }
    public void setShortPutBid(double shortPutBid) {
        this.shortPutBid = shortPutBid;
    }

    public double getShortPutAsk() {
        return shortPutAsk;
    }
    public void setShortPutAsk(double shortPutAsk) {
        this.shortPutAsk = shortPutAsk;
    }

    public double getLongPutBid() {
        return longPutBid;
    }
    public void setLongPutBid(double longPutBid) {
        this.longPutBid = longPutBid;
    }

    public double getLongPutAsk() {
        return longPutAsk;
    }
    public void setLongPutAsk(double longPutAsk) {
        this.longPutAsk = longPutAsk;
    }

    public double getStrikeInterval() {
        return strikeInterval;
    }
    public void setStrikeInterval(double strikeInterval) {
        this.strikeInterval = strikeInterval;
    }

    public double getLongCallStrike() {
        return longCallStrike;
    }
    public void setLongCallStrike(double longCallStrike) {
        this.longCallStrike = longCallStrike;
    }

    public double getShortCallStrike() {
        return shortCallStrike;
    }
    public void setShortCallStrike(double shortCallStrike) {
        this.shortCallStrike = shortCallStrike;
    }

    public double getShortPutStrike() {
        return shortPutStrike;
    }
    public void setShortPutStrike(double shortPutStrike) {
        this.shortPutStrike = shortPutStrike;
    }

    public double getLongPutStrike() {
        return longPutStrike;
    }
    public void setLongPutStrike(double longPutStrike) {
        this.longPutStrike = longPutStrike;
    }
}
