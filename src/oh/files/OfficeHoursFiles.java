package oh.files;

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
import java.math.BigDecimal;
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
import oh.OfficeHoursApp;
import static oh.OfficeHoursPropertyType.OH_OFFICE_HOURS_TABLE_VIEW;
import static oh.OfficeHoursPropertyType.OH_TAS_TABLE_VIEW;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;
import oh.transactions.AddTA_OHTransaction;

/**
 * This class serves as the file component for the TA
 * manager app. It provides all saving and loading 
 * services for the application.
 * 
 * @author Richard McKenna
 */
public class OfficeHoursFiles implements AppFileComponent {
    // THIS IS THE APP ITSELF
    OfficeHoursApp app;
    
    // THESE ARE USED FOR IDENTIFYING JSON TYPES
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

    public OfficeHoursFiles(OfficeHoursApp initApp) {
        app = initApp;
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
	// CLEAR THE OLD DATA OUT
	OfficeHoursData dataManager = (OfficeHoursData)data;
        dataManager.reset();

	// LOAD THE JSON FILE WITH ALL THE DATA
	JsonObject json = loadJSONFile(filePath);

	// LOAD THE START AND END HOURS
	String startHour = json.getString(JSON_START_HOUR);
        String endHour = json.getString(JSON_END_HOUR);
        dataManager.initHours(startHour, endHour);

        // NOW LOAD ALL THE UNDERGRAD TAs
        JsonArray jsonTAArray = json.getJsonArray(JSON_UNDERGRAD_TAS);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String name = jsonTA.getString(JSON_NAME);
            String email = jsonTA.getString(JSON_EMAIL);
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email);
            ta.setType("Undergraduate");
            dataManager.addTA(ta);
        }
        // NOW LOAD ALL THE UNDERGRAD TAs
        jsonTAArray = json.getJsonArray(JSON_GRAD_TAS);
        for (int i = 0; i < jsonTAArray.size(); i++) {
            JsonObject jsonTA = jsonTAArray.getJsonObject(i);
            String name = jsonTA.getString(JSON_NAME);
            String email = jsonTA.getString(JSON_EMAIL);
            TeachingAssistantPrototype ta = new TeachingAssistantPrototype(name,email);
            ta.setType("Graduate");
            dataManager.addTA(ta);
        }
        //LOAD ALL OFFICE HOURS
        JsonArray jsonOHArray = json.getJsonArray(JSON_OFFICE_HOURS);
        AppGUIModule gui = app.getGUIModule();
        TableView ohTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
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
        for (int i = 0; i < dataManager.getTAs().size(); i++) {
            String taName = dataManager.getTAs().get(i).getName();
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
            dataManager.getTAs().get(i).setTimeSlot(count);
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
        TableView ohTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
	// GET THE DATA
	OfficeHoursData dataManager = (OfficeHoursData)data;
	// NOW BUILD THE TA JSON OBJCTS TO SAVE
	JsonArrayBuilder ugTAArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder gradTAArrayBuilder = Json.createArrayBuilder();
	Iterator<TeachingAssistantPrototype> tasIterator = dataManager.teachingAssistantsIterator();
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
        int dif = ((12-dataManager.getStartHour())*2);
        for (int i = 2; i < 7; i++) {
            if(i == 2){
                for (int j = 0; j < ohTimeSlotsArray.length; j++) {
                    if (((TimeSlot)ohTimeSlotsArray[j]).getMonday() != null) {
                        for (int k = 0; k < ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).size(); k++) {
                            if(dataManager.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
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
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"MONDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.MONDAY).get(k).getName()).build();
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
                            if(dataManager.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
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
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"TUESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.TUESDAY).get(k).getName()).build();
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
                            if(dataManager.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
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
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"WEDNESDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.WEDNESDAY).get(k).getName()).build();
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
                            if(dataManager.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
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
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"THURSDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.THURSDAY).get(k).getName()).build();
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
                            if(dataManager.getStartHour() < 12){
                                if (j < dif) {
                                    if(j%2 == 0){
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00am").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30am").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
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
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_00pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
                                        ohArrayBuilder.add(ohJson);
                                    }else{
                                        JsonObject ohJson = Json.createObjectBuilder()
                                            .add(JSON_START_TIME,(dataManager.getStartHour()+(j/2))+"_30pm").add(JSON_DAY_OF_WEEK,"FRIDAY").add(JSON_NAME, ((TimeSlot)ohTimeSlotsArray[j]).getTAs().get(DayOfWeek.FRIDAY).get(k).getName()).build();
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

        
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_START_HOUR, "" + dataManager.getStartHour())
		.add(JSON_END_HOUR, "" + dataManager.getEndHour())
                .add(JSON_UNDERGRAD_TAS, undergradTAsArray)
                .add(JSON_GRAD_TAS,gradTAsArray)
                .add(JSON_OFFICE_HOURS, ohArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
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
}