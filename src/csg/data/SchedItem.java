
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
public class SchedItem {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty type;
    private final StringProperty date;
    private final StringProperty title;
    private final StringProperty topic;
    private final StringProperty link;
    /**
     * Constructor initializes both the TA name and email.
     */
    public SchedItem(String initType, String initDate, String initTitle, String initTopic,String initLink) {
        type = new SimpleStringProperty(initType);
        date = new SimpleStringProperty(initDate);
        title = new SimpleStringProperty(initTitle);
        topic = new SimpleStringProperty(initTopic);
        link = new SimpleStringProperty(initLink);
    }
    // ACCESSORS AND MUTATORS FOR THE PROPERTIES

    public void setType(String initType) {
        type.set(initType);
    }
    public void setTitle(String initTitle) {
        title.set(initTitle);
    }
    public void setTopic(String initTopic) {
        topic.set(initTopic);
    }
    public void setDate(String initDate) {
        date.set(initDate);
    }
    public void setLink(String initLink) {
        link.set(initLink);
    }
    public String getType() {
        return type.get();
    }
    public String getTitle() {
        return title.get();
    }
    public String getDate() {
        return date.get();
    }
    public String getTopic() {
        return topic.get();
    }
     public String getLink() {
        return link.get();
    }
    public StringProperty typeProperty() {
        return type;
    } 
    public StringProperty dateProperty() {
        return date;
    } 
    public StringProperty titleProperty() {
        return title;
    }
    public StringProperty topicProperty() {
        return topic;
    }
     public StringProperty linkProperty() {
        return link;
    }
    @Override
    public String toString() {
        return title.getValue();
    }
    @Override
    public SchedItem clone(){
        SchedItem ta = new SchedItem(type.getValue(),date.getValue(),title.getValue(),topic.getValue(),link.getValue());
        return ta;
    }
}