package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PConstants;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.EnumMap;

/**
 * Created by rm538 on 19/08/2014.
 */
public class UIZoneDataRow extends UIObject{

    private WheelchairGUIFrame parent;
    private Zone dataZone;
    private RowPositionTracker positionTracker;

    private EnumMap<Sensor.SensorType, UISensorDataRow> sensorDataRows = new EnumMap<Sensor.SensorType, UISensorDataRow>(Sensor.SensorType.class);

    private String headingText = "";

    private static int TEXT_SIZE = 12;

    public UIZoneDataRow(WheelchairGUIFrame parent, Zone zone, RowPositionTracker positionTracker){

        this.parent = parent;
        this.dataZone = zone;
        this.positionTracker = positionTracker;

        this.headingText = "Node " + zone.getParentNode().getId() + " - Zone " + zone.getZoneNumber();

        Sensor sensor;

        for(Sensor.SensorType type : Sensor.SensorType.values()){
            sensor = zone.getSensorByType(type);
            if(sensor != null) {
                sensorDataRows.put(type, new UISensorDataRow(parent, sensor, positionTracker));
            }
        }
    }

    @Override
    public void draw(){

        // Only draw zone if its parent Node's data format is known
        if(dataZone.getParentNode().isDataFormatKnown()) {

            positionTracker.incrementYPosition(10);

            parent.fill(50);
            parent.textSize(TEXT_SIZE);
            parent.textAlign(PConstants.LEFT);
            parent.text(headingText, positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);

            positionTracker.incrementYPosition(20);

            // Draw the rows for this zone's sensors
            int sensorCount = 0;
            for(UISensorDataRow sensorDataRow : sensorDataRows.values()){
                sensorDataRow.draw();
                sensorCount ++;
            }

            if(sensorCount == 0){
                positionTracker.incrementYPosition(5);
                parent.textSize(10);
                parent.text(parent.s("zone_no_sensors"), positionTracker.getX(), positionTracker.getY());
            }
        }
    }
}
