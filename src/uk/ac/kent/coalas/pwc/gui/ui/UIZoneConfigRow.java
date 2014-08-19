package uk.ac.kent.coalas.pwc.gui.ui;

import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.ArrayList;

/**
 * Created by rm538 on 14/08/2014.
 */
public class UIZoneConfigRow extends UIObject {

    private WheelchairGUIFrame parent;
    private Zone srcZone;
    private RowPositionTracker positionTracker;

    private ArrayList<UISensorConfigRow> uiSensorConfigRows = new ArrayList<UISensorConfigRow>();

    public UIZoneConfigRow(WheelchairGUIFrame parent, Zone srcZone, RowPositionTracker positionTracker){

        this.parent = parent;
        this.srcZone = srcZone;
        this.positionTracker = positionTracker;

        for(Sensor.SensorType type : Sensor.SensorType.values()){
            Sensor sensor = srcZone.getSensorByType(type);
            if(sensor != null){
                uiSensorConfigRows.add(new UISensorConfigRow(parent, sensor, positionTracker));
            }
        }

        positionTracker.incrementYPosition(this.getClass());
    }

    public void draw(){
        // Do nothing
    }
}
