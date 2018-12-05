package csg.transactions;

import jtps.jTPS_Transaction;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lecture;
import csg.data.TeachingAssistantPrototype;

/**
 *
 * @author McKillaGorilla
 */
public class AddLecture_Transaction implements jTPS_Transaction {
    CourseSiteGeneratorData data;
    Lecture lect;
    Boolean isAdd;
    public AddLecture_Transaction(CourseSiteGeneratorData initData, Lecture initLect, Boolean initIsAdd) {
        data = initData;
        lect = initLect;
        isAdd = initIsAdd;
    }

    @Override
    public void doTransaction() {
        if (isAdd) {
            data.addLecture(lect);     
        }else{
            data.removeLecture(lect);
        }
              
    }

    @Override
    public void undoTransaction() {
        if (isAdd) {
            data.addLecture(lect);     
        }else{
            data.removeLecture(lect);
        }
    }
}
