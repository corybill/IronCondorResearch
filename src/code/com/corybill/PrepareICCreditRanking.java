package code.com.corybill;

import code.com.corybill.control.dataLoad.ICCreditRankingDataLoad;
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
public class PrepareICCreditRanking extends PrepareBase<PrepareICCreditRanking> {
    private static Logger log = Logger.getLogger(PrepareICCreditRanking.class);
    private ICCreditRankingDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareICCreditRanking> getInstance() {
        return (PrepareICCreditRanking) MySpringLoader.getInstance().getContext().getBean("creditRanking");
    }

    public void run(){
        dataLoad.setExpirations(this.list);
        dataLoad.invoke();
    }

    public ICCreditRankingDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(ICCreditRankingDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareICCreditRanking base = new PrepareICCreditRanking();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetICProfitLossUpdate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ExpirationThreadHelper.prepareStarterThreads(new PrepareICCreditRanking(), es, 5);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            log.error(e.getStackTrace());
        }

        UpdateDateDao.udpateICProfitLossUpdate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }
}
