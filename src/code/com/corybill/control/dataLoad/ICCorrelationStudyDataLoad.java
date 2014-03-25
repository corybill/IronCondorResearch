package code.com.corybill.control.dataLoad;

import code.com.corybill.control.mongoDao.IronCondorDao;
import code.com.corybill.helper.ICCorrelationStudyHelper;
import code.com.corybill.model.IronCondor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class ICCorrelationStudyDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(ICCorrelationStudyDataLoad.class);

    private IronCondorDao ironCondorDao;
    private ICCorrelationStudyDataLoad studyResultDao;
    private ICCorrelationStudyHelper dlHelper;
    private List<String> expirations;

    List<IronCondor> condorQuery;

    @Override
    public void invoke() {
        List<String> reverse = new ArrayList<String>();
        for(String expiration : expirations){
            reverse.add(0,expiration);
        }
        expirations = reverse;

        for(String expiration : expirations){
            long start = Calendar.getInstance().getTimeInMillis();
            condorQuery = ironCondorDao.getForICProfitLossDataLoad(expiration);

            getData(expiration);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug( expiration + " - " + totalTimeInMinutes);
        }

        dlHelper.writeToFile(expirations);
    }

    @Override
    public void getData(String expiration,double... doubles) {
        try{
            for(IronCondor condor : condorQuery){
               dlHelper.addValuesToSummedMaps(condor);
            }
            dlHelper.prepareAddValuesToMaps();
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

    public ICCorrelationStudyHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(ICCorrelationStudyHelper dlHelper) {
        this.dlHelper = dlHelper;
    }
}
