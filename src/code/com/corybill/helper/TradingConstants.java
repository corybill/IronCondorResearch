package code.com.corybill.helper;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/25/13
 * Time: 8:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradingConstants {
    public final static String CALL = "CALL";
    public final static String PUT = "PUT";
    public final static String SHORT = "SHORT";
    public final static String LONG = "LONG";

    public final static int NUM_VOL_CALC_DAYS = 260;

    public final static int IRON_CONDOR_NUM_THREADS = 1;
    public final static int IC_RANGES_RANKING_NUM_THREADS = 1;
    public final static int IC_RANKING_NUM_THREADS = 1;
    public final static int QUOTES_NUM_THREADS = 15;
    public final static int VANILLA_OPTIONS_NUM_THREADS = 5;
    public final static int QUOTE_VOL_NUM_THREADS = 15;
    public final static int CURRENT_IC_NUM_THREADS = 1;

    public final static int IC_STUDIES_NUM_THREADS = 1;

    public final static int OPTION_PRICING_ROUNDING_PRECISION = 4;
    public final static int IC_PRICING_ROUNDING_PRECISION = 4;
    public final static int STOCK_PRICING_ROUNDING_PRECISION = 2;
    public final static int ZERO_ROUNDING_PRECISION = 0;

    public final static int MAX_RISK = 500;
    //public final static String OPTIONS_HISTORY_DATE = "2007-12-29";
    public final static String OPTIONS_HISTORY_DATE = "1970-01-01";
    public final static String ORIGINAL_START_DATE = "1970-01-01";

    public static final int TRADING_DAYS_IN_YEAR = 260;
    public static final int TRADING_DAYS_IN_HALF_YEAR = 130;
    public static final int TRADING_DAYS_IN_MONTH= 21;
    public static final int TRADING_DAYS_IN_2_WEEKS= 10;
    public static final int TRADING_DAYS_IN_WEEK= 5;

    public static final double DAYS_IN_A_YEAR = 365;

    public static double[] deviations = {1.25,1.5,1.75,2.0};

    public static int[] condorDays = {TRADING_DAYS_IN_MONTH};

    public static int[] condorRangesDays = {TRADING_DAYS_IN_MONTH};

    public static int[] allDays = {TRADING_DAYS_IN_WEEK,
                                   TRADING_DAYS_IN_2_WEEKS,
                                   TRADING_DAYS_IN_MONTH,
                                   TRADING_DAYS_IN_HALF_YEAR,
                                   TRADING_DAYS_IN_YEAR};

    public static int[] maxLosses = {300,400};
    public static int[] maxRangesLosses = {300,400};
    public static int[] maxGains = {100,125,150,175};
    public static int[] doubledMaxGains = {200,250,300,350};
}
