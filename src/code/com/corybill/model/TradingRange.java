package code.com.corybill.model;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/12/13
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingRange {
    private double high;
    private double low;

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

    public TradingRange(){
        high = 0;
        low = -1;
    }
}
