package code.com.corybill;

import code.com.corybill.control.dataLoad.CurrentOptionsDataLoad;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.SymbolThreadHelper;
import code.com.corybill.helper.TradingConstants;
import code.com.corybill.spring.MySpringLoader;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/27/12
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareCurrentOptions extends PrepareBase<PrepareCurrentOptions> {
    private static Logger log = Logger.getLogger(PrepareCurrentOptions.class);
    private CurrentOptionsDataLoad dataLoad;

    public CurrentOptionsDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(CurrentOptionsDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    @Override
    public PrepareBase<PrepareCurrentOptions> getInstance(){
        return (PrepareCurrentOptions) MySpringLoader.getInstance().getContext().getBean("currentOptions");
    }

    @Override
    public void run(){
        dataLoad.setListOfSymbols(list);
        dataLoad.invoke();
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareCurrentOptions base = new PrepareCurrentOptions();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            SymbolThreadHelper.prepareStarterThreads(new PrepareCurrentOptions(), es, TradingConstants.CURRENT_IC_NUM_THREADS);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

        UpdateDateDao.updateIronCondorDate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }
}
