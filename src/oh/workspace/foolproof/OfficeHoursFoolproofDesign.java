package oh.workspace.foolproof;

import djf.modules.AppGUIModule;
import djf.ui.foolproof.FoolproofDesign;
import oh.OfficeHoursApp;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursFoolproofDesign implements FoolproofDesign {
    OfficeHoursApp app;
    
    public OfficeHoursFoolproofDesign(OfficeHoursApp initApp) {
        app = initApp;
    }

    @Override
    public void updateControls() {
        AppGUIModule gui = app.getGUIModule();
        
    }
}