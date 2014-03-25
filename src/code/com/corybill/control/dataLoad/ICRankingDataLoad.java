package code.com.corybill.control.dataLoad;

import code.com.corybill.control.math.MathUtil;
import code.com.corybill.control.mongoDao.IronCondorDao;
import code.com.corybill.helper.ICCreditRankingHelper;
import code.com.corybill.helper.ICProfitAndLossRankingHelper;
import code.com.corybill.model.IronCondor;
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
public class ICRankingDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(ICRankingDataLoad.class);

    private MathUtil mathUtil;
    private IronCondorDao ironCondorDao;
    private ICProfitAndLossRankingHelper pnlRankingHelper;
    private ICCreditRankingHelper creditRankingHelper;
    private List<String> expirations;

    double profitable = 0;
    double unProfitable = 0;

    double profitLoss = 0.0;
    double deviation = 0.0;
    int day = 0;

    List<IronCondor> condorQuery;

    @Override
    public void invoke() {
        for(String expiration : expirations){
            long start = Calendar.getInstance().getTimeInMillis();
            pnlRankingHelper.resetSaveLists();
            creditRankingHelper.resetSaveLists();
            condorQuery =  ironCondorDao.getForICProfitLossDataLoad(expiration);
            getData(expiration);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug( expiration + " - " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String symbol,double... doubles) {
        try{
            for(IronCondor condor : condorQuery){
                pnlRankingHelper.addToOrderedPaLList(condor);
                creditRankingHelper.addToOrderedCreditList(condor);
            }
            pnlRankingHelper.setRanks();
            creditRankingHelper.setRanks();
            ironCondorDao.saveIronCondors(condorQuery);
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

    public IronCondorDao getIronCondorDao() {
        return ironCondorDao;
    }
    public void setIronCondorDao(IronCondorDao ironCondorDao) {
        this.ironCondorDao = ironCondorDao;
    }

    public ICProfitAndLossRankingHelper getPnlRankingHelper() {
        return pnlRankingHelper;
    }
    public void setPnlRankingHelper(ICProfitAndLossRankingHelper pnlRankingHelper) {
        this.pnlRankingHelper = pnlRankingHelper;
    }

    public ICCreditRankingHelper getCreditRankingHelper() {
        return creditRankingHelper;
    }
    public void setCreditRankingHelper(ICCreditRankingHelper creditRankingHelper) {
        this.creditRankingHelper = creditRankingHelper;
    }
}
