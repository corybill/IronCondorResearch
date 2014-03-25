package code.com.corybill;

import code.com.corybill.control.dataLoad.ICStudiesDataLoad;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.ExpirationThreadHelper;
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
 * Date: 1/21/13
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareICStudies extends PrepareBase<PrepareICStudies> {
    private static Logger log = Logger.getLogger(PrepareICStudies.class);
    private ICStudiesDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareICStudies> getInstance() {
        return (PrepareICStudies) MySpringLoader.getInstance().getContext().getBean("icStudies");
    }

    @Override
    public void run() {
        dataLoad.setExpirations(this.list);
        dataLoad.invoke();
    }

    public ICStudiesDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(ICStudiesDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareICStudies base = new PrepareICStudies();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetICProfitLossUpdate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ExpirationThreadHelper.prepareStarterThreads(new PrepareICStudies(), es, TradingConstants.IC_STUDIES_NUM_THREADS);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

        UpdateDateDao.udpateICProfitLossUpdate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }
}
