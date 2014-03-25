package code.com.corybill.model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/22/12
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class Quote implements Serializable {
    @Id
    private ObjectId id;

    @Indexed(value = IndexDirection.ASC, unique = false)
    private String symbol;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String date;

    @Embedded
    private OHLC ohlc;
    private String volume;
    private double nonAdjustedClose;
    private double dailyLogDiff;

    /**
     * Map of Volatility's.  Key will be the number of days as a string and the value will be the volatility for that many days.
     * Example:  {"30" : .4856}
     */
    private Map<Integer,Double> volatility;

    /**
     * Map of Volatility ranks.  Key will be the number of days as a string and the value will be the volatility rank of all symbols with this date
     * Example:  {"30" : .4856}
     */
    private Map<Integer,Integer> volRank;

    /**
     * Map of Standard Deviations.  Key will be the number of days as a string and the value will be the standard deviation for that many days.
     * Example:  {"10" : 3.9648}
     */
    private Map<Integer,Double> standardDeviation;

    /**
     * Map of Standard Deviations ranks.  Key will be the number of days as a string and the value will be the Standard Deviations rank of all symbols with this date
     * Saved as a percentage of opening stock price.
     * Example:  {"30" : .4856}
     */
    private Map<Integer,Integer> sdRank;

    /**
     * Map of means.  Key will be the number of days as a string and the value will be the mean for that many days.
     * Example:  {"10" : 3.9648}
     */
    private Map<Integer,Double> mean;

    /**
     * Map of trading ranges.  Key will be the number of days as a string and the value will be the a TradingRange for that given time period.
     * Example:  {"10" : 3.9648}
     */
    private Map<Integer,TradingRange> tradingRanges;

    public Quote(){  }

    public Quote(String symbol, String[] values){
        this.symbol = symbol;
        this.date = values[0];

        ohlc = new OHLC();
        ohlc.setOpen(Double.parseDouble(values[1]));
        ohlc.setHigh(Double.parseDouble(values[2]));
        ohlc.setLow(Double.parseDouble(values[3]));
        ohlc.setClose(Double.parseDouble(values[4]));

        this.volume =  values[5];
        this.nonAdjustedClose =  Double.parseDouble(values[6]);
    }
    public Quote(String symbol){
        this.symbol = symbol;
    }

    public double getAverageVolatility(){
        double total = 0;
        for(double vol : volatility.values()){
            total += vol;
        }
        total = total / volatility.size();
        return total;
    }

    public void setValues(String[] values){
        this.date = values[0];

        ohlc = new OHLC();
        ohlc.setOpen(Double.parseDouble(values[1]));
        ohlc.setHigh(Double.parseDouble(values[2]));
        ohlc.setLow(Double.parseDouble(values[3]));
        ohlc.setClose(Double.parseDouble(values[6]));

        this.volume =  values[5];
        this.nonAdjustedClose =  Double.parseDouble(values[4]);
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

    public OHLC getOhlc() {
        return ohlc;
    }
    public void setOhlc(OHLC high) {
        this.ohlc = ohlc;
    }

    public double getAdjustedClose() {
        return nonAdjustedClose;
    }
    public void setAdjustedClose(double nonAdjustedClose) {
        this.nonAdjustedClose = nonAdjustedClose;
    }

    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    public Map<Integer, Double> getVolatility() {
        return volatility;
    }
    public void setVolatility(Map<Integer, Double> volatility) {
        this.volatility = volatility;
    }

    public double getNonAdjustedClose() {
        return nonAdjustedClose;
    }
    public void setNonAdjustedClose(double nonAdjustedClose) {
        this.nonAdjustedClose = nonAdjustedClose;
    }

    public Map<Integer, Double> getStandardDeviation() {
        return standardDeviation;
    }
    public void setStandardDeviation(Map<Integer, Double> standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getDailyLogDiff() {
        return dailyLogDiff;
    }
    public void setDailyLogDiff(double dailyLogDiff) {
        this.dailyLogDiff = dailyLogDiff;
    }

    public Map<Integer, Double> getMean() {
        return mean;
    }
    public void setMean(Map<Integer, Double> mean) {
        this.mean = mean;
    }

    public Map<Integer, TradingRange> getTradingRanges() {
        return tradingRanges;
    }
    public void setTradingRanges(Map<Integer, TradingRange> tradingRanges) {
        this.tradingRanges = tradingRanges;
    }

    public Map<Integer, Integer> getSdRank() {
        if(sdRank == null){
            sdRank = new HashMap<Integer, Integer>();
        }
        return sdRank;
    }
    public void setSdRank(Map<Integer, Integer> sdRank) {
        this.sdRank = sdRank;
    }

    public Map<Integer, Integer> getVolRank() {
        if(volRank == null){
            volRank = new HashMap<Integer, Integer>();
        }
        return volRank;
    }
    public void setVolRank(Map<Integer, Integer> volRank) {
        this.volRank = volRank;
    }
}
