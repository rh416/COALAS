package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PConstants;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.Random;

/**
 * Created by rm538 on 19/08/2014.
 *
 * A class for handling drawing sensor data to the screen
 *
 */
public class UISensorDataRow extends UIObject implements RowPositionTracker.YIncrementer{

    private WheelchairGUIFrame parent;
    private Sensor sensor;
    private RowPositionTracker positionTracker;

    private int BAR_HEIGHT = 10;
    private int BAR_MAX_WIDTH = 225;

    private int BAR_FILL_COLOUR_US = 0xFF68DC52;
    private int BAR_FILL_COLOUR_IR = 0xFFFF463B;
    private int BAR_FILL_COLOUR_FUSED = 0xFF0F89D4;

    private int TEXT_SIZE = 10;

    public UISensorDataRow(WheelchairGUIFrame parent, Sensor sensor, RowPositionTracker positionTracker){

        this.parent = parent;
        this.positionTracker = positionTracker;
        this.sensor = sensor;
    }

    @Override
    public void draw(){

        // If the sensor that this row relates to doesn't exist, quit
        if(sensor == null){
            return;
        }

        // Align ourselves to the left
        positionTracker.resetX();

        // Output the current value
        parent.fill(50);
        parent.textSize(TEXT_SIZE);
        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getCurrentValue(), positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);
        positionTracker.incrementXPosition(25);

        // Output the interpretation of this value
        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getDataInterpretation().getSuffix(), positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);
        positionTracker.incrementXPosition(15);


        // Scale the bar to represent the value
        double barWidth, barWidthProportion;

        // If the sensor's data is boolean, either make the bar visible or not
        if(sensor.getDataInterpretation() == Sensor.SensorDataInterpretation.THRESHOLD){
            barWidthProportion = (sensor.getCurrentBoolValue() ? 1 : 0);
        } else {
            // Otherwise, calculate the
            barWidthProportion = (double) sensor.getCurrentValue() / (double) DiagnosticsFrame.SCALE_MAX;

            // TODO: Remove before production
            if(parent.frameCount % 30 == 0) {
                sensor.parseDataString(String.format("%04x", new Random().nextInt(DiagnosticsFrame.SCALE_MAX)));
            }
        }

        barWidth = BAR_MAX_WIDTH * barWidthProportion;

        // Set the colour of the bar, depending on the sensor type
        if(sensor.getType() == Sensor.SensorType.ULTRASONIC) {
            parent.fill(BAR_FILL_COLOUR_US);
        } else if(sensor.getType() == Sensor.SensorType.INFRARED){
            parent.fill(BAR_FILL_COLOUR_IR);
        } else if(sensor.getType() == Sensor.SensorType.FUSED){
            parent.fill(BAR_FILL_COLOUR_FUSED);
        }
        // Draw the bar
        parent.stroke(0);
        parent.rect(positionTracker.getX(), positionTracker.getY(), (int) barWidth, BAR_HEIGHT);

        // Output the type of sensor as an additional reminder
        parent.fill(50);
        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getType().getName(), 300, positionTracker.getY() + TEXT_SIZE);

        // Move down the screen, ready for the next row
        positionTracker.incrementYPosition(this);
    }

    @Override
    public int incrementY() {
        return 15;
    }
}
