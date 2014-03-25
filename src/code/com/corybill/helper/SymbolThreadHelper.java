package code.com.corybill.helper;

import code.com.corybill.PrepareBase;
import code.com.corybill.control.dataLoad.CBOEDataLoad;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/26/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class SymbolThreadHelper {

    public static void prepareStarterThreads(PrepareBase<? extends PrepareBase> base, ExecutorService es, int numThreads ){
        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"resources/applicationContext.xml"});
        CBOEDataLoad cboe = (CBOEDataLoad) context.getBean("cboeBean");
        cboe.invoke();

        List<String> masterList = cboe.quotes;

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
