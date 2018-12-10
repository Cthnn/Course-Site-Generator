package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.TeachingAssistantPrototype;
import javafx.scene.control.CheckBox;

/**
 *
 * @author McKillaGorilla
 */
public class CheckBox_Transaction implements jTPS_Transaction {
    CheckBox box;
    Boolean isSelected;
    
    public CheckBox_Transaction(CheckBox initBox, Boolean initIsSelected) {
        box = initBox;
        isSelected = initIsSelected;
    }

    @Override
    public void doTransaction() {
        if (isSelected) {
            box.setSelected(true);
        }else{
            box.setSelected(false);
        }
        
    }

    @Override
    public void undoTransaction() {
        if (isSelected) {
            box.setSelected(false);
        }else{
            box.setSelected(true);
        }
    }
}
