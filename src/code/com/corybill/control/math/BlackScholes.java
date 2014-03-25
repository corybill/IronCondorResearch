package code.com.corybill.control.math;

import code.com.corybill.helper.TradingConstants;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 12/30/12
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlackScholes {
    private static final double riskFreeRateOfReturn = .08;

    public static double getCallPrice(MathUtil mathUtil, double stockPrice, double strikePrice, double volatility,double time,String side) {
        if(time <= 0 && stockPrice > strikePrice){
            return stockPrice - strikePrice;
        }else if(time <=0 && stockPrice <= strikePrice){
            return 0;
        }else if(stockPrice <= 0 || strikePrice <= 0 || volatility <= 0){
            return 0.0;
        }

        double dSubOne = dSubOne(stockPrice,strikePrice,time,volatility);
        double dSubTwo = dSubTwo(dSubOne,time,volatility);
        double dSubOnePhi = Gaussian.Phi(dSubOne);
        double dSubTwoPhi = Gaussian.Phi(dSubTwo);
        double price = (stockPrice * dSubOnePhi - strikePrice * Math.exp(riskFreeRateOfReturn*time) * dSubTwoPhi);

        if(side.equals(TradingConstants.SHORT)){
            price = (price - .01) >= 0 ? (price - .01) : 0;
        }else if(side.equals(TradingConstants.LONG)){
            price += .01;
        }

        return mathUtil.round(price, TradingConstants.OPTION_PRICING_ROUNDING_PRECISION);
    }

    public static double getPutPrice(MathUtil mathUtil, double stockPrice, double strikePrice, double volatility,double time, String side) {
        if(time == 0 && stockPrice < strikePrice){
            return strikePrice - stockPrice;
        }else if(time <=0 && stockPrice >= strikePrice){
            return 0;
        }else if(stockPrice <= 0 || strikePrice <= 0 || volatility <= 0){
            return 0.0;
        }

        double dSubOne = dSubOne(stockPrice,strikePrice,time,volatility);
        double dSubTwo = dSubTwo(dSubOne,time,volatility);
        double dSubOnePhi = Gaussian.Phi(-dSubOne);
        double dSubTwoPhi = Gaussian.Phi(-dSubTwo);
        double price = (strikePrice * Math.exp(riskFreeRateOfReturn*time) * dSubTwoPhi - stockPrice * dSubOnePhi);

        if(side.equals(TradingConstants.SHORT)){
            price = (price - .01) >= 0 ? (price - .01) : 0;
        }else if(side.equals(TradingConstants.LONG)){
            price += .01;
        }

        return mathUtil.round(price, TradingConstants.OPTION_PRICING_ROUNDING_PRECISION);
    }

    private static double dSubOne(double stockPrice, double strikePrice, double time, double volatility) {
        return (Math.log(stockPrice/strikePrice) + (riskFreeRateOfReturn + volatility * volatility/2) * time) / (volatility * Math.sqrt(time));
    }

    private static double dSubTwo(double dSubOne, double time, double volatility) {
        return dSubOne - volatility * Math.sqrt(time);
    }
}
