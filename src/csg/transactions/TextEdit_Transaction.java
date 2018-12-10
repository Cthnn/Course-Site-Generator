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
import csg.data.TeachingAssistantPrototype;
import csg.workspace.CourseSiteGeneratorWorkspace;
import csg.workspace.controllers.CourseSiteGeneratorController;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Ethan
 */
public class TextEdit_Transaction implements jTPS_Transaction{
    private Node node;
    private String oldText;
    private String newText;
    private String type;
    private int editString;
    CourseSiteGeneratorWorkspace wrkspace;
    public TextEdit_Transaction(Node initNode, String initOldText, String initNewText,String initType,int initEditString,CourseSiteGeneratorWorkspace workspace) {
       node = initNode;
       oldText = initOldText;
       newText = initNewText;
       type = initType;
       editString = initEditString;
       wrkspace = workspace;
    }
    @Override
    public void doTransaction() {
        if (type.equalsIgnoreCase("TF")) {
            ((TextField)node).setText(newText);
        }else if(type.equalsIgnoreCase("TextArea")){
            ((TextArea)node).setText(newText);
        }  
        wrkspace.textEdit(editString, newText);
    }

    @Override
    public void undoTransaction() {
        if (type.equalsIgnoreCase("TF")) {
        ((TextField)node).setText(oldText);
        }else if(type.equalsIgnoreCase("TextArea")){
            ((TextArea)node).setText(oldText);
        }
        wrkspace.textEdit(editString, oldText);
    }
}
