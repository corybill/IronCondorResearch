package code.com.corybill;

import code.com.corybill.control.dataLoad.QuoteVolRankingDataLoad;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.DateThreadHelper;
import code.com.corybill.spring.MySpringLoader;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/24/13
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareQuoteVolRanking extends PrepareBase<PrepareQuoteVolRanking> {
    private static Logger log = Logger.getLogger(PrepareQuoteVolRanking.class);
    private QuoteVolRankingDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareQuoteVolRanking> getInstance(){
        return (PrepareQuoteVolRanking) MySpringLoader.getInstance().getContext().getBean("quoteVolRanking");
    }

    @Override
    public void run(){
        dataLoad.setDates(this.list);
        dataLoad.invoke();
    }

    public static void main(String[] args){
        LogManager.getRootLogger().setLevel((Level) Level.ALL);
        MySpringLoader.getInstance();
        PrepareQuoteVolRanking base = new PrepareQuoteVolRanking();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetLastVolatilityDate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            DateThreadHelper.prepareStarterThreads(new PrepareQuoteVolRanking(), es, 20);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

        UpdateDateDao.updateLastVolatilityDate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }

    public QuoteVolRankingDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(QuoteVolRankingDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }
}
