package code.com.corybill.control.dataLoad;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/22/12
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IDataLoad {
    public void invoke();
    public void getData(String symbol, double... doubleVals);
}
