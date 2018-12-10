/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csg.transactions;

import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.files.CourseSiteGeneratorFiles;
import csg.workspace.CourseSiteGeneratorWorkspace;
import djf.modules.AppGUIModule;
import jtps.jTPS_Transaction;
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author Ethan
 */
public class CBEdit_Transaction implements jTPS_Transaction{
    private ComboBox cb;
    private String oldText;
    private String newText;
    private ArrayList<String> options;
    private ArrayList<String> transac;
    private int editNum;
    private CourseSiteGeneratorApp app;
    boolean transacDone = false;
    public CBEdit_Transaction(ComboBox initCB, String initOldText, String initNewText,ArrayList<String> initOptions,ArrayList<String> transactions,int initEditNum,CourseSiteGeneratorApp initApp) {
       cb = initCB;
       oldText = initOldText;
       newText = initNewText;
       options = initOptions;
       transac = transactions;
       editNum = initEditNum;
       app = initApp;
    }
    @Override
    public void doTransaction() {
        boolean exists = false;
            for (int i = 0; i < options.size(); i++) {
                if(newText.equals (options.get(i))){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                options.add(newText);
                for (int i = 0; i < options.size(); i++) {
                    boolean inList = false;
                    for (int j = 0; j < cb.getItems().size(); j++) {
                        if (cb.getItems().get(j).equals(options.get(i))) {
                            inList = true;
                        }
                    }
                    if(!inList){
                        cb.getItems().add(options.get(i));
                        
                    }
                }
                
            }
        if (transacDone) {
            redoTransaction();
        }else{
            cb.getSelectionModel().select(newText);  
            ((CourseSiteGeneratorWorkspace)app.getWorkspaceComponent()).cbEdit(editNum, newText);   
            AppGUIModule gui = app.getGUIModule();
            new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
            HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
            exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
            Label dirText = new Label(".\\export\\"+((ComboBox) gui.getGUINode(CSG_SUBJECT_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
            ((ComboBox) gui.getGUINode(CSG_NUMBER_CB)).getSelectionModel().getSelectedItem().toString()+"_"+((ComboBox) gui.getGUINode(CSG_SEM_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
            ((ComboBox) gui.getGUINode(CSG_YEAR_CB)).getSelectionModel().getSelectedItem().toString()+"\\public_html");
            exportDirBox.getChildren().add(dirText);
            transacDone = true;
        }
        
    }

    @Override
    public void undoTransaction() {
        transac.add("Undo");
        cb.getSelectionModel().select(oldText);
        ((CourseSiteGeneratorWorkspace)app.getWorkspaceComponent()).cbEdit(editNum, oldText);
        AppGUIModule gui = app.getGUIModule();
        new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
        HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
        exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
        Label dirText = new Label(".\\export\\"+((ComboBox) gui.getGUINode(CSG_SUBJECT_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
        ((ComboBox) gui.getGUINode(CSG_NUMBER_CB)).getSelectionModel().getSelectedItem().toString()+"_"+((ComboBox) gui.getGUINode(CSG_SEM_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
        ((ComboBox) gui.getGUINode(CSG_YEAR_CB)).getSelectionModel().getSelectedItem().toString()+"\\public_html");
        exportDirBox.getChildren().add(dirText);
    }
    public void redoTransaction() {
        transac.add("Redo");
        cb.getSelectionModel().select(newText);  
        ((CourseSiteGeneratorWorkspace)app.getWorkspaceComponent()).cbEdit(editNum, newText);   
        AppGUIModule gui = app.getGUIModule();
        new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
        HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
        exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
        Label dirText = new Label(".\\export\\"+((ComboBox) gui.getGUINode(CSG_SUBJECT_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
        ((ComboBox) gui.getGUINode(CSG_NUMBER_CB)).getSelectionModel().getSelectedItem().toString()+"_"+((ComboBox) gui.getGUINode(CSG_SEM_CB)).getSelectionModel().getSelectedItem().toString()+"_"+
        ((ComboBox) gui.getGUINode(CSG_YEAR_CB)).getSelectionModel().getSelectedItem().toString()+"\\public_html");
        exportDirBox.getChildren().add(dirText);
    }
}
