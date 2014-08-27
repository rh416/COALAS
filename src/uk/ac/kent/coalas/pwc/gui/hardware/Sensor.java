package uk.ac.kent.coalas.pwc.gui.hardware;

import uk.ac.kent.coalas.pwc.gui.ui.UISensorDataRow;

import java.util.Random;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Sensor {

    private SensorType sensorType;
    private SensorDataFormat dataFormat = SensorDataFormat.UNKNOWN;
    private SensorDataInterpretation dataInterpretation = SensorDataInterpretation.UNKNOWN;
    private boolean faulty = false;
    private short currentValue = 0;

    public static enum SensorType {
        ULTRASONIC("US", 4), INFRARED("IR", 8), FUSED("F", 12); //, ADDITIONAL_SENSOR_TYPE(16);

        private final String name;
        private final int bitmask;
        private SensorType(final String name, final int code){
            this.name = name;
            this.bitmask = code;

        }
        public String getName(){ return this.name; }
        public int getBitmask(){ return this.bitmask; }
    }

    public static enum SensorDataFormat{
        UNKNOWN('-', 0), BYTE('B', 2), WORD('W', 4), DWORD('D', 8), LOGIC('L', 1);

        private final char representation;
        private final int charLength;
        private SensorDataFormat(final char representation,  int charLength){
            this.representation = representation;
            this.charLength = charLength;
        }
        public char getRepresentation(){ return this.representation; }
        public int getCharLength(){ return this.charLength; }
    }

    public static enum SensorDataInterpretation{
        // UNKNOWN MUST BE FIRST! The rest don't matter
        UNKNOWN('u', " - "), DISABLED('0', "D"), CM('c', "cm"), RAW('r', "raw"), THRESHOLD('1', "T");

        private final char representation;
        private final String suffix;
        private SensorDataInterpretation(final char representation, final String suffix){
            this.representation = representation;
            this.suffix = suffix;
        }
        public char getRepresentation(){ return this.representation; }
        public String getSuffix(){ return this.suffix; }
    }

    public static SensorDataFormat getDataFormatFromCharacter(char dataFormatChar){

        SensorDataFormat responseFormat = SensorDataFormat.UNKNOWN;

        // Cycle through all the data formats to see which one is represented by the given character
        for(SensorDataFormat format : SensorDataFormat.values()){
            if(dataFormatChar == format.getRepresentation()){
                responseFormat = format;
                break;
            }
        }

        return responseFormat;
    }

    public static SensorDataInterpretation getDataInterpretationFromCharacter(char dataInterpretationChar) {

        SensorDataInterpretation responseInterpretation = SensorDataInterpretation.UNKNOWN;

        // Cycle through all the data representations to see which one is represented by the given character
        for (SensorDataInterpretation interpretation : SensorDataInterpretation.values()) {
            if (dataInterpretationChar == interpretation.getRepresentation()) {
                responseInterpretation = interpretation;
                break;
            }
        }

        return responseInterpretation;
    }

    public Sensor(SensorType sensorType){
        this.sensorType = sensorType;
    }

    public SensorDataFormat getDataFormat() {
        return dataFormat;
    }

    public SensorDataInterpretation getDataInterpretation() {
        return dataInterpretation;
    }

    public SensorType getType(){

        return sensorType;
    }

    public short getCurrentValue(){

        return currentValue;
    }

    public boolean getCurrentBoolValue(){

        return (getCurrentValue() == 1);
    }

    public void parseDataString(String currentSensorData) {

        currentValue = (short)Integer.parseInt(currentSensorData, 16); // Get the integer value of the hex input
    }

    public void setDataFormat(char dataFormatChar){

        setDataFormat(Sensor.getDataFormatFromCharacter(dataFormatChar));
    }

    public void setDataFormat(SensorDataFormat dataFormat){

        this.dataFormat = dataFormat;
    }

    public void setDataInterpretation(char dataInterpretationChar){

        setDataInterpretation(Sensor.getDataInterpretationFromCharacter(dataInterpretationChar));
    }

    public void setDataInterpretation(SensorDataInterpretation dataInterpretation){

        this.dataInterpretation = dataInterpretation;
    }

    public void setFaulty(boolean faulty) {
        this.faulty = faulty;
    }

    public boolean isFaulty(){

        return this.faulty;
    }
}
