package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab_Recitation;
import csg.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class AddLab_Rec_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Lab_Recitation lab_rec;
    String type = "";
    Boolean isAdd;
    public AddLab_Rec_Transaction(CourseSiteGeneratorData initData, Lab_Recitation initLabRec, String initType, Boolean initIsAdd) {
        data = initData;
        lab_rec = initLabRec;
        type = initType;
        isAdd = initIsAdd;
    }

    @Override
    public void doTransaction() {
        if (isAdd) {
            if (type.equalsIgnoreCase("Lab")) {
                data.addLab(lab_rec);
            }else if (type.equalsIgnoreCase("Rec")) {
                data.addRec(lab_rec);
            }
        }else{
            if (type.equalsIgnoreCase("Lab")) {
                data.removeLab(lab_rec);
            }else if (type.equalsIgnoreCase("Rec")) {
                data.removeRec(lab_rec);
            } 
        }
              
    }

    @Override
    public void undoTransaction() {
        if (isAdd) {
            if (type.equalsIgnoreCase("Lab")) {
                data.addLab(lab_rec);
            }else if (type.equalsIgnoreCase("Rec")) {
                data.addRec(lab_rec);
            }
        }else{
            if (type.equalsIgnoreCase("Lab")) {
                data.removeLab(lab_rec);
            }else if (type.equalsIgnoreCase("Rec")) {
                data.removeRec(lab_rec);
            } 
        }
    }
}
