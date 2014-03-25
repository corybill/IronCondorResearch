package code.com.corybill.control.dataLoad;

import code.com.corybill.control.mongoDao.QuoteDao;
import code.com.corybill.helper.VolRankingHelper;
import code.com.corybill.model.Quote;
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
public class QuoteVolRankingDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(QuoteVolRankingDataLoad.class);

    private QuoteDao quoteDao;
    private VolRankingHelper dlHelper;

    private List<String> dates;
    List<Quote> quotes;

    @Override
    public void invoke() {
        for(String date : dates){
            long start = Calendar.getInstance().getTimeInMillis();

            dlHelper.resetSaveMaps();
            quotes = quoteDao.getQuotesByDate(date);
            getData(date);

            long end = Calendar.getInstance().getTimeInMillis();
            double totalTimeInMinutes = (end - start) / 60000.00;
            log.debug(date + " - " + totalTimeInMinutes);
        }
    }

    @Override
    public void getData(String date,double... doubles) {
        try{
            for(Quote quote : quotes){
                dlHelper.setAllQuoteVolRanks(quote);
            }
            dlHelper.setRanks();
            quoteDao.saveQuotes(quotes);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public VolRankingHelper getDlHelper() {
        return dlHelper;
    }
    public void setDlHelper(VolRankingHelper dlHelper) {
        this.dlHelper = dlHelper;
    }

    public QuoteDao getQuoteDao() {
        return quoteDao;
    }
    public void setQuoteDao(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    public List<String> getDates() {
        return dates;
    }
    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
