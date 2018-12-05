package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.SchedItem;
import javafx.collections.ObservableList;

/**
 *
 * @author McKillaGorilla
 */
public class EditSchedItem_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    SchedItem currentItem;
    SchedItem newItem;
    SchedItem oldItem;
    public EditSchedItem_Transaction(CourseSiteGeneratorData initData, SchedItem initNewItem, SchedItem initOldItem) {
        data = initData;
        currentItem = initOldItem;
        newItem = initNewItem;
        oldItem = initOldItem.clone();
    }

    @Override
    public void doTransaction() {
        ObservableList<SchedItem> schedules = data.getSchedules();
        for (SchedItem s: schedules) {
            if (s == currentItem) {
                s.setDate(newItem.getDate());
                s.setTopic(newItem.getTopic());
                s.setLink(newItem.getLink());
                s.setType(newItem.getType());
                s.setTitle(newItem.getTitle());
                System.out.println(s.getDate());
            }
        }       
    }

    @Override
    public void undoTransaction() {
        ObservableList<SchedItem> schedules = data.getSchedules();
        for (SchedItem s: schedules) {
            if (s == currentItem) {
                s.setDate(oldItem.getDate());
                s.setTopic(oldItem.getTopic());
                s.setLink(oldItem.getLink());
                s.setType(oldItem.getType());
                s.setTitle(oldItem.getTitle());
            }
        }
    }
}
