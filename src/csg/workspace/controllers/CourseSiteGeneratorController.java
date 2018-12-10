package csg.workspace.controllers;

import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import csg.transactions.AddTA_Transaction;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorController {

    CourseSiteGeneratorApp app;
    private String type = "";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public CourseSiteGeneratorController(CourseSiteGeneratorApp initApp) {
        app = initApp;
    }
    public void processAddTA(){
        StackPane root = new StackPane();
        Stage duplicateWindow = new Stage();
        AppGUIModule gui = app.getGUIModule();
        TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableView ohTableView = (TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        for (int i = 0; i < taTableView.getColumns().size(); i++) {
            ((TableColumn)taTableView.getColumns().get(i)).setSortable(false);
        }
        boolean dupliName = false;
        boolean dupliEmail = false;
        TextField nameTF = (TextField) gui.getGUINode(CSG_NAME_TEXT_FIELD);
        String name = nameTF.getText();
        TextField emailTF = (TextField) gui.getGUINode(CSG_EMAIL_TEXT_FIELD);
        String email = emailTF.getText();
        CourseSiteGeneratorData data = (CourseSiteGeneratorData)app.getDataComponent();
        TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
        TableColumn emailCol = (TableColumn)taTableView.getColumns().get(1);
        if(CourseSiteGeneratorController.validate(email)){
            for(int i = 0; i < data.getAll().size();i++){
                if(name.equals(data.getAll().get(i).getName())){
                    dupliName = true;
                }
            }
            if(!dupliName){
                for(int i = 0; i < data.getAll().size();i++){
                if(email.equals(data.getAll().get(i).getEmail())){
                    dupliEmail = true;
                }
            }
                if(!dupliEmail){
                    TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email,type);
                    AddTA_Transaction addTATransaction = new AddTA_Transaction((CourseSiteGeneratorData)app.getDataComponent(),ta);
                    app.processTransaction(addTATransaction);
                    
                }else{
                    Text emailDialog = new Text("Duplicate Email!");
                    emailDialog.setFont(Font.font ("Verdana", 20));
                    emailDialog.setFill(Color.RED);
                    root.getChildren().addAll(emailDialog);
                    StackPane.setAlignment(emailDialog,Pos.CENTER);
                    Scene scene = new Scene(root, 190, 100);
                    duplicateWindow.setScene(scene);
                    duplicateWindow.show();
                }
            }else{
                Text nameDialog = new Text("Duplicate Name!");
                nameDialog.setFont(Font.font ("Verdana", 20));
                nameDialog.setFill(Color.RED);
                root.getChildren().addAll(nameDialog);
                StackPane.setAlignment(nameDialog,Pos.CENTER);
                Scene scene = new Scene(root, 190, 100);
                duplicateWindow.setScene(scene);
                duplicateWindow.show();
            }
        }else{
            Text emailDialog = new Text("Invalid Email!");
            emailDialog.setFont(Font.font ("Verdana", 20));
            emailDialog.setFill(Color.RED);
            root.getChildren().addAll(emailDialog);
            StackPane.setAlignment(emailDialog,Pos.CENTER);
            Scene scene = new Scene(root, 170, 100);
            duplicateWindow.setScene(scene);
            duplicateWindow.show();
        }
//         NOW CLEAR THE TEXT FIELDS
        nameTF.setText("");
        nameTF.requestFocus();
        emailTF.setText("");
        emailTF.requestFocus();
    }
     public void editTA(String name, String email,String type, int index){
        AppGUIModule gui = app.getGUIModule();
        CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
        String currentName = data.getAll().get(index).getName();
        data.getAll().get(index).setName(name);
        data.getAll().get(index).setEmail(email);
        data.getAll().get(index).setType(type);
        ObservableList<TeachingAssistantPrototype> all = data.getAll();
        ObservableList<TeachingAssistantPrototype> undergrad = FXCollections.observableArrayList();
        ObservableList<TeachingAssistantPrototype> grad = FXCollections.observableArrayList();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getType().equalsIgnoreCase("undergraduate")) {
                undergrad.add(all.get(i));
            }else if(all.get(i).getType().equalsIgnoreCase("graduate")){
                grad.add(all.get(i));
            }
        }
        data.setGraduate(grad);
        data.setUndergraduate(undergrad);
        //Change all OH
        ObservableList<TimeSlot> ohData = data.getOH();
        for (int i = 0; i < ohData.size(); i++) {
            HashMap<TimeSlot.DayOfWeek, ArrayList<TeachingAssistantPrototype>> tas = ohData.get(i).getTAs();
            HashMap<TimeSlot.DayOfWeek, StringProperty> dayText = ohData.get(i).getDayText();
            String taNames = "";
            for (int j = 0; j < tas.get(DayOfWeek.MONDAY).size(); j++) {
                if (tas.get(DayOfWeek.MONDAY).get(j).getName().equals(currentName)) {
                    tas.get(DayOfWeek.MONDAY).get(j).setName(name);
                }
                taNames = taNames + tas.get(DayOfWeek.MONDAY).get(j).getName() + "\n";
            }
            dayText.get(DayOfWeek.MONDAY).setValue(taNames);
            taNames = "";
            for (int j = 0; j < tas.get(DayOfWeek.TUESDAY).size(); j++) {
                if (tas.get(DayOfWeek.TUESDAY).get(j).getName().equals(currentName)) {
                    tas.get(DayOfWeek.TUESDAY).get(j).setName(name);
                }
                taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(j).getName() + "\n";
            }
            dayText.get(DayOfWeek.TUESDAY).setValue(taNames);
            taNames = "";
            for (int j = 0; j < tas.get(DayOfWeek.WEDNESDAY).size(); j++) {
                if (tas.get(DayOfWeek.WEDNESDAY).get(j).getName().equals(currentName)) {
                    tas.get(DayOfWeek.WEDNESDAY).get(j).setName(name);
                }
                taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(j).getName() + "\n";
            }
            dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
            taNames = "";
            for (int j = 0; j < tas.get(DayOfWeek.THURSDAY).size(); j++) {
                if (tas.get(DayOfWeek.THURSDAY).get(j).getName().equals(currentName)) {
                    tas.get(DayOfWeek.THURSDAY).get(j).setName(name);
                }
                taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(j).getName() + "\n";
            }
            dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
            taNames = "";
            for (int j = 0; j < tas.get(DayOfWeek.FRIDAY).size(); j++) {
                if (tas.get(DayOfWeek.FRIDAY).get(j).getName().equals(currentName)) {
                    tas.get(DayOfWeek.FRIDAY).get(j).setName(name);
                }
                taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(j).getName() + "\n";
            }
            dayText.get(DayOfWeek.FRIDAY).setValue(taNames);
        }
        
    }
    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
        public void setType(String type){
        this.type = type;
    }
    public void setCurrent(String type){
        AppGUIModule gui = app.getGUIModule();
        CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
        if (type.equalsIgnoreCase("all")) {
            data.setCurrentToAll();
        }else if(type.equalsIgnoreCase("undergraduate")){
            data.setCurrentToUG();
        }else if(type.equalsIgnoreCase("graduate")){
            data.setCurrentToG();
        }else{
            
        }
    }
    public String getCurrent(){
        return type;
    } 
}
