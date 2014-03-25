package code.com.corybill.helper;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/20/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class SymbolHelper {
    public static String cleanSymbols(String oldSymbol){
        if(oldSymbol.equals("BRKB")){
            return "BRK-B";
        }else if(oldSymbol.equals("NDX")){
            return "%5ENDX";
        }else if(oldSymbol.equals("RUT")){
            return "%5ERUT";
        }else if(oldSymbol.equals("OEX")){
            return "%5EOEX";
        }else if(oldSymbol.equals("SPY7")){
            return "SPY";
        }else if(oldSymbol.equals("AMZN7")){
            return "AMZN";
        }else if(oldSymbol.equals("AAPL7")){
            return "AAPL";
        }else if(oldSymbol.equals("GOOG7")){
            return "GOOG";
        }else if(oldSymbol.equals("GLD7")){
            return "GLD";
        }

        return oldSymbol.trim();
    }

    public static String unCleanSymbols(String oldSymbol){
        if(oldSymbol.equals("BRK-B")){
            return "BRKB";
        }else if(oldSymbol.equals("%5ENDX")){
            return "NDX";
        }else if(oldSymbol.equals("%5ERUT")){
            return "RUT";
        }else if(oldSymbol.equals("%5EOEX")){
            return "OEX";
        }

        return oldSymbol;
    }

    public static String ironCondorDataLoadCleaner(String symbol){
        return symbol.trim();
    }
}
