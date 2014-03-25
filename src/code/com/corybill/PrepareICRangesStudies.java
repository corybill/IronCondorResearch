package code.com.corybill;

import code.com.corybill.control.dataLoad.ICRangesStudiesDataLoad;
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
public class PrepareICRangesStudies extends PrepareBase<PrepareICRangesStudies> {
    private static Logger log = Logger.getLogger(PrepareICRangesStudies.class);
    private ICRangesStudiesDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareICRangesStudies> getInstance() {
        return (PrepareICRangesStudies) MySpringLoader.getInstance().getContext().getBean("icRangesStudies");
    }

    @Override
    public void run() {
        dataLoad.setExpirations(this.list);
        dataLoad.invoke();
    }

    public ICRangesStudiesDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(ICRangesStudiesDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareICRangesStudies base = new PrepareICRangesStudies();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetICProfitLossUpdate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ExpirationThreadHelper.prepareStarterThreads(new PrepareICRangesStudies(), es, TradingConstants.IC_STUDIES_NUM_THREADS);
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
