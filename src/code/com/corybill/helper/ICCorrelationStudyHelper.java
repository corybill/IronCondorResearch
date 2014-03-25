package code.com.corybill.helper;

import code.com.corybill.model.ICStudyResult;
import code.com.corybill.model.IronCondor;
import code.com.corybill.model.VanillaOption;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/7/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICCorrelationStudyHelper {
    Map<String,ICStudyResult> summedRanks = new HashMap<String,ICStudyResult>();
    Map<String,List<ICStudyResult>> averageRanksOverTime = new HashMap<String,List<ICStudyResult>>();

    public boolean topTenKey(String key,String[] topTen){
        for(String s : topTen){
            if(s.equals(key)){
                return true;
            }
        }
        return false;
    }

    /**
     * We need to get one of the options from the first day of the trading week so we can get the volatility rank.
     * @param map
     * @return
     */
    public int getUnderlyingVolRank(Map<Integer,List<VanillaOption>> map,String days){
        for(int i=Calendar.MONDAY; i<Calendar.FRIDAY; i++){
            List<VanillaOption> list = map.get(i);
            if(list != null){
                try{
                    return list.get(0).getQuote().getVolRank().get(Integer.parseInt(days));
                }catch (Exception e){
                    //This happens when a QUOTE is in the data from a bad date. (i.e. 2010-01-18 was a market holiday but we have a quote for DIA)
                    e.printStackTrace();
                }
            }
        }
        //Should not be able to get here;
        return -1;
    }

    public void addValuesToSummedMaps(IronCondor condor){
        String key = condor.getKey();
        for(int maxLoss : TradingConstants.maxLosses){
            if(condor.getRanks().get(maxLoss) <= 10){
                ICStudyResult result = summedRanks.get(key+"_"+maxLoss);
                if(result == null){
                    result = new ICStudyResult();
                    summedRanks.put(key + "_" + maxLoss, result);
                }
                int newCreditRank = condor.getCreditRank();
                result.addToCreditRank(newCreditRank);

                int newVolRank = getUnderlyingVolRank(condor.getOptionsByDay(),key.split("_")[1]);
                result.addToVolRank(newVolRank);

                int newPlRank = condor.getRanks().get(maxLoss);
                result.addToPlRank(newPlRank);

                result.setCounter(result.getCounter() + 1);
            }
        }
    }

    public void prepareAddValuesToMaps(){
        Iterator it = summedRanks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String overTimeKey = (String)pairs.getKey();

            ICStudyResult result = summedRanks.get(overTimeKey);
            List<ICStudyResult> overTimeList = averageRanksOverTime.get(overTimeKey);

            if(overTimeList == null){
                overTimeList = new ArrayList<ICStudyResult>();
                averageRanksOverTime.put(overTimeKey, overTimeList);
            }
            result.setAverages();
            overTimeList.add(result);
        }
    }

    public void writeToFile(List<String> expirations){
        PrintWriter pw = null;
        try{
            File overTimeFile = new File("output/CorrelationOverTime.csv");
            if(overTimeFile.exists()){
                overTimeFile.delete();
            }

            overTimeFile.createNewFile();
            pw = new PrintWriter(overTimeFile);
            Iterator it = averageRanksOverTime.entrySet().iterator();

            String dates = ",";
            for(String expiration : expirations){
                dates += expiration + ",";
            }
            pw.println(dates);
            pw.flush();

            boolean odd = true;
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                String plKey = pairs.getKey() + "_PL" + ",";
                String volKey = pairs.getKey() + "_VOL" + ",";
                String creditKey = pairs.getKey() + "_CREDIT" + ",";

                for(ICStudyResult result : (List<ICStudyResult>)pairs.getValue()){
                    if(odd){
                        plKey += result.getPlRankAverage()+.001 + ",";
                    }else{
                        plKey += result.getPlRankAverage()-.001 + ",";
                    }
                    volKey += result.getVolRankAverage() + ",";
                    creditKey += result.getCreditRankAverage() + ",";
                    odd = !odd;
                }
                pw.println(plKey);
                pw.println(volKey);
                pw.println(creditKey);
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
}
