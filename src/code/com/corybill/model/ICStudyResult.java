package code.com.corybill.model;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/15/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ICStudyResult {
    private int plRank;
    private int creditRank;
    private int volRank;
    private int counter;

    private double plRankAverage;
    private double creditRankAverage;
    private double volRankAverage;

    public void setAverages(){
        plRankAverage = plRank / counter * 1.0;
        creditRankAverage = creditRank / counter * 1.0;
        volRankAverage = volRank / counter * 1.0;
    }

    public void addToPlRank(int rank){
        plRank += rank;
    }
    public void addToCreditRank(int rank){
        creditRank += rank;
    }
    public void addToVolRank(int rank){
        volRank += rank;
    }

    public int getCreditRank() {
        return creditRank;
    }
    public void setCreditRank(int creditRank) {
        this.creditRank = creditRank;
    }

    public int getPlRank() {
        return plRank;
    }
    public void setPlRank(int plRank) {
        this.plRank = plRank;
    }

    public int getVolRank() {
        return volRank;
    }
    public void setVolRank(int volRank) {
        this.volRank = volRank;
    }

    public double getCreditRankAverage() {
        return creditRankAverage;
    }
    public void setCreditRankAverage(double creditRankAverage) {
        this.creditRankAverage = creditRankAverage;
    }

    public double getPlRankAverage() {
        return plRankAverage;
    }
    public void setPlRankAverage(double plRankAverage) {
        this.plRankAverage = plRankAverage;
    }

    public double getVolRankAverage() {
        return volRankAverage;
    }
    public void setVolRankAverage(double volRankAverage) {
        this.volRankAverage = volRankAverage;
    }

    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
}
