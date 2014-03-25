package code.com.corybill.helper;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.model.IronCondorRanges;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/7/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICRangesProfitAndLossRankingHelper {
    private Map<String,List<IronCondorRanges>> map;
    private MathUtil mathUtil;

    /**
     * Orders Items in the list by profit assuming that we always hold the Condor to expiration.
     * @param condor
     */
    public void addToOrderedPaLList(IronCondorRanges condor){
        for(Integer maxLoss : TradingConstants.maxRangesLosses){
            String key = condor.getKey() + "_" + maxLoss;
            List<IronCondorRanges> list = map.get(key);
            if(list == null){
                double profitLoss = condor.calculateProfitAndLossFor(maxLoss);
                list = new ArrayList<IronCondorRanges>();
                list.add(condor);
                map.put(key,list);
                continue;
            }

            //This will get profitLoss for here and it will set the value in the IronCondor as well.
            double profitLoss = condor.calculateProfitAndLossFor(maxLoss);
            boolean found = false;
            for(int i=0; i<list.size(); i++){
                IronCondorRanges condorQ = list.get(i);
                double profitLossQ = condorQ.calculateProfitAndLossFor(maxLoss);

                if(profitLoss >= profitLossQ){
                    list.add(i,condor);
                    found = true;
                    break;
                }
            }
            if(!found){
                list.add(condor);
            }
        }
    }

    public void setRanks(){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String[] keyParts = ((String)pairs.getKey()).split("_");
            Integer maxLoss = Integer.parseInt(keyParts[keyParts.length-1]);

            List<IronCondorRanges> list = (List<IronCondorRanges>)pairs.getValue();
            for(int i=0; i<list.size(); i++){
                IronCondorRanges condor = list.get(i);
                condor.getRanks().put(maxLoss,i+1);
            }
        }
    }

    public void resetSaveLists(){
        map = new HashMap<String,List<IronCondorRanges>>();
    }

    public MathUtil getMathUtil() {
        return mathUtil;
    }
    public void setMathUtil(MathUtil mathUtil) {
        this.mathUtil = mathUtil;
    }
}
