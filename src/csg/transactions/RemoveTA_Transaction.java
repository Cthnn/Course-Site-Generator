package csg.transactions;

import djf.modules.AppGUIModule;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.RadioButton;
import jtps.jTPS_Transaction;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.CSG_TAS_RADIO_BUTTON_ALL;
import static csg.CourseSiteGeneratorPropertyType.CSG_TAS_RADIO_BUTTON_G;
import static csg.CourseSiteGeneratorPropertyType.CSG_TAS_RADIO_BUTTON_UG;
import csg.clipboard.CourseSiteGeneratorClipboard;
import csg.data.CourseSiteGeneratorData;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;

/**
 *
 * @author McKillaGorilla
 */
public class RemoveTA_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorApp app;
    private TeachingAssistantPrototype ta;
    private boolean cut = false;
    int row;
    private ObservableList<TimeSlot> ohClone;
    public RemoveTA_Transaction(TeachingAssistantPrototype ta, CourseSiteGeneratorApp app,int row,ObservableList<TimeSlot> ohClone){
        this.ta = ta;
        this.app = app;
        this.row = row;
        this.ohClone = FXCollections.observableArrayList(ohClone);
    }

    @Override
    public void doTransaction() {
        if(cut){
            ObservableList<TeachingAssistantPrototype> tas = ((CourseSiteGeneratorData)app.getDataComponent()).getAll();
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
            ((CourseSiteGeneratorData)app.getDataComponent()).setAll(tas);
            ((CourseSiteGeneratorData)app.getDataComponent()).setUndergraduate(undergrad);
            ((CourseSiteGeneratorData)app.getDataComponent()).setGraduate(grad);
            ObservableList<TimeSlot> ohClone = this.ohClone;
            CourseSiteGeneratorData data = ((CourseSiteGeneratorData)app.getDataComponent());   
            AppGUIModule gui = app.getGUIModule();
            RadioButton all = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
            RadioButton ug = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
            RadioButton g = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
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
        }else{
            CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
            data.removeTA(ta);
            cut = true;
        }
    }

    @Override
    public void undoTransaction() {
        if(cut){
            ObservableList<TeachingAssistantPrototype> tas = ((CourseSiteGeneratorData)app.getDataComponent()).getAll();
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
            ((CourseSiteGeneratorData)app.getDataComponent()).setAll(tas);
            ((CourseSiteGeneratorData)app.getDataComponent()).setUndergraduate(undergrad);
            ((CourseSiteGeneratorData)app.getDataComponent()).setGraduate(grad);
            ObservableList<TimeSlot> ohClone = this.ohClone;
            CourseSiteGeneratorData data = ((CourseSiteGeneratorData)app.getDataComponent());   
            AppGUIModule gui = app.getGUIModule();
            RadioButton all = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
            RadioButton ug = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
            RadioButton g = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
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
        }else{
            CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
            data.removeTA(ta);
            cut = true;
        }
    }
}
