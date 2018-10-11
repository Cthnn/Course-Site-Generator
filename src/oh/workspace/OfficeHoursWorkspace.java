package oh.workspace;

import djf.components.AppWorkspaceComponent;
import djf.modules.AppFoolproofModule;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import djf.ui.foolproof.ClipboardFoolproofDesign;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import static javax.management.Query.value;
import properties_manager.PropertiesManager;
import oh.OfficeHoursApp;
import oh.OfficeHoursPropertyType;
import static oh.OfficeHoursPropertyType.*;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.data.TimeSlot.DayOfWeek;
import oh.transactions.AddTA_OHTransaction;
import oh.transactions.EditTA_Transaction;
import oh.workspace.controllers.OfficeHoursController;
import oh.workspace.foolproof.OfficeHoursFoolproofDesign;
import static oh.workspace.style.OHStyle.*;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursWorkspace extends AppWorkspaceComponent {
    private TeachingAssistantPrototype copiedTA;
    Boolean goodName = false;
    Boolean goodEmail = false;
    Boolean goodNameTF = false;
    Boolean goodEmailTF = false;
    Button globalTAButton;
    String currentType;
    public OfficeHoursWorkspace(OfficeHoursApp app) {
        super(app);

        // LAYOUT THE APP
        initLayout();

        // INIT THE EVENT HANDLERS
        initControllers();

        // SETUP FOOLPROOF DESIGN FOR THIS APP
        initFoolproofDesign();
    }

    // THIS HELPER METHOD INITIALIZES ALL THE CONTROLS IN THE WORKSPACE
    private void initLayout() {
        // FIRST LOAD THE FONT FAMILIES FOR THE COMBO BOX
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // THIS WILL BUILD ALL OF OUR JavaFX COMPONENTS FOR US
        AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();

        // INIT THE HEADER ON THE LEFT
        VBox leftPane = ohBuilder.buildVBox(OH_LEFT_PANE, null, CLASS_OH_PANE, ENABLED);
        HBox tasHeaderBox = ohBuilder.buildHBox(OH_TAS_HEADER_PANE, leftPane, CLASS_OH_BOX, ENABLED);
        ohBuilder.buildLabel(OfficeHoursPropertyType.OH_TAS_HEADER_LABEL, tasHeaderBox, CLASS_OH_HEADER_LABEL, ENABLED);
        RadioButton all = ohBuilder.buildRadioButton(OH_TAS_RADIO_BUTTON_ALL, tasHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED);
        RadioButton undergrad = ohBuilder.buildRadioButton(OH_TAS_RADIO_BUTTON_UG, tasHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED);
        RadioButton grad = ohBuilder.buildRadioButton(OH_TAS_RADIO_BUTTON_G, tasHeaderBox, CLASS_OH_RADIO_BUTTON, ENABLED);
        HBox radioButtons = new HBox();
        tasHeaderBox.setSpacing(150);
        radioButtons.setSpacing(15);
        radioButtons.getChildren().addAll(all,undergrad,grad);
        tasHeaderBox.getChildren().addAll(radioButtons);

        // MAKE THE TABLE AND SETUP THE DATA MODEL
        TableView<TeachingAssistantPrototype> taTable = ohBuilder.buildTableView(OH_TAS_TABLE_VIEW, leftPane, CLASS_OH_TABLE_VIEW, ENABLED);
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn nameColumn = ohBuilder.buildTableColumn(OH_NAME_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        nameColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0/4.0));
        TableColumn emailColumn = ohBuilder.buildTableColumn(OH_EMAIL_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        emailColumn.setCellValueFactory(new PropertyValueFactory<String, String>("email"));
        emailColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.5/4.0));
        TableColumn hoursColumn = ohBuilder.buildTableColumn(OH_SLOTS_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        hoursColumn.setCellValueFactory(new PropertyValueFactory<String, Integer>("timeSlot"));
        hoursColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0/4.0));
        TableColumn typeColumn = ohBuilder.buildTableColumn(OH_TYPE_TABLE_COLUMN, taTable, CLASS_OH_COLUMN);
        typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
        typeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0/4.0));
        // ADD BOX FOR ADDING A TA
        HBox taBox = ohBuilder.buildHBox(OH_ADD_TA_PANE, leftPane, CLASS_OH_PANE, ENABLED);
        HBox taEmailBox = ohBuilder.buildHBox(OH_ADD_TA_EMAIL_PANE, leftPane, CLASS_OH_PANE, ENABLED);
        TextField name = ohBuilder.buildTextField(OH_NAME_TEXT_FIELD, taBox, CLASS_OH_TEXT_FIELD, ENABLED);
        TextField email = ohBuilder.buildTextField(OH_EMAIL_TEXT_FIELD, taEmailBox, CLASS_OH_TEXT_FIELD, ENABLED);
        Button addTAButton = ohBuilder.buildTextButton(OH_ADD_TA_BUTTON, taBox, CLASS_OH_BUTTON, ENABLED);
        addTAButton.setDisable(true);
        
        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
        VBox.setVgrow(taTable, Priority.ALWAYS);

        // INIT THE HEADER ON THE RIGHT
        VBox rightPane = ohBuilder.buildVBox(OH_RIGHT_PANE, null, CLASS_OH_PANE, ENABLED);
        HBox officeHoursHeaderBox = ohBuilder.buildHBox(OH_OFFICE_HOURS_HEADER_PANE, rightPane, CLASS_OH_PANE, ENABLED);
        ohBuilder.buildLabel(OH_OFFICE_HOURS_HEADER_LABEL, officeHoursHeaderBox, CLASS_OH_HEADER_LABEL, ENABLED);

        // SETUP THE OFFICE HOURS TABLE
        TableView<TimeSlot> officeHoursTable = ohBuilder.buildTableView(OH_OFFICE_HOURS_TABLE_VIEW, rightPane, CLASS_OH_OFFICE_HOURS_TABLE_VIEW, ENABLED);
        TableColumn startTimeColumn = ohBuilder.buildTableColumn(OH_START_TIME_TABLE_COLUMN, officeHoursTable, CLASS_OH_TIME_COLUMN);
        TableColumn endTimeColumn = ohBuilder.buildTableColumn(OH_END_TIME_TABLE_COLUMN, officeHoursTable, CLASS_OH_TIME_COLUMN);
        TableColumn mondayColumn = ohBuilder.buildTableColumn(OH_MONDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN);
        TableColumn tuesdayColumn = ohBuilder.buildTableColumn(OH_TUESDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN);
        TableColumn wednesdayColumn = ohBuilder.buildTableColumn(OH_WEDNESDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN);
        TableColumn thursdayColumn = ohBuilder.buildTableColumn(OH_THURSDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN);
        TableColumn fridayColumn = ohBuilder.buildTableColumn(OH_FRIDAY_TABLE_COLUMN, officeHoursTable, CLASS_OH_DAY_OF_WEEK_COLUMN);
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("endTime"));
        mondayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("monday"));
        tuesdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("tuesday"));
        wednesdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("wednesday"));
        thursdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("thursday"));
        fridayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("friday"));
        for (int i = 0; i < officeHoursTable.getColumns().size(); i++) {
            ((TableColumn)officeHoursTable.getColumns().get(i)).prefWidthProperty().bind(officeHoursTable.widthProperty().multiply(1.0/7.0));
        }

        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
        VBox.setVgrow(officeHoursTable, Priority.ALWAYS);

        // BOTH PANES WILL NOW GO IN A SPLIT PANE
        SplitPane sPane = new SplitPane(leftPane, rightPane);
        sPane.setDividerPositions(.4);
        workspace = new BorderPane();

        // AND PUT EVERYTHING IN THE WORKSPACE
        ((BorderPane)workspace).setCenter(sPane);
        all.setSelected(true);
    }

    private void initControllers() {
        OfficeHoursController controller = new OfficeHoursController((OfficeHoursApp) app);
        AppGUIModule gui = app.getGUIModule();
        OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
        // DON'T LET ANYONE SORT THE TABLES
        TableView officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
            ((TableColumn)officeHoursTableView.getColumns().get(i)).setSortable(false);
        }
        TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
        for (int i = 0; i < taTableView.getColumns().size(); i++) {
            ((TableColumn)taTableView.getColumns().get(i)).setSortable(false);
        }
        ((Button) gui.getGUINode(OH_ADD_TA_BUTTON)).setOnAction(e -> {
            controller.processAddTA();
        });
        RadioButton all = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
        RadioButton undergradButton = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
        RadioButton gradButton = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
        all.setUserData("All");
        undergradButton.setUserData("Undergraduate");
        gradButton.setUserData("Graduate");
        ToggleGroup typeGroup = new ToggleGroup();
        gradButton.setToggleGroup(typeGroup);
        undergradButton.setToggleGroup(typeGroup);
        all.setToggleGroup(typeGroup);
        TextField nameField = (TextField) gui.getGUINode(OH_NAME_TEXT_FIELD);
        TextField emailField = (TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD);
        globalTAButton = (Button) gui.getGUINode(OH_ADD_TA_BUTTON);
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
                OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
                goodName = true;
                for(int i = 0; i < data.getAll().size();i++){
                    if(nameField.getText().equals(data.getAll().get(i).getName())){
                        nameField.setStyle("-fx-text-fill: red");
                        goodName = false;
                        break;
                    }else{
                        nameField.setStyle("-fx-text-fill: black");
                        goodName = true;
                    }
                }
                if (goodName && goodEmail && !all.isSelected()) {
                    globalTAButton.setDisable(false);
                }else{
                    globalTAButton.setDisable(true);
                }
                
            }
        });
        emailField.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
                if (!OfficeHoursController.validate(emailField.getText())) {
                    emailField.setStyle("-fx-text-fill: red");
                    goodEmail = false;
                }else{
                    emailField.setStyle("-fx-text-fill: black");
                    goodEmail = true;
                }
                for(int i = 0; i < data.getAll().size(); i++){
                    if(emailField.getText().equals(data.getAll().get(i).getEmail()) || !OfficeHoursController.validate(emailField.getText())){
                        emailField.setStyle("-fx-text-fill: red");
                        goodEmail = false;
                        break;
                    }else{
                        emailField.setStyle("-fx-text-fill: black");
                        goodEmail = true;
                    }
                }
                if (goodEmail && goodName && !all.isSelected()) {
                    globalTAButton.setDisable(false);
                }else{
                    globalTAButton.setDisable(true);
                }
                
            }
        });
        //CHECK FOR RADIO BUTTON CHANGES
        typeGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (typeGroup.getSelectedToggle() != null) {
                controller.setCurrent(typeGroup.getSelectedToggle().getUserData().toString());
            }
            if (typeGroup.getSelectedToggle().getUserData().toString().equalsIgnoreCase("all") || typeGroup.getSelectedToggle().getUserData().toString().equalsIgnoreCase("undergraduate")
                || typeGroup.getSelectedToggle().getUserData().toString().equalsIgnoreCase("")){
                controller.setType("Undergraduate");
            }else if(typeGroup.getSelectedToggle().getUserData().toString().equalsIgnoreCase("Graduate")){
                controller.setType("Graduate");
            }
            currentType = typeGroup.getSelectedToggle().getUserData().toString();
            if (goodEmail && goodName && !all.isSelected()) {
                globalTAButton.setDisable(false);
            }else{
                globalTAButton.setDisable(true);
            }
        });
        ObservableList taObsList = taTableView.getSelectionModel().getSelectedCells();
        taObsList.addListener(new ListChangeListener(){
            public void onChanged(Change c){
                TablePosition tablePosition = (TablePosition) taObsList.get(0);
                Object nameVal = ((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow());
                Object emailVal = ((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow());
                Object typeVal =  ((TableColumn)taTableView.getColumns().get(3)).getCellData(tablePosition.getRow());
                copiedTA = new TeachingAssistantPrototype(nameVal.toString(),emailVal.toString(),typeVal.toString());
            }
        });
        ((TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW)).getSelectionModel().setCellSelectionEnabled(true);
        //EVENT HANDLING OF TA's COLUMN(OBTAINING THE TA TO PLACE INTO THE OFFICE HOURS)
        taTableView.setOnMouseClicked(e->{
            BorderPane root = new BorderPane();
            Stage editWindow = new Stage();
            ClipboardFoolproofDesign clipboardFPD = new ClipboardFoolproofDesign(app);
            clipboardFPD.updateControls();
            ObservableList taObservableList = taTableView.getSelectionModel().getSelectedCells();
            ObservableList ohObsList = officeHoursTableView.getColumns();
            officeHoursTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
            officeHoursTableView.getSelectionModel().selectionModeProperty().set(SelectionMode.MULTIPLE);
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            TeachingAssistantPrototype currentTa = (TeachingAssistantPrototype)taTableView.getSelectionModel().getSelectedItem();
            Object nameVal = ((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow());
            Object emailVal = ((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow());
            Object typeVal = ((TableColumn)taTableView.getColumns().get(3)).getCellData(tablePosition.getRow());
            for (int i = 2; i < 7; i++) {
                TableColumn tempCol = (TableColumn)officeHoursTableView.getColumns().get(i);
                tempCol.setCellFactory(new Callback<TableColumn<String,String>,TableCell<String,String>>(){
                    @Override
                    public TableCell<String, String> call(TableColumn<String, String> param) {
                        return new TableCell<String,String>(){
                            public void changeColor(final String item, final boolean empty){
                                super.updateItem(item,empty);
                                if (item!= null) {
                                    setText(item);
                                    if (item.contains(currentTa.getName())) {
                                        setStyle("-fx-background-color: yellow; -fx-text-fill: black");
                                    }else{
                                        setStyle(CLASS_OH_COLUMN);
                                    }
                                }else{
                                    setText(null);
                                }
                            }
                        };
                    }
                    
                });
            }
            int row = tablePosition.getRow();
            AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
            String nameStr = "Name";
            String emailStr = "Email";
            String typeStr = "Type: ";
            String save = "Save";
            String close = "Cancel";
            String title = "Edit Teaching Assistant";
            String ug = "Undergraduate";
            String g = "Graduate";
            if(ohBuilder.getLanguageSettings().getCurrentLanguage().equalsIgnoreCase("Spanish")){
                nameStr = "Nombre";
                emailStr = "Email";
                typeStr = "Tipo: ";
                save = "Salvar";
                close = "Cancelar";
                title = "Editar Los Asistentes";
                ug = "Estudiante Universitario";
                g = "Graduado";
                        
            }
            if(e.getClickCount() == 2 && !(nameVal.toString().equals(""))){
                Text titleText = new Text(title);
                titleText.setFont(Font.font ("Verdana", 20));
                root.setTop(titleText);
                GridPane changePane = new GridPane();
                changePane.setHgap(5.5);
                changePane.setVgap(5.5);
                Label name =new Label(nameStr + ": ");
                TextField nameTF = new TextField();
                nameTF.setPromptText(nameStr);
                nameTF.setText(((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow()).toString());
                Label email = new Label(emailStr+ ": ");
                TextField emailTF = new TextField();
                emailTF.setPromptText(emailStr);
                emailTF.setText(((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow()).toString());
                Label type = new Label(typeStr);
                ToggleGroup typeStudent = new ToggleGroup();
                RadioButton undergrad = new RadioButton(ug);
                RadioButton grad = new RadioButton(g);
                undergrad.setToggleGroup(typeStudent);
                grad.setToggleGroup(typeStudent);
                if (typeVal.toString().equalsIgnoreCase("undergraduate")) {
                    undergrad.setSelected(true);
                }else if(typeVal.toString().equalsIgnoreCase("graduate")){
                    grad.setSelected(true);
                }
                changePane.add(name,0,0);
                changePane.add(nameTF,1,0);
                changePane.add(email,0,1);
                changePane.add(emailTF,1,1);
                changePane.add(type,0,2);
                changePane.add(undergrad,1,2);
                changePane.add(grad,2,2);
                changePane.setAlignment(Pos.CENTER);
                root.setLeft(changePane);
                Button edit = new Button(save);
                Button cancel = new Button(close);
                HBox h = new HBox();
                h.getChildren().add(edit);
                h.getChildren().add(cancel);
                h.setAlignment(Pos.TOP_CENTER);
                root.setBottom(h);
                Scene scene = new Scene(root, 400, 400);
                editWindow.setScene(scene);
                editWindow.show();
                goodNameTF= false;
                goodEmailTF= false;
                nameField.setStyle("-fx-text-fill: red");
                emailField.setStyle("-fx-text-fill: red");
                nameTF.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                        TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                        TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
                        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
                        for(int i = 0; i < data.getAll().size();i++){
                            if(nameTF.getText().equals(data.getAll().get(i).getName())){
                                nameTF.setStyle("-fx-text-fill: red");
                                goodNameTF = false;
                                break;
                            }else{
                                nameTF.setStyle("-fx-text-fill: black");
                                goodNameTF = true;
                            }
                        }
                    }
                });
                emailTF.textProperty().addListener(new ChangeListener<String>(){
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                        OfficeHoursData data = (OfficeHoursData)app.getDataComponent();
                        for(int i = 0; i < data.getAll().size(); i++){
                            if(emailTF.getText().equals(data.getAll().get(i).getEmail())||!OfficeHoursController.validate(emailTF.getText())){
                                emailTF.setStyle("-fx-text-fill: red");
                                goodEmailTF = false;
                                break;
                            }else{
                                emailTF.setStyle("-fx-text-fill: black");
                                goodEmailTF = true;
                            }
                        }
                    }
                });
                edit.setOnAction(b -> {
                    String studentType = "";
                    if (undergrad.isSelected()) {
                        studentType = "Undergraduate";
                    }else if(grad.isSelected()){
                        studentType = "Graduate";
                    }
                    String nameText = nameTF.getText();
                    String emailText = emailTF.getText();
                    EditTA_Transaction edited =  new EditTA_Transaction(controller,nameText,emailText,studentType,row,copiedTA,(OfficeHoursApp)app);
                    app.processTransaction(edited);
                    editWindow.close();
                    controller.setCurrent("all");
                    controller.setCurrent(currentType);
                });
                cancel.setOnAction(c ->{
                    editWindow.close();
                });
            }else{
                copiedTA = new TeachingAssistantPrototype(nameVal.toString(),emailVal.toString(),typeVal.toString());
                
            }
        });
        //EVENT HANDLING OF OH COLUMN(MEANT TO ADD TA TO HOURS)
        officeHoursTableView.setOnMouseClicked(e ->{
            ObservableList ohObservableList = officeHoursTableView.getSelectionModel().getSelectedCells();
                TablePosition tablePosition = (TablePosition) ohObservableList.get(0);
                TimeSlot ohTimeSlots = (TimeSlot)officeHoursTableView.getSelectionModel().getSelectedItem();
                int col = tablePosition.getColumn();
                if(col >= 2){
                    AddTA_OHTransaction addToOHTransaction = new AddTA_OHTransaction(col,ohTimeSlots,copiedTA,(OfficeHoursApp)app);
                    app.processTransaction(addToOHTransaction);
                    controller.setCurrent(currentType);
                }
        });
        taTableView.refresh();
        officeHoursTableView.refresh();
    }

    private void initFoolproofDesign(){
        AppGUIModule gui = app.getGUIModule();
        AppFoolproofModule foolproofSettings = app.getFoolproofModule();
        foolproofSettings.registerModeSettings(OH_FOOLPROOF_SETTINGS,
                new OfficeHoursFoolproofDesign((OfficeHoursApp) app));
    }

    @Override
    public void showNewDialog() {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }
    public String getCurrentType(){
        return currentType;
    }
}
