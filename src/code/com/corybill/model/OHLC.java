package code.com.corybill.model;

import com.google.code.morphia.annotations.Embedded;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/21/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Embedded
public class OHLC {
    private double open;
    private double high;
    private double low;
    private double close;

    //Needed for options OHLC. We need to know how much time value should be associated each price point since they
    //could be different if this is a Weekly OHLC or greater.
    private String dateOfHighValue;
    private String dateOfLowValue;

    public OHLC(){   }

    public double getOpen() {
        return open;
    }
    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }
    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }
    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }
    public void setClose(double close) {
        this.close = close;
    }

    public String getDateOfLowValue() {
        return dateOfLowValue;
    }
    public void setDateOfLowValue(String dateOfLowValue) {
        this.dateOfLowValue = dateOfLowValue;
    }

    public String getDateOfHighValue() {
        return dateOfHighValue;
    }
    public void setDateOfHighValue(String dateOfHighValue) {
        this.dateOfHighValue = dateOfHighValue;
    }
}
