/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import djf.modules.AppGUIModule;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import jtps.jTPS_Transaction;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_ALL;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_G;
import static oh.OfficeHoursPropertyType.OH_TAS_RADIO_BUTTON_UG;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.workspace.controllers.OfficeHoursController;

/**
 *
 * @author Ethan
 */
public class EditTA_Transaction implements jTPS_Transaction{
    private OfficeHoursController controller;
    private String name;
    private String email;
    private int index;
    private String type;
    private TeachingAssistantPrototype ta;
    boolean changed = false;
    OfficeHoursApp app;
    public EditTA_Transaction(OfficeHoursController initController, String initName, String initEmail, String initType, int initIndex, TeachingAssistantPrototype ta,OfficeHoursApp app) {
        controller = initController;
        name = initName;
        email = initEmail;
        type = initType;
        index = initIndex;
        this.ta = ta;
        this.app = app;
        
    }
    @Override
    public void doTransaction() {
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
        if(changed){
            controller.editTA(ta.getName(),ta.getEmail(),ta.getType(),index);
            controller.setCurrent(stuType);
            changed = false;
        }else{
            controller.editTA(name,email,type,index);
            controller.setCurrent(stuType);
            changed = true;
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
        if(changed){
            controller.editTA(ta.getName(),ta.getEmail(),ta.getType(),index);
            controller.setCurrent(stuType);
            changed = false;
        }else{
            controller.editTA(name,email,type,index);
            controller.setCurrent(stuType);
            changed = true;
        }
    }
    
}
