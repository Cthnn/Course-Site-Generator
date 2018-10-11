
package oh.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Teaching Assistant for the table of TAs.
 * 
 * @author Richard McKenna
 */
public class TeachingAssistantPrototype {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty name;
    private final StringProperty email;
    private final IntegerProperty timeSlot;
    private final StringProperty type;

    /**
     * Constructor initializes both the TA name and email.
     */
    public TeachingAssistantPrototype(String initName, String initEmail) {
        name = new SimpleStringProperty(initName);
        email = new SimpleStringProperty(initEmail);
        timeSlot = new SimpleIntegerProperty(0);
        type = new SimpleStringProperty("Undergraduate");
    }
    public TeachingAssistantPrototype(String initName, String initEmail, String initType) {
        name = new SimpleStringProperty(initName);
        email = new SimpleStringProperty(initEmail);
        timeSlot = new SimpleIntegerProperty(0);
        type = new SimpleStringProperty(initType);
    }

    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public String getName() {
        return name.get();
    }

    public void setName(String initName) {
        name.set(initName);
    }
    public void setTimeSlot(int occurrences) {
        timeSlot.set(occurrences);
    }
    public int getTimeSlot() {
        return timeSlot.get();
    }
    public String getType() {
        return type.get();
    }

    public void setType(String initType) {
        type.set(initType);
    }
    public StringProperty typeProperty() {
        return type;
    } 
    public IntegerProperty timeSlotProperty() {
        return timeSlot;
    } 
    public StringProperty nameProperty() {
        return name;
    }
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String initEmail) {
        email.set(initEmail);
    }
    
    public StringProperty emailProperty() {
        return email;
    }
    @Override
    public String toString() {
        return name.getValue();
    }
    @Override
    public TeachingAssistantPrototype clone(){
        TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name.getValue(),email.getValue(),type.getValue());
        return ta;
    }
}