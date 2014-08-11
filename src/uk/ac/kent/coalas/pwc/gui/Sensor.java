package uk.ac.kent.coalas.pwc.gui;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Sensor {

    private Type type;

    public static enum Type{
        ULTRASONIC(4), INFRARED(8), FUSED(12);

        private final int bitmask;
        private Type(final int code){ this.bitmask = code; }
        public int getBitmask(){ return this.bitmask; }
    }

    public Sensor(Type sensorType){
        type = sensorType;
    }
}
