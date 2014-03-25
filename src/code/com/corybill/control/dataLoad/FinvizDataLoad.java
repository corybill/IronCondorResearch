package code.com.corybill.control.dataLoad;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/21/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FinvizDataLoad implements IDataLoad {
    private static Logger log = Logger.getLogger(FinvizDataLoad.class);

    private static final String FINVIZ_DATA_URL = "http://www.finviz.com/screener.ashx?v=152&ft=4&t=OEX,XEO,SPX*,SPX*,SPX*,SPX*,SPX*,SPX*,DJX,NDX,RUT,AGQ,DIA,EEM,EFA,EWJ,EWZ,FAS,FAZ,FXE,FXI,GLD,GDX,IWM,QQQ,SDS,SLV,SPY,SSO,TBT,TLT,TNA,TZA,USO,UNG,VXX,XLB,XLE,XLF,XME,AA,AAPL,ABX,ACN,AET,AIG,AMRN,AMZN,ANF,ANR,APA,APC,APKT,APOL,ARNA,AXP,BA,BAC,BAX,BIDU,BMY,BP,BRKB,C,CAT,CF,CHK,CLF,CMG,COP,CSCO,CREE,CRM,CVX,DE,DELL,DMND,DNDN,EMC,F,FB,FCX,FFIV,FMCN,FSLR,GE,GM,GMCR,GOOG,GPS,GRPN,GS,HAL,HD,HLF,HPQ,IBM,IDCC,INTC,IOC,JCP,JNJ,JOY,JPM,KCG,KO,LNKD,LO,LULU,LVS,MA,MCD,MCP,MET,MGM,MMM,MMR,MOS,MRVL,MS,MSFT,MU,NAV,NEM,NFLX,NKE,NOK,NTAP,NVDA,ORCL,OXY,PBR,PCLN,PCX,PFE,PG,POT,QCOM,QCOR,RIG,RIMM,S,SBUX,SHLD,SINA,SLB,SLW,SNDK,STX,SU,T,TXN,UNH,UNP,UTX,V,VHC,VZ,WDC,WFC,WLT,WMB,WMT,WYNN,X,XOM,YHOO,YOKU,ZNGA&c=0,1,2,48,50,51,59,65";
    private static final String WEEKLYS_SPLIT = "<td height=\"10\" align=\"left\" class=\"body-table-nw\" title=\"cssbody=[tabchrtbdy] cssheader=[tabchrthdr] body=[<img src='chart.ashx?s=m&ty=c&p=d&t=";
    private static final String GET_NEXT_PAGE = "class=\"screener-pages\">";

    public String url;

    @Override
    public void invoke(){
        String[] s = new String[1];
        try{


            System.out.print("");
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }

    public void getData(String symbol,double... doubles){
        String[] data = null;
        try{
            URL finviz = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(finviz.openStream()));

            String whole = "";
            String inputLine;
            int count = 0;
            while ((inputLine = in.readLine()) != null){
                whole += inputLine;
            }
            in.close();
            //data = whole.split(splitStr);
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getStackTrace());
        }
    }
}
