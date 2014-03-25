package code.com.corybill;

import code.com.corybill.control.dataLoad.ICCorrelationStudyDataLoad;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.ExpirationThreadHelper;
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
public class PrepareICCorrelationStudy extends PrepareBase<PrepareICCorrelationStudy> {
    private static Logger log = Logger.getLogger(PrepareICCorrelationStudy.class);
    private ICCorrelationStudyDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareICCorrelationStudy> getInstance() {
        return (PrepareICCorrelationStudy) MySpringLoader.getInstance().getContext().getBean("icCorrealtionStudy");
    }

    @Override
    public void run() {
        dataLoad.setExpirations(this.list);
        dataLoad.invoke();
    }

    public ICCorrelationStudyDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(ICCorrelationStudyDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareICCorrelationStudy base = new PrepareICCorrelationStudy();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetICProfitLossUpdate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ExpirationThreadHelper.prepareStarterThreads(new PrepareICCorrelationStudy(), es, 1);
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
