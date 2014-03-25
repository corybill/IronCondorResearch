package code.com.corybill.helper;

import code.com.corybill.model.IronCondorCurrent;
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
public class ICCurrentCreditRankingHelper {
    public Map<String,List<IronCondorCurrent>> map = new HashMap<String, List<IronCondorCurrent>>();

    public void addToOrderedCreditList(IronCondorCurrent condor){
        //This will get profitLoss for here and it will set the value in the IronCondor as well.
        double credit = condor.calculateCreditForCondor();

        String key = condor.getKey();
        List<IronCondorCurrent> list = map.get(key);
        if(list == null){
            list = new ArrayList<IronCondorCurrent>();
            list.add(condor);
            map.put(key,list);
            return;
        }

        for(int i=0; i<list.size(); i++){
            IronCondorCurrent condorQ = list.get(i);
            double creditQ = condorQ.calculateCreditForCondor();

            if(credit >= creditQ){
                list.add(i,condor);
                return;
            }
        }
        list.add(condor);
    }


    public void setRanks(){
        for(List<IronCondorCurrent> list : map.values()){
            for(int i=0; i<list.size(); i++){
                IronCondorCurrent condor = list.get(i);
                condor.setCreditRank(i + 1);
            }
        }
    }

    public void writeToFile(){
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
                List<IronCondorCurrent> condors = (List<IronCondorCurrent>)pairs.getValue();
                for(IronCondorCurrent c : condors){
                    String key = (String)pairs.getKey();
                    List<VanillaOption> list = c.getOptions();
                    String strikes = "";
                    for(VanillaOption option : list){
                        strikes += option.getStrike() + "-";
                    }
                    strikes = strikes.substring(0,strikes.length()-1);
                    int numDays = Integer.parseInt(key.split("_")[1]);

                    double sd = c.getQuote().getStandardDeviation().get(numDays) * Double.parseDouble(key.split("_")[0]);
                    pw.println(c.getCreditRank() + "," + c.getSymbol() + "," + pairs.getKey() + "," + sd + "," + c.getCredit() + "," + strikes);
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
}
