package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.IronCondorCurrentRangesDao;
import code.com.corybill.helper.ICCurrentRangesCreditRankingHelper;
import code.com.corybill.model.IronCondorCurrentRanges;
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
public class CurrentICRangesCreditRankingDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(CurrentICRangesCreditRankingDataLoad.class);

    private MathUtil mathUtil;
    private IronCondorCurrentRangesDao icCurrentRangesDao;
    private ICCurrentRangesCreditRankingHelper dlHelper;
    private List<String> expirations;

    double profitable = 0;
    double unProfitable = 0;

    double profitLoss = 0.0;
    double deviation = 0.0;
    String date;
    int day = 0;

    List<IronCondorCurrentRanges> condorQuery;

    @Override
    public void invoke() {

        long start = Calendar.getInstance().getTimeInMillis();
        condorQuery =  icCurrentRangesDao.getForICCreditDataLoad();

        getData("");

        icCurrentRangesDao.saveIronCondors(condorQuery);

        long end = Calendar.getInstance().getTimeInMillis();
        double totalTimeInMinutes = (end - start) / 60000.00;
        log.debug(totalTimeInMinutes);

        dlHelper.writeToFile();
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            for(IronCondorCurrentRanges condor : condorQuery){
                dlHelper.addToOrderedCreditList(condor);
            }
            dlHelper.setRanks();
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

    public MathUtil getMathUtil() {
        return mathUtil;
    }
    public void setMathUtil(MathUtil mathUtil) {
        this.mathUtil = mathUtil;
    }

    public ICCurrentRangesCreditRankingHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(ICCurrentRangesCreditRankingHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public IronCondorCurrentRangesDao getIcCurrentRangesDao() {
        return icCurrentRangesDao;
    }
    public void setIcCurrentRangesDao(IronCondorCurrentRangesDao icCurrentRangesDao) {
        this.icCurrentRangesDao = icCurrentRangesDao;
    }
}
