package code.com.corybill.helper;

import code.com.corybill.model.IronCondorRanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/7/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICRangesCreditRankingHelper {
    public Map<String,List<IronCondorRanges>> map;

    public void addToOrderedCreditList(IronCondorRanges condor){
        String key = condor.getKey();
        List<IronCondorRanges> list = map.get(key);
        if(list == null){
            double profitLoss = condor.calculateCreditForCondor();
            list = new ArrayList<IronCondorRanges>();
            list.add(condor);
            map.put(key,list);
            return;
        }

        //This will get profitLoss for here and it will set the value in the IronCondor as well.
        double credit = condor.calculateCreditForCondor();
        for(int i=0; i<list.size(); i++){
            IronCondorRanges condorQ = list.get(i);
            double creditQ = condorQ.calculateCreditForCondor();

            if(credit >= creditQ){
                list.add(i,condor);
                return;
            }
        }
        list.add(condor);
    }

    public void setRanks(){
        for(List<IronCondorRanges> list : map.values()){
            for(int i=0; i<list.size(); i++){
                IronCondorRanges condor = list.get(i);
                list.get(i).setCreditRank(i+1);
            }
        }
    }

    public void resetSaveLists(){
        map = new HashMap<String, List<IronCondorRanges>>();
    }
}
