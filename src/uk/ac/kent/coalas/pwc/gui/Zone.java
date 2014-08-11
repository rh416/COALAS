package uk.ac.kent.coalas.pwc.gui;

import java.util.ArrayList;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Zone {

    public static enum Position{
        UNKNOWN(""),
        FRONT_CENTRE("F"), BACK_CENTRE("B"), LEFT_CENTRE("L"), RIGHT_CENTRE("R"),
            FRONT_LEFT_CORNER("1"), FRONT_RIGHT_CORNER("2"),
            BACK_RIGHT_CORNER("3"), BACK_LEFT_CORNER("4");

        private final String code;
        private Position(final String code){ this.code = code; }
        public String toString(){ return this.code; }
    }

    public static enum Orientation{
        UNKNOWN(""),
        FORWARD("a"),       // 0°
        FORWARD_RIGHT("b"), // 45°
        RIGHT("c"),          // 90°
        BACK_RIGHT("d"),     // 135°
        BACK("e"),           // 180°
        BACK_LEFT("f"),      // 225°
        LEFT("g"),           // 270°
        FORWARD_LEFT("h");   // 315°

        private final String code;
        private Orientation(final String code){ this.code = code; }
        public String toString(){ return this.code; }
    }

    private int zoneNum;
    private Position position;
    private Orientation orientation;
    private ArrayList<Sensor> sensors;

    public static ArrayList<Sensor> decodeSensorString(String sensorString){

        return new ArrayList<Sensor>();
    }

    public Zone(int zoneNumber, Position position, Orientation orientation){

        zoneNum = zoneNumber;
        this.position = position;
        this.orientation = orientation;
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

    public void setSensors(ArrayList<Sensor> inSensors){

        sensors = inSensors;
    }
}
