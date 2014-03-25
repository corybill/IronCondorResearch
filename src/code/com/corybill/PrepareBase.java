package code.com.corybill;

import code.com.corybill.control.dataLoad.CBOEDataLoad;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Cory
 * Date: 1/26/13
 * Time: 1:12 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PrepareBase<T extends PrepareBase> implements Runnable {
    T base;

    private static CBOEDataLoad cboe;
    public List<String> list;

    public abstract PrepareBase<T> getInstance();
}
