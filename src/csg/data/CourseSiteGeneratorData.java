package csg.data;

import javafx.collections.ObservableList;
import djf.components.AppDataComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.workspace.controllers.CourseSiteGeneratorController;

/**
 * This is the data component for TAManagerApp. It has all the data needed
 * to be set by the user via the User Interface and file I/O can set and get
 * all the data from this object
 * 
 * @author Richard McKenna
 */
public class CourseSiteGeneratorData implements AppDataComponent {

    // WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
    CourseSiteGeneratorApp app;

    // NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
    // DATA IN THE ROWS OF THE TABLE VIEW
    ObservableList<TeachingAssistantPrototype> teachingAssistants = FXCollections.observableArrayList();
    ObservableList<TeachingAssistantPrototype> all = FXCollections.observableArrayList();
    ObservableList<TeachingAssistantPrototype> undergrad = FXCollections.observableArrayList();
    ObservableList<TeachingAssistantPrototype> grad = FXCollections.observableArrayList();
    ObservableList<TimeSlot> officeHours = FXCollections.observableArrayList();
    ObservableList<Lab_Recitation> labs = FXCollections.observableArrayList();
    ObservableList<Lab_Recitation> recitations = FXCollections.observableArrayList();
    ObservableList<Lecture> lectures = FXCollections.observableArrayList();
    ObservableList<SchedItem> schedule = FXCollections.observableArrayList();
    // THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
    // THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
    // THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
    // NO MEANS FOR CHANGING THESE VALUES
    int startHour;
    int endHour;
    
    // DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
    public static final int MIN_START_HOUR = 9;
    public static final int MAX_END_HOUR = 21;

    /**
     * This constructor will setup the required data structures for
     * use, but will have to wait on the office hours grid, since
     * it receives the StringProperty objects from the Workspace.
     * 
     * @param initApp The application this data manager belongs to. 
     */
    public CourseSiteGeneratorData(CourseSiteGeneratorApp initApp){
        // KEEP THIS FOR LATER
        app = initApp;
        AppGUIModule gui = app.getGUIModule();

        // CONSTRUCT THE LIST OF TAs FOR THE TABLE
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableView<TimeSlot> ohTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(all);
        teachingAssistants = taTableView.getItems();
        lectures = ((TableView)gui.getGUINode(CSG_LECT_TABLE_VIEW)).getItems();
        recitations = ((TableView)gui.getGUINode(CSG_REC_TABLE_VIEW)).getItems();
        labs = ((TableView)gui.getGUINode(CSG_LAB_TABLE_VIEW)).getItems();
        schedule = ((TableView)gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getItems();
        // THESE ARE THE DEFAULT OFFICE HOURS
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        
        resetOfficeHours();
        initHours(""+startHour,""+endHour);
    }
    public Iterator<TeachingAssistantPrototype> teachingAssistantsIterator() {
        return teachingAssistants.iterator();
    }
    public Iterator<Lab_Recitation> labsIterator() {
        return labs.iterator();
    }
    public Iterator<Lab_Recitation> recitationsIterator() {
        return recitations.iterator();
    }
    public Iterator<SchedItem> schedIterator() {
        return schedule.iterator();
    }
    public Iterator<Lecture> lecturesIterator() {
        return lectures.iterator();
    }
    public void initHours(String startHourText, String endHourText) {
        int initStartHour = Integer.parseInt(startHourText);
        int initEndHour = Integer.parseInt(endHourText);
        if (initStartHour <= initEndHour) {
            // THESE ARE VALID HOURS SO KEEP THEM
            // NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
            startHour = initStartHour;
            endHour = initEndHour;     
        }
        int hourInt = initStartHour;
        boolean halfHour = false;
        int timeslots = 0;
        if((initEndHour > 12 && initStartHour < 12) || (initEndHour == 12 && initStartHour < 12) || (initStartHour == 12 && initEndHour > 12)){
            timeslots = initEndHour-initStartHour;
        }else if((initEndHour > 12 && initStartHour > 12) ||(initEndHour < 12 && initStartHour < 12)){
            timeslots = initEndHour - initStartHour;
        }
        for (int i = timeslots*2; i < officeHours.size(); i++) {
            officeHours.get(i).setStartTime("");
            officeHours.get(i).setEndTime("");
        }
        for (int i = 0; i < timeslots*2; i++) {
            String startTime = "";
            String endTime = "";
            if (hourInt < 12) {
                if(hourInt == 0){
                    if (halfHour) {
                        startTime = 12+":30am";
                        endTime = 1+":00am";
                        officeHours.get(i).setStartTime(startTime);
                        officeHours.get(i).setEndTime(endTime);
                        hourInt++;
                        halfHour = false;
                    }else{
                        startTime = 12+":00am";
                        endTime = 12+":30am";
                        officeHours.get(i).setStartTime(startTime);
                        officeHours.get(i).setEndTime(endTime);
                        halfHour = true;
                    }
                }else{
                    if (halfHour) {
                    startTime = hourInt+":30am";
                    endTime = (hourInt+1)+":00";
                        if (hourInt+1 == 12) {
                            endTime += "pm";
                        }else{
                            endTime+="am";
                        }
                    officeHours.get(i).setStartTime(startTime);
                    officeHours.get(i).setEndTime(endTime);
                    hourInt++;
                    halfHour = false;
                    }else{
                    startTime = hourInt+":00am";
                    endTime = (hourInt)+":30am";
                    officeHours.get(i).setStartTime(startTime);
                    officeHours.get(i).setEndTime(endTime);
                    halfHour = true;
                    }
                }
            }else{
                if (hourInt == 12 && !halfHour) {
                    startTime = hourInt+":00pm";
                    endTime = (hourInt)+":30pm";
                    officeHours.get(i).setStartTime(startTime);
                    officeHours.get(i).setEndTime(endTime);
                    halfHour = true;
                }else if(hourInt == 12 && halfHour){
                    startTime = hourInt+":30pm";
                    endTime = (hourInt+1-12)+":00pm";
                    officeHours.get(i).setStartTime(startTime);
                    officeHours.get(i).setEndTime(endTime);
                    hourInt++;
                    halfHour = false;
                }else{
                    if (halfHour) {
                        if (hourInt == 23) {
                            startTime = (hourInt-12)+":30pm";
                            endTime = 12+":00am";
                            officeHours.get(i).setStartTime(startTime);
                            officeHours.get(i).setEndTime(endTime);
                            hourInt++;
                            halfHour = false;
                        }else if (hourInt == 24) {
                            startTime = 12+":30am";
                            endTime = 1+":00am";
                            officeHours.get(i).setStartTime(startTime);
                            officeHours.get(i).setEndTime(endTime);
                            halfHour = false;
                            hourInt = 1;
                        }else{
                            startTime = (hourInt-12)+":30pm";
                            endTime = (hourInt+1-12)+":00pm";
                            officeHours.get(i).setStartTime(startTime);
                            officeHours.get(i).setEndTime(endTime);
                            hourInt++;
                            halfHour = false;
                        }
                    }else{
                        if (hourInt == 24) {
                            startTime = 12+":00am";
                            endTime = 12+":30am";
                            officeHours.get(i).setStartTime(startTime);
                            officeHours.get(i).setEndTime(endTime);
                            halfHour = true;
                        }else{
                            startTime = (hourInt-12)+":00pm";
                            endTime = (hourInt-12)+":30pm";
                            officeHours.get(i).setStartTime(startTime);
                            officeHours.get(i).setEndTime(endTime);
                            halfHour = true;
                        }
                    }
                }
            }
        }
    }
    public void setOH(ObservableList<TimeSlot> officeHours, String type){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        this.officeHours.clear();
        for (int i = 0; i < officeHours.size(); i++) {
            this.officeHours.add(officeHours.get(i));
        }
        if (type.equalsIgnoreCase("all")) {
            for (int i = 0; i < officeHours.size(); i++) {
            this.officeHours.get(i).showAll();
        }
        }else if(type.equalsIgnoreCase("undergraduate")){
            for (int i = 0; i < officeHours.size(); i++) {
                this.officeHours.get(i).showUndergrad(all);
            }
        }else if(type.equalsIgnoreCase("graduate")){
            for (int i = 0; i < officeHours.size(); i++) {
                this.officeHours.get(i).showGrad(all);
            }
        }
        
        ohTableView.refresh();
    }
    private void resetOfficeHours() {
        //THIS WILL STORE OUR OFFICE HOURS
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        officeHours = officeHoursTableView.getItems(); 
        officeHours.clear();
        for (int i = startHour; i <= endHour; i++) {
            TimeSlot timeSlot = new TimeSlot(   this.getTimeString(i, true),
                                                this.getTimeString(i, false));
            officeHours.add(timeSlot);
            
            TimeSlot halfTimeSlot = new TimeSlot(   this.getTimeString(i, false),
                                                    this.getTimeString(i+1, true));
            officeHours.add(halfTimeSlot);
        }
        for (int i = ((endHour-startHour)*2)+1; i < 24; i++) {
            officeHours.add(new TimeSlot("",""));
        }
    }
    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    private String getTimeString(int militaryHour, boolean onHour) {
        String minutesText = "00";
        if (!onHour) {
            minutesText = "30";
        }

        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutesText;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }
    public TimeSlot getTimeSlot(String startTime) {
        Iterator<TimeSlot> timeSlotsIterator = officeHours.iterator();
        while (timeSlotsIterator.hasNext()) {
            TimeSlot timeSlot = timeSlotsIterator.next();
            String timeSlotStartTime = timeSlot.getStartTime().replace(":", "_");
            if (timeSlotStartTime.equals(startTime))
                return timeSlot;
        }
        return null;
    }
    public ObservableList<TeachingAssistantPrototype> getAll(){
        return all;
    }
    public void addLecture(Lecture lect){
        lectures.add(lect);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_LECT_TABLE_VIEW)).refresh();
    }
    public void removeLecture(Lecture lect){
        lectures.remove(lect);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_LECT_TABLE_VIEW)).refresh();
    }
    public void removeLab(Lab_Recitation lab){
        labs.remove(lab);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_LAB_TABLE_VIEW)).refresh();
    }
    public void removeRec(Lab_Recitation rec){
        recitations.remove(rec);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_REC_TABLE_VIEW)).refresh();
    }
    public void removeSchedItem(SchedItem item){
        schedule.remove(item);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
    }
    public void addLab(Lab_Recitation lab){
        labs.add(lab);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_LAB_TABLE_VIEW)).refresh();
    }
    public void addRec(Lab_Recitation rec){
        recitations.add(rec);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_REC_TABLE_VIEW)).refresh();
    }
    public void addSchedItem(SchedItem item){
        schedule.add(item);
        AppGUIModule gui = app.getGUIModule();
        ((TableView)gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
    }
    public void addTA(TeachingAssistantPrototype ta) {
        boolean exists = false;
        for (int i = 0; i < all.size(); i++) {
            if (ta.getName().equals(all.get(i).getName()) || ta.getEmail().equals(all.get(i).getEmail())) {
                exists = true;
            }
        }
        if (!exists) {
            this.all.add(ta);
            if(ta.getType().equalsIgnoreCase("undergraduate")){
                this.undergrad.add(ta);
            }else if(ta.getType().equalsIgnoreCase("graduate")){
                this.grad.add(ta);
            }  
        }else{
            TeachingAssistantPrototype taClone = ta.clone();
            int index = 0;
            for (int i = 0; i < ta.getEmail().length(); i++) {
                if (ta.getEmail().charAt(i) == '@') {
                    index = i;
                    break;
                }
            }
            int counter = 1;
            String taName = ta.getName();
            String taEmailOne = ta.getEmail().substring(0,index);
            String taEmailRest = ta.getEmail().substring(index,ta.getEmail().length());
            String newEmail = taEmailOne + counter + taEmailRest;
            taClone.setName(taName + counter);
            taClone.setEmail(newEmail);
            exists = false;
            for (int i = 0; i < all.size(); i++) {
                if (taClone.getName().equals(all.get(i).getName()) || taClone.getEmail().equals(all.get(i).getEmail())) {
                    exists = true;
                }
            }
            counter++;
            while(exists){
                newEmail = taEmailOne+counter+taEmailRest;
                taClone.setName(taName + counter);
                taClone.setEmail(newEmail);
                boolean existsIn = false;
                for (int i = 0; i < all.size(); i++) {
                    if (taClone.getName().equals(all.get(i).getName()) || taClone.getEmail().equals(all.get(i).getEmail())) {
                        existsIn = true;
                    }
                }
                if (existsIn) {
                    counter++;
                }else{
                    exists = false;
                }
                
            }
            this.all.add(taClone);
            if(taClone.getType().equalsIgnoreCase("undergraduate")){
                this.undergrad.add(taClone);
            }else if(taClone.getType().equalsIgnoreCase("graduate")){
                this.grad.add(taClone);
            }      
        }
        AppGUIModule gui = app.getGUIModule();
        RadioButton allRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
        RadioButton ugRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
        RadioButton gRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
        if (allRB.isSelected()) {
            this.setCurrentToAll();
        }else if(ugRB.isSelected()){
            this.setCurrentToUG();
        }else if(gRB.isSelected()){
            this.setCurrentToG();
        }
        
    }
    
    public void removeTA(TeachingAssistantPrototype ta) {
        // REMOVE THE TA FROM THE LIST OF TAs
        this.all.remove(ta);
        this.teachingAssistants = this.all;
        if (ta.getType().equalsIgnoreCase("undergraduate")){
            undergrad.remove(ta);
        }else if(ta.getType().equalsIgnoreCase("graduate")){
            grad.remove(ta);
        }
        // AND REMOVE THE TA FROM ALL THEIR OFFICE HOURS
        AppGUIModule gui = app.getGUIModule();
        TableView ohTableView = (TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        for (int i = 0; i < ohTableView.getItems().size(); i++) {
            for (int j = 2; j < 7; j++) {
                ((TimeSlot)ohTableView.getItems().get(i)).removingTA(ta, j);
            }
        }
        RadioButton allRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
        RadioButton ugRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
        RadioButton gRB = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
        if (allRB.isSelected()) {
            this.setCurrentToAll();
        }else if(ugRB.isSelected()){
            this.setCurrentToUG();
        }else if(gRB.isSelected()){
            this.setCurrentToG();
        }
        ohTableView.refresh();
    }
    public ObservableList<TeachingAssistantPrototype> getTAs(){
        return teachingAssistants;
    }
    public ObservableList<Lab_Recitation> getLabs(){
        return labs;
    }
    public ObservableList<SchedItem> getSchedules(){
        return schedule;
    }
    public ObservableList<Lab_Recitation> getRecitations(){
        return recitations;
    }
    public ObservableList<Lecture> getLectures(){
        return lectures;
    }
    public ObservableList<TeachingAssistantPrototype> getUndergraduate(){
        return undergrad;
    }
    public ObservableList<TeachingAssistantPrototype> getGraduate(){
        return grad;
    }
    public void setAll(ObservableList<TeachingAssistantPrototype> all){
        this.all = all;
    }
    public void setUndergraduate(ObservableList<TeachingAssistantPrototype> undergrad){
        this.undergrad=undergrad;
    }
    public void setGraduate(ObservableList<TeachingAssistantPrototype> grad){
        this.grad = grad;
    }
    public void setCurrentToUG(){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(undergrad);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showUndergrad(all);
        }
        
        ohTableView.refresh();
        
    }
    public void setCurrentToAll(){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(all);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showAll();
        }
        ohTableView.refresh();
    }
    public void setCurrentToG(){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(grad);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showGrad(all);
        }
        ohTableView.refresh();
    }
    public ObservableList<TimeSlot> getOH(){
        return officeHours;
    }
    public ObservableList<TimeSlot> getOHClone(){
        ObservableList<TimeSlot> ohClone = FXCollections.observableArrayList();
        for (int i = 0; i < officeHours.size(); i++) {
            TimeSlot temp = officeHours.get(i).clone();
            ohClone.add(temp);    
        }
        return ohClone;
    } 
    @Override
    public void reset() {
    }
}