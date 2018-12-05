package csg.data;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * This class stores information for a single row in our
 * office hours table.
 * 
 * @author Richard McKenna
 */
public class TimeSlot {

    public enum DayOfWeek {   
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }   
    private StringProperty startTime;
    private StringProperty endTime;
    private HashMap<DayOfWeek, ArrayList<TeachingAssistantPrototype>> tas;
    private HashMap<DayOfWeek, StringProperty> dayText;

    public TimeSlot(String initStartTime, String initEndTime) {
        startTime = new SimpleStringProperty(initStartTime);
        endTime = new SimpleStringProperty(initEndTime);
        tas = new HashMap();
        dayText = new HashMap();
        for (DayOfWeek dow : DayOfWeek.values()) {
            tas.put(dow, new ArrayList());
            dayText.put(dow, new SimpleStringProperty());
        }
    }
    public HashMap<DayOfWeek, ArrayList<TeachingAssistantPrototype>> getTAs(){
        return tas;
    }
    public HashMap<DayOfWeek, StringProperty> getDayText(){
        return dayText;
    }
    public void setTAs(HashMap<DayOfWeek, ArrayList<TeachingAssistantPrototype>> tasMap){
        this.tas = tasMap;
    }
    public void setDayText(HashMap<DayOfWeek, StringProperty> dayTextMap){
        this.dayText = dayTextMap;
    }
    

    // ACCESSORS AND MUTATORS
    public String getStartTime() {
        return startTime.getValue();
    }
    
    public void setStartTime(String initStartTime) {
        startTime.setValue(initStartTime);
    }
    
    public StringProperty startTimeProperty() {
        return startTime;
    }
    
    public String getEndTime() {
        return endTime.getValue();
    }
    
    public void setEndTime(String initEndTime) {
        endTime.setValue(initEndTime);
    }
    
    public StringProperty endTimeProperty() {
        return endTime;
    }
    
    public String getMonday() {
        return dayText.get(DayOfWeek.MONDAY).getValue();
    }
    
    public void setMonday(String initMonday) {
        dayText.get(DayOfWeek.MONDAY).setValue(initMonday);
    }
    
    public StringProperty mondayProperty() {
        return this.dayText.get(DayOfWeek.MONDAY);
    }
    
    public String getTuesday() {
        return dayText.get(DayOfWeek.TUESDAY).getValue();
    }
    
    public void setTuesday(String initTuesday) {
        dayText.get(DayOfWeek.TUESDAY).setValue(initTuesday);
    }
    
    public StringProperty tuesdayProperty() {
        return this.dayText.get(DayOfWeek.TUESDAY);
    }
    
    public String getWednesday() {
        return dayText.get(DayOfWeek.WEDNESDAY).getValue();
    }
    
    public void setWednesday(String initWednesday) {
        dayText.get(DayOfWeek.WEDNESDAY).setValue(initWednesday);
    }
    
    public StringProperty wednesdayProperty() {
        return this.dayText.get(DayOfWeek.WEDNESDAY);
    }
    
    public String getThursday() {
        return dayText.get(DayOfWeek.THURSDAY).getValue();
    }
    
    public void setThursday(String initThursday) {
        dayText.get(DayOfWeek.THURSDAY).setValue(initThursday);
    }
    
    public StringProperty thursdayProperty() {
        return this.dayText.get(DayOfWeek.THURSDAY);
    }
    
    public String getFriday() {
        return dayText.get(DayOfWeek.FRIDAY).getValue();
    }
    
    public void setFriday(String initFriday) {
        dayText.get(DayOfWeek.FRIDAY).setValue(initFriday);
    }
    
    public StringProperty fridayProperty() {
        return this.dayText.get(DayOfWeek.FRIDAY);
    }
    public boolean exists(TeachingAssistantPrototype ta, int col){
        boolean exists = false;
        if(ta != null){
            if(col == 2){
                for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
                    if(tas.get(DayOfWeek.MONDAY).get(i).getName().equals(ta.getName())){
                        exists = true;
                    }
                }
            }else if(col == 3){
                for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
                    if(tas.get(DayOfWeek.TUESDAY).get(i).getName().equals(ta.getName())){
                        exists = true;
                    }
                }
            }else if(col == 4){
                for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
                    if(tas.get(DayOfWeek.WEDNESDAY).get(i).getName().equals(ta.getName())){
                        exists = true;
                    }
                }
            }else if(col == 5){
                for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
                    if(tas.get(DayOfWeek.THURSDAY).get(i).getName().equals(ta.getName())){
                        exists = true;
                    }
                }
            }else if(col == 6){
                for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
                    if(tas.get(DayOfWeek.FRIDAY).get(i).getName().equals(ta.getName())){
                        exists = true;
                    }
                }
            }
        }
        return exists;
    }
    public void removingTA(TeachingAssistantPrototype ta, int col){
        String taNames = "";
        if(col == 2){
            for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
                if(tas.get(DayOfWeek.MONDAY).get(i).getName().equals(ta.getName())){
                    tas.get(DayOfWeek.MONDAY).remove(i);
                }
            }
            for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
                taNames = taNames + tas.get(DayOfWeek.MONDAY).get(i)+"\n";
            }
            dayText.get(DayOfWeek.MONDAY).setValue(taNames);
        }
        if(col == 3){
            for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
                if(tas.get(DayOfWeek.TUESDAY).get(i).getName().equals(ta.getName())){
                    tas.get(DayOfWeek.TUESDAY).remove(i);
                   
                }
            }
            for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
                taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(i)+"\n";
            }
            dayText.get(DayOfWeek.TUESDAY).setValue(taNames);
        }
        if(col == 4){
            for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
                if(tas.get(DayOfWeek.WEDNESDAY).get(i).getName().equals(ta.getName())){
                    tas.get(DayOfWeek.WEDNESDAY).remove(i);
                }
            }
            for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
                taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(i)+"\n";
            }
            dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
        }
        if(col == 5){
            for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
                if(tas.get(DayOfWeek.THURSDAY).get(i).getName().equals(ta.getName())){
                    tas.get(DayOfWeek.THURSDAY).remove(i);
                }
            }
            for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
                taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(i)+"\n";
            }
            dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
        }
        if(col == 6){
            for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
                if(tas.get(DayOfWeek.FRIDAY).get(i).getName().equals(ta.getName())){
                    tas.get(DayOfWeek.FRIDAY).remove(i);
                }
            }
            for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
                taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(i)+"\n";
            }
            dayText.get(DayOfWeek.FRIDAY).setValue(taNames);
        }
    }
    public void addToTAs(TeachingAssistantPrototype ta, int col){
        if(ta != null){
            String taNames = "";
            boolean exists = false;
            if(col == 2){           
                tas.get(DayOfWeek.MONDAY).add(ta);
                for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
                    taNames = taNames + tas.get(DayOfWeek.MONDAY).get(i)+"\n";
                }
                dayText.get(DayOfWeek.MONDAY).setValue(taNames);
            }else if(col == 3){
                    tas.get(DayOfWeek.TUESDAY).add(ta);
                    for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
                        taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(i)+"\n";
                    }
                    dayText.get(DayOfWeek.TUESDAY).setValue(taNames);                
            }else if(col == 4){
                tas.get(DayOfWeek.WEDNESDAY).add(ta);
                for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
                    taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(i)+"\n";
                }
                dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
            }else if(col == 5){
                tas.get(DayOfWeek.THURSDAY).add(ta);
                for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
                    taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(i)+"\n";
                }
                dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
            }else if(col == 6){
                tas.get(DayOfWeek.FRIDAY).add(ta);
                for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
                    taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(i)+"\n";
                }
                dayText.get(DayOfWeek.FRIDAY).setValue(taNames);
            }
        }
    }
    public void reset() {
        for (DayOfWeek dow : DayOfWeek.values()) {
            tas.get(dow).clear();
            dayText.get(dow).setValue("");
        }
    }
    public void showGrad(ObservableList<TeachingAssistantPrototype> taList){        
        ArrayList<String> gTANames = new ArrayList();
        for (int i = 0; i < taList.size(); i++) {
            if (taList.get(i).getType().equalsIgnoreCase("Graduate")) {
                gTANames.add(taList.get(i).getName());
            }
        }
        String taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.MONDAY).get(i).getName();
            for (int j = 0; j < gTANames.size(); j++) {
                if (gTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.MONDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.MONDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.TUESDAY).get(i).getName();
            for (int j = 0; j < gTANames.size(); j++) {
                if (gTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.TUESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.WEDNESDAY).get(i).getName();
            for (int j = 0; j < gTANames.size(); j++) {
                if (gTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.THURSDAY).get(i).getName();
            for (int j = 0; j < gTANames.size(); j++) {
                if (gTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
        taNames = " ";
        for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.FRIDAY).get(i).getName();
            for (int j = 0; j < gTANames.size(); j++) {
                if (gTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.FRIDAY).setValue(taNames);
    }
    public void showAll(){
        String taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
            taNames = taNames + tas.get(DayOfWeek.MONDAY).get(i).getName() + "\n";
        }
        dayText.get(DayOfWeek.MONDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
            taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(i).getName() + "\n";
        }
        dayText.get(DayOfWeek.TUESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
            taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(i).getName() + "\n";
        }
        dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
            taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(i).getName() + "\n";
        }
        dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
            taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(i).getName() + "\n";
        }
        dayText.get(DayOfWeek.FRIDAY).setValue(taNames);

    }
    public void showUndergrad(ObservableList<TeachingAssistantPrototype> taList){
        ArrayList<String> ugTANames = new ArrayList();
        for (int i = 0; i < taList.size(); i++) {
            if (taList.get(i).getType().equalsIgnoreCase("Undergraduate")) {
                ugTANames.add(taList.get(i).getName());
            }
        }
        String taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.MONDAY).get(i).getName();
            for (int j = 0; j < ugTANames.size(); j++) {
                if (ugTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.MONDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.MONDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.TUESDAY).get(i).getName();
            for (int j = 0; j < ugTANames.size(); j++) {
                if (ugTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.TUESDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.TUESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.WEDNESDAY).get(i).getName();
            for (int j = 0; j < ugTANames.size(); j++) {
                if (ugTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.WEDNESDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.WEDNESDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.THURSDAY).get(i).getName();
            for (int j = 0; j < ugTANames.size(); j++) {
                if (ugTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.THURSDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.THURSDAY).setValue(taNames);
        taNames = "";
        for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
            String taName = tas.get(DayOfWeek.FRIDAY).get(i).getName();
            for (int j = 0; j < ugTANames.size(); j++) {
                if (ugTANames.get(j).equals(taName)) {
                    taNames = taNames + tas.get(DayOfWeek.FRIDAY).get(i)+"\n";
                }
            }
            
        }
        dayText.get(DayOfWeek.FRIDAY).setValue(taNames);
    }
    public TimeSlot clone(){
        TimeSlot newSlot = new TimeSlot(startTime.getValue(),endTime.getValue());
        for (int i = 0; i < tas.get(DayOfWeek.MONDAY).size(); i++) {
            newSlot.addToTAs(tas.get(DayOfWeek.MONDAY).get(i).clone(),2);
        }
        for (int i = 0; i < tas.get(DayOfWeek.TUESDAY).size(); i++) {
            newSlot.addToTAs(tas.get(DayOfWeek.TUESDAY).get(i).clone(),3);
        }
        for (int i = 0; i < tas.get(DayOfWeek.WEDNESDAY).size(); i++) {
            newSlot.addToTAs(tas.get(DayOfWeek.WEDNESDAY).get(i).clone(),4);
        }
        for (int i = 0; i < tas.get(DayOfWeek.THURSDAY).size(); i++) {
            newSlot.addToTAs(tas.get(DayOfWeek.THURSDAY).get(i).clone(),5);
        }
        for (int i = 0; i < tas.get(DayOfWeek.FRIDAY).size(); i++) {
            newSlot.addToTAs(tas.get(DayOfWeek.FRIDAY).get(i).clone(),6);
        }
        return newSlot;
    }
}