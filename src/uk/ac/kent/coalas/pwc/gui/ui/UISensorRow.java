package uk.ac.kent.coalas.pwc.gui.ui;

import g4p_controls.*;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;

/**
 * Created by rm538 on 14/08/2014.
 */
public class UISensorRow extends UIObject {

    private WheelchairGUIFrame parent;
    private Sensor srcSensor;
    private RowPositionTracker positionTracker;

    private GLabel LabelSensor;
    private GDropList DropListSeparation, DropListDataInterpretation, DropListMode;
    private GTextField TextThreshold;

    public UISensorRow(WheelchairGUIFrame parent, Sensor sensor, RowPositionTracker positionTracker){

        this.parent = parent;
        this.srcSensor = sensor;
        this.positionTracker = positionTracker;

        RowPositionTracker pt = positionTracker;

        pt.resetX();

        String sensorLabel = "F";

        if(srcSensor.getType() == Sensor.SensorType.ULTRASONIC){
            sensorLabel = "US";
        } else if(srcSensor.getType() == Sensor.SensorType.INFRARED){
            sensorLabel = "IR";
        }

        LabelSensor = new GLabel(parent, pt.getX(), pt.getY() + 3, 35, 15, sensorLabel);

        pt.incrementXPosition(35);

        DropListSeparation = new GDropList(parent, pt.getX(), pt.getY(), 65, 120);
        DropListSeparation.setItems(new String[]{"Separate", "Fused"}, 0);
        DropListSeparation.addEventHandler(this, "handleDropListEvents");

        pt.incrementXPosition(70);

        DropListDataInterpretation = new GDropList(parent, pt.getX(), pt.getY(), 65, 120);
        DropListDataInterpretation.setItems(new String[]{"Disabled", "Raw", "cm", "Threshold"}, 0);
        DropListDataInterpretation.addEventHandler(this, "handleDropListEvents");

        pt.incrementXPosition(70);

        TextThreshold = new GTextField(parent, pt.getX(), pt.getY(), 30, 15);

        pt.incrementXPosition(35);

        if(srcSensor.getType() == Sensor.SensorType.ULTRASONIC) {
            DropListMode = new GDropList(parent, pt.getX(), pt.getY(), 75, 120);
            DropListMode.setItems(new String[]{"Continuous", "Pulsed"}, 0);
            DropListMode.addEventHandler(this, "handleDropListEvents");
        }


        positionTracker.incrementYPosition(UISensorRow.class);
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        System.out.println("Event: " + event);

        if(event == GEvent.SELECTED){
            if(list == DropListSeparation){

            } else if (list == DropListDataInterpretation){

            } else if (list == DropListMode){

            }
            list.setLocalColorScheme(GConstants.YELLOW_SCHEME);
        }
    }

    public void draw(){



    }
}
