package code.com.corybill.control.dataStructures;

import code.com.corybill.model.Quote;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/26/13
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyArrayList extends ArrayList<Quote> {
    //Summ of all elements in the queue.
    public double total=0.0;
    public double mean=0.0;

    public MyArrayList(){
        super();
    }

    public double getClosingMean(){
        double closingMean = 0.0;
        for(Quote quote : this){
            closingMean += quote.getOhlc().getClose();
        }
        return closingMean / this.size() * 1.0;
    }
}
