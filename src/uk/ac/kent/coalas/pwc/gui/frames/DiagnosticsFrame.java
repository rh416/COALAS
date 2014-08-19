package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.GButton;
import g4p_controls.GDropList;
import g4p_controls.GEvent;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEventPayload;
import uk.ac.kent.coalas.pwc.gui.ui.RowPositionTracker;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneDataRow;

import java.util.ArrayList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class DiagnosticsFrame extends WheelchairGUIFrame {

    private ArrayList<Zone> monitoredZones;
    private ArrayList<UIZoneDataRow> zoneDataRows = new ArrayList<UIZoneDataRow>();
    private int updatePeriod = 500;
    private int lastUpdateTime = 0;

    private RowPositionTracker positionTracker = new RowPositionTracker(20, 40);

    private GButton btnUpdatePause;
    private GDropList dropListUpdateRate;

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

        monitoredZones = new ArrayList<Zone>((WheelchairGUI.MAX_NODES + 1) * Node.MAX_ZONES);

        for(int i = 0; i < (WheelchairGUI.MAX_NODES + 1) * Node.MAX_ZONES; i++){
            monitoredZones.add(i, null);
        }

        btnUpdatePause = new GButton(this, 20, 10, 150, 30, s("pause"));
        btnUpdatePause.addEventHandler(this, "handleButtonEvents");

        dropListUpdateRate = new GDropList(this, 200, 10, 100, 150);
        dropListUpdateRate.setItems(new String[]{"100ms", "200ms", "500ms", "1s", "2s", "5s"}, 2);
        dropListUpdateRate.addEventHandler(this, "handleDropListEvents");

    }

    @Override
    public void setup() {

        super.setup();
        initDataOutput();
    }

    @Override
    public void draw() {
        background(255);

        if(!updatePaused && millis() - lastUpdateTime > updatePeriod){
            System.out.println(millis() - lastUpdateTime);
            System.out.println(updatePeriod);
            updateSensorData();
            System.out.println(lastUpdateTime);
        }
    }

    private void initDataOutput(){

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

            case NODE_CURRENT_DATA:


        }

    }

    private void updateSensorData(){

        for(Zone zone : monitoredZones){
            if(zone != null){
                zone.getParentNode().requestCurrentData();
            }
        }

        // Keep track of the last time this function was called so that we can update the information at the correct rate
        lastUpdateTime = millis();
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

        }
    }
}
