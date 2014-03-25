package code.com.corybill.helper;

import code.com.corybill.control.dataStructures.MutableDouble;
import code.com.corybill.control.math.MathUtil;
import code.com.corybill.model.IronCondorInvest;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 3/17/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrentOptionsHelper {
    private static Logger log = Logger.getLogger(CurrentOptionsHelper.class);
    private MathUtil mathUtil;

    public double getLowerBound(double stockPrice, double sd,double strikeInterval){
        double lowerBound = stockPrice - mathUtil.round(sd,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double change = mathUtil.round(lowerBound % strikeInterval,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        lowerBound = mathUtil.round(lowerBound - change,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);

        return lowerBound;
    }
    public double getUpperBound(double stockPrice, double sd,double strikeInterval){
        double upperBound = stockPrice + mathUtil.round(sd,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double change = mathUtil.round(upperBound % strikeInterval,TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        upperBound = mathUtil.round((upperBound + (strikeInterval - change)),TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        return upperBound;
    }

    /**
     * 1. Get the first strike above Stock Price
     * 2. Get that value + (10*strike interval)
     * 3. if callPrice is greater than this number, then it must be a multiple of (2*strikeInterval) instead of (1*strikeInterval)
     * @param stockPrice
     * @param shortCallStrike
     * @param longCallStrike
     * @param strikeInterval
     * @return
     */
    public void verifyCorrectCall(Double stockPrice, MutableDouble shortCallStrike,MutableDouble longCallStrike,Double strikeInterval){
        if(strikeInterval == 5){
            return;
        }
        double firstStrike = stockPrice - mathUtil.round((stockPrice % strikeInterval),TradingConstants.STOCK_PRICING_ROUNDING_PRECISION) + strikeInterval;
        double topLevelBeforeIntervalIncrease = firstStrike + (10*strikeInterval);

        //TODO:  Verify if this needs to be > or >= for both statements

        //If this is true then we need to make sure shortCallStrike is a multiple of strikeInterval*2
        if(shortCallStrike.getValue() > topLevelBeforeIntervalIncrease){
            if(shortCallStrike.getValue() % (2*strikeInterval) != 0){
                shortCallStrike.setValue(shortCallStrike.getValue() + strikeInterval);
                longCallStrike.setValue(shortCallStrike.getValue() + 2*strikeInterval);
            }else{
                //short call is okay, lets just reset the long call strike to make sure.
                longCallStrike.setValue(shortCallStrike.getValue() + 2*strikeInterval);
            }
        }else if(longCallStrike.getValue() > topLevelBeforeIntervalIncrease){
            if(longCallStrike.getValue() % (2*strikeInterval) != 0){
                longCallStrike.setValue(longCallStrike.getValue() + strikeInterval);
            }
        }
    }
    public void verifyCorrectPut(Double stockPrice, MutableDouble shortPutStrike,MutableDouble longPutStrike,Double strikeInterval){
        if(strikeInterval == 5){
            return;
        }

        double firstStrike = stockPrice - mathUtil.round((stockPrice % strikeInterval), TradingConstants.STOCK_PRICING_ROUNDING_PRECISION);
        double topLevelBeforeIntervalIncrease = firstStrike - (10*strikeInterval);


        //TODO:  Verify if this needs to be < or <= for both statements

        //If this is true then we need to make sure shortCallStrike is a multiple of strikeInterval*2
        if(shortPutStrike.getValue() < topLevelBeforeIntervalIncrease){
            if(shortPutStrike.getValue() % (2*strikeInterval) != 0){
                shortPutStrike.setValue(shortPutStrike.getValue() - strikeInterval);
                longPutStrike.setValue(shortPutStrike.getValue() - 2*strikeInterval);
            }else{
                //short call is okay, lets just reset the long call strike to make sure.
                longPutStrike.setValue(shortPutStrike.getValue() - 2*strikeInterval);
            }
        }else if(longPutStrike.getValue() < topLevelBeforeIntervalIncrease){
            if(longPutStrike.getValue() % (2*strikeInterval) != 0){
                longPutStrike.setValue(longPutStrike.getValue() - strikeInterval);
            }
        }
    }

    public double getStrikeInterval(double price){
        if(price < 50){
            return .5;
        }else if(price < 100){
            return 2.50;
        }else{
            return 5;
        }
    }

    public String prepareOptionId(String symbol,String expiration,double strike,String side){
        String id = symbol + expiration + side;
        int intStrike = (int)(strike * 1000);
        String idStrike = intStrike + "";
        while(idStrike.length() < 8){
            idStrike = "0" + idStrike;
        }
        id += idStrike;
        return id;
    }

    public String getOptionHtml(String id){
        String htmlReturn="";
        try{
            String urlString = "http://finance.yahoo.com/q?s=" + id;

            URL url = new URL(urlString);
            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );

            String inputLine = in.readLine();
            while ((inputLine = in.readLine()) != null){
                htmlReturn += inputLine;
            }
            in.close();
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return htmlReturn;
    }

    public double getBidAskPrice(String html,String split1Str,String id){
        String[] split3 = null;
        try{
            String[] split1 = html.split(split1Str);

            String split2Str = "</span>";
            String[] split2 = split1[1].split(split2Str);

            String split3Str = ">";
            split3 = split2[0].split(split3Str);
        }catch (Exception e){
            //e.printStackTrace();
            return 0;
        }


        return Double.parseDouble(split3[split3.length-1]);
    }

    public List<IronCondorInvest> orderCreditHighToLow(List<IronCondorInvest> list){
        List<IronCondorInvest> orderedList = new ArrayList<IronCondorInvest>();

        for(IronCondorInvest unordered : list){
            boolean placedInOrderList = false;
            for(int i=0; i<orderedList.size(); i++){
                IronCondorInvest ordered = orderedList.get(i);
                if(unordered.getCredit() > ordered.getCredit()){
                    placedInOrderList = true;
                    orderedList.add(i,unordered);
                    break;
                }
            }
            if(!placedInOrderList){
                orderedList.add(unordered);
            }
        }
        return orderedList;
    }

    public void writeToFile(Map<Double,List<IronCondorInvest>> map){
        PrintWriter pw = null;
        try{
            File file = new File("output/CurrentWeekCondors.csv");
            if(file.exists()){
                file.delete();
            }

            file.createNewFile();
            pw = new PrintWriter(file);
            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                List<IronCondorInvest> condors = (List<IronCondorInvest>)pairs.getValue();
                int rank = 1;
                for(IronCondorInvest c : condors){
                    Double key = (Double)pairs.getKey();
                    String strikes = c.getLongCallStrike() + "-" + c.getShortCallStrike() + "-" + c.getShortPutStrike() + "-" + c.getLongPutStrike();

                    double sd = c.getQuote().getStandardDeviation().get(21) * key;
                    pw.println(rank + "," + c.getSymbol() + "," + pairs.getKey() + "," + sd + "," + c.getCredit() + "," + strikes);
                    rank++;
                }
                pw.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(pw != null){
                pw.flush();
                pw.close();
            }
        }
    }

    public MathUtil getMathUtil() {
        return mathUtil;
    }
    public void setMathUtil(MathUtil mathUtil) {
        this.mathUtil = mathUtil;
    }
}
