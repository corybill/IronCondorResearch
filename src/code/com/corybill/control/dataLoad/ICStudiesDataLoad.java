package code.com.corybill.control.dataLoad;

import code.com.corybill.control.mongoDao.ICStudyResultDao;
import code.com.corybill.helper.ICCorrelationStudyHelper;
import code.com.corybill.helper.ICProfitLossStudyHelper;
import code.com.corybill.control.mongoDao.IronCondorDao;
import code.com.corybill.helper.ICTopFiveStudyHelper;
import code.com.corybill.helper.ICTopTenStudyHelper;
import code.com.corybill.model.IronCondor;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class ICStudiesDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(ICStudiesDataLoad.class);

    private IronCondorDao ironCondorDao;
    private ICProfitLossStudyHelper pnlHelper;
    private ICCorrelationStudyHelper correlationHelper;
    private ICTopFiveStudyHelper topFiveHelper;
    private ICTopTenStudyHelper topTenHelper;

    private ICStudyResultDao studyResultDao;
    private List<String> expirations;

    List<IronCondor> condorQuery;

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
        pnlHelper.writeToFile(expirations);
        correlationHelper.writeToFile(expirations);
        topFiveHelper.writeToFile(expirations);
        //topTenHelper.writeToFile(expirations);
    }

    @Override
    public void getData(String expiration,double... doubles) {
        try{
            for(IronCondor condor : condorQuery){
                pnlHelper.addValuesToSummedMaps(condor);
                correlationHelper.addValuesToSummedMaps(condor);
                topFiveHelper.addValuesToSummedMaps(condor);
                //topTenHelper.addValuesToSummedMaps(condor);
            }
            pnlHelper.prepareAddValuesToMaps();
            correlationHelper.prepareAddValuesToMaps();
            topFiveHelper.prepareAddValuesToMaps();
            //topTenHelper.prepareAddValuesToMaps();

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

    public IronCondorDao getIronCondorDao() {
        return ironCondorDao;
    }
    public void setIronCondorDao(IronCondorDao ironCondorDao) {
        this.ironCondorDao = ironCondorDao;
    }

    public ICProfitLossStudyHelper getPnlHelper() {
        return pnlHelper;
    }
    public void setPnlHelper(ICProfitLossStudyHelper pnlHelper) {
        this.pnlHelper = pnlHelper;
    }

    public ICCorrelationStudyHelper getCorrelationHelper() {
        return correlationHelper;
    }
    public void setCorrelationHelper(ICCorrelationStudyHelper correlationHelper) {
        this.correlationHelper = correlationHelper;
    }

    public ICTopFiveStudyHelper getTopFiveHelper() {
        return topFiveHelper;
    }
    public void setTopFiveHelper(ICTopFiveStudyHelper topFiveHelper) {
        this.topFiveHelper = topFiveHelper;
    }

    public ICTopTenStudyHelper getTopTenHelper() {
        return topTenHelper;
    }
    public void setTopTenHelper(ICTopTenStudyHelper topTenHelper) {
        this.topTenHelper = topTenHelper;
    }
}
