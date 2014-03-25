package code.com.corybill.control.dataStructures;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/31/13
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MutableDouble {
    private double value;

    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }

    public MutableDouble(double d){
        this.value = d;
    }
}
