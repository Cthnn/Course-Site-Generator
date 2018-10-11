package oh.transactions;

import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_ALL;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_G;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_UG;
import static oh.OfficeHoursPropertyType.OH_TAS_TABLE_VIEW;
import oh.clipboard.OfficeHoursClipboard;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;

/**
 *
 * @author McKillaGorilla
 */
public class CutTA_OHTransaction implements jTPS_Transaction {
    OfficeHoursApp app;
    private TeachingAssistantPrototype ta;
    private OfficeHoursClipboard clipboard;
    private boolean cut = false;
    int row;
    private ObservableList<TimeSlot> ohClone;
    public CutTA_OHTransaction(TeachingAssistantPrototype ta, OfficeHoursApp app,int row,OfficeHoursClipboard clipboard,ObservableList<TimeSlot> ohClone){
        this.ta = ta;
        this.app = app;
        this.row = row;
        this.clipboard = clipboard;
        this.ohClone = FXCollections.observableArrayList(ohClone);
    }

    @Override
    public void doTransaction() {
        if(cut){
            ArrayList<TeachingAssistantPrototype> clipboardCutItems = clipboard.getClipboardCutItems();
            clipboardCutItems.remove(clipboardCutItems.size()-1);
            ObservableList<TeachingAssistantPrototype> tas = ((OfficeHoursData)app.getDataComponent()).getAll();
            tas.add(ta);
            ObservableList<TeachingAssistantPrototype> undergrad = FXCollections.observableArrayList();
            ObservableList<TeachingAssistantPrototype> grad = FXCollections.observableArrayList();
            for (int i = 0; i < tas.size(); i++) {
                if (tas.get(i).getType().equalsIgnoreCase("Undergraduate")) {
                    undergrad.add(tas.get(i));
                }else if(tas.get(i).getType().equalsIgnoreCase("Graduate")){
                    grad.add(tas.get(i));
                }
            }
            ((OfficeHoursData)app.getDataComponent()).setAll(tas);
            ((OfficeHoursData)app.getDataComponent()).setUndergraduate(undergrad);
            ((OfficeHoursData)app.getDataComponent()).setGraduate(grad);
            ObservableList<TimeSlot> ohClone = this.ohClone;
            OfficeHoursData data = ((OfficeHoursData)app.getDataComponent());   
            AppGUIModule gui = app.getGUIModule();
            RadioButton all = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
            RadioButton ug = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
            RadioButton g = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
            String type = "";
            if (all.isSelected()) {
                type = "all";
            }else if(ug.isSelected()){
                type = "undergraduate";
            }else if(g.isSelected()){
                type = "graduate";
            }else{
                type = "all";
            } 
            data.setOH(ohClone,type);
            cut = false;
            for (int i = clipboard.getRecentAction().size()-1; i >= 0; i--) {
                if (clipboard.getRecentAction().get(i).equalsIgnoreCase("CUT")) {
                    clipboard.getRecentAction().remove(i);
                    break;
                }
            }
        }else{
            OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
            data.removeTA(ta);
            ArrayList<TeachingAssistantPrototype> clipboardCutItems = clipboard.getClipboardCutItems();
            clipboardCutItems.add(ta);
            cut = true;
            clipboard.getRecentAction().add("CUT");
        }
        
    }

    @Override
    public void undoTransaction() {
        if(cut){
            ArrayList<TeachingAssistantPrototype> clipboardCutItems = clipboard.getClipboardCutItems();
            clipboardCutItems.remove(clipboardCutItems.size()-1);
            ObservableList<TeachingAssistantPrototype> tas = ((OfficeHoursData)app.getDataComponent()).getAll();
            tas.add(row,ta);
            ObservableList<TeachingAssistantPrototype> undergrad = FXCollections.observableArrayList();
            ObservableList<TeachingAssistantPrototype> grad = FXCollections.observableArrayList();
            for (int i = 0; i < tas.size(); i++) {
                if (tas.get(i).getType().equalsIgnoreCase("Undergraduate")) {
                    undergrad.add(tas.get(i));
                }else if(tas.get(i).getType().equalsIgnoreCase("Graduate")){
                    grad.add(tas.get(i));
                }
            }
            ((OfficeHoursData)app.getDataComponent()).setAll(tas);
            ((OfficeHoursData)app.getDataComponent()).setUndergraduate(undergrad);
            ((OfficeHoursData)app.getDataComponent()).setGraduate(grad);
            ObservableList<TimeSlot> ohClone = this.ohClone;
            OfficeHoursData data = ((OfficeHoursData)app.getDataComponent());   
            AppGUIModule gui = app.getGUIModule();
            RadioButton all = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
            RadioButton ug = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
            RadioButton g = (RadioButton)gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
            String type = "";
            if (all.isSelected()) {
                type = "all";
                data.setCurrentToAll();
            }else if(ug.isSelected()){
                type = "undergraduate";
                data.setCurrentToAll();
                data.setCurrentToUG();
            }else if(g.isSelected()){
                type = "graduate";
                data.setCurrentToAll();
                data.setCurrentToG();
            }else{
                type = "all";
                data.setCurrentToAll();
            } 
            data.setOH(ohClone,type);
            cut = false;
            for (int i = clipboard.getRecentAction().size()-1; i >= 0; i--) {
                if (clipboard.getRecentAction().get(i).equalsIgnoreCase("CUT")) {
                    clipboard.getRecentAction().remove(i);
                    break;
                }
            }
        }else{
            OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
            data.removeTA(ta);
            ArrayList<TeachingAssistantPrototype> clipboardCutItems = clipboard.getClipboardCutItems();
            clipboardCutItems.add(ta);
            cut = true;
            clipboard.getRecentAction().add("CUT");
        }
        
    }
}
