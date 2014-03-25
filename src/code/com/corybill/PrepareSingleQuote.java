package code.com.corybill;

import code.com.corybill.control.dataLoad.SingleQuoteDataLoad;
import code.com.corybill.control.mongoDao.MarketDatesDao;
import code.com.corybill.control.mongoDao.UpdateDateDao;
import code.com.corybill.helper.SingleQuoteHelper;
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
 * Date: 12/21/12
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareSingleQuote extends PrepareBase<PrepareSingleQuote> {
    private static Logger log = Logger.getLogger(PrepareSingleQuote.class);
    private SingleQuoteDataLoad dataLoad;
    //http://biz.yahoo.com/research/earncal/20101015.html
    public SingleQuoteDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(SingleQuoteDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    @Override
    public PrepareBase<PrepareSingleQuote> getInstance(){
        return (PrepareSingleQuote) MySpringLoader.getInstance().getContext().getBean("singleQuote");
    }

    @Override
    public void run(){
        dataLoad.quotes = list;
        dataLoad.invoke();
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareSingleQuote base = new PrepareSingleQuote();
        base.start();
    }

    public void start(){
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetSingleQuoteDate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            SymbolThreadHelper.prepareStarterThreads(new PrepareSingleQuote(), es, TradingConstants.QUOTES_NUM_THREADS);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

        MarketDatesDao marketDatesDao = new MarketDatesDao();
        marketDatesDao.saveMarketDates(SingleQuoteHelper.getMap());
        UpdateDateDao.updateSingleQuoteDate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }
}