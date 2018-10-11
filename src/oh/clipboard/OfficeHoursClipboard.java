package oh.clipboard;

import djf.components.AppClipboardComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.*;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;
import oh.transactions.AddTA_Transaction;
import oh.transactions.CutTA_OHTransaction;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursClipboard implements AppClipboardComponent {
    OfficeHoursApp app;
    ArrayList<TeachingAssistantPrototype> clipboardCutItems;
    ArrayList<TeachingAssistantPrototype> clipboardCopiedItems;
    ArrayList <String> recentAction; 
    public OfficeHoursClipboard(OfficeHoursApp initApp) {
        app = initApp;
        clipboardCutItems = new ArrayList();
        clipboardCopiedItems = new ArrayList();
        recentAction = new ArrayList();
    }
    
    @Override
    public void cut() {

        AppGUIModule gui = app.getGUIModule();
        TableView tasTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
        RadioButton all = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
        RadioButton ug = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
        RadioButton g = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
        if (hasSomethingToCut()) {
            ObservableList taObservableList = tasTableView.getSelectionModel().getSelectedCells();
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            int row = tablePosition.getRow();
            ObservableList<TeachingAssistantPrototype> tas = FXCollections.observableArrayList();
            if (all.isSelected()) {
                tas = ((OfficeHoursData)app.getDataComponent()).getAll();
            }else if(ug.isSelected()){
                tas = ((OfficeHoursData)app.getDataComponent()).getUndergraduate();
            }else if(g.isSelected()){
                tas = ((OfficeHoursData)app.getDataComponent()).getGraduate();
            }else{
                tas = ((OfficeHoursData)app.getDataComponent()).getAll();
            }
            clipboardCutItems.add(tas.get(row).clone());
            ObservableList<TimeSlot> ohClone = ((OfficeHoursData)app.getDataComponent()).getOHClone();
            CutTA_OHTransaction cutTA = new CutTA_OHTransaction(tas.get(row),app,row,this,ohClone);
            app.processTransaction(cutTA);
        }
    }
    public ArrayList<TeachingAssistantPrototype> getClipboardCutItems(){
        return clipboardCutItems;
    }
    public ArrayList<String> getRecentAction(){
        return recentAction;
    }

    @Override
    public void copy() {
        AppGUIModule gui = app.getGUIModule();
        TableView tasTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
        if(hasSomethingToCopy()){
            ObservableList taObservableList = tasTableView.getSelectionModel().getSelectedCells();
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            int row = tablePosition.getRow();
            ObservableList<TeachingAssistantPrototype> tas = ((OfficeHoursData)app.getDataComponent()).getTAs();
            clipboardCopiedItems.add(tas.get(row).clone());
            recentAction.add("COPY");
        }
    }
    
    @Override
    public void paste() {
        TeachingAssistantPrototype ta = null;
        if (hasSomethingToPaste()) {
            if (recentAction.get(recentAction.size()-1).equalsIgnoreCase("COPY")) {
                ta = clipboardCopiedItems.get(clipboardCopiedItems.size()-1).clone();
                ta.setTimeSlot(0);
            }else if(recentAction.get(recentAction.size()-1).equalsIgnoreCase("CUT")){
                ta = clipboardCutItems.get(clipboardCutItems.size()-1).clone();
                ta.setTimeSlot(0);
                recentAction.remove(recentAction.get(recentAction.size()-1));
            }
            if (ta != null) {
                boolean exists = false;
                OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
                ObservableList<TeachingAssistantPrototype> all = data.getAll();
                for (int i = 0; i < all.size(); i++) {
                    if (ta.getName().equals(all.get(i).getName()) || ta.getEmail().equals(all.get(i).getEmail())) {
                        exists = true;
                    }
                }
                if (!exists) {
                    AddTA_Transaction addTA = new AddTA_Transaction(data,ta);
                    app.processTransaction(addTA);
                }else{
                    TeachingAssistantPrototype taClone = ta.clone();
                    int index = 0;
                    for (int i = 0; i < ta.getEmail().length(); i++) {
                        if (ta.getEmail().charAt(i) == '@') {
                            index = i;
                            break;
                        }
                    }
                    int counter = 1;
                    String taName = ta.getName();
                    String taEmailOne = ta.getEmail().substring(0,index);
                    String taEmailRest = ta.getEmail().substring(index,ta.getEmail().length());
                    String newEmail = taEmailOne + counter + taEmailRest;
                    taClone.setName(taName + counter);
                    taClone.setEmail(newEmail);
                    exists = false;
                    for (int i = 0; i < all.size(); i++) {
                        if (taClone.getName().equals(all.get(i).getName()) || taClone.getEmail().equals(all.get(i).getEmail())) {
                            exists = true;
                        }
                    }
                    counter++;
                    while(exists){
                        newEmail = taEmailOne+counter+taEmailRest;
                        taClone.setName(taName + counter);
                        taClone.setEmail(newEmail);
                        boolean existsIn = false;
                        for (int i = 0; i < all.size(); i++) {
                            if (taClone.getName().equals(all.get(i).getName()) || taClone.getEmail().equals(all.get(i).getEmail())) {
                                existsIn = true;
                            }
                        }
                        if (existsIn) {
                            counter++;
                        }else{
                            exists = false;
                        }

                    }
                    AddTA_Transaction addTA = new AddTA_Transaction(data,taClone);
                    app.processTransaction(addTA);      
                }
            }    
        }
        
        
    }    

    @Override
    public boolean hasSomethingToCut() {
        return ((OfficeHoursData)app.getDataComponent()).isTASelected();
    }

    @Override
    public boolean hasSomethingToCopy() {
        return ((OfficeHoursData)app.getDataComponent()).isTASelected();
    }

    @Override
    public boolean hasSomethingToPaste() {
        if ((clipboardCutItems != null) && (!clipboardCutItems.isEmpty()))
            return true;
        else if ((clipboardCopiedItems != null) && (!clipboardCopiedItems.isEmpty()))
            return true;
        else
            return false;
    }
}