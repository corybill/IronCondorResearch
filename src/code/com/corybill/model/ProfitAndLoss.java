package code.com.corybill.model;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/22/13
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfitAndLoss {
    private double loss;
    private double profit;

    private int success;
    private int failure;

    private String key;

    public void incrementSuccess(){
        success++;
    }
    public void incrementFailure(){
        failure++;
    }

    public void addToProfit(double d){
        profit += d;
    }
    public void addToLoss(double d){
        loss += d;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public double getLoss() {
        return loss;
    }
    public void setLoss(double loss) {
        this.loss = loss;
    }

    public double getProfit() {
        return profit;
    }
    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getSuccess() {
        return success;
    }
    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }
    public void setFailure(int failure) {
        this.failure = failure;
    }
}
