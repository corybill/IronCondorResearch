package code.com.corybill.helper;

import code.com.corybill.PrepareBase;
import code.com.corybill.control.mongoDao.MarketDatesDao;
import code.com.corybill.model.MarketDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/26/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateThreadHelper {

    public static void prepareStarterThreads(PrepareBase<? extends PrepareBase> base, ExecutorService es, int numThreads ){
        MarketDatesDao marketDatesDao = new MarketDatesDao();
        List<MarketDate> masterListMarketDate = marketDatesDao.getAllDates();
        List<String> masterList = new ArrayList<String>();

        for(MarketDate marketDate : masterListMarketDate){
            masterList.add(marketDate.getMarketDate());
        }

        Collections.sort(masterList);

        int numberOfSymbolsPerThread = masterList.size() / numThreads;
        int leftOver = masterList.size() % numThreads;

        int[] countPerThread = new int[numThreads];
        for(int i=0;i<numThreads;i++){
            if(leftOver > 0){
                leftOver--;
                countPerThread[i] = numberOfSymbolsPerThread + 1;
            }else{
                countPerThread[i] = numberOfSymbolsPerThread;
            }
        }

        int total = 0;
        for(int i=0; i<numThreads; i++){
            int bottomIndex = total;
            int topIndex = total + countPerThread[i];

            PrepareBase<?> instance = base.getInstance();
            instance.list = masterList.subList(bottomIndex,topIndex);
            es.execute(instance);

            total = topIndex;
        }
    }

}
