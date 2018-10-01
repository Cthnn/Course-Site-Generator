package oh.data;

import javafx.collections.ObservableList;
import djf.components.AppDataComponent;
import djf.modules.AppGUIModule;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static oh.OfficeHoursPropertyType.OH_TAS_TABLE_VIEW;
import oh.data.TimeSlot.DayOfWeek;

/**
 * This is the data component for TAManagerApp. It has all the data needed
 * to be set by the user via the User Interface and file I/O can set and get
 * all the data from this object
 * 
 * @author Richard McKenna
 */
public class OfficeHoursData implements AppDataComponent {

    // WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
    OfficeHoursApp app;

    // NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
    // DATA IN THE ROWS OF THE TABLE VIEW
    ObservableList<TeachingAssistantPrototype> teachingAssistants;
    ObservableList<TeachingAssistantPrototype> all = FXCollections.observableArrayList();
    ObservableList<TeachingAssistantPrototype> undergrad = FXCollections.observableArrayList();
    ObservableList<TeachingAssistantPrototype> grad = FXCollections.observableArrayList();
    ObservableList<TimeSlot> officeHours;
    

    // THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
    // THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
    // THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
    // NO MEANS FOR CHANGING THESE VALUES
    int startHour;
    int endHour;
    
    // DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
    public static final int MIN_START_HOUR = 9;
    public static final int MAX_END_HOUR = 20;

    /**
     * This constructor will setup the required data structures for
     * use, but will have to wait on the office hours grid, since
     * it receives the StringProperty objects from the Workspace.
     * 
     * @param initApp The application this data manager belongs to. 
     */
    public OfficeHoursData(OfficeHoursApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
        AppGUIModule gui = app.getGUIModule();

        // CONSTRUCT THE LIST OF TAs FOR THE TABLE
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        TableView<TimeSlot> ohTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(all);
        teachingAssistants = taTableView.getItems();
        
        // THESE ARE THE DEFAULT OFFICE HOURS
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        
        resetOfficeHours();
    }
    
    private void resetOfficeHours() {
        //THIS WILL STORE OUR OFFICE HOURS
        AppGUIModule gui = app.getGUIModule();
        TableView<TimeSlot> officeHoursTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
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
    
    public void initHours(String startHourText, String endHourText) {
        int initStartHour = Integer.parseInt(startHourText);
        int initEndHour = Integer.parseInt(endHourText);
        if (initStartHour <= initEndHour) {
            // THESE ARE VALID HOURS SO KEEP THEM
            // NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
            startHour = initStartHour;
            endHour = initEndHour;
        }
        resetOfficeHours();
    }
        
    /**
     * Called each time new work is created or loaded, it resets all data
     * and data structures such that they can be used for new values.
     */
    @Override
    public void reset() {
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        teachingAssistants.clear();
        
        for (int i = MIN_START_HOUR; i <= MAX_END_HOUR; i++) {
            TimeSlot timeSlot = officeHours.get(i);
            timeSlot.reset();
        }
    }
    
    // ACCESSOR METHODS

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    
    public boolean isTASelected() {
        AppGUIModule gui = app.getGUIModule();
        TableView tasTable = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        return tasTable.getSelectionModel().getSelectedItem() != null;
    }
    
    public void addTA(TeachingAssistantPrototype ta) {
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        if (!this.teachingAssistants.contains(ta)){
            this.all.add(ta);
        }
        if(ta.getType().equalsIgnoreCase("undergraduate")){
            this.undergrad.add(ta);
        }else if(ta.getType().equalsIgnoreCase("graduate")){
            this.grad.add(ta);
        }
    }
    
    public void removeTA(TeachingAssistantPrototype ta) {
        // REMOVE THE TA FROM THE LIST OF TAs
        this.all.remove(ta);
        this.teachingAssistants = this.all;
        
        // AND REMOVE THE TA FROM ALL THEIR OFFICE HOURS
    }
    public boolean isDayOfWeekColumn(int columnNumber) {
        return columnNumber >= 2;
    }
    
    public DayOfWeek getColumnDayOfWeek(int columnNumber) {
        return TimeSlot.DayOfWeek.values()[columnNumber-2];
    }

    public Iterator<TeachingAssistantPrototype> teachingAssistantsIterator() {
        return teachingAssistants.iterator();
    }
    
    public Iterator<TimeSlot> officeHoursIterator() {
        return officeHours.iterator();
    }

    public TeachingAssistantPrototype getTAWithName(String name) {
        Iterator<TeachingAssistantPrototype> taIterator = teachingAssistants.iterator();
        while (taIterator.hasNext()) {
            TeachingAssistantPrototype ta = taIterator.next();
            if (ta.getName().equals(name))
                return ta;
        }
        return null;
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
    public ObservableList<TeachingAssistantPrototype> getTAs(){
        return teachingAssistants;
    }
    public ObservableList<TeachingAssistantPrototype> getAll(){
        return all;
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
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(undergrad);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showUndergrad(all);
        }
        
    }
    public void setCurrentToAll(){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(all);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showAll();
        }
    }
    public void setCurrentToG(){
        AppGUIModule gui = app.getGUIModule();
        TableView<TeachingAssistantPrototype> taTableView = (TableView)gui.getGUINode(OH_TAS_TABLE_VIEW);
        TableView<TeachingAssistantPrototype> ohTableView = (TableView)gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        taTableView.setItems(grad);
        for (int i = 0; i < officeHours.size(); i++) {
            officeHours.get(i).showGrad(all);
        }
    }
    public ObservableList<TimeSlot> getOH(){
        return officeHours;
    }
}