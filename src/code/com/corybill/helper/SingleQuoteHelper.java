package code.com.corybill.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 3/2/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleQuoteHelper {
    private static Map<String,String> map = new HashMap<String,String>();

    public static synchronized void addToMap(String s){
        map.put(s,s);
    }
    public static Map<String,String> getMap(){
        return map;
    }
}
