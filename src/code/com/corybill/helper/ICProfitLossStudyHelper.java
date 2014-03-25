package code.com.corybill.helper;

import code.com.corybill.model.IronCondor;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/16/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICProfitLossStudyHelper {
    Map<String,Double> profitsLosses = new HashMap<String,Double>();
    Map<String,List<Double>> plOverTime = new HashMap<String,List<Double>>();

    private static Map<String,Double> pls = new HashMap<String,Double>();

    public synchronized void addToPAndLs(Map<String,Double> map){
        pls.putAll(map);
    }
    public synchronized double getPAndL(String key){
        return pls.get(key);
    }

    public void addValuesToSummedMaps(IronCondor condor){
        String key = condor.getKey();
        for(int maxLoss : TradingConstants.maxLosses){

            Double total = profitsLosses.get(key + "_" + maxLoss);
            if(total == null){
                total = new Double(condor.getProfitLoss().get(maxLoss));
                //Accounts start with $100,000
                total += 100000;
            }else{
                total += condor.getProfitLoss().get(maxLoss);
            }
            profitsLosses.put(key + "_" + maxLoss, total);
        }
    }

    public void prepareAddValuesToMaps(){
        Iterator it = profitsLosses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String overTimeKey = (String)pairs.getKey();

            double pl = profitsLosses.get(overTimeKey);
            List<Double> overTimeList = plOverTime.get(overTimeKey);

            if(overTimeList == null){
                overTimeList = new ArrayList<Double>();
                plOverTime.put(overTimeKey, overTimeList);
            }
            overTimeList.add(pl);
        }
    }

    public void writeToFile(List<String> expirations){
        PrintWriter pw = null;
        try{
            Iterator it = profitsLosses.entrySet().iterator();
            File file = new File("output/AllProfitLossTotals.csv");
            if(file.exists()){
                file.delete();
            }

            file.createNewFile();
            pw = new PrintWriter(file);
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();

                pw.println(pairs.getKey() + "," + pairs.getValue());
                pw.flush();
            }

            File overTimeFile = new File("output/AllOverTime.csv");
            if(overTimeFile.exists()){
                overTimeFile.delete();
            }

            overTimeFile.createNewFile();
            pw = new PrintWriter(overTimeFile);
            it = plOverTime.entrySet().iterator();

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
