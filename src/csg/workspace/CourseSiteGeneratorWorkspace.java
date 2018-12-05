package csg.workspace;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
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
import csg.CourseSiteGeneratorApp;
import csg.CourseSiteGeneratorPropertyType;
import static csg.CourseSiteGeneratorPropertyType.*;
import csg.data.CourseSiteGeneratorData;
import csg.data.Lab_Recitation;
import csg.data.Lecture;
import csg.data.SchedItem;
import csg.data.TeachingAssistantPrototype;
import csg.data.TimeSlot;
import csg.files.CourseSiteGeneratorFiles;
import csg.transactions.AddLab_Rec_Transaction;
import csg.transactions.AddLecture_Transaction;
import csg.transactions.AddSchedItem_Transaction;
import csg.transactions.AddTA_OHTransaction;
import csg.transactions.EditSchedItem_Transaction;
import csg.transactions.EditTA_Transaction;
import csg.transactions.RemoveTA_Transaction;
import csg.workspace.controllers.CourseSiteGeneratorController;
import csg.workspace.foolproof.CourseSiteGeneratorFoolproofDesign;
import static csg.workspace.style.OHStyle.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 *
 * @author McKillaGorilla
 */
public class CourseSiteGeneratorWorkspace extends AppWorkspaceComponent {
    private TeachingAssistantPrototype copiedTA;
    private Lecture currentLect;
    private Lab_Recitation currentLab;
    private Lab_Recitation currentRec;
    private boolean isUpdate = false;
    Boolean goodName = false;
    Boolean goodEmail = false;
    Boolean goodNameTF = false;
    Boolean goodEmailTF = false;
    Button globalTAButton;
    String currentType = "all";
    public static String faviconFP = "";
    public static String lfootFP = "";
    public static String rfootFP = "";
    public static String navbarFP = "";
    
    
    public CourseSiteGeneratorWorkspace(CourseSiteGeneratorApp app) {
        super(app);

        // LAYOUT THE APP
        initLayout();

        // INIT THE EVENT HANDLERS
        initControllers();

        // SETUP FOOLPROOF DESIGN FOR THIS APP
        initFoolproofDesign();
    }

    // THIS HELPER METHOD INITIALIZES ALL THE CONTROLS IN THE WORKSPACE
    private void initLayout(){
        // FIRST LOAD THE FONT FAMILIES FOR THE COMBO BOX
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        props.addPropertyOptionsList("SUBJECT_OPTIONS", new ArrayList<String>());
        props.addPropertyOptionsList("YEAR_OPTIONS", new ArrayList<String>());
        props.getPropertyOptionsList("YEAR_OPTIONS").add(""+Calendar.getInstance().get(Calendar.YEAR));
        props.getPropertyOptionsList("YEAR_OPTIONS").add(""+(Calendar.getInstance().get(Calendar.YEAR)+1));
        props.addPropertyOptionsList("SEMESTER_OPTIONS", new ArrayList<String>());
        props.addPropertyOptionsList("NUMBER_OPTIONS", new ArrayList<String>());
        props.addPropertyOptionsList("CSS_OPTIONS", new ArrayList<String>());
        // THIS WILL BUILD ALL OF OUR JavaFX COMPONENTS FOR US
        AppNodesBuilder csgBuilder = app.getGUIModule().getNodesBuilder();
        CourseSiteGeneratorFiles file = new CourseSiteGeneratorFiles((CourseSiteGeneratorApp) app);
        file.loadOptions(props);
        VBox displayPane = csgBuilder.buildVBox(CSG_TABS_PANE, null, CLASS_CSG_BOX, ENABLED);
       
        TabPane siteTabs = new TabPane();
        siteTabs.setStyle("-fx-background-color: #a7f464");
        siteTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Tab siteTab = new Tab("Site");
        Tab syllabusTab = new Tab("Syllabus");
        Tab mtTab = new Tab("Meeting Times");
        Tab ohTab = new Tab("Office Hours");
        Tab schedTab = new Tab("Schedule");
        siteTabs.getTabs().addAll(siteTab,syllabusTab,mtTab,ohTab,schedTab);
        //Create elements in Site tab
        ArrayList<String> startTimeList = new ArrayList<String>();
        ArrayList<String> endTimeList = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            String time = "";
            if (i < 12 && i!=0) {
                time = i+":00am";
            }else if(i == 12){
                time = i+":00pm";
            }else if(i == 0){
                time = 12+":00am";
            }
            else{
                time = (i-12)+":00pm";
            }
            if(i<13){
                startTimeList.add(time);
                endTimeList.add(time);
            }else{
                endTimeList.add(time);
            }
            
        }
        props.addPropertyOptionsList("START_TIME_OPTIONS",startTimeList);
        props.addPropertyOptionsList("END_TIME_OPTIONS", endTimeList);
        ScrollPane scrollSite = new ScrollPane();
        VBox sitePane = csgBuilder.buildVBox(CSG_SITE_PANE,null,CLASS_CSG_BOX,ENABLED);
        sitePane.setPadding((new Insets(10, 15, 10, 15)));
        sitePane.setSpacing(15);
        VBox bannerBox = csgBuilder.buildVBox(CSG_BANNER_BOX,sitePane,CLASS_CSG_BOX,ENABLED);
        bannerBox.setPadding((new Insets(10, 15, 10, 15)));
        bannerBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        csgBuilder.buildLabel(CSG_BANNER_HEADER_LABEL, bannerBox, CLASS_CSG_HEADER_LABEL, ENABLED);
        HBox subjAndNum = csgBuilder.buildHBox(CSG_DROPDOWN_ONE_BOX,bannerBox,CLASS_CSG_BOX,ENABLED);
        subjAndNum.setPadding(new Insets(10, 15, 10, 15));
        subjAndNum.setSpacing(15);
        csgBuilder.buildLabel(CSG_SUBJECT_LABEL, subjAndNum, CLASS_CSG_LABEL, ENABLED);
        ComboBox subjDropDown = csgBuilder.buildComboBox(CSG_SUBJECT_CB,"SUBJECT_OPTIONS", "CSE", subjAndNum, CLASS_CSG_BUTTON, ENABLED);
        subjDropDown.getSelectionModel().select("CSE");
        csgBuilder.buildLabel(CSG_NUM_LABEL, subjAndNum, CLASS_CSG_LABEL, ENABLED);
        ComboBox numDropDown = csgBuilder.buildComboBox(CSG_NUMBER_CB,"NUMBER_OPTIONS", "219", subjAndNum, CLASS_CSG_BUTTON, ENABLED);
        numDropDown.getSelectionModel().select("219");
        HBox semYear = csgBuilder.buildHBox(CSG_DROPDOWN_TWO_BOX,bannerBox,CLASS_CSG_BUTTON,ENABLED);
        semYear.setPadding(new Insets(10, 15, 10, 15));
        semYear.setSpacing(15);
        csgBuilder.buildLabel(CSG_SEM_LABEL, semYear, CLASS_CSG_LABEL, ENABLED);
        ComboBox semDropDown = csgBuilder.buildComboBox(CSG_SEM_CB, "SEMESTER_OPTIONS", "Fall", semYear, CLASS_CSG_BUTTON, ENABLED);
        semDropDown.getSelectionModel().select("Fall");
        csgBuilder.buildLabel(CSG_YEAR_LABEL, semYear, CLASS_CSG_LABEL,ENABLED);
        ComboBox yearDropDown = csgBuilder.buildComboBox(CSG_YEAR_CB,"YEAR_OPTIONS", "2018", semYear, CLASS_CSG_BUTTON, ENABLED);
        for (int i = 0; i < yearDropDown.getItems().size(); i++) {
            if(yearDropDown.getItems().get(i).equals(""+Calendar.getInstance().get(Calendar.YEAR))){
                yearDropDown.getSelectionModel().select(i);
            }
        }
        HBox titleBox = csgBuilder.buildHBox(CSG_TITLE_BOX,bannerBox,CLASS_CSG_BOX,ENABLED);
        titleBox.setPadding(new Insets(10, 15, 10, 15));
        titleBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_TITLE_LABEL, titleBox, CLASS_CSG_LABEL,ENABLED);
        TextField titleTF = csgBuilder.buildTextField(CSG_TITLE_TEXT_FIELD, titleBox, CLASS_CSG_TEXT_FIELD, ENABLED);
        HBox exportDirBox = csgBuilder.buildHBox(CSG_DIR_BOX,bannerBox,CLASS_CSG_BOX,ENABLED);
        exportDirBox.setPadding(new Insets(10,15,10,15));
        exportDirBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_DIR_LABEL, exportDirBox, CLASS_CSG_LABEL,ENABLED);
        Label dirText = new Label(".\\export\\"+subjDropDown.getSelectionModel().getSelectedItem().toString()+"_"+
            numDropDown.getSelectionModel().getSelectedItem().toString()+"_"+semDropDown.getSelectionModel().getSelectedItem().toString()+"_"+
            yearDropDown.getSelectionModel().getSelectedItem().toString()+"\\public_html");
        exportDirBox.getChildren().add(dirText);
        HBox pagesBox = csgBuilder.buildHBox(CSG_PAGES_BOX,sitePane,CLASS_CSG_BOX,ENABLED);
        pagesBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        pagesBox.setPadding(new Insets(10, 15, 10, 15));
        pagesBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_PAGES_LABEL, pagesBox, CLASS_CSG_HEADER_LABEL, ENABLED);
        csgBuilder.buildCheckBox(CSG_HOME_PAGE_BUTTON, pagesBox, CLASS_CSG_BUTTON, ENABLED);
        csgBuilder.buildCheckBox(CSG_SYLLABUS_PAGE_BUTTON, pagesBox, CLASS_CSG_BUTTON, ENABLED);
        csgBuilder.buildCheckBox(CSG_SCHEDULE_PAGE_BUTTON, pagesBox, CLASS_CSG_BUTTON, ENABLED);
        csgBuilder.buildCheckBox(CSG_HW_PAGE_BUTTON, pagesBox, CLASS_CSG_BUTTON, ENABLED);
        
        VBox styleBox = csgBuilder.buildVBox(CSG_STYLE_BOX,sitePane,CLASS_CSG_BOX,ENABLED);
        styleBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        styleBox.setPadding(new Insets(10, 15, 10, 15));
        styleBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_STYLE_LABEL,styleBox,CLASS_CSG_HEADER_LABEL,ENABLED);
        HBox faviconBox = csgBuilder.buildHBox(CSG_FAVICON_BOX,styleBox,CLASS_CSG_BOX,ENABLED);
        faviconBox.setSpacing(15);
        faviconBox.setAlignment(Pos.BOTTOM_LEFT);
        csgBuilder.buildTextButton(CSG_FAVICON_BUTTON, faviconBox, CLASS_CSG_BUTTON, ENABLED);
        try{
        Image image = new Image(new FileInputStream("../CourseSiteGenerator/images/SBUCSLogo.png"));
        faviconFP = ("../CourseSiteGenerator/images/SBUCSLogo.png");
        ImageView favIV = new ImageView();
        favIV.setImage(image);
        faviconBox.getChildren().add(favIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        HBox navbarBox = csgBuilder.buildHBox(CSG_NAVBAR_BOX,styleBox,CLASS_CSG_BOX,ENABLED);
        navbarBox.setSpacing(15);
        navbarBox.setAlignment(Pos.BOTTOM_LEFT);
        csgBuilder.buildTextButton(CSG_NAVBAR_BUTTON, navbarBox, CLASS_CSG_BUTTON, ENABLED);
        try{
        Image navimage = new Image(new FileInputStream("../CourseSiteGenerator/images/SBUWhiteShieldLogo.jpg"));
        navbarFP = ("../CourseSiteGenerator/images/SBUWhiteShieldLogo.jpg");
        ImageView navIV = new ImageView();
        navIV.setImage(navimage);
        navbarBox.getChildren().add(navIV);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        HBox lFootBox = csgBuilder.buildHBox(CSG_LFOOT_BOX,styleBox,CLASS_CSG_BOX,ENABLED);
        lFootBox.setSpacing(15);
        lFootBox.setAlignment(Pos.BOTTOM_LEFT);
        csgBuilder.buildTextButton(CSG_LFOOT_BUTTON, lFootBox, CLASS_CSG_BUTTON, ENABLED);
        try{
        Image lFootimage = new Image(new FileInputStream("../CourseSiteGenerator/images/SBUWhiteShieldLogo.jpg"));
        lfootFP = "../CourseSiteGenerator/images/SBUWhiteShieldLogo.jpg";
        ImageView lFoot = new ImageView();
        lFoot.setImage(lFootimage);
        lFootBox.getChildren().add(lFoot);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        HBox rFootBox = csgBuilder.buildHBox(CSG_RFOOT_BOX,styleBox,CLASS_CSG_BOX,ENABLED);
        rFootBox.setSpacing(15);
        rFootBox.setAlignment(Pos.BOTTOM_LEFT);
        csgBuilder.buildTextButton(CSG_RFOOT_BUTTON, rFootBox, CLASS_CSG_BUTTON, ENABLED);
        try{
        Image rFootimage = new Image(new FileInputStream("../CourseSiteGenerator/images/SBUDarkRedShieldLogo.png"));
        rfootFP = "../CourseSiteGenerator/images/SBUDarkRedShieldLogo.png";
        ImageView rFoot = new ImageView();
        rFoot.setImage(rFootimage);
        rFootBox.getChildren().add(rFoot);
        }catch(FileNotFoundException e){
            System.out.println("Error");
        }
        HBox cssBox = csgBuilder.buildHBox(CSG_CSS_BOX,styleBox,CLASS_CSG_BOX,ENABLED);
        cssBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_CSS_LABEL, cssBox, CLASS_CSG_LABEL, ENABLED);
        ComboBox cssFiles = csgBuilder.buildComboBox(CSG_CSS_CB,"CSS_OPTIONS", "CSE", cssBox, CLASS_CSG_BUTTON, ENABLED);
        cssFiles.editableProperty().setValue(false);
        csgBuilder.buildLabel(CSG_NOTE_LABEL,styleBox,CLASS_CSG_LABEL,ENABLED);
        cssFiles.getSelectionModel().select("sea_wolf");
        VBox instrBox = csgBuilder.buildVBox(CSG_INSTRUCTOR_BOX,sitePane,CLASS_CSG_BOX,ENABLED);
        instrBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        instrBox.setPadding(new Insets(10, 15, 10, 15));
        instrBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_INSTRUCTOR_LABEL,instrBox,CLASS_CSG_HEADER_LABEL,ENABLED);
        HBox nmRmBox = csgBuilder.buildHBox(CSG_INSTR_ONE_BOX,instrBox,CLASS_CSG_BOX,ENABLED);
        nmRmBox.setPadding(new Insets(10, 15, 10, 15));
        nmRmBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_INSTR_NAME_LABEL, nmRmBox, CLASS_CSG_LABEL, ENABLED);
        csgBuilder.buildTextField(CSG_INSTR_NAMETF, nmRmBox,CLASS_CSG_TEXT_FIELD,ENABLED);
        csgBuilder.buildLabel(CSG_INSTR_ROOM_LABEL, nmRmBox, CLASS_CSG_LABEL, ENABLED);
        csgBuilder.buildTextField(CSG_INSTR_ROOMTF, nmRmBox,CLASS_CSG_TEXT_FIELD,ENABLED);
        HBox mailPage = csgBuilder.buildHBox(CSG_INSTR_TWO_BOX,instrBox,CLASS_CSG_BOX,ENABLED);
        mailPage.setPadding(new Insets(10, 15, 10, 15));
        mailPage.setSpacing(15);
        csgBuilder.buildLabel(CSG_INSTR_EMAIL_LABEL, mailPage, CLASS_CSG_LABEL, ENABLED);
        csgBuilder.buildTextField(CSG_INSTR_EMAILTF, mailPage,CLASS_CSG_TEXT_FIELD,ENABLED);
        csgBuilder.buildLabel(CSG_INSTR_PAGE_LABEL, mailPage, CLASS_CSG_LABEL, ENABLED);
        csgBuilder.buildTextField(CSG_INSTR_PAGETF, mailPage,CLASS_CSG_TEXT_FIELD,ENABLED);
        HBox instrOH = csgBuilder.buildHBox(CSG_INSTR_OH_BOX,instrBox,CLASS_CSG_BOX,ENABLED);
        instrOH.setPadding(new Insets(10,15, 10, 15));
        instrOH.setSpacing(15);
        Button expandButton = csgBuilder.buildTextButton(CSG_INSTR_EXPAND_BUTTON,instrOH,CLASS_CSG_BUTTON,ENABLED);
        csgBuilder.buildLabel(CSG_INSTR_OH_LABEL,instrOH,CLASS_CSG_LABEL,ENABLED);
        expandButton.setText("+");
        TextArea ohField = csgBuilder.buildTextArea(CSG_OH_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        ohField.setVisible(false);
        ohField.setPrefSize(200, 250);
        scrollSite.setContent(sitePane);
        sitePane.prefWidthProperty().bind(scrollSite.widthProperty());
        sitePane.setStyle("-fx-background-color: #a7f464");
        siteTab.contentProperty().setValue(scrollSite);
        //Create elements in Syllabus tab
        ScrollPane scrollSyllabus = new ScrollPane();
        VBox syllabusPane = csgBuilder.buildVBox(CSG_SYLLABUS_PANE,null,CLASS_CSG_BOX,ENABLED);
        syllabusPane.prefWidthProperty().bind(scrollSyllabus.widthProperty());
        syllabusPane.setStyle("-fx-background-color: #a7f464");
        syllabusPane.setPadding((new Insets(10, 15, 10, 15)));
        syllabusPane.setSpacing(15);
        VBox descBox = csgBuilder.buildVBox(CSG_DESC_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        descBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        descBox.setPadding((new Insets(10, 15, 10, 15)));
        descBox.setSpacing(15);
        HBox descHeader = csgBuilder.buildHBox(CSG_DESC_HEADER_BOX,descBox,CLASS_CSG_BOX,ENABLED);
        Button descExp = csgBuilder.buildTextButton(CSG_EXPAND_DESC_BUTTON,descHeader,CLASS_CSG_BUTTON,ENABLED);
        descExp.setText("+");
        csgBuilder.buildLabel(CSG_DESC_LABEL, descHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea descField = csgBuilder.buildTextArea(CSG_DESC_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        descField.setVisible(false);
        descField.setPrefSize(200, 300);
        VBox topicsBox = csgBuilder.buildVBox(CSG_TOPICS_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        topicsBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        topicsBox.setPadding((new Insets(10, 15, 10, 15)));
        topicsBox.setSpacing(15);
        HBox topicsHeader = csgBuilder.buildHBox(CSG_TOPICS_HEADER_BOX,topicsBox,CLASS_CSG_BOX,ENABLED);
        Button topicsExp = csgBuilder.buildTextButton(CSG_EXPAND_TOPICS_BUTTON,topicsHeader,CLASS_CSG_BUTTON,ENABLED);
        topicsExp.setText("+");
        csgBuilder.buildLabel(CSG_TOPICS_LABEL, topicsHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea topicsField = csgBuilder.buildTextArea(CSG_TOPICS_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        topicsField.setVisible(false);
        topicsField.setPrefSize(200, 300); 
        VBox preqBox = csgBuilder.buildVBox(CSG_PREQ_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        preqBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        preqBox.setPadding((new Insets(10, 15, 10, 15)));
        preqBox.setSpacing(15);
        HBox preqHeader = csgBuilder.buildHBox(CSG_PREQ_HEADER_BOX,preqBox,CLASS_CSG_BOX,ENABLED);
        Button preqExp = csgBuilder.buildTextButton(CSG_EXPAND_PREQ_BUTTON,preqHeader,CLASS_CSG_BUTTON,ENABLED);
        preqExp.setText("+");
        csgBuilder.buildLabel(CSG_PREQ_LABEL, preqHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea preqField = csgBuilder.buildTextArea(CSG_PREQ_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        preqField.setVisible(false);
        preqField.setPrefSize(200, 300); 
        VBox outcomesBox = csgBuilder.buildVBox(CSG_OUTCOMES_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        outcomesBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        outcomesBox.setPadding((new Insets(10, 15, 10, 15)));
        outcomesBox.setSpacing(15);
        HBox outcomesHeader = csgBuilder.buildHBox(CSG_OUTCOMES_HEADER_BOX,outcomesBox,CLASS_CSG_BOX,ENABLED);
        Button outcomesExp = csgBuilder.buildTextButton(CSG_EXPAND_OUTCOMES_BUTTON,outcomesHeader,CLASS_CSG_BUTTON,ENABLED);
        outcomesExp.setText("+");
        csgBuilder.buildLabel(CSG_OUTCOMES_LABEL, outcomesHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea outcomesField = csgBuilder.buildTextArea(CSG_OUTCOMES_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        outcomesField.setVisible(false);
        outcomesField.setPrefSize(200, 300);
        VBox txtbksBox = csgBuilder.buildVBox(CSG_TXTBKS_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        txtbksBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        txtbksBox.setPadding((new Insets(10, 15, 10, 15)));
        txtbksBox.setSpacing(15);
        HBox txtbksHeader = csgBuilder.buildHBox(CSG_TXTBKS_HEADER_BOX,txtbksBox,CLASS_CSG_BOX,ENABLED);
        Button txtbksExp = csgBuilder.buildTextButton(CSG_EXPAND_TXTBKS_BUTTON,txtbksHeader,CLASS_CSG_BUTTON,ENABLED);
        txtbksExp.setText("+");
        csgBuilder.buildLabel(CSG_TXTBKS_LABEL, txtbksHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea txtbkField = csgBuilder.buildTextArea(CSG_TXTBKS_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        txtbkField.setVisible(false);
        txtbkField.setPrefSize(200, 300);
        VBox gcBox = csgBuilder.buildVBox(CSG_GC_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        gcBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        gcBox.setPadding((new Insets(10, 15, 10, 15)));
        gcBox.setSpacing(15);
        HBox gcHeader = csgBuilder.buildHBox(CSG_GC_HEADER_BOX,gcBox,CLASS_CSG_BOX,ENABLED);
        Button gcExp = csgBuilder.buildTextButton(CSG_EXPAND_GC_BUTTON,gcHeader,CLASS_CSG_BUTTON,ENABLED);
        gcExp.setText("+");
        csgBuilder.buildLabel(CSG_GC_LABEL, gcHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea gcField = csgBuilder.buildTextArea(CSG_GC_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        gcField.setVisible(false);
        gcField.setPrefSize(200, 300);
        VBox gnBox = csgBuilder.buildVBox(CSG_GN_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        gnBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        gnBox.setPadding((new Insets(10, 15, 10, 15)));
        gnBox.setSpacing(15);
        HBox gnHeader = csgBuilder.buildHBox(CSG_GN_HEADER_BOX,gnBox,CLASS_CSG_BOX,ENABLED);
        Button gnExp = csgBuilder.buildTextButton(CSG_EXPAND_GN_BUTTON,gnHeader,CLASS_CSG_BUTTON,ENABLED);
        gnExp.setText("+");
        csgBuilder.buildLabel(CSG_GN_LABEL, gnHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea gnField = csgBuilder.buildTextArea(CSG_GN_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        gnField.setVisible(false);
        gnField.setPrefSize(200, 300);
        VBox adBox = csgBuilder.buildVBox(CSG_AD_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        adBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        adBox.setPadding((new Insets(10, 15, 10, 15)));
        adBox.setSpacing(15);
        HBox adHeader = csgBuilder.buildHBox(CSG_AD_HEADER_BOX,adBox,CLASS_CSG_BOX,ENABLED);
        Button adExp = csgBuilder.buildTextButton(CSG_EXPAND_AD_BUTTON,adHeader,CLASS_CSG_BUTTON,ENABLED);
        adExp.setText("+");
        csgBuilder.buildLabel(CSG_AD_LABEL, adHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea adField = csgBuilder.buildTextArea(CSG_AD_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        adField.setVisible(false);
        adField.setPrefSize(200, 300);
        VBox saBox = csgBuilder.buildVBox(CSG_SA_BOX,syllabusPane,CLASS_CSG_BOX,ENABLED);
        saBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        saBox.setPadding((new Insets(10, 15, 10, 15)));
        saBox.setSpacing(15);
        HBox saHeader = csgBuilder.buildHBox(CSG_SA_HEADER_BOX,saBox,CLASS_CSG_BOX,ENABLED);
        Button saExp = csgBuilder.buildTextButton(CSG_EXPAND_SA_BUTTON,saHeader,CLASS_CSG_BUTTON,ENABLED);
        saExp.setText("+");
        csgBuilder.buildLabel(CSG_SA_LABEL, saHeader,CLASS_CSG_LABEL,ENABLED);
        TextArea saField = csgBuilder.buildTextArea(CSG_SA_TEXTFIELD, null, CLASS_CSG_TEXT_FIELD, ENABLED);
        saField.setVisible(false);
        saField.setPrefSize(200, 300);
        scrollSyllabus.setContent(syllabusPane);
        syllabusTab.contentProperty().setValue(scrollSyllabus);
        //Create elements in Meeting Times tab
        ScrollPane scrollMT = new ScrollPane();
        VBox mtPane = csgBuilder.buildVBox(CSG_MT_PANE, null, CLASS_CSG_BOX, ENABLED);
        mtPane.prefWidthProperty().bind(scrollMT.widthProperty());
        mtPane.setStyle("-fx-background-color: #a7f464");
        mtPane.setPadding((new Insets(10, 15, 10, 15)));
        mtPane.setSpacing(15);
        Callback<TableColumn, TableCell> cellFactory =
             new Callback<TableColumn, TableCell>() {
                 public TableCell call(TableColumn p) {
                    return new TextFieldTableCell();
                 }
             };
        VBox lecturesBox = csgBuilder.buildVBox(CSG_LECT_BOX, mtPane, CLASS_CSG_BOX, ENABLED);
        lecturesBox.setPadding((new Insets(10, 15, 10, 15)));
        lecturesBox.setSpacing(15);
        lecturesBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        HBox lectHead = csgBuilder.buildHBox(CSG_LECT_HEAD_BOX,lecturesBox,CLASS_CSG_BOX,ENABLED);
        lectHead.setPadding((new Insets(10, 15, 10, 15)));
        lectHead.setSpacing(15);
        Button addLect = csgBuilder.buildTextButton(CSG_ADD_LECT_BUTTON,lectHead,CLASS_CSG_BUTTON,ENABLED);
        addLect.setText("+");
        Button removeLect = csgBuilder.buildTextButton(CSG_REMOVE_LECT_BUTTON,lectHead,CLASS_CSG_BUTTON,ENABLED);
        removeLect.setText("-");
        csgBuilder.buildLabel(CSG_LECT_LABEL,lectHead,CLASS_CSG_LABEL,ENABLED);
        HBox lectLayout = csgBuilder.buildHBox(CSG_LECT_LAYOUT, lecturesBox,CLASS_CSG_BOX,ENABLED);
        lectLayout.setPadding((new Insets(10, 15, 10, 15)));
        lectLayout.setSpacing(15);
        TableView lectTable = csgBuilder.buildTableView(CSG_LECT_TABLE_VIEW, lectLayout, CLASS_CSG_TABLE_VIEW, ENABLED);
        lectTable.editableProperty().set(true);
        lectTable.setPrefSize(1500, 200);
        TableColumn secColumn = csgBuilder.buildTableColumn(CSG_SEC_LECT_TC, lectTable, CLASS_CSG_COLUMN);
        secColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        secColumn.editableProperty().set(true);
        secColumn.setCellFactory(cellFactory);
        secColumn.prefWidthProperty().bind(lectTable.widthProperty().multiply(0.5/3.0));
        TableColumn dayColumn = csgBuilder.buildTableColumn(CSG_DAY_LECT_TC, lectTable, CLASS_CSG_COLUMN);
        dayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days"));
        dayColumn.editableProperty().set(true);
        dayColumn.setCellFactory(cellFactory);
        dayColumn.prefWidthProperty().bind(lectTable.widthProperty().multiply(0.5/3.0));
        TableColumn timeColumn = csgBuilder.buildTableColumn(CSG_TIME_LECT_TC, lectTable, CLASS_CSG_COLUMN);
        timeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Time"));
        timeColumn.editableProperty().set(true);
        timeColumn.setCellFactory(cellFactory);
        timeColumn.prefWidthProperty().bind(lectTable.widthProperty().multiply(1/3.0));
        TableColumn roomColumn = csgBuilder.buildTableColumn(CSG_ROOM_LECT_TC, lectTable, CLASS_CSG_COLUMN);
        roomColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        roomColumn.prefWidthProperty().bind(lectTable.widthProperty().multiply(1/3.0));
        roomColumn.editableProperty().set(true);
        roomColumn.setCellFactory(cellFactory);
        VBox recitationsBox = csgBuilder.buildVBox(CSG_REC_BOX, mtPane, CLASS_CSG_BOX, ENABLED);
        recitationsBox.setPadding((new Insets(10, 15, 10, 15)));
        recitationsBox.setSpacing(15);
        recitationsBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        HBox recHead = csgBuilder.buildHBox(CSG_REC_HEAD_BOX,recitationsBox,CLASS_CSG_BOX,ENABLED);
        recHead.setPadding((new Insets(10, 15, 10, 15)));
        recHead.setSpacing(15);
        Button addRec = csgBuilder.buildTextButton(CSG_ADD_REC_BUTTON,recHead,CLASS_CSG_BUTTON,ENABLED);
        addRec.setText("+");
        Button removeRec = csgBuilder.buildTextButton(CSG_REMOVE_REC_BUTTON,recHead,CLASS_CSG_BUTTON,ENABLED);
        removeRec.setText("-");
        csgBuilder.buildLabel(CSG_REC_LABEL,recHead,CLASS_CSG_LABEL,ENABLED);
        HBox recLayout = csgBuilder.buildHBox(CSG_REC_LAYOUT, recitationsBox,CLASS_CSG_BOX,ENABLED);
        recLayout.setPadding((new Insets(10, 15, 10, 15)));
        recLayout.setSpacing(15);
        TableView recTable = csgBuilder.buildTableView(CSG_REC_TABLE_VIEW, recLayout, CLASS_CSG_TABLE_VIEW, ENABLED);
        recTable.editableProperty().set(true);
        recTable.setPrefSize(1500, 200);
        TableColumn secRecColumn = csgBuilder.buildTableColumn(CSG_SEC_REC_TC, recTable, CLASS_CSG_COLUMN);
        secRecColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        secRecColumn.prefWidthProperty().bind(recTable.widthProperty().multiply(0.5/4.0));
        secRecColumn.editableProperty().set(true);
        secRecColumn.setCellFactory(cellFactory);
        TableColumn dayTimeColumn = csgBuilder.buildTableColumn(CSG_DAY_REC_TC, recTable, CLASS_CSG_COLUMN);
        dayTimeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days"));
        dayTimeColumn.editableProperty().set(true);
        dayTimeColumn.setCellFactory(cellFactory);
        dayTimeColumn.prefWidthProperty().bind(recTable.widthProperty().multiply(1/4.0));
        TableColumn roomRecColumn = csgBuilder.buildTableColumn(CSG_ROOM_REC_TC, recTable, CLASS_CSG_COLUMN);
        roomRecColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        roomRecColumn.editableProperty().set(true);
        roomRecColumn.setCellFactory(cellFactory);
        roomRecColumn.prefWidthProperty().bind(recTable.widthProperty().multiply(0.5/4.0));
        TableColumn ta1RecColumn = csgBuilder.buildTableColumn(CSG_TA1_REC_TC, recTable, CLASS_CSG_COLUMN);
        ta1RecColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA1"));
        ta1RecColumn.editableProperty().set(true);
        ta1RecColumn.setCellFactory(cellFactory);
        ta1RecColumn.prefWidthProperty().bind(recTable.widthProperty().multiply(1/4.0));
        TableColumn ta2RecColumn = csgBuilder.buildTableColumn(CSG_TA2_REC_TC, recTable, CLASS_CSG_COLUMN);
        ta2RecColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA2"));
        ta2RecColumn.editableProperty().set(true);
        ta2RecColumn.setCellFactory(cellFactory);
        ta2RecColumn.prefWidthProperty().bind(recTable.widthProperty().multiply(1/4.0));
        
        VBox labsBox = csgBuilder.buildVBox(CSG_LAB_BOX, mtPane, CLASS_CSG_BOX, ENABLED);
        labsBox.setPadding((new Insets(10, 15, 10, 15)));
        labsBox.setSpacing(15);
        labsBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        HBox labsHead = csgBuilder.buildHBox(CSG_LAB_HEAD_BOX,labsBox,CLASS_CSG_BOX,ENABLED);
        labsHead.setPadding((new Insets(10, 15, 10, 15)));
        labsHead.setSpacing(15);
        Button addLabs = csgBuilder.buildTextButton(CSG_ADD_LAB_BUTTON,labsHead,CLASS_CSG_BUTTON,ENABLED);
        addLabs.setText("+");
        Button removeLabs = csgBuilder.buildTextButton(CSG_REMOVE_LAB_BUTTON,labsHead,CLASS_CSG_BUTTON,ENABLED);
        removeLabs.setText("-");
        csgBuilder.buildLabel(CSG_LAB_LABEL,labsHead,CLASS_CSG_LABEL,ENABLED);
        HBox labsLayout = csgBuilder.buildHBox(CSG_LAB_LAYOUT, labsBox,CLASS_CSG_BOX,ENABLED);
        labsLayout.setPadding((new Insets(10, 15, 10, 15)));
        labsLayout.setSpacing(15);
        TableView labsTable = csgBuilder.buildTableView(CSG_LAB_TABLE_VIEW, labsLayout, CLASS_CSG_TABLE_VIEW, ENABLED);
        labsTable.editableProperty().set(true);
        labsTable.setPrefSize(1500, 200);
        TableColumn secLabColumn = csgBuilder.buildTableColumn(CSG_SEC_LAB_TC, labsTable, CLASS_CSG_COLUMN);
        secLabColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        secLabColumn.editableProperty().set(true);
        secLabColumn.setCellFactory(cellFactory);
        secLabColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(0.5/4.0));
        TableColumn dayLabColumn = csgBuilder.buildTableColumn(CSG_DAY_LAB_TC, labsTable, CLASS_CSG_COLUMN);
        dayLabColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days"));
        dayLabColumn.editableProperty().set(true);
        dayLabColumn.setCellFactory(cellFactory);
        dayLabColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1/4.0));
        TableColumn roomLabColumn = csgBuilder.buildTableColumn(CSG_ROOM_LAB_TC, labsTable, CLASS_CSG_COLUMN);
        roomLabColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        roomLabColumn.editableProperty().set(true);
        roomLabColumn.setCellFactory(cellFactory);
        roomLabColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(0.5/4.0));
        TableColumn ta1LabColumn = csgBuilder.buildTableColumn(CSG_TA1_LAB_TC, labsTable, CLASS_CSG_COLUMN);
        ta1LabColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA1"));
        ta1LabColumn.editableProperty().set(true);
        ta1LabColumn.setCellFactory(cellFactory);
        ta1LabColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1/4.0));
        TableColumn ta2LabColumn = csgBuilder.buildTableColumn(CSG_TA2_LAB_TC, labsTable, CLASS_CSG_COLUMN);
        ta2LabColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA2"));
        ta2LabColumn.editableProperty().set(true);
        ta2LabColumn.setCellFactory(cellFactory);
        ta2LabColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1/4.0));
        scrollMT.setContent(mtPane);
        mtTab.contentProperty().setValue(scrollMT);
        //Create elements in Office Hours tab
        VBox ohPane = csgBuilder.buildVBox(CSG_OH_PANE,null,CLASS_CSG_BOX,ENABLED);
        ohPane.setPadding((new Insets(10, 15, 10, 15)));
        ohPane.setSpacing(15);
        VBox taBox = csgBuilder.buildVBox(CSG_TAS_BOX, ohPane, CLASS_CSG_BOX, ENABLED);
        taBox.setPrefHeight(500);
        taBox.setPadding((new Insets(10, 15, 10, 15)));
        taBox.setSpacing(15);
        taBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        HBox taHeader = csgBuilder.buildHBox(CSG_TAS_HEADER_PANE, taBox, CLASS_CSG_BOX, ENABLED);
        Button removeTA = csgBuilder.buildTextButton(CSG_REMOVE_TAS_BUTTON,taHeader,CLASS_CSG_BOX,ENABLED);
        removeTA.setText("-");
        csgBuilder.buildLabel(CSG_TAS_HEADER_LABEL, taHeader, CLASS_CSG_HEADER_LABEL, ENABLED);
        RadioButton all = csgBuilder.buildRadioButton(CSG_TAS_RADIO_BUTTON_ALL, taHeader, CLASS_CSG_RADIO_BUTTON, ENABLED);
        RadioButton undergrad = csgBuilder.buildRadioButton(CSG_TAS_RADIO_BUTTON_UG, taHeader, CLASS_CSG_RADIO_BUTTON, ENABLED);
        RadioButton grad = csgBuilder.buildRadioButton(CSG_TAS_RADIO_BUTTON_G, taHeader, CLASS_CSG_RADIO_BUTTON, ENABLED);
        HBox radioButtons = new HBox();
        taHeader.setSpacing(10);
        radioButtons.setSpacing(15);
        radioButtons.getChildren().addAll(all,undergrad,grad);
        taHeader.getChildren().addAll(radioButtons);
        HBox tableLayout = csgBuilder.buildHBox(CSG_TAS_LAYOUT_PANE, taBox, CLASS_CSG_BOX, ENABLED);
        tableLayout.setPadding(new Insets(15, 15, 15, 15));
        TableView taTable = csgBuilder.buildTableView(CSG_TAS_TABLE_VIEW, tableLayout, CLASS_CSG_TABLE_VIEW, ENABLED);
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        taTable.prefHeightProperty().set(200);
        taTable.prefWidthProperty().set(1500);
        TableColumn nameColumn = csgBuilder.buildTableColumn(CSG_NAME_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        nameColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        TableColumn emailColumn = csgBuilder.buildTableColumn(CSG_EMAIL_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
        emailColumn.setCellValueFactory(new PropertyValueFactory<String, String>("email"));
        emailColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.5/3.0));
        TableColumn hoursColumn = csgBuilder.buildTableColumn(CSG_SLOTS_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
        hoursColumn.setCellValueFactory(new PropertyValueFactory<String, Integer>("timeSlot"));
        hoursColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        TableColumn typeColumn = csgBuilder.buildTableColumn(CSG_TYPE_TABLE_COLUMN, taTable, CLASS_CSG_COLUMN);
        typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
        typeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        HBox addTABox = csgBuilder.buildHBox(CSG_TAS_HEADER_PANE,taBox,CLASS_CSG_BOX,ENABLED);
        addTABox.setSpacing(10);
        addTABox.setPadding(new Insets(10, 15, 10, 15));
        TextField name = csgBuilder.buildTextField(CSG_NAME_TEXT_FIELD, addTABox, CLASS_CSG_TEXT_FIELD, ENABLED);
        TextField email = csgBuilder.buildTextField(CSG_EMAIL_TEXT_FIELD, addTABox, CLASS_CSG_TEXT_FIELD, ENABLED);
        Button addTAButton = csgBuilder.buildTextButton(CSG_ADD_TA_BUTTON, addTABox, CLASS_CSG_BUTTON, ENABLED);
        ohTab.contentProperty().setValue(ohPane);
        addTAButton.setDisable(true);
        
        VBox ohBox = csgBuilder.buildVBox(CSG_TAS_BOX, ohPane, CLASS_CSG_BOX, ENABLED);
        ohBox.setPadding((new Insets(10, 15, 10, 15)));
        ohBox.setSpacing(15);
        ohBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        HBox ohHeader = csgBuilder.buildHBox(CSG_OFFICE_HOURS_HEADER_PANE, ohBox, CLASS_CSG_BOX, ENABLED);
        ohHeader.setSpacing(300);
        csgBuilder.buildLabel(CSG_OFFICE_HOURS_HEADER_LABEL, ohHeader, CLASS_CSG_HEADER_LABEL, ENABLED);
        HBox timesCBBox = csgBuilder.buildHBox(CSG_TIMES_CB_HEADER, ohHeader, CLASS_CSG_BOX, ENABLED);
        timesCBBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_START_TIME_LABEL,timesCBBox,CLASS_CSG_LABEL,ENABLED);
        ComboBox startTimeCB = csgBuilder.buildComboBox(CSG_START_TIME_CB, "START_TIME_OPTIONS", "", timesCBBox, CLASS_CSG_BUTTON, ENABLED);
        startTimeCB.editableProperty().setValue(false);
        startTimeCB.getSelectionModel().select("9:00am");
        csgBuilder.buildLabel(CSG_END_TIME_LABEL,timesCBBox,CLASS_CSG_LABEL,ENABLED);
        ComboBox endTimeCB = csgBuilder.buildComboBox(CSG_END_TIME_CB, "END_TIME_OPTIONS", "", timesCBBox, CLASS_CSG_BUTTON, ENABLED);
        int startTimeIndex = startTimeCB.getSelectionModel().getSelectedIndex();
        ObservableList<String> endTimeSplit = FXCollections.observableArrayList();
        for (int i = startTimeIndex+1; i < startTimeIndex+14; i++) {
            endTimeSplit.add(endTimeList.get(i));
        }
        endTimeCB.setItems(endTimeSplit);
        endTimeCB.editableProperty().setValue(false);
        endTimeCB.getSelectionModel().select("9:00pm");
        HBox ohLayout = csgBuilder.buildHBox(CSG_OFFICE_HOURS_LAYOUT_PANE, ohBox, CLASS_CSG_BOX, ENABLED);
        ohLayout.setPadding(new Insets(15,15,15,15));
        TableView<TimeSlot> ohTable = csgBuilder.buildTableView(CSG_OFFICE_HOURS_TABLE_VIEW, ohLayout, CLASS_CSG_OFFICE_HOURS_TABLE_VIEW, ENABLED);
        ohTable.prefHeightProperty().set(600);
        ohTable.prefWidthProperty().set(1500);
        TableColumn startTimeColumn = csgBuilder.buildTableColumn(CSG_START_TIME_TABLE_COLUMN, ohTable, CLASS_CSG_TIME_COLUMN);
        TableColumn endTimeColumn = csgBuilder.buildTableColumn(CSG_END_TIME_TABLE_COLUMN, ohTable, CLASS_CSG_TIME_COLUMN);
        TableColumn mondayColumn = csgBuilder.buildTableColumn(CSG_MONDAY_TABLE_COLUMN, ohTable, CLASS_CSG_DAY_OF_WEEK_COLUMN);
        TableColumn tuesdayColumn = csgBuilder.buildTableColumn(CSG_TUESDAY_TABLE_COLUMN, ohTable, CLASS_CSG_DAY_OF_WEEK_COLUMN);
        TableColumn wednesdayColumn = csgBuilder.buildTableColumn(CSG_WEDNESDAY_TABLE_COLUMN, ohTable, CLASS_CSG_DAY_OF_WEEK_COLUMN);
        TableColumn thursdayColumn = csgBuilder.buildTableColumn(CSG_THURSDAY_TABLE_COLUMN, ohTable, CLASS_CSG_DAY_OF_WEEK_COLUMN);
        TableColumn fridayColumn = csgBuilder.buildTableColumn(CSG_FRIDAY_TABLE_COLUMN, ohTable, CLASS_CSG_DAY_OF_WEEK_COLUMN);
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("endTime"));
        mondayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("monday"));
        tuesdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("tuesday"));
        wednesdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("wednesday"));
        thursdayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("thursday"));
        fridayColumn.setCellValueFactory(new PropertyValueFactory<String, String>("friday"));
        for (int i = 0; i < ohTable.getColumns().size(); i++) {
            ((TableColumn)ohTable.getColumns().get(i)).prefWidthProperty().bind(ohTable.widthProperty().multiply(1.0/7.0));
        }
        //Create elements in Schedule tab
        ScrollPane scrollSchd = new ScrollPane();
        VBox schdPane = csgBuilder.buildVBox(CSG_MT_PANE, null, CLASS_CSG_BOX, ENABLED);
        schdPane.prefWidthProperty().bind(scrollMT.widthProperty());
        schdPane.setPadding((new Insets(10, 15, 10, 15)));
        schdPane.setSpacing(15);
        schdPane.setStyle("-fx-background-color: #a7f464");
        VBox calendarBox =  csgBuilder.buildVBox(CSG_CAL_PANE, schdPane, CLASS_CSG_BOX, ENABLED);
        calendarBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        calendarBox.setPadding((new Insets(10, 15, 10, 15)));
        csgBuilder.buildLabel(CSG_CAL_LABEL,calendarBox,CLASS_CSG_HEADER_LABEL,ENABLED);
        HBox dateBox = csgBuilder.buildHBox(CSG_DATE_PANE, calendarBox, CLASS_CSG_BOX, ENABLED);
        dateBox.setSpacing(50);
        HBox monBox = csgBuilder.buildHBox(CSG_STMON_PANE, dateBox, CLASS_CSG_BOX, ENABLED);
        monBox.setSpacing(15);
        csgBuilder.buildLabel(CSG_STMON_LABEL,monBox,CLASS_CSG_LABEL,ENABLED);
        DatePicker d = new DatePicker();
        monBox.getChildren().add(d);
        HBox friBox = csgBuilder.buildHBox(CSG_STFRI_PANE, dateBox, CLASS_CSG_BOX, ENABLED);
        csgBuilder.buildLabel(CSG_STFRI_LABEL,friBox,CLASS_CSG_LABEL,ENABLED);
        DatePicker e = new DatePicker();
        friBox.getChildren().add(e);
        scrollSchd.setContent(schdPane);
        VBox schdBox = csgBuilder.buildVBox(CSG_SCHED_PANE, schdPane, CLASS_CSG_BOX, ENABLED);
        schdBox.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        schdBox.setPadding((new Insets(10, 15, 10, 15)));
        schedTab.contentProperty().setValue(scrollSchd);
        HBox scHeadBox = csgBuilder.buildHBox(CSG_SCHEADER_PANE, schdBox,CLASS_CSG_BOX,ENABLED);
        scHeadBox.setSpacing(15);
        Button removeItem = csgBuilder.buildTextButton(CSG_REM_ITEM_BUTTON,scHeadBox,CLASS_CSG_BUTTON,ENABLED);
        removeItem.setText("-");
        csgBuilder.buildLabel(CSG_SCHEAD_LABEL,scHeadBox,CLASS_CSG_HEADER_LABEL,ENABLED);
        TableView schedTable = csgBuilder.buildTableView(CSG_SCHED_TABLE_VIEW, schdBox, CLASS_CSG_TABLE_VIEW, ENABLED);
        TableColumn schedTypeColumn = csgBuilder.buildTableColumn(CSG_SCHED_TYPE_TABLE_COLUMN, schedTable, CLASS_CSG_COLUMN);
        TableColumn schedDateColumn = csgBuilder.buildTableColumn(CSG_SCHED_DATE_TABLE_COLUMN, schedTable, CLASS_CSG_COLUMN);
        TableColumn schedTitleColumn = csgBuilder.buildTableColumn(CSG_SCHED_TITLE_TABLE_COLUMN, schedTable, CLASS_CSG_COLUMN);
        TableColumn schedTopicColumn = csgBuilder.buildTableColumn(CSG_SCHED_TOPIC_TABLE_COLUMN, schedTable, CLASS_CSG_COLUMN);
        schedTypeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Type"));
        schedTypeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        schedDateColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Date"));
        schedDateColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        schedTitleColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Title"));
        schedTitleColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(0.5/3.0));
        schedTopicColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Topic"));
        schedTopicColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(2/3.0));
        VBox addEdit = csgBuilder.buildVBox(CSG_ADDEDIT_PANE, schdPane, CLASS_CSG_BOX, ENABLED);
        addEdit.setStyle("-fx-background-color: #c2c3c4 ; -fx-border-color: black");
        addEdit.setPadding((new Insets(10, 15, 10, 15)));
        addEdit.setSpacing(15);
        csgBuilder.buildLabel(CSG_ADDEDIT_LABEL, addEdit,CLASS_CSG_HEADER_LABEL,ENABLED);
        HBox typeBox = csgBuilder.buildHBox(CSG_ADD_TYPE_PANE, addEdit, CLASS_CSG_BOX, ENABLED);
        typeBox.setSpacing(30);
        csgBuilder.buildLabel(CSG_ADD_TYPE_LABEL,typeBox,CLASS_CSG_LABEL, ENABLED);
        csgBuilder.buildComboBox(CSG_TYPE_CB,"TYPE_OPTIONS", "Options", typeBox, CLASS_CSG_BUTTON, ENABLED);
        HBox addDateBox = csgBuilder.buildHBox(CSG_ADD_DATE_PANE, addEdit, CLASS_CSG_BOX, ENABLED);
        csgBuilder.buildLabel(CSG_ADD_DATE_LABEL, addDateBox,CLASS_CSG_LABEL,ENABLED);
        addDateBox.setSpacing(30);
        DatePicker addDatePicker = new DatePicker();
        addDateBox.getChildren().add(addDatePicker);
        HBox addTitleBox = csgBuilder.buildHBox(CSG_ADD_TITLE_PANE, addEdit, CLASS_CSG_BOX, ENABLED);
        addTitleBox.setSpacing(30);
        csgBuilder.buildLabel(CSG_ADD_TITLE_LABEL, addTitleBox,CLASS_CSG_LABEL,ENABLED);
        csgBuilder.buildTextField(CSG_ADD_TITLE_TF, addTitleBox, CLASS_CSG_TEXT_FIELD, ENABLED);
        HBox addTopicBox = csgBuilder.buildHBox(CSG_ADD_TOPIC_PANE, addEdit, CLASS_CSG_BOX, ENABLED);
        addTopicBox.setSpacing(23);
        csgBuilder.buildLabel(CSG_ADD_TOPIC_LABEL, addTopicBox,CLASS_CSG_LABEL,ENABLED);
        csgBuilder.buildTextField(CSG_ADD_TOPIC_TF, addTopicBox, CLASS_CSG_TEXT_FIELD, ENABLED);
        HBox addLinkBox = csgBuilder.buildHBox(CSG_ADD_LINK_PANE, addEdit, CLASS_CSG_BOX, ENABLED);
        addLinkBox.setSpacing(30);
        csgBuilder.buildLabel(CSG_ADD_LINK_LABEL, addLinkBox,CLASS_CSG_LABEL,ENABLED);
        csgBuilder.buildTextField(CSG_ADD_LINK_TF, addLinkBox, CLASS_CSG_TEXT_FIELD, ENABLED);
        HBox addButtonBox = csgBuilder.buildHBox(CSG_ADD_BUTTONS_PANE, addEdit,CLASS_CSG_BOX,ENABLED);
        addButtonBox.setSpacing(70);
        csgBuilder.buildTextButton(CSG_ADD_ITEM_BUTTON,addButtonBox,CLASS_CSG_BUTTON,ENABLED);
        csgBuilder.buildTextButton(CSG_CLEAR_ITEM_BUTTON,addButtonBox,CLASS_CSG_BUTTON,ENABLED);
        schedTable.prefHeightProperty().set(200);
        schedTable.prefWidthProperty().set(1500);
        displayPane.getChildren().add(siteTabs);
        workspace = new BorderPane();
        ((BorderPane)workspace).setCenter(displayPane);
        all.setSelected(true);
        new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
    }
    private void initControllers() {
        AppGUIModule gui = app.getGUIModule();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        TableView taPane = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
        CourseSiteGeneratorController controller = new CourseSiteGeneratorController((CourseSiteGeneratorApp) app);
        CourseSiteGeneratorData data = (CourseSiteGeneratorData) app.getDataComponent();
        // DON'T LET ANYONE SORT THE TABLES
        TableView officeHoursTableView = (TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW);
        for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
            ((TableColumn)officeHoursTableView.getColumns().get(i)).setSortable(false);
        }
        TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
        for (int i = 0; i < taTableView.getColumns().size(); i++) {
            ((TableColumn)taTableView.getColumns().get(i)).setSortable(false);
        }
        ((Button) gui.getGUINode(CSG_FAVICON_BUTTON)).setOnAction(e -> {
            Stage filePicker = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File image = fileChooser.showOpenDialog(filePicker);
            faviconFP = image.getPath();
            if (image !=null){
                HBox faviconBox = ((HBox) gui.getGUINode(CSG_FAVICON_BOX));
                faviconBox.getChildren().remove(1);
                Image newImage = new Image(image.toURI().toString());
                ImageView iv = new ImageView();
                iv.setImage(newImage);
                faviconBox.getChildren().add(iv);
            }
        });
        ((Button) gui.getGUINode(CSG_LFOOT_BUTTON)).setOnAction(e -> {
            Stage filePicker = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File image = fileChooser.showOpenDialog(filePicker);
            lfootFP = image.getPath();
            if (image !=null){
                HBox lFootBox = ((HBox) gui.getGUINode(CSG_LFOOT_BOX));
                lFootBox.getChildren().remove(1);
                Image newImage = new Image(image.toURI().toString());
                ImageView iv = new ImageView();
                iv.setImage(newImage);
                lFootBox.getChildren().add(iv);
            }
        });
        ((Button) gui.getGUINode(CSG_RFOOT_BUTTON)).setOnAction(e -> {
            Stage filePicker = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File image = fileChooser.showOpenDialog(filePicker);
            rfootFP = image.getPath();
            if (image !=null){
                HBox rFootBox = ((HBox) gui.getGUINode(CSG_RFOOT_BOX));
                rFootBox.getChildren().remove(1);
                Image newImage = new Image(image.toURI().toString());
                ImageView iv = new ImageView();
                iv.setImage(newImage);
                rFootBox.getChildren().add(iv);
            }
        });
        ((Button) gui.getGUINode(CSG_NAVBAR_BUTTON)).setOnAction(e -> {
            Stage filePicker = new Stage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File image = fileChooser.showOpenDialog(filePicker);
            navbarFP = image.getPath();
            if (image !=null){
                HBox navbarBox = ((HBox) gui.getGUINode(CSG_NAVBAR_BOX));
                navbarBox.getChildren().remove(1);
                Image newImage = new Image(image.toURI().toString());
                ImageView iv = new ImageView();
                iv.setImage(newImage);
                navbarBox.getChildren().add(iv);
            }
        });
        ((Button) gui.getGUINode(CSG_ADD_TA_BUTTON)).setOnAction(e -> {
            controller.processAddTA();
        });
        ((Button) gui.getGUINode(CSG_REMOVE_TAS_BUTTON)).setOnAction(e -> {
            RadioButton all = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
            RadioButton ug = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
            RadioButton g = (RadioButton)gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
            ObservableList taObservableList = ((TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW)).getSelectionModel().getSelectedCells();
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            int row = tablePosition.getRow();
            ObservableList<TeachingAssistantPrototype> tas = FXCollections.observableArrayList();
            if (all.isSelected()) {
                tas = ((CourseSiteGeneratorData)app.getDataComponent()).getAll();
            }else if(ug.isSelected()){
                tas = ((CourseSiteGeneratorData)app.getDataComponent()).getUndergraduate();
            }else if(g.isSelected()){
                tas = ((CourseSiteGeneratorData)app.getDataComponent()).getGraduate();
            }else{
                tas = ((CourseSiteGeneratorData)app.getDataComponent()).getAll();
            }
            ObservableList<TimeSlot> ohClone = ((CourseSiteGeneratorData)app.getDataComponent()).getOHClone();
            RemoveTA_Transaction removeTA = new RemoveTA_Transaction(tas.get(row),(CourseSiteGeneratorApp)app,row,ohClone);
            app.processTransaction(removeTA);
            if (all.isSelected()) {
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToUG();
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToAll();
            }else if(ug.isSelected()){
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToAll();
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToUG();
            }else if(g.isSelected()){
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToAll();
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToG();
            }else{
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToG();
                ((CourseSiteGeneratorData) app.getDataComponent()).setCurrentToAll();
            }
        });
        ComboBox startCB = ((ComboBox) gui.getGUINode(CSG_START_TIME_CB));
        startCB.setOnAction(e -> {
            String startTime = ((ComboBox) gui.getGUINode(CSG_START_TIME_CB)).getSelectionModel().getSelectedItem().toString();
            ArrayList<String> fullEndTime = props.getPropertyOptionsList("END_TIME_OPTIONS");
            int endTimeIndex = 0;
            for (int i = 0; i < fullEndTime.size(); i++) {
                if (((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().getSelectedItem().toString().equals(fullEndTime.get(i))) {
                    endTimeIndex = i;
                }
            }
            for (int i = 0; i < fullEndTime.size(); i++){
                if (fullEndTime.get(i).equals(startTime)) {
                    if (fullEndTime.size()-i == 14) {
                        ObservableList<String> endTimeSplit = FXCollections.observableArrayList();
                        for (int j = i+1; j < fullEndTime.size(); j++) {
                            endTimeSplit.add(fullEndTime.get(j));
                        }
                        ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).setItems(endTimeSplit);
                        if(endTimeIndex > i+14){
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }
                        if(endTimeIndex <= i){
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }
                    }else if(fullEndTime.size() - i > 15){
                        ObservableList<String> endTimeSplit = FXCollections.observableArrayList();
                        for (int j = i+1; j < i+14; j++) {
                            endTimeSplit.add(fullEndTime.get(j));   
                        }
                        ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).setItems(endTimeSplit);
                        if(endTimeIndex > i+14){
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }
                        if(endTimeIndex <= i){
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }                        
                    }else{
                        ObservableList<String> endTimeSplit = FXCollections.observableArrayList();
                        for (int j = i+1; j < fullEndTime.size(); j++) {
                            endTimeSplit.add(fullEndTime.get(j));
                        }
                        ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).setItems(endTimeSplit);
                        if (endTimeIndex <= i) {
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }
                        if (endTimeIndex > endTimeSplit.size()-1) {
                            ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().select(((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getItems().size()-1);
                        }
                    }
                }
            }
            String endTime = ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().getSelectedItem().toString();
            int sTime = 0;
            int eTime = 0;
            for (int i = 0; i < startTime.length(); i++) {
                    if (startTime.charAt(i) ==':') {
                        sTime = Integer.parseInt(startTime.substring(0,i));
                    }
                }
            if (!startTime.substring(startTime.length()-2, startTime.length()).equals("am")) {
                if (sTime != 12) {
                    sTime += 12;
                }
            }
            for (int i = 0; i < endTime.length(); i++) {
                    if (endTime.charAt(i) ==':') {
                        eTime = Integer.parseInt(endTime.substring(0,i));
                    }
                }
            if (!endTime.substring(endTime.length()-2, endTime.length()).equals("am")) {
                if (eTime != 12) {
                    eTime += 12;
                }
            }
            if(startTime.equals("12:00am")){
                sTime = 0;
            }
            ((CourseSiteGeneratorData) app.getDataComponent()).initHours(""+sTime, ""+eTime);
        });
        ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).setOnAction(e -> {
            try{
            String startTime = ((ComboBox) gui.getGUINode(CSG_START_TIME_CB)).getSelectionModel().getSelectedItem().toString();
            String endTime = ((ComboBox) gui.getGUINode(CSG_END_TIME_CB)).getSelectionModel().getSelectedItem().toString();
            int sTime = 0;
            int eTime = 0;
            for (int i = 0; i < startTime.length(); i++) {
                    if (startTime.charAt(i) ==':') {
                        sTime = Integer.parseInt(startTime.substring(0,i));
                    }
                }
            if (!startTime.substring(startTime.length()-2, startTime.length()).equals("am")) {
                if (sTime != 12) {
                    sTime += 12;
                }
            }
            for (int i = 0; i < endTime.length(); i++) {
                    if (endTime.charAt(i) ==':') {
                        eTime = Integer.parseInt(endTime.substring(0,i));
                    }
                }
            if (!endTime.substring(endTime.length()-2, endTime.length()).equals("am")) {
                if (eTime != 12) {
                    eTime += 12;
                }
            }
            if(startTime.equals("12:00am")){
                sTime = 0;
            }
            ((CourseSiteGeneratorData) app.getDataComponent()).initHours(""+sTime, ""+eTime);
            }
            catch(NullPointerException ex){
                
            }
        });
        ((Button) gui.getGUINode(CSG_INSTR_EXPAND_BUTTON)).setOnAction(e -> {
            TextArea ohField = (TextArea) gui.getGUINode(CSG_OH_TEXTFIELD);
            VBox instrBox = (VBox)gui.getGUINode(CSG_INSTRUCTOR_BOX);
            if(ohField.isVisible()){
                ohField.setVisible(false);
                instrBox.getChildren().remove(ohField);
                instrBox.autosize();
                ((Button) gui.getGUINode(CSG_INSTR_EXPAND_BUTTON)).setText("+");
                
            }else{
                ohField.setVisible(true);
                instrBox.getChildren().add(ohField);
                instrBox.autosize();
                ((Button) gui.getGUINode(CSG_INSTR_EXPAND_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_DESC_BUTTON)).setOnAction(e -> {
            TextArea descField = (TextArea) gui.getGUINode(CSG_DESC_TEXTFIELD);
            VBox descBox = (VBox)gui.getGUINode(CSG_DESC_BOX);
            if(descField.isVisible()){
                descField.setVisible(false);
                descBox.getChildren().remove(descField);
                descBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_DESC_BUTTON)).setText("+");
                
            }else{
                descField.setVisible(true);
                descBox.getChildren().add(descField);
                descBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_DESC_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_TOPICS_BUTTON)).setOnAction(e -> {
            TextArea topicsField = (TextArea) gui.getGUINode(CSG_TOPICS_TEXTFIELD);
            VBox topicsBox = (VBox)gui.getGUINode(CSG_TOPICS_BOX);
            if(topicsField.isVisible()){
                topicsField.setVisible(false);
                topicsBox.getChildren().remove(topicsField);
                topicsBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_TOPICS_BUTTON)).setText("+");
                
            }else{
                topicsField.setVisible(true);
                topicsBox.getChildren().add(topicsField);
                topicsBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_TOPICS_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_PREQ_BUTTON)).setOnAction(e -> {
            TextArea preqField = (TextArea) gui.getGUINode(CSG_PREQ_TEXTFIELD);
            VBox preqBox = (VBox)gui.getGUINode(CSG_PREQ_BOX);
            if(preqField.isVisible()){
                preqField.setVisible(false);
                preqBox.getChildren().remove(preqField);
                preqBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_PREQ_BUTTON)).setText("+");
                
            }else{
                preqField.setVisible(true);
                preqBox.getChildren().add(preqField);
                preqBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_PREQ_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_OUTCOMES_BUTTON)).setOnAction(e -> {
            TextArea outcomesField = (TextArea) gui.getGUINode(CSG_OUTCOMES_TEXTFIELD);
            VBox outcomesBox = (VBox)gui.getGUINode(CSG_OUTCOMES_BOX);
            if(outcomesField.isVisible()){
                outcomesField.setVisible(false);
                outcomesBox.getChildren().remove(outcomesField);
                outcomesBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_OUTCOMES_BUTTON)).setText("+");
                
            }else{
                outcomesField.setVisible(true);
                outcomesBox.getChildren().add(outcomesField);
                outcomesBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_OUTCOMES_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_TXTBKS_BUTTON)).setOnAction(e -> {
            TextArea txtbkField = (TextArea) gui.getGUINode(CSG_TXTBKS_TEXTFIELD);
            VBox txtbkBox = (VBox)gui.getGUINode(CSG_TXTBKS_BOX);
            if(txtbkField.isVisible()){
                txtbkField.setVisible(false);
                txtbkBox.getChildren().remove(txtbkField);
                txtbkBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_TXTBKS_BUTTON)).setText("+");
                
            }else{
                txtbkField.setVisible(true);
                txtbkBox.getChildren().add(txtbkField);
                txtbkBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_TXTBKS_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_GC_BUTTON)).setOnAction(e -> {
            TextArea gcField = (TextArea) gui.getGUINode(CSG_GC_TEXTFIELD);
            VBox gcBox = (VBox)gui.getGUINode(CSG_GC_BOX);
            if(gcField.isVisible()){
                gcField.setVisible(false);
                gcBox.getChildren().remove(gcField);
                gcBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_GC_BUTTON)).setText("+");
                
            }else{
                gcField.setVisible(true);
                gcBox.getChildren().add(gcField);
                gcBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_GC_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_GN_BUTTON)).setOnAction(e -> {
            TextArea gnField = (TextArea) gui.getGUINode(CSG_GN_TEXTFIELD);
            VBox gnBox = (VBox)gui.getGUINode(CSG_GN_BOX);
            if(gnField.isVisible()){
                gnField.setVisible(false);
                gnBox.getChildren().remove(gnField);
                gnBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_GN_BUTTON)).setText("+");
                
            }else{
                gnField.setVisible(true);
                gnBox.getChildren().add(gnField);
                gnBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_GN_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_AD_BUTTON)).setOnAction(e -> {
            TextArea adField = (TextArea) gui.getGUINode(CSG_AD_TEXTFIELD);
            VBox adBox = (VBox)gui.getGUINode(CSG_AD_BOX);
            if(adField.isVisible()){
                adField.setVisible(false);
                adBox.getChildren().remove(adField);
                adBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_AD_BUTTON)).setText("+");
                
            }else{
                adField.setVisible(true);
                adBox.getChildren().add(adField);
                adBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_AD_BUTTON)).setText("-");
            }
        });
        ((Button) gui.getGUINode(CSG_EXPAND_SA_BUTTON)).setOnAction(e -> {
            TextArea saField = (TextArea) gui.getGUINode(CSG_SA_TEXTFIELD);
            VBox saBox = (VBox)gui.getGUINode(CSG_SA_BOX);
            if(saField.isVisible()){
                saField.setVisible(false);
                saBox.getChildren().remove(saField);
                saBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_SA_BUTTON)).setText("+");
                
            }else{
                saField.setVisible(true);
                saBox.getChildren().add(saField);
                saBox.autosize();
                ((Button) gui.getGUINode(CSG_EXPAND_SA_BUTTON)).setText("-");
            }
        });
        ComboBox semCB = ((ComboBox) gui.getGUINode(CSG_SEM_CB));
        ComboBox numCB = ((ComboBox) gui.getGUINode(CSG_NUMBER_CB));
        ComboBox yearCB = ((ComboBox) gui.getGUINode(CSG_YEAR_CB));
        ComboBox subjCB = ((ComboBox) gui.getGUINode(CSG_SUBJECT_CB));
        semCB.setOnAction(e -> {
            ArrayList<String> semList = props.getPropertyOptionsList("SEMESTER_OPTIONS");
            boolean exists = false;
            for (int i = 0; i < semList.size(); i++) {
                if(semCB.getSelectionModel().getSelectedItem().toString().equals (semList.get(i))){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                
                props.getPropertyOptionsList("SEMESTER_OPTIONS").add(semCB.getSelectionModel().getSelectedItem().toString());
                for (int i = 0; i < props.getPropertyOptionsList("SEMESTER_OPTIONS").size(); i++) {
                    boolean inList = false;
                    for (int j = 0; j < semCB.getItems().size(); j++) {
                        if (semCB.getItems().get(j).equals(props.getPropertyOptionsList("SEMESTER_OPTIONS").get(i))) {
                            inList = true;
                        }
                    }
                    if(!inList){
                        semCB.getItems().add(props.getPropertyOptionsList("SEMESTER_OPTIONS").get(i));
                        
                    }
                }
                
            }
            new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
            HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
            exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
            Label dirText = new Label(".\\export\\"+subjCB.getSelectionModel().getSelectedItem().toString()+"_"+
            numCB.getSelectionModel().getSelectedItem().toString()+"_"+semCB.getSelectionModel().getSelectedItem().toString()+"_"+
            yearCB.getSelectionModel().getSelectedItem().toString()+"\\public_html");
            exportDirBox.getChildren().add(dirText);
        });
        yearCB.setOnAction(e -> {
            ArrayList<String> yearList = props.getPropertyOptionsList("YEAR_OPTIONS");
            boolean exists = false;
            for (int i = 0; i < yearList.size(); i++) {
                if(yearCB.getSelectionModel().getSelectedItem().toString().equals (yearList.get(i))){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                
                props.getPropertyOptionsList("YEAR_OPTIONS").add(yearCB.getSelectionModel().getSelectedItem().toString());
                for (int i = 0; i < props.getPropertyOptionsList("YEAR_OPTIONS").size(); i++) {
                    boolean inList = false;
                    for (int j = 0; j < yearCB.getItems().size(); j++) {
                        if (yearCB.getItems().get(j).equals(props.getPropertyOptionsList("YEAR_OPTIONS").get(i))) {
                            inList = true;
                        }
                    }
                    if(!inList){
                        yearCB.getItems().add(props.getPropertyOptionsList("YEAR_OPTIONS").get(i));
                        
                    }
                }
                
            }
            new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
            HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
            exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
            Label dirText = new Label(".\\export\\"+subjCB.getSelectionModel().getSelectedItem().toString()+"_"+
            numCB.getSelectionModel().getSelectedItem().toString()+"_"+semCB.getSelectionModel().getSelectedItem().toString()+"_"+
            yearCB.getSelectionModel().getSelectedItem().toString()+"\\public_html");
            exportDirBox.getChildren().add(dirText);
        });
        
        numCB.setOnAction(e -> {
            ArrayList<String> numList = props.getPropertyOptionsList("NUMBER_OPTIONS");
            boolean exists = false;
            for (int i = 0; i < numList.size(); i++) {
                if(numCB.getSelectionModel().getSelectedItem().toString().equals (numList.get(i))){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                
                props.getPropertyOptionsList("NUMBER_OPTIONS").add(numCB.getSelectionModel().getSelectedItem().toString());
                for (int i = 0; i < props.getPropertyOptionsList("NUMBER_OPTIONS").size(); i++) {
                    boolean inList = false;
                    for (int j = 0; j < numCB.getItems().size(); j++) {
                        if (numCB.getItems().get(j).equals(props.getPropertyOptionsList("NUMBER_OPTIONS").get(i))) {
                            inList = true;
                        }
                    }
                    if(!inList){
                        numCB.getItems().add(props.getPropertyOptionsList("NUMBER_OPTIONS").get(i));
                        
                    }
                }
                
            }
            new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
            HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
            exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
            Label dirText = new Label(".\\export\\"+subjCB.getSelectionModel().getSelectedItem().toString()+"_"+
            numCB.getSelectionModel().getSelectedItem().toString()+"_"+semCB.getSelectionModel().getSelectedItem().toString()+"_"+
            yearCB.getSelectionModel().getSelectedItem().toString()+"\\public_html");
            exportDirBox.getChildren().add(dirText);
        });
        subjCB.setOnAction(e -> {
            ArrayList<String> subjList = props.getPropertyOptionsList("SUBJECT_OPTIONS");
            boolean exists = false;
            for (int i = 0; i < subjList.size(); i++) {
                if(subjCB.getSelectionModel().getSelectedItem().toString().equals (subjList.get(i))){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                
                props.getPropertyOptionsList("SUBJECT_OPTIONS").add(subjCB.getSelectionModel().getSelectedItem().toString());
                for (int i = 0; i < props.getPropertyOptionsList("SUBJECT_OPTIONS").size(); i++) {
                    boolean inList = false;
                    for (int j = 0; j < subjCB.getItems().size(); j++) {
                        if (subjCB.getItems().get(j).equals(props.getPropertyOptionsList("SUBJECT_OPTIONS").get(i))) {
                            inList = true;
                        }
                    }
                    if(!inList){
                        subjCB.getItems().add(props.getPropertyOptionsList("SUBJECT_OPTIONS").get(i));
                        
                    }
                }
                
            }
            new CourseSiteGeneratorFiles((CourseSiteGeneratorApp)app).saveOptions();
            HBox exportDirBox = ((HBox) gui.getGUINode(CSG_DIR_BOX));
            exportDirBox.getChildren().remove(exportDirBox.getChildren().size()-1);
            Label dirText = new Label(".\\export\\"+subjCB.getSelectionModel().getSelectedItem().toString()+"_"+
            numCB.getSelectionModel().getSelectedItem().toString()+"_"+semCB.getSelectionModel().getSelectedItem().toString()+"_"+
            yearCB.getSelectionModel().getSelectedItem().toString()+"\\public_html");
            exportDirBox.getChildren().add(dirText);
        });
        RadioButton all = (RadioButton) gui.getGUINode(CSG_TAS_RADIO_BUTTON_ALL);
        RadioButton undergradButton = (RadioButton) gui.getGUINode(CSG_TAS_RADIO_BUTTON_UG);
        RadioButton gradButton = (RadioButton) gui.getGUINode(CSG_TAS_RADIO_BUTTON_G);
        all.setUserData("All");
        undergradButton.setUserData("Undergraduate");
        gradButton.setUserData("Graduate");
        ToggleGroup typeGroup = new ToggleGroup();
        gradButton.setToggleGroup(typeGroup);
        undergradButton.setToggleGroup(typeGroup);
        all.setToggleGroup(typeGroup);
        TextField nameField = (TextField) gui.getGUINode(CSG_NAME_TEXT_FIELD);
        TextField emailField = (TextField) gui.getGUINode(CSG_EMAIL_TEXT_FIELD);
        globalTAButton = (Button) gui.getGUINode(CSG_ADD_TA_BUTTON);
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
                TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
                CourseSiteGeneratorData data = (CourseSiteGeneratorData)app.getDataComponent();
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
                TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
                CourseSiteGeneratorData data = (CourseSiteGeneratorData)app.getDataComponent();
                if (!CourseSiteGeneratorController.validate(emailField.getText())) {
                    emailField.setStyle("-fx-text-fill: red");
                    goodEmail = false;
                }else{
                    emailField.setStyle("-fx-text-fill: black");
                    goodEmail = true;
                }
                for(int i = 0; i < data.getAll().size(); i++){
                    if(emailField.getText().equals(data.getAll().get(i).getEmail()) || !CourseSiteGeneratorController.validate(emailField.getText())){
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
            public void onChanged(ListChangeListener.Change c){
                TablePosition tablePosition = (TablePosition) taObsList.get(0);
                Object nameVal = ((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow());
                Object emailVal = ((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow());
                Object typeVal =  ((TableColumn)taTableView.getColumns().get(3)).getCellData(tablePosition.getRow());
                copiedTA = new TeachingAssistantPrototype(nameVal.toString(),emailVal.toString(),typeVal.toString());
            }
        });
        ((TableView) gui.getGUINode(CSG_OFFICE_HOURS_TABLE_VIEW)).getSelectionModel().setCellSelectionEnabled(true);
        //EVENT HANDLING OF TA's COLUMN(OBTAINING THE TA TO PLACE INTO THE OFFICE HOURS)
        taTableView.setOnMouseClicked(e->{
            BorderPane root = new BorderPane();
            Stage editWindow = new Stage();
            ObservableList taObservableList = taTableView.getSelectionModel().getSelectedCells();
            ObservableList ohObsList = officeHoursTableView.getColumns();
            officeHoursTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
            officeHoursTableView.getSelectionModel().selectionModeProperty().set(SelectionMode.MULTIPLE);
            TablePosition tablePosition = (TablePosition) taObservableList.get(0);
            TeachingAssistantPrototype currentTa = (TeachingAssistantPrototype)taTableView.getSelectionModel().getSelectedItem();
            Object nameVal = ((TableColumn)taTableView.getColumns().get(0)).getCellData(tablePosition.getRow());
            Object emailVal = ((TableColumn)taTableView.getColumns().get(1)).getCellData(tablePosition.getRow());
            Object typeVal = ((TableColumn)taTableView.getColumns().get(3)).getCellData(tablePosition.getRow());
            int row = tablePosition.getRow();
            AppNodesBuilder ohBuilder = app.getGUIModule().getNodesBuilder();
            String nameStr = "Name";
            String emailStr = "Email";
            String typeStr = "Type: ";
            String title = "Edit Teaching Assistant";
            String ug = "Undergraduate";
            String g = "Graduate";
            String saveStr = "Save";
            String cancelStr = "Cancel";
            if(ohBuilder.getLanguageSettings().getCurrentLanguage().equalsIgnoreCase("Spanish")){
                nameStr = "Nombre";
                emailStr = "Email";
                typeStr = "Tipo: ";
                title = "Editar Los Asistentes";
                ug = "Estudiante Universitario";
                g = "Graduado";
                saveStr = "Salvar";
                cancelStr = "Cancelar";
                        
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
                root.setCenter(changePane);
                HBox buttonsBox = ohBuilder.buildHBox(CSG_DIALOG_HBOX, null, CLASS_CSG_BOX, ENABLED);
                Button edit = new Button();
                edit.setText(saveStr);
                Button cancel = new Button();
                cancel.setText(cancelStr);
                buttonsBox.setAlignment(Pos.TOP_CENTER);
                root.setBottom(buttonsBox);
                Scene scene = new Scene(root, 400, 400);
                editWindow.setScene(scene);
                editWindow.show();
                goodNameTF= false;
                goodEmailTF= false;
                buttonsBox.getChildren().addAll(edit,cancel);
                nameTF.setStyle("-fx-text-fill: red");
                emailTF.setStyle("-fx-text-fill: red");
                nameTF.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
                        TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
                        TableColumn nameCol = (TableColumn)taTableView.getColumns().get(0);
                        CourseSiteGeneratorData data = (CourseSiteGeneratorData)app.getDataComponent();
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
                        TableView taTableView = (TableView) gui.getGUINode(CSG_TAS_TABLE_VIEW);
                        CourseSiteGeneratorData data = (CourseSiteGeneratorData)app.getDataComponent();
                        for(int i = 0; i < data.getAll().size(); i++){
                            if(emailTF.getText().equals(data.getAll().get(i).getEmail())||!CourseSiteGeneratorController.validate(emailTF.getText())){
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
                    EditTA_Transaction edited =  new EditTA_Transaction(controller,nameText,emailText,studentType,row,copiedTA,(CourseSiteGeneratorApp)app);
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
                    AddTA_OHTransaction addToOHTransaction = new AddTA_OHTransaction(col,ohTimeSlots,copiedTA,(CourseSiteGeneratorApp)app);
                    app.processTransaction(addToOHTransaction);
                    controller.setCurrent(currentType);
                }
        });
        taTableView.refresh();
        officeHoursTableView.refresh();
        
        //EVENT HANDLING ADD/REMOVE LECT
        ((Button) gui.getGUINode(CSG_ADD_LECT_BUTTON)).setOnAction(e -> {
            Lecture newLect = new Lecture("?","?","?","?");
            AddLecture_Transaction addLect = new AddLecture_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newLect,true);
            app.processTransaction(addLect);
        });
        ((Button) gui.getGUINode(CSG_ADD_LAB_BUTTON)).setOnAction(e -> {
            Lab_Recitation newLab = new Lab_Recitation("?","?","?","?","?");
            AddLab_Rec_Transaction addLab = new AddLab_Rec_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newLab,"Lab",true);
            app.processTransaction(addLab);
        });
        ((Button) gui.getGUINode(CSG_ADD_REC_BUTTON)).setOnAction(e -> {
            Lab_Recitation newRec = new Lab_Recitation("?","?","?","?","?");
            AddLab_Rec_Transaction addRec = new AddLab_Rec_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newRec,"Rec",true);
            app.processTransaction(addRec);
        });
        ((Button) gui.getGUINode(CSG_REMOVE_LECT_BUTTON)).setOnAction(e -> {
            Lecture newLect = new Lecture("?","?","?","?");
            AddLecture_Transaction addLect = new AddLecture_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newLect,false);
            app.processTransaction(addLect);
        });
        ((Button) gui.getGUINode(CSG_REMOVE_LAB_BUTTON)).setOnAction(e -> {
            Lab_Recitation newLab = new Lab_Recitation("?","?","?","?","?");
            AddLab_Rec_Transaction addLab = new AddLab_Rec_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newLab,"Lab",false);
            app.processTransaction(addLab);
        });
        ((Button) gui.getGUINode(CSG_REMOVE_REC_BUTTON)).setOnAction(e -> {
            Lab_Recitation newRec = new Lab_Recitation("?","?","?","?","?");
            AddLab_Rec_Transaction addRec = new AddLab_Rec_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newRec,"Rec",false);
            app.processTransaction(addRec);
        });
        //EVENT HANDLING FOR MEETING TIMES TV
        TableColumn secLect = (TableColumn)(((TableView) gui.getGUINode(CSG_LECT_TABLE_VIEW)).getColumns().get(0));
        //
        ((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).setOnMouseClicked(e -> {
            ObservableList schedTV = ((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getSelectionModel().getSelectedCells();
            ObservableList schedColObs = ((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getColumns();
            officeHoursTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
            officeHoursTableView.getSelectionModel().selectionModeProperty().set(SelectionMode.MULTIPLE);
            TablePosition tablePosition = (TablePosition) schedTV.get(0);
            SchedItem currentItem = (SchedItem)(((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getSelectionModel().getSelectedItem());
            Object typeVal = ((TableColumn)((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getColumns().get(0)).getCellData(tablePosition.getRow());
            Object dateVal = ((TableColumn)((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getColumns().get(1)).getCellData(tablePosition.getRow());
            Object titleVal = ((TableColumn)((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getColumns().get(2)).getCellData(tablePosition.getRow());
            Object topicVal = ((TableColumn)((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getColumns().get(3)).getCellData(tablePosition.getRow());
            if(e.getClickCount() == 2 && !(titleVal.toString().equals(""))){
                ((ComboBox) gui.getGUINode(CSG_TYPE_CB)).getSelectionModel().select(typeVal.toString());
                ((TextField) gui.getGUINode(CSG_ADD_TITLE_TF)).setText(titleVal.toString());
                ((TextField) gui.getGUINode(CSG_ADD_TOPIC_TF)).setText(topicVal.toString());
                ((TextField) gui.getGUINode(CSG_ADD_LINK_TF)).setText(currentItem.getLink());
                isUpdate = true;
            }
        });
        ((Button) gui.getGUINode(CSG_ADD_ITEM_BUTTON)).setOnAction(e -> {
            if (isUpdate) {
                SchedItem currentItem = (SchedItem)(((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).getSelectionModel().getSelectedItem());
                DatePicker pickedDate = ((DatePicker)((HBox) gui.getGUINode(CSG_ADD_DATE_PANE)).getChildren().get(((HBox) gui.getGUINode(CSG_ADD_DATE_PANE)).getChildren().size()-1));
                String year = ""+ pickedDate.getValue().getYear();
                String month = ""+pickedDate.getValue().getMonthValue();
                String fullDate = pickedDate.getValue().toString();
                String date = fullDate.substring(fullDate.length()-2,fullDate.length());
                fullDate = month+"/"+date+"/"+year;
                SchedItem updateSchedItem = new SchedItem(((ComboBox) gui.getGUINode(CSG_TYPE_CB)).getSelectionModel().getSelectedItem().toString(),fullDate,((TextField) gui.getGUINode(CSG_ADD_TITLE_TF)).getText(),((TextField) gui.getGUINode(CSG_ADD_TOPIC_TF)).getText(),((TextField) gui.getGUINode(CSG_ADD_LINK_TF)).getText());
                EditSchedItem_Transaction updateItem = new EditSchedItem_Transaction((CourseSiteGeneratorData)app.getDataComponent(),updateSchedItem,currentItem);
                app.processTransaction(updateItem);
                ((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
                isUpdate = false;
            }else{
                DatePicker pickedDate = ((DatePicker)((HBox) gui.getGUINode(CSG_ADD_DATE_PANE)).getChildren().get(((HBox) gui.getGUINode(CSG_ADD_DATE_PANE)).getChildren().size()-1));
                String year = ""+ pickedDate.getValue().getYear();
                String month = ""+pickedDate.getValue().getMonthValue();
                String fullDate = pickedDate.getValue().toString();
                String date = fullDate.substring(fullDate.length()-2,fullDate.length());
                fullDate = month+"/"+date+"/"+year;
                SchedItem newItem = new SchedItem(((ComboBox) gui.getGUINode(CSG_TYPE_CB)).getSelectionModel().getSelectedItem().toString(),fullDate,((TextField) gui.getGUINode(CSG_ADD_TITLE_TF)).getText(),((TextField) gui.getGUINode(CSG_ADD_TOPIC_TF)).getText(),((TextField) gui.getGUINode(CSG_ADD_LINK_TF)).getText());
                AddSchedItem_Transaction addItem = new AddSchedItem_Transaction((CourseSiteGeneratorData)app.getDataComponent(),newItem,true);
                app.processTransaction(addItem);
                ((TableView) gui.getGUINode(CSG_SCHED_TABLE_VIEW)).refresh();
            }
        });
    } 
    private void initFoolproofDesign(){
        AppGUIModule gui = app.getGUIModule();
        AppFoolproofModule foolproofSettings = app.getFoolproofModule();
        foolproofSettings.registerModeSettings(CSG_FOOLPROOF_SETTINGS,
                new CourseSiteGeneratorFoolproofDesign((CourseSiteGeneratorApp) app));
    }

    @Override
    public void showNewDialog() {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }
    public ArrayList getImages(){
        ArrayList<String> imageList = new ArrayList();
        imageList.add(faviconFP);
        imageList.add(navbarFP);
        imageList.add(lfootFP);
        imageList.add(rfootFP);
        return imageList;
    }
}
