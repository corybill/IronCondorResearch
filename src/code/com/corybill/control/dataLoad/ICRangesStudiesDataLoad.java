package code.com.corybill.control.dataLoad;

import code.com.corybill.control.mongoDao.IronCondorRangesDao;
import code.com.corybill.helper.ICRangesTopFiveStudyHelper;
import code.com.corybill.helper.ICRangesTopTenStudyHelper;
import code.com.corybill.model.IronCondorRanges;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class ICRangesStudiesDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(ICRangesStudiesDataLoad.class);

    private IronCondorRangesDao ironCondorDao;
    private ICRangesTopFiveStudyHelper topFiveHelper;
    private ICRangesTopTenStudyHelper topTenHelper;

    private List<String> expirations;

    List<IronCondorRanges> condorQuery;

    @Override
    public void invoke() {
        //We want the dates in starting in the past and going to most recent
        //Do the work here.  Loop through the expirations and let getData take care of the rest.
        for(String expiration : expirations){
            long start = Calendar.getInstance().getTimeInMillis();
            condorQuery = ironCondorDao.getForICProfitLossDataLoad(expiration);

            getData(expiration);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug( expiration + " - " + totalTimeInMinutes);
        }

        //Start printing the data out to .csv files.
        topFiveHelper.writeToFile(expirations);
        topTenHelper.writeToFile(expirations);
    }

    @Override
    public void getData(String expiration,double... doubles) {
        try{
            for(IronCondorRanges condor : condorQuery){
                topFiveHelper.addValuesToSummedMaps(condor);
                topTenHelper.addValuesToSummedMaps(condor);
            }
            topFiveHelper.prepareAddValuesToMaps();
            topTenHelper.prepareAddValuesToMaps();

        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public List<String> getExpirations() {
        return expirations;
    }
    public void setExpirations(List<String> expirations) {
        this.expirations = expirations;
    }

    public IronCondorRangesDao getIronCondorDao() {
        return ironCondorDao;
    }
    public void setIronCondorDao(IronCondorRangesDao ironCondorDao) {
        this.ironCondorDao = ironCondorDao;
    }

    public ICRangesTopFiveStudyHelper getTopFiveHelper() {
        return topFiveHelper;
    }
    public void setTopFiveHelper(ICRangesTopFiveStudyHelper topFiveHelper) {
        this.topFiveHelper = topFiveHelper;
    }

    public ICRangesTopTenStudyHelper getTopTenHelper() {
        return topTenHelper;
    }
    public void setTopTenHelper(ICRangesTopTenStudyHelper topTenHelper) {
        this.topTenHelper = topTenHelper;
    }
}
