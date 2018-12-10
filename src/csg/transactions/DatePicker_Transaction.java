package csg.transactions;

import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.CSG_SCHED_TABLE_VIEW;
import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.SchedItem;
import csg.workspace.CourseSiteGeneratorWorkspace;
import djf.modules.AppGUIModule;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

/**
 *
 * @author McKillaGorilla
 */
public class DatePicker_Transaction implements jTPS_Transaction {
    DatePicker datePicker;
    String newItem;
    String oldItem;
    int oldMonth;
    int oldYear;
    int oldDay;
    int newMonth;
    int newYear;
    int newDay;
    int editNum;
    CourseSiteGeneratorWorkspace wrkspace;
    ArrayList<String> transac;
    boolean transacDone = false;
    public DatePicker_Transaction(DatePicker initDatePicker, String initNewItem, String initOldItem, int editNum, CourseSiteGeneratorWorkspace workspace,ArrayList<String> transaction) {
        datePicker = initDatePicker;
        newItem = initNewItem;
        oldItem = initOldItem;
        oldYear = Integer.parseInt(initOldItem.substring(0,4));
        oldMonth = Integer.parseInt(initOldItem.substring(5,7));
        oldDay = Integer.parseInt(initOldItem.substring(8,10));
        newYear = Integer.parseInt(initNewItem.substring(0,4));
        newMonth = Integer.parseInt(initNewItem.substring(5,7));
        newDay = Integer.parseInt(initNewItem.substring(8,10));
        wrkspace = workspace;
        this.editNum = editNum;
        transac = transaction;
    }

    @Override
    public void doTransaction() {
        if (transacDone) {
           redoTransaction(); 
        }else{
            datePicker.setValue(LocalDate.of(newYear,newMonth,newDay));
            wrkspace.datePickEdit(editNum,newItem);
            transacDone = true;
        }
    }

    @Override
    public void undoTransaction() {
        transac.add("undo");
        datePicker.setValue(LocalDate.of(oldYear,oldMonth,oldDay));
        wrkspace.datePickEdit(editNum,oldItem);
    }
    public void redoTransaction(){
        transac.add("redo");
        datePicker.setValue(LocalDate.of(newYear,newMonth,newDay));
        wrkspace.datePickEdit(editNum,newItem);
    }
}
