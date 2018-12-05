
package csg.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Teaching Assistant for the table of TAs.
 * 
 * @author Richard McKenna
 */
public class Lab_Recitation {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty section;
    private final StringProperty days;
    private final StringProperty ta1;
    private final StringProperty ta2;
    private final StringProperty room;
    

    /**
     * Constructor initializes both the TA name and email.
     */
    public Lab_Recitation(String initSection, String initDays, String initTA1, String initTA2, String initRoom) {
        section = new SimpleStringProperty(initSection);
        days = new SimpleStringProperty(initDays);
        ta1 = new SimpleStringProperty(initTA1);
        ta2 = new SimpleStringProperty(initTA2);
        room = new SimpleStringProperty(initRoom);
    }
    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public void setSection(String initSection) {
        section.set(initSection);
    }
    public void setTA1(String initTA1) {
        ta1.set(initTA1);
    }
    public void setTA2(String initTA2) {
        ta2.set(initTA2);
    }
    public void setRoom(String initRoom) {
        room.set(initRoom);
    }
    public void setDays(String initDays) {
        days.set(initDays);
    }
    public String getSection() {
        return section.get();
    }
    public String getTA1() {
        return ta1.get();
    }
    public String getTA2() {
        return ta2.get();
    }
    public String getDays() {
        return days.get();
    }
    public String getRoom() {
        return room.get();
    }
    public StringProperty sectionProperty() {
        return section;
    } 
    public StringProperty daysProperty() {
        return days;
    } 
    public StringProperty ta1Property() {
        return ta1;
    }
     public StringProperty ta2Property() {
        return ta2;
    }
    public StringProperty roomProperty() {
        return room;
    }
    @Override
    public String toString() {
        return section.getValue();
    }
    @Override
    public Lab_Recitation clone(){
        Lab_Recitation ta = new Lab_Recitation(section.getValue(),days.getValue(),ta1.getValue(),ta2.getValue(),room.getValue());
        return ta;
    }
}