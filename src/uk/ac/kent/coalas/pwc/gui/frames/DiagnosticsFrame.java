package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.*;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEventPayload;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfacePayloadNodeDataFormat;
import uk.ac.kent.coalas.pwc.gui.ui.RowPositionTracker;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneDataRow;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rm538 on 06/08/2014.
 */
public class DiagnosticsFrame extends WheelchairGUIFrame {

    private ArrayList<Zone> monitoredZones;
    private ArrayList<UIZoneDataRow> zoneDataRows;
    public static int updatePeriod = 500;
    public static int lastUpdateTime = 0;

    private static int MAX_Y_POSITION = 400;

    public static int SCALE_MAX = 2000;

    private int defaultUpdatePeriodOption = 2;
    private String[] updatePeriodOptions;

    private RowPositionTracker positionTracker = new RowPositionTracker(20, 40);

    private Pattern updatePeriodRegex = Pattern.compile("^([0-9]*)(ms|s)$");      // Use this regex to extract the number from the update period option
    private Pattern scaleValueRegex = Pattern.compile("^[0-9]*(\\.[0-9]*)?$");    // Use this regex to validate the scale value entered

    private GButton btnUpdatePause;
    private GDropList dropListUpdateRate;
    private GTextField txtScaleMax;

    private boolean updatePaused = false;

    public DiagnosticsFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public DiagnosticsFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    @Override
    public void init(){
        super.init();

        zoneDataRows = new ArrayList<UIZoneDataRow>();

        monitoredZones = new ArrayList<Zone>((WheelchairGUI.MAX_NODES + 1) * Node.MAX_ZONES);

        for(int i = 0; i < (WheelchairGUI.MAX_NODES + 1) * Node.MAX_ZONES; i++){
            monitoredZones.add(i, null);
        }

        btnUpdatePause = new GButton(this, 10, 10, 100, 30, s("pause"));
        btnUpdatePause.addEventHandler(this, "handleButtonEvents");

        updatePeriodOptions = new String[]{"100ms", "200ms", "500ms", "1s", "2s", "5s"};

        dropListUpdateRate = new GDropList(this, 120, 10, 100, 150);
        dropListUpdateRate.setItems(updatePeriodOptions, defaultUpdatePeriodOption);
        dropListUpdateRate.addEventHandler(this, "handleDropListEvents");

        txtScaleMax = new GTextField(this, 230, 10, 80, 30);
        txtScaleMax.setText(String.valueOf(SCALE_MAX));
        txtScaleMax.addEventHandler(this, "handleTextFieldEvents");


    }

    @Override
    public void setup() {

        super.setup();
        initDataOutput();
    }

    @Override
    public void draw() {
        background(255);

        positionTracker.resetY();

        if(!updatePaused && millis() - lastUpdateTime > updatePeriod){
            updateSensorData();
        }

        for(UIZoneDataRow zoneRow : zoneDataRows){
            zoneRow.draw();
            if(positionTracker.getY() > MAX_Y_POSITION){
                noStroke();
                fill(255);
                rect(0, height - 40, width, 40);
                textAlign(CENTER);
                textSize(12);
                fill(50);
                text(s("too_many_zones"), width / 2, height - 40);
                break;
            }
        }
    }

    private void initDataOutput(){

        zoneDataRows.clear();

        for(Zone zone : monitoredZones){
            if(zone != null){
                zoneDataRows.add(new UIZoneDataRow(this, zone, positionTracker));
            }
        }
    }

    private int getZoneIndex(Zone zone){

        // Return an index that is unique for this Node / Zone combination.
        // Basically, we multiply the current node number by the max number of zones, then add the specific zone number
        // This means we get a continuous line of indexes, with no gaps and no overlaps
        return (zone.getParentNode().getId() * Node.MAX_ZONES + zone.getZoneNumber()) - 1; // -1 as index is zero based
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        PWCInterfaceEvent.EventType type = e.getType();
        PWCInterfaceEventPayload payload = e.getPayload();

        switch(type){

            // When we receive the data format for a particular node
            case NODE_DATA_FORMAT:
                PWCInterfacePayloadNodeDataFormat formatPayload = (PWCInterfacePayloadNodeDataFormat) payload;
                // Get that node...
                Node node = formatPayload.getNode();
                // ... and request its data
                node.requestCurrentData();
                break;
        }

    }

    private void updateSensorData(){

        Node previousNode = null;

        for(Zone zone : monitoredZones){
            if(zone != null){
                // As this list of zones is ordered by Node then Zone Number, we can use this to only request data
                // once per Node, even if several Zones are being monitored.
                if(zone.getParentNode() != previousNode){
                    previousNode = zone.getParentNode();

                    // Do we know how this node expects to receive data?
                    if(previousNode.isDataFormatKnown()) {
                        // If we do, request some data
                        previousNode.requestCurrentData();
                    } else {
                        // If not, request the data format - there's no point requesting data otherwise
                        previousNode.requestDataFormat();
                    }
                }
            }
        }

        // Keep track of the last time this function was called so that we can update the information at the correct rate
        // lastUpdateTime = millis();       TODO: Uncomment this line for production
    }

    public void monitorZone(Zone zone){

        int listIndex = getZoneIndex(zone);
        monitoredZones.set(listIndex, zone);
        initDataOutput();
        updateSensorData();
    }

    public void stopMonitoringZone(Zone zone){

        int listIndex = getZoneIndex(zone);
        monitoredZones.set(listIndex, null);
        initDataOutput();
    }

    public boolean isZoneBeingMonitored(Zone zone){

        return (monitoredZones.get(getZoneIndex(zone)) != null);
    }


    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnUpdatePause) {
            // Switch between paused and active states
            updatePaused = !updatePaused;

            // Update button text
            if (updatePaused) {
                btnUpdatePause.setText(s("resume"));
            } else {
                btnUpdatePause.setText(s("pause"));
            }
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        if(list == dropListUpdateRate){
            if(event == GEvent.SELECTED){
                String selectedUpdatePeriod = list.getSelectedText();
                Matcher updatePeriodMatcher = updatePeriodRegex.matcher(selectedUpdatePeriod);

                if(updatePeriodMatcher.find()){
                    int newUpdatePeriod = Integer.parseInt(updatePeriodMatcher.group(1));
                    String units = updatePeriodMatcher.group(2);

                    if("s".equals(units)){
                        newUpdatePeriod = newUpdatePeriod * 1000;
                    }
                    updatePeriod = newUpdatePeriod;
                    console(updatePeriod);
                }
            }
        }
    }

    public void handleTextFieldEvents(GTextField textField, GEvent event){

        if(textField == txtScaleMax){
            String scaleVal = textField.getText().trim();
            switch(event){
                case CHANGED:
                    // Check that the entered scale is valid
                    if(isScaleValueValid(scaleVal)){
                        // If it is, style the input box normally
                        textField.setLocalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);
                    } else {
                        // If not, make the text red
                        textField.setLocalColorScheme(GConstants.RED_SCHEME);
                    }
                    break;

                case ENTERED:
                case LOST_FOCUS:
                    // Check that the value is valid
                    if(isScaleValueValid(scaleVal)){
                        // If so, use it!
                        SCALE_MAX = Integer.parseInt(scaleVal);
                    } else {
                        // Otherwise, reset the field value back to what it was
                        textField.setText(String.valueOf(SCALE_MAX));
                        // And reset the input box style
                        textField.setLocalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);
                    }
                    break;
            }
        }

    }

    private boolean isScaleValueValid(String scaleValue){

        Matcher scaleValueMatcher = scaleValueRegex.matcher(scaleValue);

        return scaleValueMatcher.find();
    }
}
