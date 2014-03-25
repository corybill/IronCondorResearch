package code.com.corybill.control.dataLoad;

import code.com.corybill.helper.SymbolHelper;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/22/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class CBOEDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(CBOEDataLoad.class);

    public String urlStr = "http://www.cboe.com/publish/weelkysmf/weeklysmf.xls";
    HSSFWorkbook workbook;

    public List<String> quotes = new ArrayList<String>();

    public void invoke(){
        try{
            URL url = new URL(urlStr);
            URLConnection uc = url.openConnection();
            workbook = new HSSFWorkbook(uc.getInputStream());
            getData("");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public void getData(String symbol,double... doubles){
        try{
            HSSFSheet worksheet = workbook.getSheet("Sheet1");

            for(int i=0; i<worksheet.getLastRowNum(); i++){
                long start = Calendar.getInstance().getTimeInMillis();

                symbol = worksheet.getRow(i).getCell(0).getStringCellValue();

                //if symbol is empty string, we have all the symbols
                if(symbol.equals("")){
                    break;
                }else if(symbol.length() < 6 && !symbol.equalsIgnoreCase("TICKER")){
                    //We have no data for DJX
                    if(!symbol.equals("DJX")){
                        quotes.add(SymbolHelper.ironCondorDataLoadCleaner(symbol));
                    }
                }


                long end = Calendar.getInstance().getTimeInMillis();
                double totalTimeInMinutes = (end - start) / 60000.00;
                log.debug(symbol + " - " + totalTimeInMinutes);

                symbol = null;
            }
            worksheet=null;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }
}
