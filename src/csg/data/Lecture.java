
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
public class Lecture {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty section;
    private final StringProperty days;
    private final StringProperty time;
    private final StringProperty room;

    /**
     * Constructor initializes both the TA name and email.
     */
    public Lecture(String initSection, String initDays, String initTime, String initRoom) {
        section = new SimpleStringProperty(initSection);
        days = new SimpleStringProperty(initDays);
        time = new SimpleStringProperty(initTime);
        room = new SimpleStringProperty(initRoom);
    }
    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public void setSection(String initSection) {
        section.set(initSection);
    }
    public void setTime(String initTime) {
        time.set(initTime);
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
    public String getTime() {
        return time.get();
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
    public StringProperty timeProperty() {
        return time;
    }
    public StringProperty roomProperty() {
        return room;
    }
    @Override
    public String toString() {
        return section.getValue();
    }
    @Override
    public Lecture clone(){
        Lecture ta = new Lecture(section.getValue(),days.getValue(),time.getValue(),room.getValue());
        return ta;
    }
}