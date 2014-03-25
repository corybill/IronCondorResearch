package code.com.corybill.helper;

import code.com.corybill.model.IronCondorRanges;

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
public class ICRangesTopTenStudyHelper {

    Map<Integer,Map<String,Double>> summedProfitsLosses = new HashMap<Integer,Map<String,Double>>();
    Map<Integer,Map<String,List<Double>>> plOverTime = new HashMap<Integer,Map<String,List<Double>>>();

    public boolean key(String key,String[] array){
        for(String s : array){
            if(s.equals(key)){
                return true;
            }
        }
        return false;
    }

    public void addValuesToMap(Map<String,Double> summedProfitsLosses,Map<String, List<Double>> plOverTime){
        Iterator it = summedProfitsLosses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String overTimeKey = (String)pairs.getKey();

            double pl = summedProfitsLosses.get(overTimeKey);
            List<Double> overTimeList = plOverTime.get(overTimeKey);

            if(overTimeList == null){
                overTimeList = new ArrayList<Double>();
                plOverTime.put(overTimeKey, overTimeList);
            }
            overTimeList.add(pl);
        }
    }
    public void addValuesToSummedMaps(IronCondorRanges condor){
        String key = condor.getKey();
        for(int maxLoss : TradingConstants.maxRangesLosses){
            for(int maxGain : TradingConstants.maxGains){
                //Top Ten Credits

                Map<String,Double> summedMap = summedProfitsLosses.get(maxGain);
                if(summedMap == null){
                    summedMap = new HashMap<String,Double>();
                    summedProfitsLosses.put(maxGain,summedMap);
                }

                if(condor.getCreditRank() <= 10){
                    String plKey = key + "_" + maxLoss + "_CREDIT";
                    Double total = summedMap.get(plKey);
                    Double condorPL = condor.calculateProfitAndLossFor(maxLoss,maxGain);

                    if(total == null){
                        total = new Double(condorPL);
                        if(total > maxGain){
                            total = maxGain * 1.0;
                        }
                    }else{
                        if(condorPL > maxGain){
                            total += maxGain * 1.0;
                        }else{
                            total += condorPL;
                        }
                    }
                    summedMap.put(plKey, total);
                }
            }
        }
    }

    public void writeToFile(List<String> expirations){
        String filePrefix = "output/TopTenRangesOverTime";
        String fileEnding = ".csv";
        for(Integer maxGain : TradingConstants.maxGains){
            String fileName = filePrefix + maxGain + fileEnding;
            printOverTime(plOverTime.get(maxGain), fileName, expirations);
        }
    }

    public void prepareAddValuesToMaps(){
        for(Integer maxGain : TradingConstants.maxGains){
            Map<String,List<Double>> map = plOverTime.get(maxGain);
            Map<String,Double> summedMap = summedProfitsLosses.get(maxGain);
            if(map == null){
                map = new HashMap<String, List<Double>>();
                plOverTime.put(maxGain,map);
            }
            addValuesToMap(summedMap, map);
        }
    }

    public void printOverTime(Map<String,List<Double>> plOverTime,String file, List<String> expirations){
        PrintWriter pw = null;
        try{
            File overTimeFile = new File(file);
            if(overTimeFile.exists()){
                overTimeFile.delete();
            }

            overTimeFile.createNewFile();
            pw = new PrintWriter(overTimeFile);
            Iterator it = plOverTime.entrySet().iterator();

            String dates = ",";
            for(String expiration : expirations){
                dates += expiration + ",";
            }
            pw.println(dates);
            pw.flush();

            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                String s = pairs.getKey() + ",";
                for(Double d : (List<Double>)pairs.getValue()){
                    s += d + ",";
                }
                pw.println(s);
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
