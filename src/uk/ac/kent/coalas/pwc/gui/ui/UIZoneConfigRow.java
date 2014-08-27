package uk.ac.kent.coalas.pwc.gui.ui;

import g4p_controls.GDropList;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.ArrayList;

/**
 * Created by rm538 on 14/08/2014.
 */
public class UIZoneConfigRow extends UIObject implements RowPositionTracker.YIncrementer {

    private WheelchairGUIFrame parent;
    private Zone srcZone;
    private RowPositionTracker positionTracker;

    GLabel zoneLabel;
    GDropList sensorSeparation;

    private String[] sensorSeparationOptions;

    private ArrayList<UISensorConfigRow> uiSensorConfigRows = new ArrayList<UISensorConfigRow>();

    public UIZoneConfigRow(WheelchairGUIFrame parent, Zone srcZone, RowPositionTracker positionTracker){

        this.parent = parent;
        this.srcZone = srcZone;
        this.positionTracker = positionTracker;

        positionTracker.resetX();

        // Create Zone Config elements
        zoneLabel = new GLabel(parent, positionTracker.getX(), positionTracker.getY(), 50, 15, String.format(s("zone_heading"), srcZone.getZoneNumber()));

        positionTracker.incrementXPosition(zoneLabel, 10);

        int separationIndex = 1;
        int selectedSeparationIndex = 0;
        sensorSeparationOptions = new String[Sensor.SensorDataInterpretation.values().length];
        sensorSeparationOptions[0] = " - ";

        for(Zone.SensorSeparation separation : Zone.SensorSeparation.values()){
            sensorSeparationOptions[separationIndex] = WheelchairGUI.firstCharUpperCase(separation.toString());

            if(srcZone.getSensorSeparation() == separation){
                selectedSeparationIndex = separationIndex;
            }
            separationIndex++;
        }

        sensorSeparation = new GDropList(parent, positionTracker.getX(), positionTracker.getY(), 80, 90);
        sensorSeparation.setItems(sensorSeparationOptions, selectedSeparationIndex);
        sensorSeparation.addEventHandler(this, "handleDropListEvents");

        positionTracker.incrementYPosition(this);

        for(Sensor.SensorType type : Sensor.SensorType.values()){
            Sensor sensor = srcZone.getSensorByType(type);
            if(sensor != null){
                uiSensorConfigRows.add(new UISensorConfigRow(parent, sensor, positionTracker));
            }
        }

        positionTracker.incrementYPosition(this);
    }

    public void draw(){
        // Do nothing
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        if(event == GEvent.SELECTED){
            if (list == sensorSeparation){
                Zone.SensorSeparation selectedSeparation = Zone.SensorSeparation.values()[list.getSelectedIndex() - 1];
                ConfigurationFrame.highlightChangedField(list, selectedSeparation, srcZone.getSensorSeparation());
            }
        }

    }

    @Override
    public int incrementY() {
        return 25;
    }
}
