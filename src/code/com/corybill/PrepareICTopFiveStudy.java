package code.com.corybill;

import code.com.corybill.control.dataLoad.ICTopFiveStudyDataLoad;
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
public class PrepareICTopFiveStudy extends PrepareBase<PrepareICTopFiveStudy> {
    private static Logger log = Logger.getLogger(PrepareICTopFiveStudy.class);
    private ICTopFiveStudyDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareICTopFiveStudy> getInstance() {
        return (PrepareICTopFiveStudy) MySpringLoader.getInstance().getContext().getBean("icTopFiveStudy");
    }

    @Override
    public void run() {
        dataLoad.setExpirations(this.list);
        dataLoad.invoke();
    }

    public ICTopFiveStudyDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(ICTopFiveStudyDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareICTopFiveStudy base = new PrepareICTopFiveStudy();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetICProfitLossUpdate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ExpirationThreadHelper.prepareStarterThreads(new PrepareICTopFiveStudy(), es, 1);
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
