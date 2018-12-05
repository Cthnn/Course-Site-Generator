package csg.workspace.foolproof;

import djf.modules.AppGUIModule;
import djf.ui.foolproof.FoolproofDesign;
import csg.CourseSiteGeneratorApp;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorFoolproofDesign implements FoolproofDesign {
    CourseSiteGeneratorApp app;
    
    public CourseSiteGeneratorFoolproofDesign(CourseSiteGeneratorApp initApp) {
        app = initApp;
    }

    @Override
    public void updateControls() {
        AppGUIModule gui = app.getGUIModule();
        
    }
}