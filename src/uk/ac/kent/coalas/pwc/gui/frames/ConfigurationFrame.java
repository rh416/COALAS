package uk.ac.kent.coalas.pwc.gui.frames;

import controlP5.ControlEvent;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;

/**
 * Created by rm538 on 06/08/2014.
 */
public class ConfigurationFrame extends WheelchairGUIFrame {

    private Node node;

    public ConfigurationFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    public void setup() {

        super.setup();

        for(int i = 0; i < Node.MAX_ZONES; i++){
            Zone zone = node.getZone(i + 1);
        }
    }

    @Override
    public void draw() {

        background(255);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {


    }

    public void setConfigNode(Node node){

        this.node = node;
    }

    public void setupZoneRow(Zone zone){

        Sensor IRSensor = zone.getSensorByType(Sensor.SensorType.INFRARED);
        Sensor UltrasoundSensor = zone.getSensorByType(Sensor.SensorType.ULTRASONIC);

    }
}
