package uk.ac.kent.coalas.pwc.gui.ui;

import g4p_controls.*;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;

/**
 * Created by rm538 on 14/08/2014.
 */
public class UISensorConfigRow extends UIObject implements RowPositionTracker.YIncrementer {

    private WheelchairGUIFrame parent;
    private Sensor srcSensor;
    private RowPositionTracker positionTracker;
    private GPanel parentPanel;

    private GLabel LabelSensor;
    private GDropList DropListDataInterpretation;

    private  String[] dataInterpretationOptions;

    public UISensorConfigRow(WheelchairGUIFrame parent, Sensor sensor, RowPositionTracker positionTracker, GPanel parentPanel){

        this.parent = parent;
        this.srcSensor = sensor;
        this.positionTracker = positionTracker;
        this.parentPanel = parentPanel;

        RowPositionTracker pt = positionTracker;

        pt.resetX();
        pt.incrementXPosition(20);  // Indent these controls

        String sensorLabel = "Fused";

        if(srcSensor.getType() == Sensor.SensorType.ULTRASONIC){
            sensorLabel = "Ultrasound";
        } else if(srcSensor.getType() == Sensor.SensorType.INFRARED){
            sensorLabel = "Infrared";
        }

        LabelSensor = new GLabel(parent, pt.getX(), pt.getY() + 3, 75, 15, sensorLabel);

        pt.incrementXPosition(LabelSensor, 0);

        int interpretationIndex = 1;
        int selectedInterpretationIndex = 0;
        dataInterpretationOptions = new String[Sensor.SensorDataInterpretation.values().length];
        dataInterpretationOptions[0] = " - ";

        for(Sensor.SensorDataInterpretation interpretation : Sensor.SensorDataInterpretation.values()){
            // Ignore UNKNOWN
            if(interpretation == Sensor.SensorDataInterpretation.UNKNOWN){
                continue;
            }
            dataInterpretationOptions[interpretationIndex] = WheelchairGUI.firstCharUpperCase(interpretation.toString());

            if(srcSensor.getDataInterpretation() == interpretation){
                selectedInterpretationIndex = interpretationIndex;
            }
            interpretationIndex++;
        }

        DropListDataInterpretation = new GDropList(parent, pt.getX(), pt.getY(), 80, 90);
        DropListDataInterpretation.setItems(dataInterpretationOptions, selectedInterpretationIndex);
        DropListDataInterpretation.addEventHandler(this, "handleDropListEvents");

        parentPanel.addControl(LabelSensor);
        parentPanel.addControl(DropListDataInterpretation);

        pt.incrementXPosition(DropListDataInterpretation, 5);


        positionTracker.incrementYPosition(this);
    }

    public String validateAndReturnResponse(){

        int selectedIndex = DropListDataInterpretation.getSelectedIndex();

        // Reset the field to the correct colour
        handleDropListEvents(DropListDataInterpretation, GEvent.SELECTED);

        // Check that a valid option has been selected
        if(selectedIndex == 0){
            DropListDataInterpretation.setLocalColorScheme(WheelchairGUI.ERROR_COLOUR_SCHEME);
            return null;
        }

        Sensor.SensorDataInterpretation sensorDataInterpretation = Sensor.SensorDataInterpretation.values()[selectedIndex];
        return String.valueOf(sensorDataInterpretation.getRepresentation()).toUpperCase();
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        if(event == GEvent.SELECTED){
            if (list == DropListDataInterpretation){
                Sensor.SensorDataInterpretation selectedInterpretation = Sensor.SensorDataInterpretation.values()[list.getSelectedIndex()];
                ConfigurationFrame.highlightChangedField(list, selectedInterpretation, srcSensor.getDataInterpretation());
            }
        }
    }

    @Override
    public int incrementY() {
        return 25;
    }
}
