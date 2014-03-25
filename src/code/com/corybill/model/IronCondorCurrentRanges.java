package code.com.corybill.model;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.helper.TradingConstants;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/24/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorCurrentRanges {

    @Id
    private ObjectId id;

    //A map of daily options.  Key is the day of week (i.e. Calendar.MONDAY) and value is a list of four options that make up the IronCondor.
    @Embedded
    private List<VanillaOption> options;

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

    @Embedded
    private OHLC weeklyOHLC;

    public IronCondorCurrentRanges(){   }

    public String getKey(){
        return standardDeviationInterval + "_" + standardDeviationDays;
    }

    public static VanillaOption getShortCall(List<VanillaOption> list){
        for(VanillaOption option : list){
            if(option.getType().equals(TradingConstants.CALL) && option.getSide().equals(TradingConstants.SHORT)){
                return option;
            }
        }
        return null;
    }
    public static VanillaOption getLongCall(List<VanillaOption> list){
        for(VanillaOption option : list){
            if(option.getType().equals(TradingConstants.CALL) && option.getSide().equals(TradingConstants.LONG)){
                return option;
            }
        }
        return null;
    }
    public static VanillaOption getShortPut(List<VanillaOption> list){
        for(VanillaOption option : list){
            if(option.getType().equals(TradingConstants.PUT) && option.getSide().equals(TradingConstants.SHORT)){
                return option;
            }
        }
        return null;
    }
    public static VanillaOption getLongPut(List<VanillaOption> list){
        for(VanillaOption option : list){
            if(option.getType().equals(TradingConstants.PUT) && option.getSide().equals(TradingConstants.LONG)){
                return option;
            }
        }
        return null;
    }

    public double calculateCreditForCondor(){
        MathUtil mathUtil = new MathUtil();
        double thisRisk = getThisRisk();
        double multiplier = mathUtil.round(((TradingConstants.MAX_RISK / 100) / thisRisk),TradingConstants.ZERO_ROUNDING_PRECISION);
        this.credit = weeklyOHLC.getOpen() * 100 * multiplier;

        return credit;
    }

    /**
     * Gets the max risk of this condor based on one contract per leg.
     * @return
     */
    private double getThisRisk(){
        for(int i= Calendar.MONDAY; i<Calendar.FRIDAY; i++){
            double callDiff = getLongCall(options).getStrike() - getShortCall(options).getStrike();
            double putDiff = getShortPut(options).getStrike() - getLongPut(options).getStrike();
            return (callDiff > putDiff) ? callDiff : putDiff;
        }
        //SHOULD NEVER REACH HERE
        return 0.0;
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

    public OHLC getWeeklyOHLC() {
        return weeklyOHLC;
    }
    public void setWeeklyOHLC(OHLC weeklyOHLC) {
        this.weeklyOHLC = weeklyOHLC;
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

    public List<VanillaOption> getOptions() {
        return options;
    }
    public void setOptions(List<VanillaOption> options) {
        this.options = options;
    }
}
