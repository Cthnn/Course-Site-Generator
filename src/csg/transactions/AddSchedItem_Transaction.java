package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.SchedItem;

/**
 *
 * @author McKillaGorilla
 */
public class AddSchedItem_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    SchedItem item;
    Boolean isAdd;
    public AddSchedItem_Transaction(CourseSiteGeneratorData initData, SchedItem initItem,Boolean initIsAdd) {
        data = initData;
        item = initItem;
        isAdd = initIsAdd;
    }

    @Override
    public void doTransaction() {
        if (isAdd) {
            data.addSchedItem(item);
        }else{
            data.removeSchedItem(item);
        }       
    }

    @Override
    public void undoTransaction() {
        if (!isAdd) {
            data.addSchedItem(item);
        }else{
            data.removeSchedItem(item);
        }
    }
}
