/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oh.transactions;

import javafx.scene.control.TableView;
import jtps.jTPS_Transaction;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.workspace.controllers.OfficeHoursController;

/**
 *
 * @author Ethan
 */
public class EditTA_Transaction implements jTPS_Transaction{
    OfficeHoursController controller;
    private String name;
    private String email;
    private int index;
    private String type;
    public EditTA_Transaction(OfficeHoursController initController, String initName, String initEmail, String initType, int initIndex) {
        controller = initController;
        name = initName;
        email = initEmail;
        type = initType;
        index = initIndex;
    }

    @Override
    public void doTransaction() {
        controller.editTA(name,email,type,index);        
    }

    @Override
    public void undoTransaction() {
        controller.editTA(name,email,type,index);
    }
    
}
