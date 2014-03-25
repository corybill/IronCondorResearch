package code.com.corybill;

import code.com.corybill.spring.MySpringLoader;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/12/13
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrepareEntireHistory {
    private static Logger log = Logger.getLogger(PrepareEntireHistory.class);

    private static String LOG_PROPERTIES_FILE = "C:\\Users\\Cory\\IdeaProjects\\untitled\\src\\resources\\log4j.properties";

    public static void main(String[] args){
        MySpringLoader.getInstance();
        initializeLogger();

        LogManager.getRootLogger().setLevel((Level) Level.ALL);
        long start = Calendar.getInstance().getTimeInMillis();

        //TODO update all update dates and the UpdateDateDao to reflect correct dates.  Don't forget to include history v current
       /*singleQuotes();

        try{
            ExecutorService ironCondor = Executors.newCachedThreadPool();
            ironCondor.execute(new IronCondorStandardDeviation());
            //ironCondor.execute(new IronCondorRanges());
            ironCondor.shutdown();
            ironCondor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            ExecutorService rankStudy = Executors.newCachedThreadPool();
            rankStudy.execute(new IronCondorRankStudyStandardDeviation());
            //rankStudy.execute(new IronCondorRankStudyRanges());
            rankStudy.shutdown();
            rankStudy.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        try{
            ExecutorService currentStudy = Executors.newCachedThreadPool();
            currentStudy.execute(new CurrentIronCondorStandardDeviation());
            //currentStudy.execute(new CurrentIronCondorRanges());
            currentStudy.shutdown();
            currentStudy.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }

        //currentOptions();

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.trace("Complete: " + totalTimeInMinutes);
    }

    private static void singleQuotes(){
        (new PrepareSingleQuote()).start();
        (new PrepareQuoteVolatility()).start();
        (new PrepareQuoteVolRanking()).start();
    }

    private static void currentOptions(){
        (new PrepareCurrentOptions()).start();
    }

    public static class IronCondorStandardDeviation implements Runnable{
        @Override
        public void run(){
            (new PrepareIronCondors()).start();
        }
    }
    public static class IronCondorRanges implements Runnable{
        @Override
        public void run(){
            (new PrepareIronCondorRanges()).start();
        }
    }
    public static class IronCondorRankStudyStandardDeviation implements Runnable{
        @Override
        public void run(){
            (new PrepareICRanking()).start();
            (new PrepareICStudies()).start();
        }
    }
    public static class IronCondorRankStudyRanges implements Runnable{
        @Override
        public void run(){
            (new PrepareICRangesRanking()).start();
            (new PrepareICRangesStudies()).start();
        }
    }

    public static class CurrentIronCondorStandardDeviation implements Runnable{
        @Override
        public void run(){
            (new PrepareCurrentIC()).start();
            (new PrepareCurrentICCreditRanking()).start();
        }
    }
    public static class CurrentIronCondorRanges implements Runnable{
        @Override
        public void run(){
            (new PrepareCurrentRangesIC()).start();
            (new PrepareCurrentRangesICCreditRanking()).start();
        }
    }

    private static void vanillaOptions(){
        (new PrepareVanillaOptions()).start();
    }

    private static void initializeLogger(){
        Properties logProperties = new Properties();

        try{
            // load our log4j properties / configuration file
            logProperties.load(new FileInputStream(LOG_PROPERTIES_FILE));
            PropertyConfigurator.configure(logProperties);
            log.info("Logging initialized.");
        }catch(IOException e){
            throw new RuntimeException("Unable to load logging property " + LOG_PROPERTIES_FILE);
        }
    }
}
