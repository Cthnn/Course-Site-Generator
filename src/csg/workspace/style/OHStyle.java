package csg.workspace.style;

/**
 * This class lists all CSS style types for this application. These
 * are used by JavaFX to apply style properties to controls like
 * buttons, labels, and panes.

 * @author Richard McKenna
 * @version 1.0
 */
public class OHStyle {
    public static final String EMPTY_TEXT = "";
    public static final int BUTTON_TAG_WIDTH = 75;

    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS M3Workspace'S COMPONENTS TO A STYLE SHEET THAT IT USES
    // NOTE THAT FOUR CLASS STYLES ALREADY EXIST:
    // top_toolbar, toolbar, toolbar_text_button, toolbar_icon_button
    
    public static final String CLASS_CSG_PANE          = "oh_pane";
    public static final String CLASS_CSG_BOX           = "oh_box";
    public static final String CLASS_CSG_DIALOG        = "oh_dialog";
    public static final String CLASS_CSG_HEADER_LABEL  = "oh_header_label";
    public static final String CLASS_CSG_TEXT          = "oh_text";
    public static final String CLASS_CSG_LABEL         = "oh_label";
    public static final String CLASS_CSG_PROMPT        = "oh_prompt";
    public static final String CLASS_CSG_TEXT_FIELD    = "oh_text_field";
    public static final String CLASS_CSG_BUTTON        = "oh_button";
    public static final String CLASS_CSG_RADIO_BUTTON  = "oh_radio_button";
    public static final String CLASS_CSG_TABLE_VIEW    = "oh_table_view";
    public static final String CLASS_CSG_COLUMN        = "oh_column";
    public static final String CLASS_CSG_CENTERED_COLUMN = "oh_centered_column";
    public static final String CLASS_CSG_OFFICE_HOURS_TABLE_VIEW = "oh_office_hours_table_view";
    public static final String CLASS_CSG_TIME_COLUMN = "oh_time_column";
    public static final String CLASS_CSG_DAY_OF_WEEK_COLUMN = "oh_day_of_week_column";
}