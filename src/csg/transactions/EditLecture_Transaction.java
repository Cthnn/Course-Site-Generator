package csg.transactions;

import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.CSG_SCHED_TABLE_VIEW;
import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lecture;
import csg.data.SchedItem;
import djf.modules.AppGUIModule;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author McKillaGorilla
 */
public class EditLecture_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Lecture newItem;
    String oldText;
    int col;
    String editText;
    CourseSiteGeneratorApp app;
    TableView lect;
    public EditLecture_Transaction(Lecture newItem, String oldText,int column, String editText,TableView lectTV) {
        this.newItem = newItem;
        this.oldText = oldText;
        col = column;
        this.editText = editText;
        lect = lectTV;
    }

    @Override
    public void doTransaction() {
        if (col == 0) {
            newItem.setSection(editText);
        }else if(col == 1){
            newItem.setDays(editText);
        }else if(col == 2){
            newItem.setTime(editText);
        }else if(col == 3){
            newItem.setRoom(editText);
        }
        lect.refresh();
    }

    @Override
    public void undoTransaction() {
        if (col == 0) {
            newItem.setSection(oldText);
        }else if(col == 1){
            newItem.setDays(oldText);
        }else if(col == 2){
            newItem.setTime(oldText);
        }else if(col == 3){
            newItem.setRoom(oldText);
        }
        lect.refresh();
    }
}
