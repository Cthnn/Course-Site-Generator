package oh.workspace;

import djf.components.AppWorkspaceComponent;
import djf.modules.AppFoolproofModule;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static javax.management.Query.value;
import properties_manager.PropertiesManager;
import oh.OfficeHoursApp;
import oh.OfficeHoursPropertyType;
import static oh.OfficeHoursPropertyType.*;
import oh.clipboard.OfficeHoursClipboard;
import oh.data.OfficeHoursData;
import oh.data.TeachingAssistantPrototype;
import oh.data.TimeSlot;
import oh.transactions.AddTA_OHTransaction;
import oh.transactions.EditTA_Transaction;
import oh.workspace.controllers.OfficeHoursController;
import oh.workspace.foolproof.OfficeHoursFoolproofDesign;
import static oh.workspace.style.OHStyle.*;
import static djf.AppPropertyType.CUT_BUTTON;
import static djf.AppPropertyType.COPY_BUTTON;
import static djf.AppPropertyType.PASTE_BUTTON;
import static djf.AppPropertyType.HAS_CLIPBOARD_TOOLBAR;

/**
 *
 * @author McKillaGorilla
 */
public class OfficeHoursWorkspace extends AppWorkspaceComponent {
    private TeachingAssistantPrototype copiedTA;
    private boolean goodName = true;
    private boolean goodEmail = true;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
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
    }

    private void initControllers() {
        OfficeHoursController controller = new OfficeHoursController((OfficeHoursApp) app);
        AppGUIModule gui = app.getGUIModule();
        // DON'T LET ANYONE SORT THE TABLES
        TableView officeHoursTableView = (TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW);
        for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
            ((TableColumn)officeHoursTableView.getColumns().get(i)).setSortable(false);
        }
        TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
        for (int i = 0; i < taTableView.getColumns().size(); i++) {
            ((TableColumn)taTableView.getColumns().get(i)).setSortable(false);
        }
        ((TextField) gui.getGUINode(OH_NAME_TEXT_FIELD)).setOnAction(e -> {
            controller.processAddTA();
        });
        ((TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD)).setOnAction(e -> {
            controller.processAddTA();
        });
        ((Button) gui.getGUINode(OH_ADD_TA_BUTTON)).setOnAction(e -> {
            controller.processAddTA();
        });
        //CHECK FOR RADIO BUTTON CHANGES
        RadioButton all = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_ALL);
        RadioButton undergrad = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_UG);
        RadioButton grad = (RadioButton) gui.getGUINode(OH_TAS_RADIO_BUTTON_G);
        all.setUserData("all");
        undergrad.setUserData("undergraduate");
        grad.setUserData("graduate");
        ToggleGroup typeGroup = new ToggleGroup();
        grad.setToggleGroup(typeGroup);
        undergrad.setToggleGroup(typeGroup);
        all.setToggleGroup(typeGroup);
        typeGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (typeGroup.getSelectedToggle() != null) {
                controller.setCurrent(typeGroup.getSelectedToggle().getUserData().toString());
            }    
        });
        //CHECK NAME AND EMAIL TEXT FIELDS FOR ERRONEOUS INPUT
        TextField nameField = (TextField) gui.getGUINode(OH_NAME_TEXT_FIELD);
        TextField emailField = (TextField) gui.getGUINode(OH_EMAIL_TEXT_FIELD);
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
                for(Object o: taTableView.getItems()){
                    if(nameField.getText().equals((String)nameCol.getCellData(o))){
                        nameField.setStyle("-fx-text-fill: red");
                        goodName = false;
                        break;
                    }else{
                        nameField.setStyle("-fx-text-fill: black");
                        goodName = true;
                    }
                }
            }
        });
        emailField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TableView taTableView = (TableView) gui.getGUINode(OH_TAS_TABLE_VIEW);
                TableColumn emailCol = (TableColumn)taTableView.getColumns().get(1);
                if(!OfficeHoursController.validate(emailField.getText())){
                    emailField.setStyle("-fx-text-fill: red");
                    goodEmail = false;
                }else{
                    for(Object o: taTableView.getItems()){
                        if(emailField.getText().equals((String)emailCol.getCellData(o))){
                            emailField.setStyle("-fx-text-fill: red");
                            goodEmail = false;
                            break;
                        }else{
                            emailField.setStyle("-fx-text-fill: black");
                            goodEmail = true;
                        }
                    }  
                }
            }
        });
        Button addTAButton = (Button) gui.getGUINode(OH_ADD_TA_BUTTON);
        SimpleBooleanProperty nameBool = new SimpleBooleanProperty(!goodName);
        SimpleBooleanProperty emailBool = new SimpleBooleanProperty(!goodEmail);
        BooleanBinding textFieldBind = nameField.textProperty().isEmpty().or(emailField.textProperty().isEmpty()).or(nameBool).or(emailBool);
        addTAButton.disableProperty().bind(textFieldBind);
        //EVENT HANDLING OF TA's COLUMN(OBTAINING THE TA TO PLACE INTO THE OFFICE HOURS)
        taTableView.setOnMouseClicked(e->{
            BorderPane root = new BorderPane();
            Stage editWindow = new Stage();
            ObservableList taObservableList = taTableView.getSelectionModel().getSelectedCells();
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            int row = tablePosition.getRow();
            Object nameVal = ((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow());
            Object emailVal = ((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow());
            if(e.getClickCount() == 2 && !(nameVal.toString().equals(""))){
                Text titleText = new Text("Edit Teaching AscreateBooleanBinding(() -> !getGoodName(), \n" +
"        nameField.textProperty()))sistant");
                titleText.setFont(Font.font ("Verdana", 20));
                root.setTop(titleText);
                GridPane changePane = new GridPane();
                changePane.setHgap(5.5);
                changePane.setVgap(5.5);
                Label name =new Label("Name:");
                TextField nameTF = new TextField();
                nameTF.setText(((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow()).toString());
                Label email = new Label("Email:");
                TextField emailTF = new TextField();
                emailTF.setText(((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow()).toString());
                Label type = new Label("Type:");
                ToggleGroup typeStudent = new ToggleGroup();
                RadioButton undergradDialog = new RadioButton("Undergraduate");
                RadioButton gradDialog = new RadioButton("Graduate");
                undergradDialog.setToggleGroup(typeStudent);
                gradDialog.setToggleGroup(typeStudent);
                undergradDialog.setSelected(true);
                changePane.add(name,0,0);
                changePane.add(nameTF,1,0);
                changePane.add(email,0,1);
                changePane.add(emailTF,1,1);
                changePane.add(type,0,2);
                changePane.add(undergradDialog,1,2);
                changePane.add(gradDialog,2,2);
                changePane.setAlignment(Pos.CENTER);
                root.setLeft(changePane);
                Button edit = new Button("Save");
                Button cancel = new Button("Cancel");
                HBox h = new HBox();
                h.getChildren().add(edit);
                h.getChildren().add(cancel);
                h.setAlignment(Pos.TOP_CENTER);
                root.setBottom(h);
                Scene scene = new Scene(root, 400, 400);
                editWindow.setScene(scene);
                editWindow.show();
                
                edit.setOnAction(b -> {
                    String studentType = "";
                    if (undergradDialog.isSelected()) {
                        studentType = "Undergraduate";
                    }else if(gradDialog.isSelected()){
                        studentType = "Graduate";
                    }
                    String nameText = nameTF.getText();
                    String emailText = emailTF.getText();
                    EditTA_Transaction edited =  new EditTA_Transaction(controller,nameText,emailText,studentType,row);
                    app.processTransaction(edited);
                    editWindow.close();
                });
                cancel.setOnAction(c ->{
                    editWindow.close();
                });
            }else{
                copiedTA = new TeachingAssistantPrototype(nameVal.toString(),emailVal.toString());
                
            }
        });
        //EVENT HANDLING OF OH COLUMN(MEANT TO ADD TA TO HOURS)
        ((TableView) gui.getGUINode(OH_OFFICE_HOURS_TABLE_VIEW)).getSelectionModel().setCellSelectionEnabled(true);
        officeHoursTableView.setOnMouseClicked(e ->{
            ObservableList ohObservableList = officeHoursTableView.getSelectionModel().getSelectedCells();
                TablePosition tablePosition = (TablePosition) ohObservableList.get(0);
                TimeSlot ohTimeSlots = (TimeSlot)officeHoursTableView.getSelectionModel().getSelectedItem();
                int col = tablePosition.getColumn();
                if(col >= 2){
                    OfficeHoursData data = (OfficeHoursData) app.getDataComponent();
                    AddTA_OHTransaction addToOHTransaction = new AddTA_OHTransaction(col,ohTimeSlots,copiedTA,data);
                    app.processTransaction(addToOHTransaction);
                }
        });
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
}
