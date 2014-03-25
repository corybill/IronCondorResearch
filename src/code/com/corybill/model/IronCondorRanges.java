package code.com.corybill.model;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.helper.TradingConstants;
import com.google.code.morphia.annotations.*;
import com.google.code.morphia.utils.IndexDirection;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/19/13
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class IronCondorRanges {
    @Id
    private ObjectId id;

    //A map of daily options.  Key is the day of week (i.e. Calendar.MONDAY) and value is a list of four options that make up the IronCondor.
    @Embedded
    private Map<Integer,List<VanillaOption>> optionsByDay;

    //P&L based on a max loss value where the max loss is the key to the P&L.
    private Map<Integer,Double> profitLoss;
    //Ranks based on a max loss value where the max loss is the key to the rank.
    private Map<Integer,Integer> ranks;

    @Reference
    private List<Quote> weeksQuotes;

    @Indexed(value = IndexDirection.ASC, unique = false)
    private String symbol;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String startDate;

    @Indexed(value = IndexDirection.DESC, unique = false)
    private String expiration;

    private TradingRange tradingRange;
    private int rangeDays;

    private double credit;
    private int creditRank;

    @Embedded
    private OHLC weeklyOHLC;

    public IronCondorRanges(){
        profitLoss = new HashMap<Integer, Double>();
        ranks = new HashMap<Integer, Integer>();
    }

    public String getKey(){
        return rangeDays + "";
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

    public double calculateProfitAndLossFor(int maxLoss,int maxGain){
        double pl = 0.0;
        pl = calculateProfitAndLoss(maxLoss,maxGain);

        return pl;
    }
    private double calculateProfitAndLoss(int maxLoss,int maxGain){
        MathUtil mathUtil = new MathUtil();
        double thisRisk = getThisRisk();
        double multiplier = mathUtil.round(((TradingConstants.MAX_RISK / 100) / thisRisk),TradingConstants.ZERO_ROUNDING_PRECISION);

        double maxGainOpening = maxGain / (multiplier * 100.00);

        double open = weeklyOHLC.getOpen() > maxGainOpening ? maxGainOpening : weeklyOHLC.getOpen();

        if(((weeklyOHLC.getClose() - open) * multiplier * 100) > maxLoss){
            return maxLoss*-1;
        }else if(((weeklyOHLC.getHigh() - open) * multiplier * 100) > maxLoss){
            return maxLoss*-1;
        }
        return mathUtil.round((open - weeklyOHLC.getClose()) * multiplier * 100,TradingConstants.IC_PRICING_ROUNDING_PRECISION);
    }

    public double calculateCreditForCondor(){
        MathUtil mathUtil = new MathUtil();
        double thisRisk = getThisRisk();
        double multiplier = mathUtil.round(((TradingConstants.MAX_RISK / 100) / thisRisk),TradingConstants.ZERO_ROUNDING_PRECISION);
        this.credit = mathUtil.round(weeklyOHLC.getOpen() * 100 * multiplier,TradingConstants.IC_PRICING_ROUNDING_PRECISION);

        return credit;
    }

    public double calculateProfitAndLossFor(int maxLoss){
        double pl = 0.0;
        pl = calculateProfitAndLoss(maxLoss);
        profitLoss.put(maxLoss, pl);

        return pl;
    }
    private double calculateProfitAndLoss(int maxLoss){
        MathUtil mathUtil = new MathUtil();
        double thisRisk = getThisRisk();
        double multiplier = mathUtil.round(((TradingConstants.MAX_RISK / 100) / thisRisk),TradingConstants.ZERO_ROUNDING_PRECISION);
        if(((weeklyOHLC.getClose() - weeklyOHLC.getOpen()) * multiplier * 100) > maxLoss){
            return maxLoss*-1;
        }else if(((weeklyOHLC.getHigh() - weeklyOHLC.getOpen()) * multiplier * 100) > maxLoss){
            return maxLoss*-1;
        }
        return mathUtil.round((weeklyOHLC.getOpen() - weeklyOHLC.getClose()) * multiplier * 100,TradingConstants.IC_PRICING_ROUNDING_PRECISION);
    }

    /**
     * Gets the max risk of this condor based on one contract per leg.
     * @return
     */
    private double getThisRisk(){
        for(int i=Calendar.MONDAY; i<Calendar.FRIDAY; i++){
            List<VanillaOption> list = optionsByDay.get(i);
            if(list != null){
                double callDiff = getLongCall(list).getStrike() - getShortCall(list).getStrike();
                double putDiff = getShortPut(list).getStrike() - getLongPut(list).getStrike();
                return (callDiff > putDiff) ? callDiff : putDiff;
            }
        }
        //SHOULD NEVER REACH HERE
        return 0.0;
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

    public Map<Integer, List<VanillaOption>> getOptionsByDay() {
        return optionsByDay;
    }
    public void setOptionsByDay(Map<Integer, List<VanillaOption>> optionsByDay) {
        this.optionsByDay = optionsByDay;
    }

    public Map<Integer, Double> getProfitLoss() {
        if(profitLoss == null){
            profitLoss = new HashMap<Integer, Double>();
        }
        return profitLoss;
    }
    public void setProfitLoss(Map<Integer, Double> profitLoss) {
        this.profitLoss = profitLoss;
    }

    public Map<Integer, Integer> getRanks() {
        if(ranks == null){
            ranks = new HashMap<Integer, Integer>();
        }
        return ranks;
    }
    public void setRanks(Map<Integer, Integer> ranks) {
        this.ranks = ranks;
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

    public List<Quote> getWeeksQuotes() {
        return weeksQuotes;
    }
    public void setWeeksQuotes(List<Quote> weeksQuotes) {
        this.weeksQuotes = weeksQuotes;
    }

    public TradingRange getTradingRange() {
        return tradingRange;
    }
    public void setTradingRange(TradingRange tradingRange) {
        this.tradingRange = tradingRange;
    }

    public int getRangeDays() {
        return rangeDays;
    }
    public void setRangeDays(int rangeDays) {
        this.rangeDays = rangeDays;
    }
}
