package uk.ac.kent.coalas.pwc.gui.hardware;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Zone {

    public static enum Position{
        UNKNOWN(""), FRONT_CENTRE("F"), BACK_CENTRE("B"), LEFT_CENTRE("L"), RIGHT_CENTRE("R"),
        FRONT_LEFT_CORNER("1"), FRONT_RIGHT_CORNER("2"), BACK_RIGHT_CORNER("3"), BACK_LEFT_CORNER("4");

        private final String code;
        private Position(final String code){ this.code = code; }
        public String toString(){ return this.code; }
    }

    public static enum Orientation{
        UNKNOWN("", 0),
        FORWARD("a", 0),          // 0°
        FORWARD_RIGHT("b", 45),   // 45°
        RIGHT("c", 90),           // 90°
        BACK_RIGHT("d", 135),     // 135°
        BACK("e", 180),           // 180°
        BACK_LEFT("f", 225),      // 225°
        LEFT("g", 270),           // 270°
        FORWARD_LEFT("h", 315);   // 315°

        private final String code;
        private final int angle;
        private Orientation(final String code, final int angle){
            this.code = code;
            this.angle = angle;
        }
        public String toString(){ return this.code; }
        public int getAngle(){ return this.angle; }
    }

    private Node parentNode;
    private int zoneNum;
    private Position position;
    private Orientation orientation;
    private ArrayList<Sensor> sensors;
    private HashMap<Integer, Integer> sensorByType = new HashMap<Integer, Integer>();

    public Zone(Node parentNode, int zoneNumber, Position position, Orientation orientation){

        this.parentNode = parentNode;
        zoneNum = zoneNumber;
        this.position = position;
        this.orientation = orientation;
    }

    public Node getParentNode(){

        return parentNode;
    }

    public Orientation getOrientation(){

        return orientation;
    }

    public Position getPosition(){

        return position;
    }

    public int getZoneNumber(){

        return zoneNum;
    }

    public Sensor getSensorByType(Sensor.SensorType sensorType){

        return getSensorByTypeBitmask(sensorType.getBitmask() + getZoneNumber());
    }

    public Sensor getSensorByTypeBitmask(int typeBitmask){

        int checkZoneNum = typeBitmask & 0x3;   // Bitmask = 0x3  -> 11 (as we need to get the 1st and second bits)
        int sensorType = typeBitmask & 0x1C;    // Bitmask = Ox1C -> 11100 (as we need to get the 3rd, 4th & 5th bits

        if(getZoneNumber() != checkZoneNum){
            return null;
        }

        Integer index = sensorByType.get(sensorType);
        if(index != null){
            return sensors.get(index);
        } else {
            return null;
        }
    }

    public void setSensors(ArrayList<Sensor> inSensors){

        sensors = inSensors;
        int index = 0;
        for(Sensor sensor : sensors){
            sensorByType.put(sensor.getType().getBitmask(), index);
            index++;
        }
    }
}
