package code.com.corybill;

import code.com.corybill.control.dataLoad.QuoteVolatilityDataLoad;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.SymbolThreadHelper;
import code.com.corybill.spring.MySpringLoader;
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
public class PrepareQuoteVolatility extends PrepareBase<PrepareQuoteVolatility> {
    private static Logger log = Logger.getLogger(PrepareQuoteVolatility.class);
    private QuoteVolatilityDataLoad dataLoad;

    @Override
    public PrepareBase<PrepareQuoteVolatility> getInstance(){
        return (PrepareQuoteVolatility) MySpringLoader.getInstance().getContext().getBean("quoteVolatility");
    }

    @Override
    public void run(){
        dataLoad.setListOfSymbols(list);
        dataLoad.invoke();
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareQuoteVolatility base = new PrepareQuoteVolatility();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetLastVolatilityDate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            SymbolThreadHelper.prepareStarterThreads(new PrepareQuoteVolatility(), es, 12);
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

    public QuoteVolatilityDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(QuoteVolatilityDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }
}
