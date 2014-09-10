package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.*;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;
import uk.ac.kent.coalas.pwc.gui.ui.RowPositionTracker;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneConfigRow;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by rm538 on 06/08/2014.
 */
public class ConfigurationFrame extends WheelchairGUIFrame {


    private static enum PanelId {SENSOR_CONFIG, THRESHOLDS, ULTRASOUND_MODE};
    private EnumMap<PanelId, GPanel> panels;

    private Node node;

    private GLabel lblConfigNodeTitle;
    private GButton btnConfigureChair, btnSetMode, btnSetThresholds;
    private GDropList listUltrasoundMode;
    private GLabel lblThreshold1, lblThreshold2, lblThreshold3;
    private GTextField txtThreshold1, txtThreshold2, txtThreshold3;

    private ArrayList<UIZoneConfigRow> uiZoneConfigRows = new ArrayList<UIZoneConfigRow>();


    public static void highlightChangedField(GAbstractControl field, Object originalValue, Object newValue){

        if(newValue.equals(originalValue)){
            field.setLocalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);
        } else {
            field.setLocalColorScheme(WheelchairGUI.HIGHLIGHT_COLOUR_SCHEME);
        }
    }



    public ConfigurationFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public ConfigurationFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    @Override
    public void init() {
        super.init();

        panels = new EnumMap(PanelId.class);

        // Create window title
        lblConfigNodeTitle = new GLabel(this, 0, 10, width, 20);

        setConfigAppearance();
    }

    @Override
    public void draw() {
        background(255); // Must remain so that controls are rendered properly
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        PWCInterfaceEvent.EventType eventType = e.getType();

        PWCInterfaceEvent.EventType sourceEventType = null;
        Node sourceNode = null;

        GButton btnToUpdate = null;

        boolean responsePositive = false;

        switch(eventType){

            // Handle Acks, Nacks and Timeouts from the Wheelchairs Interface
            case ACK:
            case NACK:
            case TIMEOUT:
                // Get the request that represents the request this event is in response to
                PWCInterfaceRequest request;
                if(eventType == PWCInterfaceEvent.EventType.TIMEOUT){
                    PWCInterfacePayloadTimeout timeoutPayload = (PWCInterfacePayloadTimeout)e.getPayload();
                    request = timeoutPayload.getRequest();
                } else {
                    PWCInterfacePayloadAckNack ackNackPayload = (PWCInterfacePayloadAckNack)e.getPayload();
                    request = ackNackPayload.getRequest();
                }
                // Get the original request type and node
                sourceEventType = request.getType();
                sourceNode = request.getNode();

                // If we are receiving an Ack, that is a good thing - anything else is bad
                responsePositive = (eventType == PWCInterfaceEvent.EventType.ACK);
                break;

            case DISCONNECTED:
                // Close the window when the chair is disconnected
                getFrame().dispatchEvent(new WindowEvent(getFrame(), WindowEvent.WINDOW_CLOSING));
                break;
        }

        // If the source event has been determined, and the node it was acting on is the one we are currently configuring
        if(sourceEventType != null && sourceNode == node){
            // Determine which button we want to update
            if(sourceEventType == PWCInterfaceEvent.EventType.NODE_CONFIGURATION){
                btnToUpdate = btnConfigureChair;
            } else if(sourceEventType == PWCInterfaceEvent.EventType.NODE_THRESHOLDS){
                btnToUpdate = btnSetThresholds;
            } else if(sourceEventType == PWCInterfaceEvent.EventType.NODE_MODE){
                btnToUpdate = btnSetMode;
            }
        }

        // If we found a button to update
        if(btnToUpdate != null){
            // If saving went well, display the relevant message
            if(responsePositive){
                btnToUpdate.setText(s("changes_saved"));
                btnToUpdate.setLocalColorScheme(WheelchairGUI.SUCCESS_COLOUR_SCHEME);
            } else {
                // Otherwise, inform the user that something went wrong
                btnToUpdate.setText(s("changes_failed"));
                btnToUpdate.setLocalColorScheme(WheelchairGUI.ERROR_COLOUR_SCHEME);
            }
        }
    }

    public void setConfigNode(Node node){

        this.node = node;
        setConfigAppearance();
    }

    private void setConfigAppearance(){

        if(node != null){
            String windowTitle = String.format(s("title_config"), node.getId());
            getFrame().setTitle(windowTitle);
            lblConfigNodeTitle.setText(windowTitle);

            createSensorConfigPanel();
            createThresholdsPanel();
            createUltrasoundModePanel();

            // Expand sensor config panel
            GPanel showPanelFirst = panels.get(PanelId.SENSOR_CONFIG);
            showPanelFirst.setCollapsed(false);
            handlePanelEvents(showPanelFirst, GEvent.EXPANDED);
        }
    }

    private void createSensorConfigPanel(){

        GPanel panel = new GPanel(this, 20, 40, 280, 350, s("heading_sensor_config"));
        panel.setDraggable(false);
        panel.addEventHandler(this, "handlePanelEvents");
        panel.setCollapsed(true);
        panel.setLocalColorScheme(WheelchairGUI.PANEL_COLOUR_SCHEME);


        RowPositionTracker panelTracker = new RowPositionTracker(10, 30);

        panels.put(PanelId.SENSOR_CONFIG, panel);

        for(int i = 0; i < Node.MAX_ZONES; i++){
            Zone zone = node.getZone(i + 1);
            if(zone.getPosition() != Zone.Position.UNKNOWN){
                uiZoneConfigRows.add(new UIZoneConfigRow(this, zone, panelTracker, panel));
            }
        }

        panelTracker.setX(40);

        // Create Configure Chair button
        btnConfigureChair = new GButton(this, 50, panel.getHeight() - 35, 180, 25, s("send_config_to_chair"));
        btnConfigureChair.addEventHandler(this, "handleButtonEvents");

        panel.addControl(btnConfigureChair);

    }

    private void createThresholdsPanel(){

        RowPositionTracker panelTracker = new RowPositionTracker(10, 30);

        lblThreshold1 = new GLabel(this, panelTracker.getX(), panelTracker.getY(), 70, 25, String.format(s("zone_heading"), 1));
        panelTracker.incrementXPosition(lblThreshold1, 25);

        lblThreshold2 = new GLabel(this, panelTracker.getX(), panelTracker.getY(), 70, 25, String.format(s("zone_heading"), 2));
        panelTracker.incrementXPosition(lblThreshold2, 25);

        lblThreshold3 = new GLabel(this, panelTracker.getX(), panelTracker.getY(), 70, 25, String.format(s("zone_heading"), 3));
        panelTracker.incrementXPosition(lblThreshold3, 25);

        panelTracker.resetX();
        panelTracker.incrementYPosition(lblThreshold3, 0);

        txtThreshold1 = new GTextField(this, panelTracker.getX(), panelTracker.getY(), 70, 25);
        panelTracker.incrementXPosition(txtThreshold1, 25);

        txtThreshold2 = new GTextField(this, panelTracker.getX(), panelTracker.getY(), 70, 25);
        panelTracker.incrementXPosition(txtThreshold2, 25);

        txtThreshold3 = new GTextField(this, panelTracker.getX(), panelTracker.getY(), 70, 25);

        panelTracker.setX(70);
        panelTracker.incrementYPosition(txtThreshold3, 20);


        btnSetThresholds = new GButton(this, panelTracker.getX(), panelTracker.getY(), 140, 25, s("set_zone_thresholds"));
        btnSetThresholds.addEventHandler(this, "handleButtonEvents");

        GPanel panel = new GPanel(this, 20, 40, 280, 140, s("heading_set_zone_thresholds"));
        panel.setDraggable(false);
        panel.addEventHandler(this, "handlePanelEvents");
        panel.setCollapsed(true);
        panel.setLocalColorScheme(WheelchairGUI.PANEL_COLOUR_SCHEME);

        panel.addControl(lblThreshold1);
        panel.addControl(lblThreshold2);
        panel.addControl(lblThreshold3);
        panel.addControl(txtThreshold1);
        panel.addControl(txtThreshold2);
        panel.addControl(txtThreshold3);
        panel.addControl(btnSetThresholds);

        panels.put(PanelId.THRESHOLDS, panel);
    }

    private void createUltrasoundModePanel(){

        RowPositionTracker panelTracker = new RowPositionTracker(70, 30);

        // Create Ultrasound Mode Dropdown List
        int ultrasoundModeIndex = 0;
        String[] ultrasoundModeOptions = new String[PWCInterface.UltrasoundMode.values().length];

        for(PWCInterface.UltrasoundMode mode : PWCInterface.UltrasoundMode.values()){
            ultrasoundModeOptions[ultrasoundModeIndex] = WheelchairGUI.firstCharUpperCase(mode.toString());
            ultrasoundModeIndex++;
        }

        listUltrasoundMode = new GDropList(this, panelTracker.getX(), panelTracker.getY(), 140, 90);
        listUltrasoundMode.setItems(ultrasoundModeOptions, 0);

        panelTracker.incrementYPosition(30);

        btnSetMode = new GButton(this, panelTracker.getX(), panelTracker.getY(), 140, 25, s("set_ultrasound_mode"));
        btnSetMode.addEventHandler(this, "handleButtonEvents");


        GPanel panel = new GPanel(this, 20, 80, 280, 100, s("heading_set_ultrasound_mode"));
        panel.setDraggable(false);
        panel.addEventHandler(this, "handlePanelEvents");
        panel.setCollapsed(true);
        panel.setLocalColorScheme(WheelchairGUI.PANEL_COLOUR_SCHEME);

        panel.addControl(listUltrasoundMode);
        panel.addControl(btnSetMode);

        panels.put(PanelId.ULTRASOUND_MODE, panel);
    }

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnConfigureChair){
            String configString = "";
            String zoneConfigStr;
            for(UIZoneConfigRow zoneConfigRow : uiZoneConfigRows){
                zoneConfigStr = zoneConfigRow.validateAndReturnResponse();
                if(zoneConfigStr == null){
                    return;
                } else {
                    configString += zoneConfigStr;
                }
            }
            parent.getChairInterface().configureNodeSensors(node, configString);
            button.setText(s("changes_saving"));
        } else if(button == btnSetThresholds){
            if(validateFieldNumeric(txtThreshold1) && validateFieldNumeric(txtThreshold2) && validateFieldNumeric(txtThreshold3)){
                parent.getChairInterface().setNodeThresholds(node,
                        Integer.parseInt(txtThreshold1.getText()),
                        Integer.parseInt(txtThreshold2.getText()),
                        Integer.parseInt(txtThreshold3.getText()));
                button.setText(s("changes_saving"));
            }
        } else if(button == btnSetMode){
            PWCInterface.UltrasoundMode newMode = PWCInterface.UltrasoundMode.values()[listUltrasoundMode.getSelectedIndex()];
            parent.getChairInterface().setNodeUltrasoundMode(node, newMode);
            button.setText(s("changes_saving"));
        }
    }

    public void handlePanelEvents(GPanel panel, GEvent event){

        if(event == GEvent.COLLAPSED || event == GEvent.EXPANDED){
            // If a panel has just been expanded, collapse all of the rest
            if(event == GEvent.EXPANDED){
                for(GPanel loopPanel : panels.values()){
                    if(loopPanel != panel){
                        loopPanel.setCollapsed(true);
                    }
                }
            }

            // Recalculate the position of each panel
            RowPositionTracker panelTracker = new RowPositionTracker(20, 40);
            for(GPanel loopPanel : panels.values()){
                loopPanel.moveTo(panelTracker.getX(), panelTracker.getY());
                // If this panel is collapsed, increment Y by its tab height
                if(loopPanel.isCollapsed()){
                    panelTracker.incrementYPosition((int) panel.getTabHeight());
                } else {
                    // Otherwise, expand it by its height
                    panelTracker.incrementYPosition((int) panel.getHeight());
                }
                // Add a little padding
                panelTracker.incrementYPosition(5);
            }
        }
    }

    public boolean validateFieldNumeric(GTextField field){

        return validateFieldNumeric(field, field.getText());
    }

    public boolean validateFieldNumeric(GDropList field){

        return validateFieldNumeric(field, field.getSelectedText());
    }

    public boolean validateFieldNumeric(GAbstractControl field, String fieldValue){

        if(fieldValue != null && !fieldValue.isEmpty() && isStringNumeric(fieldValue)){
            field.setLocalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);
            return true;
        } else {
            field.setLocalColorScheme(WheelchairGUI.ERROR_COLOUR_SCHEME);
            return false;
        }
    }

    public boolean isStringNumeric(String checkString){

        for (char c : checkString.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

}
