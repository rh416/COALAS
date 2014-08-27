package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PConstants;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.EnumMap;

/**
 * Created by rm538 on 19/08/2014.
 *
 * A class for handling drawing zone sensor data to the screen
 *
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

        // Create the heading text for this Zone
        this.headingText = String.format(s("zone_data_heading"), zone.getParentNode().getId(), zone.getZoneNumber());

        for(Sensor.SensorType type : Sensor.SensorType.values()){
            Sensor sensor = zone.getSensorByType(type);
            if(sensor != null) {
                sensorDataRows.put(type, new UISensorDataRow(parent, sensor, positionTracker));
            }
        }
    }

    @Override
    public void draw(){

        Node parentNode = dataZone.getParentNode();

        // Only draw zone if its parent Node's data format is known
        if(parentNode.isDataFormatKnown()) {

            positionTracker.incrementYPosition(10);

            // Output Zone heading
            parent.fill(50);
            parent.textSize(TEXT_SIZE);
            parent.textAlign(PConstants.LEFT);
            parent.text(headingText, positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);

            positionTracker.incrementYPosition(20);

            // If this zone's parent node has timed-out, show a message
            if(parentNode.hasTimedOut()){
                positionTracker.incrementYPosition(5);
                parent.textSize(10);
                parent.text(String.format(parent.s("zone_node_timed_out"), parentNode.getId()), positionTracker.getX(), positionTracker.getY());
            } else {
                // Draw the rows for this zone's sensors
                int sensorCount = 0;
                for (UISensorDataRow sensorDataRow : sensorDataRows.values()) {
                    sensorDataRow.draw();
                    sensorCount++;
                }

                // If there are no sensors within this Zone, output a message to indicate that.
                if (sensorCount == 0) {
                    positionTracker.incrementYPosition(5);
                    parent.textSize(10);
                    parent.text(parent.s("zone_no_sensors"), positionTracker.getX(), positionTracker.getY());
                }
            }
        }
    }
}
