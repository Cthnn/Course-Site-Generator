/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import djf.modules.AppGUIModule;
import javafx.scene.control.RadioButton;
import jtps.jTPS_Transaction;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.SchedItem;
import csg.data.TeachingAssistantPrototype;
import csg.workspace.CourseSiteGeneratorWorkspace;
import csg.workspace.controllers.CourseSiteGeneratorController;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Ethan
 */
public class ClearItem_Transaction implements jTPS_Transaction{
    private ComboBox type;
    private DatePicker picker;
    private TextField title;
    private TextField topic;
    private TextField link;
    private SchedItem oldItem;
    private ArrayList<String> typeTransac;
    private ArrayList<String> dateTransac;
    CourseSiteGeneratorWorkspace wrkspace;
    public ClearItem_Transaction(ComboBox type, DatePicker picker,TextField title,TextField topic,TextField link,SchedItem initOldItem,ArrayList<String> typeTransac,ArrayList<String> dateTransac,CourseSiteGeneratorWorkspace workspace) {
        this.type = type;
        this.picker = picker;
        this.title = title;
        this.topic = topic;
        this.link = link;
        this.oldItem = initOldItem;
        this.typeTransac = typeTransac;
        this.dateTransac = dateTransac;
        this.wrkspace = workspace;
    }
    @Override
    public void doTransaction() {
        if (!oldItem.getType().equals("HW")) {
            typeTransac.add("clear");
        }
        if(!oldItem.getDate().equals(LocalDate.now().toString())) {
            dateTransac.add("clear");
        }
            type.getSelectionModel().select("HW");
            picker.setValue(LocalDate.now());
            title.setText("");
            topic.setText("");
            link.setText("");
        wrkspace.cbEdit(4, (String)type.getSelectionModel().getSelectedItem());
        wrkspace.datePickEdit(2, picker.getValue().toString());
        wrkspace.textEdit(15, "");
        wrkspace.textEdit(16, "");
        wrkspace.textEdit(17, "");
    }

    @Override
    public void undoTransaction() {
        if (!oldItem.getType().equals("HW")) {
            typeTransac.add("clear");
        }
        if(!oldItem.getDate().equals(LocalDate.now().toString())) {
            dateTransac.add("clear");
        }
        type.getSelectionModel().select(oldItem.getType());
        LocalDate oldDate = LocalDate.parse(oldItem.getDate());
        picker.setValue(oldDate);
        title.setText(oldItem.getTitle());
        topic.setText(oldItem.getTopic());
        link.setText(oldItem.getLink());
        wrkspace.cbEdit(4, (String)type.getSelectionModel().getSelectedItem());
        wrkspace.datePickEdit(2, picker.getValue().toString());
        wrkspace.textEdit(15, title.getText());
        wrkspace.textEdit(16, topic.getText());
        wrkspace.textEdit(17, link.getText());
    }
}
