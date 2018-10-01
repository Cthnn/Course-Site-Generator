package oh.clipboard;

import djf.components.AppClipboardComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import javafx.scene.control.Button;
import oh.OfficeHoursApp;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursClipboard implements AppClipboardComponent {
    OfficeHoursApp app;
    ArrayList<TeachingAssistantPrototype> clipboardCutItems;
    ArrayList<TeachingAssistantPrototype> clipboardCopiedItems;
    
    public OfficeHoursClipboard(OfficeHoursApp initApp) {
        app = initApp;
        clipboardCutItems = null;
        clipboardCopiedItems = null;
    }
    
    @Override
    public void cut() {
    }

    @Override
    public void copy() {
    }
    
    @Override
    public void paste() {

    }    

    @Override
    public boolean hasSomethingToCut() {
        return ((OfficeHoursData)app.getDataComponent()).isTASelected();
    }

    @Override
    public boolean hasSomethingToCopy() {
        return ((OfficeHoursData)app.getDataComponent()).isTASelected();
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