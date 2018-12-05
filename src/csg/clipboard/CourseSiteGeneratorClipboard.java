package csg.clipboard;

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
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorClipboard implements AppClipboardComponent {
    CourseSiteGeneratorApp app;
    ArrayList<Object> clipboardCutItems;
    ArrayList<Object> clipboardCopiedItems;
    ArrayList <String> recentAction; 
    public CourseSiteGeneratorClipboard(CourseSiteGeneratorApp initApp) {
        app = initApp;
        clipboardCutItems = new ArrayList();
        clipboardCopiedItems = new ArrayList();
        recentAction = new ArrayList();
    }
    
    @Override
    public void cut() {
    }
    public ArrayList<Object> getClipboardCutItems(){
        return clipboardCutItems;
    }
    public ArrayList<String> getRecentAction(){
        return recentAction;
    }

    @Override
    public void copy() {
    }
    
    @Override
    public void paste() {
    }    

    @Override
    public boolean hasSomethingToCut() {
        return true;
    }

    @Override
    public boolean hasSomethingToCopy() {
        return true;
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