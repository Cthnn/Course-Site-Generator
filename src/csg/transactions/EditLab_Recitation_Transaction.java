package csg.transactions;

import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.CSG_SCHED_TABLE_VIEW;
import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab_Recitation;
import csg.data.Lecture;
import csg.data.SchedItem;
import djf.modules.AppGUIModule;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author McKillaGorilla
 */
public class EditLab_Recitation_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Lab_Recitation newItem;
    String oldText;
    int col;
    String editText;
    CourseSiteGeneratorApp app;
    TableView labRec;
    public EditLab_Recitation_Transaction(Lab_Recitation newItem, String oldText,int column, String editText,TableView tableView) {
        this.newItem = newItem;
        this.oldText = oldText;
        col = column;
        this.editText = editText;
        labRec = tableView;
    }

    @Override
    public void doTransaction() {
        if (col == 0) {
            newItem.setSection(editText);
        }else if(col == 1){
            newItem.setDays(editText);
        }else if(col == 2){
            newItem.setRoom(editText);
        }else if(col == 3){
            newItem.setTA1(editText);
        }
        else if(col == 4){
            newItem.setTA2(editText);
        }
        labRec.refresh();
    }

    @Override
    public void undoTransaction() {
        if (col == 0) {
            newItem.setSection(oldText);
        }else if(col == 1){
            newItem.setDays(oldText);
        }else if(col == 2){
            newItem.setRoom(oldText);
        }else if(col == 3){
            newItem.setTA1(oldText);
        }
        else if(col == 4){
            newItem.setTA2(oldText);
        }
        labRec.refresh();
    }
}
