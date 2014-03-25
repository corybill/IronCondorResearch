package test.com.corybill.model;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.MongoDatabase;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.model.IronCondor;
import code.com.corybill.model.OHLC;
import code.com.corybill.model.VanillaOption;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 3/5/13
 * Time: 7:30 AM
 * To change this template use File | Settings | File Templates.
 */

public class IronCondorTest {
    MathUtil mathUtil = new MathUtil();
    IronCondor condor;

    @Before
    public void before(){
        condor = new IronCondor();
        VanillaOption longCall = createSingleOption("XYZ",40.00,.1,TradingConstants.LONG,TradingConstants.CALL);
        VanillaOption shortCall = createSingleOption("XYZ",39.50,.25,TradingConstants.SHORT,TradingConstants.CALL);
        VanillaOption shortPut = createSingleOption("XYZ",38.00,37.2,TradingConstants.SHORT,TradingConstants.PUT);
        VanillaOption longPut = createSingleOption("XYZ",37.50,37.05,TradingConstants.LONG,TradingConstants.PUT);

        Map<Integer, List<VanillaOption>> optionsByDay = new HashMap<Integer,List<VanillaOption>>();
        List<VanillaOption> options = new ArrayList<VanillaOption>();
        options.add(longCall);
        options.add(shortCall);
        options.add(shortPut);
        options.add(longPut);
        optionsByDay.put(2,options);
        condor.setOptionsByDay(optionsByDay);
    }

    @Test
    public void IronCondorTestMaxGain(){
        OHLC ohlc = new OHLC();
        ohlc.setOpen(.10);
        ohlc.setHigh(.12);
        ohlc.setLow(.01);
        ohlc.setClose(0.00);
        condor.setWeeklyOHLC(ohlc);
        double pnl = condor.calculateProfitAndLossFor(400);
        Assert.assertEquals(pnl,100.00);
    }

    @Test
    public void IronCondorTestMaxLoss(){
        OHLC ohlc = new OHLC();
        ohlc.setOpen(.10);
        ohlc.setHigh(.15);
        ohlc.setLow(.01);
        ohlc.setClose(1.00);
        condor.setWeeklyOHLC(ohlc);
        double pnl = condor.calculateProfitAndLossFor(400);
        Assert.assertEquals(pnl,-400.00);
    }

    @Test
     public void IronCondorTestMiddleGain(){
        OHLC ohlc = new OHLC();
        ohlc.setOpen(.10);
        ohlc.setHigh(.12);
        ohlc.setLow(.01);
        ohlc.setClose(0.05);
        condor.setWeeklyOHLC(ohlc);
        double pnl = condor.calculateProfitAndLossFor(400);
        Assert.assertEquals(pnl,50.00);
    }

    @Test
    public void IronCondorTestMiddleLoss(){
        OHLC ohlc = new OHLC();
        ohlc.setOpen(.10);
        ohlc.setHigh(.12);
        ohlc.setLow(.01);
        ohlc.setClose(0.13);
        condor.setWeeklyOHLC(ohlc);
        double pnl = condor.calculateProfitAndLossFor(400);
        Assert.assertEquals(pnl,-30.00);
    }

    @Test
    public void IronCondorTestMaxLossReachedEarly(){
        OHLC ohlc = new OHLC();
        ohlc.setOpen(.10);
        ohlc.setHigh(.40);
        ohlc.setLow(.01);
        ohlc.setClose(0.05);
        condor.setWeeklyOHLC(ohlc);
        double pnl = condor.calculateProfitAndLossFor(200);
        Assert.assertEquals(pnl,-200.00);
    }

    private VanillaOption createSingleOption(String symbol,double strike,double price,String side, String type){
        MongoDatabase mongo = MongoDatabase.getInstance();

        VanillaOption option = new VanillaOption();
        OHLC optionOHLC = new OHLC();

        double openPrice = price;
        double highPrice = price + .5;
        double lowPrice = price - .5;
        double closePrice = price;

        option.setSymbol(symbol);
        option.setOhlc(optionOHLC);
        option.setType(type);
        option.setSide(side);
        option.setStrike(strike);
        optionOHLC.setOpen(openPrice);
        optionOHLC.setHigh(highPrice);
        optionOHLC.setLow(lowPrice);
        optionOHLC.setClose(closePrice);

        return option;
    }

}
