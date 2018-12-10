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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 *
 * @author Ethan
 */
public class Image_Transaction implements jTPS_Transaction{
    private HBox box;
    private String oldText;
    private String newText;
    String boxName;
    public Image_Transaction(HBox imageBox, String initOldText, String initNewText,String boxNme) {
       box = imageBox;
       oldText = initOldText;
       newText = initNewText;
       boxName = boxNme;
    }
    @Override
    public void doTransaction() {
        try{
            box.getChildren().remove(1);
            Image newImage = new Image(new FileInputStream(newText));
            ImageView iv = new ImageView();
            iv.setImage(newImage);
            box.getChildren().add(iv);
            if (boxName.equals("fav")) {
                CourseSiteGeneratorWorkspace.faviconFP = newText;
            }else if(boxName.equalsIgnoreCase("nav")){
                CourseSiteGeneratorWorkspace.navbarFP = newText;
            }else if(boxName.equalsIgnoreCase("lfoot")){
                CourseSiteGeneratorWorkspace.lfootFP = newText;
            }else if(boxName.equalsIgnoreCase("rfoot")){
                CourseSiteGeneratorWorkspace.rfootFP = newText;
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }
    }

    @Override
    public void undoTransaction() {
        try{
            box.getChildren().remove(1);
            Image newImage = new Image(new FileInputStream(oldText));
            ImageView iv = new ImageView();
            iv.setImage(newImage);
            box.getChildren().add(iv);
            if (boxName.equals("fav")) {
                CourseSiteGeneratorWorkspace.faviconFP = oldText;
            }else if(boxName.equalsIgnoreCase("nav")){
                CourseSiteGeneratorWorkspace.navbarFP = oldText;
            }else if(boxName.equalsIgnoreCase("lfoot")){
                CourseSiteGeneratorWorkspace.lfootFP = oldText;
            }else if(boxName.equalsIgnoreCase("rfoot")){
                CourseSiteGeneratorWorkspace.rfootFP = oldText;
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }
    }
}
