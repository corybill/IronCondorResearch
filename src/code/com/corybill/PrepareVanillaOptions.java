package code.com.corybill;

import code.com.corybill.control.dataLoad.VanillaOptionsDataLoad;
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
 * Date: 1/22/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareVanillaOptions extends PrepareBase<PrepareVanillaOptions> {
    private static Logger log = Logger.getLogger(PrepareVanillaOptions.class);
    private VanillaOptionsDataLoad dataLoad;

    public VanillaOptionsDataLoad getDataLoad() {
        return dataLoad;
    }
    public void setDataLoad(VanillaOptionsDataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    @Override
    public PrepareBase<PrepareVanillaOptions> getInstance(){
        return (PrepareVanillaOptions) MySpringLoader.getInstance().getContext().getBean("vanillaOptions");
    }

    @Override
    public void run(){
        dataLoad.setListOfSymbols(list);
        dataLoad.invoke();
    }

    public static void main(String[] args){
        MySpringLoader.getInstance();
        PrepareVanillaOptions base = new PrepareVanillaOptions();
        base.start();
    }
    public void start(){
        log.trace("Starting " + PrepareVanillaOptions.class.getName());
        long start = Calendar.getInstance().getTimeInMillis();

        UpdateDateDao.resetVanillaOptionDate();

        try {
            ExecutorService es = Executors.newCachedThreadPool();
            SymbolThreadHelper.prepareStarterThreads(new PrepareVanillaOptions(), es, TradingConstants.VANILLA_OPTIONS_NUM_THREADS);
            es.shutdown();
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(e.getStackTrace());
        }

        UpdateDateDao.updateVanillaOptionDate();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }

}
