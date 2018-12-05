package csg.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import djf.modules.AppGUIModule;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import csg.CourseSiteGeneratorApp;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab_Recitation;
import csg.data.Lecture;
import csg.data.SchedItem;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.data.TimeSlot.DayOfWeek;
import csg.workspace.CourseSiteGeneratorWorkspace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import properties_manager.PropertiesManager;

/**
 * This class serves as the file component for the TA
 * manager app. It provides all saving and loading 
 * services for the application.
 * 
 * @author Richard McKenna
 */
public class CourseSiteGeneratorFiles implements AppFileComponent {
    // THIS IS THE APP ITSELF
    CourseSiteGeneratorApp app;
    
    // THESE ARE USED FOR IDENTIFYING JSON TYPES
    static final String JSON_YEAR_OPTION = "year";
    static final String JSON_SEM_OPTION = "semester";
    static final String JSON_SUBJ_OPTION = "subject";
    static final String JSON_NUM_OPTION = "num";
    static final String JSON_CSS_OPTION = "css";
    static final String JSON_TITLE = "title";
    static final String JSON_HOME = "home";
    static final String JSON_SYLLABUS = "syllabus";
    static final String JSON_SYLLABUSPG = "syllabus_page";
    static final String JSON_SCHEDULE = "schedule";
    static final String JSON_HWS = "hws";
    static final String JSON_PAGES = "pages";
    static final String JSON_DESC = "description";
    static final String JSON_TOPICS = "topics";
    static final String JSON_PREQ = "prerequisites";
    static final String JSON_OUTCOMES = "outcomes";
    static final String JSON_TXTBKS = "txtbks";
    static final String JSON_GC = "Graded Components";
    static final String JSON_GN = "Grading Notes";
    static final String JSON_AD = "Academic Dishonesty";
    static final String JSON_SA = "Special Assistance";
    static final String JSON_UNDERGRAD_TAS = "undergrad_tas";
    static final String JSON_GRAD_TAS = "grad_tas";
    static final String JSON_NAME = "name";
    static final String JSON_EMAIL = "email";
    static final String JSON_OFFICE_HOURS = "officeHours";
    static final String JSON_START_HOUR = "startHour";
    static final String JSON_END_HOUR = "endHour";
    static final String JSON_START_TIME = "time";
    static final String JSON_DAY_OF_WEEK = "day";
    static final String JSON_MONDAY = "monday";
    static final String JSON_TUESDAY = "tuesday";
    static final String JSON_WEDNESDAY = "wednesday";
    static final String JSON_THURSDAY = "thursday";
    static final String JSON_FRIDAY = "friday";
    static final String JSON_INSTR_NAME = "instr_name";
    static final String JSON_INSTR_ROOM = "intr_room";
    static final String JSON_INSTR_EMAIL = "instr_email";
    static final String JSON_INSTR_HP = "instr_hp";
    static final String JSON_INSTR_OH = "instr_oh";
    static final String JSON_INSTR = "instr";
    static final String JSON_FAVICON = "favicon";
    static final String JSON_NAVBAR = "navbar";
    static final String JSON_LFOOT = "lfoot";
    static final String JSON_RFOOT = "rfoot";
    static final String JSON_CSS = "css";
    static final String JSON_ROOM = "room";
    static final String JSON_DAYS= "days";
    static final String JSON_SECTION = "section";
    static final String JSON_TIME = "time";
    static final String JSON_TA1 = "ta1";
    static final String JSON_TA2 = "ta2";
    static final String JSON_LECTURE = "lecture";
    static final String JSON_LAB = "lab";
    static final String JSON_RECITATION = "recitation";
    static final String JSON_STYLE = "style";
    static final String JSON_BANNER = "banner";
    static final String JSON_START_MON = "mon";
    static final String JSON_START_FRI = "fri";
    static final String JSON_SCHED_TYPE = "type";
    static final String JSON_SCHED_DATE = "date";
    static final String JSON_SCHED_TOPIC = "topic";
    static final String JSON_SCHED_TITLE = "title";
    static final String JSON_SCHED_LINK = "link";
    static final String JSON_SCHED_TABLE = "schedule_table";

    public CourseSiteGeneratorFiles(CourseSiteGeneratorApp initApp) {
        app = initApp;
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        AppGUIModule gui = app.getGUIModule();
        CourseSiteGeneratorData csgData = (CourseSiteGeneratorData)data;
        csgData.reset();
        JsonObject json = loadJSONFile(filePath);
        
        JsonArray banner = json.getJsonArray(JSON_BANNER);
        ((ComboBox)gui.getGUINode(CSG_SUBJECT_CB)).getSelectionModel().select(banner.getJsonObject(0).getString(JSON_SUBJ_OPTION));
        ((ComboBox)gui.getGUINode(CSG_NUMBER_CB)).getSelectionModel().select(banner.getJsonObject(1).getString(JSON_NUM_OPTION));
        ((ComboBox)gui.getGUINode(CSG_SEM_CB)).getSelectionModel().select(banner.getJsonObject(2).getString(JSON_SEM_OPTION));
        ((ComboBox)gui.getGUINode(CSG_YEAR_CB)).getSelectionModel().select(banner.getJsonObject(3).getString(JSON_YEAR_OPTION));
        ((TextField)gui.getGUINode(CSG_TITLE_TEXT_FIELD)).setText(banner.getJsonObject(4).getString(JSON_TITLE));
        JsonArray pages = json.getJsonArray(JSON_PAGES);
        if (pages.getJsonObject(0).getString(JSON_HOME).equals("1")) {
            ((CheckBox)gui.getGUINode(CSG_HOME_PAGE_BUTTON)).setSelected(true);
        }
        if (pages.getJsonObject(1).getString(JSON_HWS).equals("1")) {
            ((CheckBox)gui.getGUINode(CSG_HW_PAGE_BUTTON)).setSelected(true);
        }
        if (pages.getJsonObject(2).getString(JSON_SYLLABUS).equals("1")) {
            ((CheckBox)gui.getGUINode(CSG_SYLLABUS_PAGE_BUTTON)).setSelected(true);
        }
        if (pages.getJsonObject(3).getString(JSON_SCHEDULE).equals("1")) {
            ((CheckBox)gui.getGUINode(CSG_SCHEDULE_PAGE_BUTTON)).setSelected(true);
        }
        JsonArray style = json.getJsonArray(JSON_STYLE);
        
        try{
        Image image = new Image(new FileInputStream(style.getJsonObject(0).getString(JSON_FAVICON)));
        ImageView favIV = new ImageView();
        favIV.setImage(image);
        ((HBox)gui.getGUINode(CSG_FAVICON_BOX)).getChildren().remove(((HBox)gui.getGUINode(CSG_FAVICON_BOX)).getChildren().size()-1);
        ((HBox)gui.getGUINode(CSG_FAVICON_BOX)).getChildren().add(favIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        try{
        Image image = new Image(new FileInputStream(style.getJsonObject(1).getString(JSON_NAVBAR)));
        ImageView navIV = new ImageView();
        navIV.setImage(image);
        ((HBox)gui.getGUINode(CSG_NAVBAR_BOX)).getChildren().remove(((HBox)gui.getGUINode(CSG_NAVBAR_BOX)).getChildren().size()-1);
        ((HBox)gui.getGUINode(CSG_NAVBAR_BOX)).getChildren().add(navIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        try{
        Image image = new Image(new FileInputStream(style.getJsonObject(2).getString(JSON_LFOOT)));
        ImageView lfootIV = new ImageView();
        lfootIV.setImage(image);
        ((HBox)gui.getGUINode(CSG_LFOOT_BOX)).getChildren().remove(((HBox)gui.getGUINode(CSG_LFOOT_BOX)).getChildren().size()-1);
        ((HBox)gui.getGUINode(CSG_LFOOT_BOX)).getChildren().add(lfootIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        try{
        Image image = new Image(new FileInputStream(style.getJsonObject(3).getString(JSON_RFOOT)));
        ImageView rfootIV = new ImageView();
        rfootIV.setImage(image);
        ((HBox)gui.getGUINode(CSG_RFOOT_BOX)).getChildren().remove(((HBox)gui.getGUINode(CSG_RFOOT_BOX)).getChildren().size()-1);
        ((HBox)gui.getGUINode(CSG_RFOOT_BOX)).getChildren().add(rfootIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        
        ((ComboBox)gui.getGUINode(CSG_CSS_CB)).getSelectionModel().select(style.getJsonObject(4).getString(JSON_CSS));
        JsonArray instr = json.getJsonArray(JSON_INSTR);
        ((TextField)gui.getGUINode(CSG_INSTR_NAMETF)).setText(instr.getJsonObject(0).getString(JSON_INSTR_NAME));
        ((TextField)gui.getGUINode(CSG_INSTR_ROOMTF)).setText(instr.getJsonObject(1).getString(JSON_INSTR_ROOM));
        ((TextField)gui.getGUINode(CSG_INSTR_EMAILTF)).setText(instr.getJsonObject(2).getString(JSON_INSTR_EMAIL));
        ((TextField)gui.getGUINode(CSG_INSTR_PAGETF)).setText(instr.getJsonObject(3).getString(JSON_INSTR_HP));
        ((TextArea)gui.getGUINode(CSG_OH_TEXTFIELD)).setText(instr.getJsonObject(4).getString(JSON_INSTR_OH));
        
        JsonArray syllabusPG = json.getJsonArray(JSON_SYLLABUSPG);
        ((TextArea)gui.getGUINode(CSG_DESC_TEXTFIELD)).setText(syllabusPG.getJsonObject(0).getString(JSON_DESC));
        ((TextArea)gui.getGUINode(CSG_TOPICS_TEXTFIELD)).setText(syllabusPG.getJsonObject(1).getString(JSON_TOPICS));
        ((TextArea)gui.getGUINode(CSG_PREQ_TEXTFIELD)).setText(syllabusPG.getJsonObject(2).getString(JSON_PREQ));
        ((TextArea)gui.getGUINode(CSG_OUTCOMES_TEXTFIELD)).setText(syllabusPG.getJsonObject(3).getString(JSON_OUTCOMES));
        ((TextArea)gui.getGUINode(CSG_TXTBKS_TEXTFIELD)).setText(syllabusPG.getJsonObject(4).getString(JSON_TXTBKS));
        ((TextArea)gui.getGUINode(CSG_GC_TEXTFIELD)).setText(syllabusPG.getJsonObject(5).getString(JSON_GC));
        ((TextArea)gui.getGUINode(CSG_GN_TEXTFIELD)).setText(syllabusPG.getJsonObject(6).getString(JSON_GN));
        ((TextArea)gui.getGUINode(CSG_AD_TEXTFIELD)).setText(syllabusPG.getJsonObject(7).getString(JSON_AD));
        ((TextArea)gui.getGUINode(CSG_SA_TEXTFIELD)).setText(syllabusPG.getJsonObject(8).getString(JSON_SA));
        
        JsonArray jsonLectArray = json.getJsonArray(JSON_LECTURE);
        for (int i = 0; i < jsonLectArray.size(); i++) {
            JsonObject lect = jsonLectArray.getJsonObject(i);
            String section = lect.getString(JSON_SECTION);
            String days = lect.getString(JSON_DAYS);
            String time = lect.getString(JSON_TIME);
            String room = lect.getString(JSON_ROOM);
            Lecture lecture = new Lecture(section,days,time,room);
            csgData.addLecture(lecture);
        }
        JsonArray jsonLabArray = json.getJsonArray(JSON_LAB);
        for (int i = 0; i < jsonLabArray.size(); i++) {
            JsonObject lab = jsonLabArray.getJsonObject(i);
            String section = lab.getString(JSON_SECTION);
            String days = lab.getString(JSON_DAYS);
            String ta1 = lab.getString(JSON_TA1);
            String ta2 = lab.getString(JSON_TA2);
            String room = lab.getString(JSON_ROOM);
            Lab_Recitation laboratory = new Lab_Recitation(section,days,ta1,ta2,room);
            csgData.addLab(laboratory);
        }
        JsonArray jsonRecArray = json.getJsonArray(JSON_RECITATION);
        for (int i = 0; i < jsonRecArray.size(); i++) {
            JsonObject rec = jsonRecArray.getJsonObject(i);
            String section = rec.getString(JSON_SECTION);
            String days = rec.getString(JSON_DAYS);
            String ta1 = rec.getString(JSON_TA1);
            String ta2 = rec.getString(JSON_TA2);
            String room = rec.getString(JSON_ROOM);
            Lab_Recitation recitation = new Lab_Recitation(section,days,ta1,ta2,room);
            csgData.addRec(recitation);
        }
        String startHour = json.getString(JSON_START_HOUR);
        String endHour = json.getString(JSON_END_HOUR);
        csgData.initHours(startHour, endHour);
        int starthr = Integer.parseInt(startHour);
        int endhr = Integer.parseInt(endHour);
        if (starthr > 12) {
            ((ComboBox)gui.getGUINode(CSG_START_TIME_CB)).getSelectionModel().select((starthr-12)+":00pm");
        }else if(starthr == 12){
            ((ComboBox)gui.getGUINode(CSG_START_TIME_CB)).getSelectionModel().select((starthr)+":00pm");
        }
        else{
            ((ComboBox)gui.getGUINode(CSG_START_TIME_CB)).getSelectionModel().select((starthr)+":00am");
        }
        if (endhr > 12) {
            ((ComboBox)gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select((endhr-12)+":00pm");
        }else if(starthr == 12){
            ((ComboBox)gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select((endhr)+":00pm");
        }
        else{
            ((ComboBox)gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select((endhr)+":00am");
        }
        // NOW LOAD ALL THE UNDERGRAD TAs
        JsonArray jsonTAArray = json.getJsonArray(JSON_UNDERGRAD_TAS);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String name = jsonTA.getString(JSON_NAME);
            String email = jsonTA.getString(JSON_EMAIL);
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email,"Undergraduate");
            csgData.addTA(ta);
        }
        // NOW LOAD ALL THE GRAD TAs
        jsonTAArray = json.getJsonArray(JSON_GRAD_TAS);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String name = jsonTA.getString(JSON_NAME);
            String email = jsonTA.getString(JSON_EMAIL);
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email,"Graduate");
            csgData.addTA(ta);
        }
        //LOAD ALL OFFICE HOURS
        JsonArray jsonOHArray = json.getJsonArray(JSON_OFFICE_HOURS);
        TableView ohTableView = (TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
        TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
        for (int i = 0; i < jsonOHArray.size(); i++) {
            JsonObject jsonTA = jsonOHArray.getJsonObject(i);
            String time = jsonTA.getString(JSON_START_TIME);
            String day = jsonTA.getString(JSON_DAY_OF_WEEK);
            String name = jsonTA.getString(JSON_NAME);
            String email = "";
            for(Object o: taTableView.getItems()){
                if(name.equals((String)nameCol.getCellData(o))){
                    email = (((TableColumn)taTableView.getColumns().get(1)).getCellData(o)).toString();
                }
            }
            int col = 0;
            if(day.equalsIgnoreCase(JSON_MONDAY)){
                col = 2;
            }else if(day.equalsIgnoreCase(JSON_TUESDAY)){
                col = 3;
            }else if(day.equalsIgnoreCase(JSON_WEDNESDAY)){
                col = 4;
            }else if(day.equalsIgnoreCase(JSON_THURSDAY)){
                col = 5;
            }else if(day.equalsIgnoreCase(JSON_FRIDAY)){
                col = 6;
            }
            int underscoreIndex = 0;
            for (int j = 0; j < time.length(); j++) {
                if (time.substring(j,j+1).equals("_")){
                    underscoreIndex = j;
                }
            }
            int timeInt = Integer.parseInt(time.substring(0,underscoreIndex));
            String halfHour = time.substring(underscoreIndex+1,underscoreIndex+2);
            String am_pm = time.substring(time.length()-2,time.length());
            boolean isAM = time.substring(time.length()-2,time.length()).equals("am");
            boolean isPM = time.substring(time.length()-2,time.length()).equals("pm");
            int row = 0;
            int start = Integer.parseInt(startHour);
            if(isAM){
                if(timeInt == start && halfHour.equals("3")){
                    row = timeInt - (start-1);
                }else if(timeInt == start && !halfHour.equals("3")){
                    row = timeInt - start;
                }else if(timeInt != start && halfHour.equals("3")){
                    row = (timeInt - start)*2+1;
                }else if(timeInt != start && !halfHour.equals("3")){
                    row = (timeInt - start)*2;
                }
            }else if(isPM){
                row = (12-start)*2;
                if(timeInt == 12 && halfHour.equals("3")){
                    row = row+1;
                }else if(timeInt == 12 && halfHour.equals("3")){
                    row = (12-start)*2;
                }else if(timeInt < 12){
                    row = (12-start)*2+2;
                    if(timeInt == 1 && halfHour.equals("3")){
                        row++;
                    }else if(timeInt !=1 && !halfHour.equals("3")){
                        row = row + ((timeInt-1)*2);
                    }else if(timeInt !=1 && halfHour.equals("3")){
                        row++;
                        row = row + ((timeInt-1)*2);
                    }
                    
                }   
            }
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email);
            TimeSlot timeSlot = (TimeSlot)ohTableView.getItems().get(row);
            timeSlot.addToTAs(ta, col);
        }
        int start = Integer.parseInt(startHour);
        int end = Integer.parseInt(endHour);
        for (int i = 0; i < csgData.getTAs().size(); i++) {
            String taName = csgData.getTAs().get(i).getName();
            int count = 0;
            for (int j = 0; j <= ((end-start)*2); j++) {
                TimeSlot timeSlot = (TimeSlot)ohTableView.getItems().get(j);
                for (int k = 0; k < timeSlot.getTAs().get(DayOfWeek.MONDAY).size(); k++) {
                    if (taName.equals(timeSlot.getTAs().get(DayOfWeek.MONDAY).get(k).getName())) {
                        count++;
                    }
                }
                for (int k = 0; k < timeSlot.getTAs().get(DayOfWeek.TUESDAY).size(); k++) {
                    if (taName.equals(timeSlot.getTAs().get(DayOfWeek.TUESDAY).get(k).getName())) {
                        count++;
                    }
                }
                for (int k = 0; k < timeSlot.getTAs().get(DayOfWeek.WEDNESDAY).size(); k++) {
                    if (taName.equals(timeSlot.getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName())) {
                        count++;
                    }
                }
                for (int k = 0; k < timeSlot.getTAs().get(DayOfWeek.THURSDAY).size(); k++) {
                    if (taName.equals(timeSlot.getTAs().get(DayOfWeek.THURSDAY).get(k).getName())) {
                        count++;
                    }
                }
                for (int k = 0; k < timeSlot.getTAs().get(DayOfWeek.FRIDAY).size(); k++) {
                    if (taName.equals(timeSlot.getTAs().get(DayOfWeek.FRIDAY).get(k).getName())) {
                        count++;
                    }
                }
            }
            csgData.getTAs().get(i).setTimeSlot(count);
        }
        //date pickers
        JsonArray schedule = json.getJsonArray(JSON_SCHED_TABLE);
        for (int i = 0; i < schedule.size(); i++) {
            JsonObject schedItem = schedule.getJsonObject(i);
            String type = schedItem.getString(JSON_SCHED_TYPE);
            String date = schedItem.getString(JSON_SCHED_DATE);
            String topic = schedItem.getString(JSON_SCHED_TOPIC);
            String title = schedItem.getString(JSON_SCHED_TITLE);
            String link = schedItem.getString(JSON_SCHED_LINK);
            SchedItem item = new SchedItem(type,date,title,topic,link);
            csgData.addSchedItem(item);
        }
    }
      
    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }

    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        AppGUIModule gui = app.getGUIModule();
        CourseSiteGeneratorData csgData = (CourseSiteGeneratorData)data;
        JsonArrayBuilder selections = Json.createArrayBuilder();
        JsonArrayBuilder pageChecks = Json.createArrayBuilder();
        //ComboBoxes(Site Page)
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        JsonObject subj = Json.createObjectBuilder().add(JSON_SUBJ_OPTION,((ComboBox)gui.getGUINode(CSG_SUBJECT_CB)).getSelectionModel().getSelectedItem().toString()).build();
        JsonObject num = Json.createObjectBuilder().add(JSON_NUM_OPTION,((ComboBox)gui.getGUINode(CSG_NUMBER_CB)).getSelectionModel().getSelectedItem().toString()).build();
        JsonObject sem = Json.createObjectBuilder().add(JSON_SEM_OPTION,((ComboBox)gui.getGUINode(CSG_SEM_CB)).getSelectionModel().getSelectedItem().toString()).build();
        JsonObject year = Json.createObjectBuilder().add(JSON_YEAR_OPTION,((ComboBox)gui.getGUINode(CSG_YEAR_CB)).getSelectionModel().getSelectedItem().toString()).build();
        JsonObject title = Json.createObjectBuilder().add(JSON_TITLE,((TextField)gui.getGUINode(CSG_TITLE_TEXT_FIELD)).getText()).build();
        selections.add(subj);
        selections.add(num);
        selections.add(sem);
        selections.add(year);
        selections.add(title);
        //Pages(Site Pane)
        int selected = 0;
        if (((CheckBox)gui.getGUINode(CSG_HOME_PAGE_BUTTON)).isSelected()) {
            selected = 1;
        }
        JsonObject homeBox = Json.createObjectBuilder().add(JSON_HOME,""+selected).build();
        selected = 0;
        pageChecks.add(homeBox);
        
        if (((CheckBox)gui.getGUINode(CSG_HW_PAGE_BUTTON)).isSelected()) {
            selected = 1;
        }
        JsonObject hwBox = Json.createObjectBuilder().add(JSON_HWS,""+selected).build();
        selected = 0;
        pageChecks.add(hwBox);
        
        if (((CheckBox)gui.getGUINode(CSG_SYLLABUS_PAGE_BUTTON)).isSelected()) {
            selected = 1;
        }
        JsonObject syllabusBox = Json.createObjectBuilder().add(JSON_SYLLABUS,""+selected).build();
        selected = 0;
        pageChecks.add(syllabusBox);
        
        if (((CheckBox)gui.getGUINode(CSG_SCHEDULE_PAGE_BUTTON)).isSelected()) {
            selected = 1;
        }
        JsonObject schedBox = Json.createObjectBuilder().add(JSON_SCHEDULE,""+selected).build();
        selected = 0;
        pageChecks.add(schedBox);
        
        //Style(Site Pane)
        JsonArrayBuilder style = Json.createArrayBuilder();
        JsonObject favicon = Json.createObjectBuilder().add(JSON_FAVICON,CourseSiteGeneratorWorkspace.faviconFP).build();
        JsonObject navbar = Json.createObjectBuilder().add(JSON_NAVBAR,CourseSiteGeneratorWorkspace.navbarFP).build();
        JsonObject lfoot = Json.createObjectBuilder().add(JSON_LFOOT,CourseSiteGeneratorWorkspace.lfootFP).build();
        JsonObject rfoot = Json.createObjectBuilder().add(JSON_RFOOT,CourseSiteGeneratorWorkspace.rfootFP).build();
        JsonObject css = Json.createObjectBuilder().add(JSON_CSS,((ComboBox)gui.getGUINode(CSG_CSS_CB)).getSelectionModel().getSelectedItem().toString()).build();
        style.add(favicon);
        style.add(navbar);
        style.add(lfoot);
        style.add(rfoot);
        style.add(css);
        //Instructor(Site Pane)
        JsonArrayBuilder instrBox = Json.createArrayBuilder();
        JsonObject instrName = Json.createObjectBuilder().add(JSON_INSTR_NAME,((TextField)gui.getGUINode(CSG_INSTR_NAMETF)).getText()).build();
        JsonObject instrRoom = Json.createObjectBuilder().add(JSON_INSTR_ROOM,((TextField)gui.getGUINode(CSG_INSTR_ROOMTF)).getText()).build();
        JsonObject instrEmail = Json.createObjectBuilder().add(JSON_INSTR_EMAIL,((TextField)gui.getGUINode(CSG_INSTR_EMAILTF)).getText()).build();
        JsonObject instrHP = Json.createObjectBuilder().add(JSON_INSTR_HP,((TextField)gui.getGUINode(CSG_INSTR_PAGETF)).getText()).build();
        JsonObject instrOH = Json.createObjectBuilder().add(JSON_INSTR_OH,((TextArea)gui.getGUINode(CSG_OH_TEXTFIELD)).getText()).build();
        instrBox.add(instrName);
        instrBox.add(instrRoom);
        instrBox.add(instrEmail);
        instrBox.add(instrHP);
        instrBox.add(instrOH);
        //Syllabus Pane
        JsonArrayBuilder syllabusPG = Json.createArrayBuilder();
        JsonObject descField = Json.createObjectBuilder().add(JSON_DESC,((TextArea)gui.getGUINode(CSG_DESC_TEXTFIELD)).getText()).build();
        JsonObject topicField = Json.createObjectBuilder().add(JSON_TOPICS,((TextArea)gui.getGUINode(CSG_TOPICS_TEXTFIELD)).getText()).build();
        JsonObject preqField = Json.createObjectBuilder().add(JSON_PREQ,((TextArea)gui.getGUINode(CSG_PREQ_TEXTFIELD)).getText()).build();
        JsonObject outcomesField = Json.createObjectBuilder().add(JSON_OUTCOMES,((TextArea)gui.getGUINode(CSG_OUTCOMES_TEXTFIELD)).getText()).build();
        JsonObject txtbksField = Json.createObjectBuilder().add(JSON_TXTBKS,((TextArea)gui.getGUINode(CSG_TXTBKS_TEXTFIELD)).getText()).build();
        JsonObject gcField = Json.createObjectBuilder().add(JSON_GC,((TextArea)gui.getGUINode(CSG_GC_TEXTFIELD)).getText()).build();
        JsonObject gnField = Json.createObjectBuilder().add(JSON_GN,((TextArea)gui.getGUINode(CSG_GN_TEXTFIELD)).getText()).build();
        JsonObject adField = Json.createObjectBuilder().add(JSON_AD,((TextArea)gui.getGUINode(CSG_AD_TEXTFIELD)).getText()).build();
        JsonObject saField = Json.createObjectBuilder().add(JSON_SA,((TextArea)gui.getGUINode(CSG_SA_TEXTFIELD)).getText()).build();
        syllabusPG.add(descField);
        syllabusPG.add(topicField);
        syllabusPG.add(preqField);
        syllabusPG.add(outcomesField);
        syllabusPG.add(txtbksField);
        syllabusPG.add(gcField);
        syllabusPG.add(gnField);
        syllabusPG.add(adField);
        syllabusPG.add(saField);
        //Meeting Times Pane
        JsonArrayBuilder lectures = Json.createArrayBuilder();
        JsonArrayBuilder recitations = Json.createArrayBuilder();
        JsonArrayBuilder labs = Json.createArrayBuilder();
        Iterator<Lecture> lecturesIterator = csgData.lecturesIterator();
        Iterator<Lab_Recitation> labsIterator = csgData.labsIterator();
        Iterator<Lab_Recitation> recitationsIterator = csgData.recitationsIterator();
        while (lecturesIterator.hasNext()) {
            Lecture lect = lecturesIterator.next();
            JsonObject lecture = Json.createObjectBuilder()
                    .add(JSON_ROOM,lect.getRoom())
                    .add(JSON_SECTION,lect.getSection())
                    .add(JSON_DAYS,lect.getDays())
                    .add(JSON_TIME,lect.getTime())
                    .build();
            lectures.add(lecture); 
	}
        while (labsIterator.hasNext()) {
            Lab_Recitation lab = labsIterator.next();
            JsonObject laboratory = Json.createObjectBuilder()
                    .add(JSON_ROOM,lab.getRoom())
                    .add(JSON_SECTION,lab.getSection())
                    .add(JSON_DAYS,lab.getDays())
                    .add(JSON_TA1,lab.getTA1())
                    .add(JSON_TA2,lab.getTA2())
                    .build();
            labs.add(laboratory); 
	}
        while (recitationsIterator.hasNext()) {
            Lab_Recitation rec = recitationsIterator.next();
            JsonObject recitation = Json.createObjectBuilder()
                    .add(JSON_ROOM,rec.getRoom())
                    .add(JSON_SECTION,rec.getSection())
                    .add(JSON_DAYS,rec.getDays())
                    .add(JSON_TA1,rec.getTA1())
                    .add(JSON_TA2,rec.getTA2())
                    .build();
            recitations.add(recitation); 
	}
        JsonArray lab = labs.build();
	JsonArray lecture = lectures.build();
        JsonArray recitation = recitations.build();
        //Office Hours Pane
        TableView ohTableView = (TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
	JsonArrayBuilder ugTAArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder gradTAArrayBuilder = Json.createArrayBuilder();
	Iterator<TeachingAssistantPrototype> tasIterator = csgData.teachingAssistantsIterator();
        while (tasIterator.hasNext()) {
            TeachingAssistantPrototype ta = tasIterator.next();
            if (ta.getType().equalsIgnoreCase("undergraduate") || ta.getType().equalsIgnoreCase("")) {
                JsonObject taJson = Json.createObjectBuilder()
		    .add(JSON_NAME, ta.getName()).add(JSON_EMAIL,ta.getEmail()).build();
                ugTAArrayBuilder.add(taJson);
            }else if(ta.getType().equalsIgnoreCase("graduate")){
                JsonObject taJson = Json.createObjectBuilder()
		    .add(JSON_NAME, ta.getName()).add(JSON_EMAIL,ta.getEmail()).build();
                gradTAArrayBuilder.add(taJson);
            }
	    
	}
        JsonArrayBuilder ohArrayBuilder = Json.createArrayBuilder();
        
        Object [] ohTimeSlotsArray = ohTableView.getItems().toArray();
        int dif = ((12-csgData.getStartHour())*2);
        for (int i = 2; i < 7; i++) {
            if(i == 2){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getMonday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).size(); k++) {
                            if(csgData.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j > dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_00pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_30pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j == dif || j == dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(i == 3){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getTuesday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).size(); k++) {
                            if(csgData.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j > dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_00pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_30pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j == dif || j == dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(i == 4){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getWednesday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).size(); k++) {
                            if(csgData.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j > dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_00pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_30pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j == dif || j == dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(i == 5){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getThursday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).size(); k++) {
                            if(csgData.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j > dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_00pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_30pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j == dif || j == dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(i == 6){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getFriday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).size(); k++) {
                            if(csgData.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j > dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_00pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(1+((j-dif-2)/2))+"_30pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }else if(j == dif || j == dif+1){
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(csgData.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        JsonArray ohArray = ohArrayBuilder.build();
	JsonArray undergradTAsArray = ugTAArrayBuilder.build();
        JsonArray gradTAsArray = gradTAArrayBuilder.build();
        //Schedule Pane
        JsonArrayBuilder schedule = Json.createArrayBuilder();
        // HBox monBox = ((HBox)gui.getGUINode(CSG_STMON_PANE));
        //DatePicker monPicker = (DatePicker)(monBox.getChildren().get(monBox.getChildren().size()-1));
        //LocalDate localDate = monPicker.getValue();
        //JsonObject startMon = Json.createObjectBuilder().add(JSON_START_MON,monPicker.getValue().toString()).build();
        //JsonObject startFri =  Json.createObjectBuilder().add(JSON_START_FRI,monPicker.getValue().toString()).build();
        //schedule.add(startMon);
        //schedule.add(startFri);
        JsonArrayBuilder scheduleItems = Json.createArrayBuilder();
        Iterator<SchedItem> schedIterator = csgData.schedIterator();
        while (schedIterator.hasNext()) {
            SchedItem sched = schedIterator.next();
	    JsonObject schedItem = Json.createObjectBuilder()
                    .add(JSON_SCHED_TYPE,sched.getType())
                    .add(JSON_SCHED_DATE,sched.getDate())
                    .add(JSON_SCHED_TOPIC,sched.getTopic())
                    .add(JSON_SCHED_TITLE,sched.getTitle())
                    .add(JSON_SCHED_LINK,sched.getTitle())
                    .build();
            scheduleItems.add(schedItem);
	}
        JsonObject optionsJSO = Json.createObjectBuilder()
                .add(JSON_BANNER, selections.build())
                .add(JSON_PAGES, pageChecks.build())
                .add(JSON_STYLE, style.build())
                .add(JSON_INSTR, instrBox.build())
                .add(JSON_SYLLABUSPG,syllabusPG.build())
                .add(JSON_LECTURE, lecture)
                .add(JSON_LAB, lab)
                .add(JSON_RECITATION, recitation)
                .add(JSON_UNDERGRAD_TAS, undergradTAsArray)
                .add(JSON_GRAD_TAS,gradTAsArray)
                .add(JSON_START_HOUR, "" + csgData.getStartHour())
		.add(JSON_END_HOUR, "" + csgData.getEndHour())
                .add(JSON_OFFICE_HOURS, ohArray)
                .add(JSON_SCHEDULE, schedule.build())
                .add(JSON_SCHED_TABLE,scheduleItems.build())
		.build();
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(optionsJSO);
        jsonWriter.close();
        try {  
            // Writing to a file  
            File file = new File(filePath);  
            file.createNewFile();  
            FileWriter fileWriter = new FileWriter(file);  
            fileWriter.write(sw.toString());  
            fileWriter.flush();  
            fileWriter.close();  

        } catch (IOException e) {  
            System.out.println("Error");  
        }  
    }
    
    // IMPORTING/EXPORTING DATA IS USED WHEN WE READ/WRITE DATA IN AN
    // ADDITIONAL FORMAT USEFUL FOR ANOTHER PURPOSE, LIKE ANOTHER APPLICATION

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void loadOptions(PropertiesManager props){
        try{
            JsonObject json = loadJSONFile("./options/csgOptions.json");
            JsonArray yearArray = json.getJsonArray(JSON_YEAR_OPTION+"_options");
            JsonArray numArray = json.getJsonArray(JSON_NUM_OPTION+"_options");
            JsonArray subjArray = json.getJsonArray(JSON_SUBJ_OPTION+"_options");
            JsonArray semArray = json.getJsonArray(JSON_SEM_OPTION+"_options");
            JsonArray cssArray = json.getJsonArray(JSON_CSS_OPTION+"_options");
            for (int i = 0; i < yearArray.size(); i++) {
                boolean exists = false;
                JsonObject jsonYear = yearArray.getJsonObject(i);
                for (int j = 0; j < props.getPropertyOptionsList("YEAR_OPTIONS").size(); j++) {
                    if (props.getPropertyOptionsList("YEAR_OPTIONS").get(j).equals(jsonYear.getString(JSON_YEAR_OPTION))) {
                        exists = true;
                    }
                }
                if (!exists) {
                    props.getPropertyOptionsList("YEAR_OPTIONS").add(jsonYear.getString(JSON_YEAR_OPTION));
                }
            }
            for (int i = 0; i < numArray.size(); i++) {
                boolean exists = false;
                JsonObject jsonNum = numArray.getJsonObject(i);
                for (int j = 0; j < props.getPropertyOptionsList("NUMBER_OPTIONS").size(); j++) {
                    if (props.getPropertyOptionsList("NUMBER_OPTIONS").get(j).equals(jsonNum.getString(JSON_NUM_OPTION))) {
                        exists = true;
                    }
                }
                if (!exists) {
                    props.getPropertyOptionsList("NUMBER_OPTIONS").add(jsonNum.getString(JSON_NUM_OPTION));
                }
            }
            for (int i = 0; i < subjArray.size(); i++) {
                boolean exists = false;
                JsonObject jsonSubj = subjArray.getJsonObject(i);
                for (int j = 0; j < props.getPropertyOptionsList("SUBJECT_OPTIONS").size(); j++) {
                    if (props.getPropertyOptionsList("SUBJECT_OPTIONS").get(j).equals(jsonSubj.getString(JSON_SUBJ_OPTION))) {
                        exists = true;
                    }
                }
                if (!exists) {

                    props.getPropertyOptionsList("SUBJECT_OPTIONS").add(jsonSubj.getString(JSON_SUBJ_OPTION));
                }
            }
            for (int i = 0; i < semArray.size(); i++) {
                boolean exists = false;
                JsonObject jsonSem = semArray.getJsonObject(i);
                for (int j = 0; j < props.getPropertyOptionsList("SEMESTER_OPTIONS").size(); j++) {
                    if (props.getPropertyOptionsList("SEMESTER_OPTIONS").get(j).equals(jsonSem.getString(JSON_SEM_OPTION))) {
                        exists = true;
                    }
                }
                if (!exists) {
                    props.getPropertyOptionsList("SEMESTER_OPTIONS").add(jsonSem.getString(JSON_SEM_OPTION));
                }
            }
            for (int i = 0; i < cssArray.size(); i++) {
                boolean exists = false;
                JsonObject jsonCSS = cssArray.getJsonObject(i);
                for (int j = 0; j < props.getPropertyOptionsList("CSS_OPTIONS").size(); j++) {
                    if (props.getPropertyOptionsList("CSS_OPTIONS").get(j).equals(jsonCSS.getString(JSON_CSS_OPTION))) {
                        exists = true;
                    }
                }
                if (!exists) {
                    props.getPropertyOptionsList("CSS_OPTIONS").add(jsonCSS.getString(JSON_CSS_OPTION));
                }
            }
        }catch(IOException e){
            System.out.println("Error");
        }
    }
    public void saveOptions(){
        JsonArrayBuilder yearOptions = Json.createArrayBuilder();
        JsonArrayBuilder semOptions = Json.createArrayBuilder();
        JsonArrayBuilder subjOptions = Json.createArrayBuilder();
        JsonArrayBuilder numOptions = Json.createArrayBuilder();
        JsonArrayBuilder cssOptions = Json.createArrayBuilder();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        for (int i = 0; i < props.getPropertyOptionsList("YEAR_OPTIONS").size(); i++) {
            JsonObject year = Json.createObjectBuilder()
                    .add(JSON_YEAR_OPTION,props.getPropertyOptionsList("YEAR_OPTIONS").get(i)).build();
            yearOptions.add(year);
        }
        for (int i = 0; i < props.getPropertyOptionsList("SUBJECT_OPTIONS").size(); i++) {
            JsonObject subject = Json.createObjectBuilder()
                    .add(JSON_SUBJ_OPTION,props.getPropertyOptionsList("SUBJECT_OPTIONS").get(i)).build();
            subjOptions.add(subject);
        }
        for (int i = 0; i < props.getPropertyOptionsList("SEMESTER_OPTIONS").size(); i++) {
            JsonObject sem = Json.createObjectBuilder()
                    .add(JSON_SEM_OPTION,props.getPropertyOptionsList("SEMESTER_OPTIONS").get(i)).build();
            semOptions.add(sem);
        }
        for (int i = 0; i < props.getPropertyOptionsList("NUMBER_OPTIONS").size(); i++) {
            JsonObject num = Json.createObjectBuilder()
                    .add(JSON_NUM_OPTION,props.getPropertyOptionsList("NUMBER_OPTIONS").get(i)).build();
            numOptions.add(num);
        }
        for (int i = 0; i < props.getPropertyOptionsList("CSS_OPTIONS").size(); i++) {
            JsonObject css = Json.createObjectBuilder()
                    .add(JSON_CSS_OPTION,props.getPropertyOptionsList("CSS_OPTIONS").get(i)).build();
            cssOptions.add(css);
        }
        JsonObject optionsJSO = Json.createObjectBuilder()
		.add(JSON_YEAR_OPTION+"_options",yearOptions)
                .add(JSON_NUM_OPTION+"_options",numOptions)
                .add(JSON_SUBJ_OPTION+"_options",subjOptions)
                .add(JSON_SEM_OPTION+"_options",semOptions)
                .add(JSON_CSS_OPTION+"_options",cssOptions)
		.build();
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = writerFactory.createWriter(sw);
            jsonWriter.writeObject(optionsJSO);
            jsonWriter.close();
            
        try {  
            // Writing to a file  
            File file = new File("./options/csgOptions.json");  
            file.createNewFile();  
            FileWriter fileWriter = new FileWriter(file);  
            fileWriter.write(sw.toString());  
            fileWriter.flush();  
            fileWriter.close();  

        } catch (IOException e) {  
            System.out.println("Error");  
        }  
    }
}