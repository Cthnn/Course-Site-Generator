package oh.transactions;

import djf.modules.AppGUIModule;
import javafx.scene.control.RadioButton;
import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_ALL;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_G;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_UG;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.workspace.controllers.OfficeHoursController;

/**
 *
 * @author McKillaGorilla
 */
public class AddTA_OHTransaction implements jTPS_Transaction {
    private int col;
    TimeSlot times;
    TeachingAssistantPrototype ta;
    boolean added;
    OfficeHoursApp app; 
    public AddTA_OHTransaction(int col, TimeSlot times, TeachingAssistantPrototype ta, OfficeHoursApp app){
        this.col = col;
        this.times = times;
        this.ta = ta;
        this.app = app;
    }

    @Override
    public void doTransaction() {
        AppGUIModule gui = app.getGUIModule();
        OfficeHoursController controller = new OfficeHoursController((OfficeHoursApp) app);
        RadioButton all = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
        RadioButton ug = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
        RadioButton g = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
        String stuType ="";
        if (all.isSelected()) {
            stuType = "All";
        }else if(g.isSelected()){
            stuType = "Graduate";
        }else if(ug.isSelected()){
            stuType = "Undergraduate";
        }else{
            stuType = "All";
        }
        if(times.exists(ta,col)){
            times.removingTA(ta, col);
            controller.setCurrent(stuType);
            added = false;
        }else{
            times.addToTAs(ta, col);
            controller.setCurrent(stuType);
            added = true;
        }
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        for (int i = 0; i < data.getAll().size(); i++) {
            if (data.getAll().get(i).getName().equals(ta.getName())){
                if(added){
                    data.getAll().get(i).setTimeSlot(data.getAll().get(i).getTimeSlot() + 1);
                }else{
                    data.getAll().get(i).setTimeSlot(data.getAll().get(i).getTimeSlot() - 1);
                }
            }
        }
    }

    @Override
    public void undoTransaction() {
        AppGUIModule gui = app.getGUIModule();
        RadioButton all = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
        RadioButton ug = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
        RadioButton g = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
        String stuType ="";
        if (all.isSelected()) {
            stuType = "All";
        }else if(g.isSelected()){
            stuType = "Graduate";
        }else if(ug.isSelected()){
            stuType = "Undergraduate";
        }else{
            stuType = "All";
        }
        OfficeHoursController controller = new OfficeHoursController((OfficeHoursApp) app);
        if(added){
            times.removingTA(ta, col);
            controller.setCurrent(stuType);
            added = false;
            
        }else{
            times.addToTAs(ta, col);
            controller.setCurrent(stuType);
            added = true;
        }
        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
        for (int i = 0; i < data.getAll().size(); i++) {
            if (data.getAll().get(i).getName().equals(ta.getName())){
                if(added){
                    data.getAll().get(i).setTimeSlot(data.getAll().get(i).getTimeSlot() + 1);
                }else{
                    data.getAll().get(i).setTimeSlot(data.getAll().get(i).getTimeSlot() - 1);
                }
            }
        }
    }

}
