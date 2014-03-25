package code.com.corybill.model;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 2/9/13
 * Time: 12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class IronCondorRank {
    //Ranked from high to low.
    private int volatilityRank;

    //Ranked from high to low assuming no max loss
    private int profitLossRank;

    //Ranked from high to low assuming max loss of IRONCONDOR.MAX_LOSS;
    private int profitLossMaxLossRank;

    //Ranked from high to low. SD rank will be as a percentage of mondays opening stock price (e.g. sd / open).
    private int standardDeviationRank;

    public int getVolatilityRank() {
        return volatilityRank;
    }
    public void setVolatilityRank(int volatilityRank) {
        this.volatilityRank = volatilityRank;
    }

    public int getStandardDeviationRank() {
        return standardDeviationRank;
    }
    public void setStandardDeviationRank(int standardDeviationRank) {
        this.standardDeviationRank = standardDeviationRank;
    }

    public int getProfitLossRank() {
        return profitLossRank;
    }
    public void setProfitLossRank(int profitLossRank) {
        this.profitLossRank = profitLossRank;
    }

    public int getProfitLossMaxLossRank() {
        return profitLossMaxLossRank;
    }
    public void setProfitLossMaxLossRank(int profitLossMaxLossRank) {
        this.profitLossMaxLossRank = profitLossMaxLossRank;
    }
}
