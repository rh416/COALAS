package uk.ac.kent.coalas.pwc.gui;

/**
 * Created by rm538 on 11/08/2014.
 */
public class Sensor {

    private SensorType sensorType;
    private SensorDataFormat dataFormat = SensorDataFormat.UNKNOWN;
    private SensorDataInterpretation dataInterpretation = SensorDataInterpretation.UNKOWN;
    private boolean faulty;
    private short currentValue;

    public static enum SensorType {
        ULTRASONIC(4), INFRARED(8), FUSED(12);

        private final int bitmask;
        private SensorType(final int code){ this.bitmask = code; }
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
        UNKOWN('u'), CM('c'), RAW('r'), THRESHOLD('t');

        private final char representation;
        private SensorDataInterpretation(final char representation){
            this.representation = representation;
        }
        public char getRepresentation(){ return this.representation; }
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

        SensorDataInterpretation responseInterpretation = SensorDataInterpretation.UNKOWN;

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
