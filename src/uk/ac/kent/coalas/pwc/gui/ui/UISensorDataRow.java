package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PConstants;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.util.Random;

/**
 * Created by rm538 on 19/08/2014.
 */
public class UISensorDataRow extends UIObject implements RowPositionTracker.YIncrementer{

    private WheelchairGUIFrame parent;
    private Sensor sensor;
    private RowPositionTracker positionTracker;

    private static int BAR_HEIGHT = 10;
    private static int BAR_MAX_WIDTH = 200;

    private static int BAR_FILL_COLOUR_US = 0xFF00FF00;
    private static int BAR_FILL_COLOUR_IR = 0xFFFF0000;
    private static int BAR_FILL_COLOUR_FUSED = 0xFF0000FF;

    private static int TEXT_SIZE = 10;

    public UISensorDataRow(WheelchairGUIFrame parent, Sensor sensor, RowPositionTracker positionTracker){

        this.parent = parent;
        this.positionTracker = positionTracker;
        this.sensor = sensor;
    }

    public void draw(){

        if(sensor == null){
            return;
        }

        positionTracker.resetX();

        parent.fill(50);
        parent.textSize(TEXT_SIZE);
        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getCurrentValue(), positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);
        positionTracker.incrementXPosition(25);

        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getDataInterpretation().getSuffix(), positionTracker.getX(), positionTracker.getY() + TEXT_SIZE);
        positionTracker.incrementXPosition(15);

        double barWidth;
        int currentValue;

        if(sensor.getDataInterpretation() == Sensor.SensorDataInterpretation.THRESHOLD){
            barWidth = BAR_MAX_WIDTH;
            currentValue = (sensor.getCurrentBoolValue() ? 1 : 0);
        } else {
            barWidth = ((double) BAR_MAX_WIDTH / (double) DiagnosticsFrame.SCALE_MAX);
            currentValue = sensor.getCurrentValue();

            System.out.println(parent.millis());
            System.out.println(parent.millis() - DiagnosticsFrame.lastUpdateTime);
            System.out.println(DiagnosticsFrame.updatePeriod);

            if(parent.frameCount % (DiagnosticsFrame.updatePeriod / 100) == 0) {
                sensor.parseDataString(String.format("%04x", new Random().nextInt(DiagnosticsFrame.SCALE_MAX)));
            }
        }

        barWidth = barWidth * currentValue;

        if(sensor.getType() == Sensor.SensorType.ULTRASONIC) {
            parent.fill(BAR_FILL_COLOUR_US);
        } else if(sensor.getType() == Sensor.SensorType.INFRARED){
            parent.fill(BAR_FILL_COLOUR_IR);
        } else if(sensor.getType() == Sensor.SensorType.FUSED){
            parent.fill(BAR_FILL_COLOUR_FUSED);
        }
        parent.stroke(0);
        parent.rect(positionTracker.getX(), positionTracker.getY(), (int) barWidth, BAR_HEIGHT);

        parent.fill(50);
        parent.textAlign(PConstants.CENTER);
        parent.text(sensor.getType().getName(), 300, positionTracker.getY() + TEXT_SIZE);

        positionTracker.incrementYPosition(this);
    }

    @Override
    public int incrementY() {
        return 15;
    }
}
