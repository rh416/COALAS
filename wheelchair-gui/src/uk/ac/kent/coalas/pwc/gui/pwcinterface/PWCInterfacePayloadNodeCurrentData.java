package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a current sensor data event from the Interface
 *
 */
public class PWCInterfacePayloadNodeCurrentData extends PWCInterfaceEventPayload {

    private static Pattern faultySensorPattern = Pattern.compile("^(U-{0,7}).*$");

    public PWCInterfacePayloadNodeCurrentData(PWCInterface chairInterface, String response) throws Exception{

        super(chairInterface, response);

        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);

        // Get the response minus the first 2 characters
        String dataStr = response.substring(3);

        Sensor currentSensor;
        int dataLength;
        String currentSensorData;

        Matcher faultySensorMatcher;

        for(int i = 0; i < node.getSensorCount(); i++){
            currentSensor = node.getSensorFromDataOrder(i);

            faultySensorMatcher = faultySensorPattern.matcher(dataStr);

            if(faultySensorMatcher.find()) {
                currentSensor.setFaulty(true);
                dataLength = faultySensorMatcher.group(1).length(); // Find out how many characters were used to signify
                                                                    //  a faulty sensor so that we can skip far enough ahead
            } else {
                dataLength = currentSensor.getDataFormat().getCharLength();
                if(dataStr.length() < dataLength){
                    throw new PWCInterfaceParseException(String.format(s("parse_exception_too_short_for_type"), dataLength));
                }
                currentSensorData = dataStr.substring(0, dataLength);
                currentSensor.parseDataString(currentSensorData);
            }

            // Make sure the data string has enough data left for us to extract
            if(dataLength <= dataStr.length()){
                dataStr = dataStr.substring(dataLength);
            } else {
                // Otherwise, throw an exception
                throw new PWCInterfaceParseException(s("parse_exception_too_short_sensor_missing"));
            }

            faultySensorMatcher.reset();                    // Reset to allow reuse in the next loop
        }
    }
}
