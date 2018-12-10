package csg.transactions;

import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.CSG_SCHED_TABLE_VIEW;
import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.SchedItem;
import djf.modules.AppGUIModule;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author McKillaGorilla
 */
public class EditSchedItem_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    SchedItem currentItem;
    SchedItem newItem;
    SchedItem oldItem;
    CourseSiteGeneratorApp app;
    public EditSchedItem_Transaction(CourseSiteGeneratorData initData, SchedItem initNewItem, SchedItem initOldItem, CourseSiteGeneratorApp initApp) {
        data = initData;
        currentItem = initOldItem;
        newItem = initNewItem;
        oldItem = initOldItem.clone();
        app = initApp;
    }

    @Override
    public void doTransaction() {
        AppGUIModule gui = app.getGUIModule();
        ObservableList<SchedItem> schedules = data.getSchedules();
        for (SchedItem s: schedules) {
            if (s == currentItem) {
                s.setDate(newItem.getDate());
                s.setTopic(newItem.getTopic());
                s.setLink(newItem.getLink());
                s.setType(newItem.getType());
                s.setTitle(newItem.getTitle());
            }
        }
        ((TableView)gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
    }

    @Override
    public void undoTransaction() {
        AppGUIModule gui = app.getGUIModule();
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
        ((TableView)gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
    }
}
