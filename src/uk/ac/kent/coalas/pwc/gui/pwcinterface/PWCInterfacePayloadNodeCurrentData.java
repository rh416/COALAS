package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfacePayloadNodeCurrentData extends PWCInterfaceEventPayload {

    private Node node;

    private static Pattern faultySensorPattern = Pattern.compile("^(U-{0,7}).*$");

    public PWCInterfacePayloadNodeCurrentData(PWCInterface chairInterface, String response) throws Exception{

        super(response);

        int nodeId = Integer.parseInt(response.substring(0, 1));
        node = chairInterface.getNode(nodeId);

        // Get the response minus the first 2 characters
        String dataStr = response.substring(2);

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
                    throw new PWCInterfaceParseException("Response is too short - the given data type expects " + dataLength + "characters");
                }
                currentSensorData = dataStr.substring(0, dataLength);
                currentSensor.parseDataString(currentSensorData);
            }

            // Make sure the data string has enough data left for us to extract
            if(dataLength <= dataStr.length()){
                dataStr = dataStr.substring(dataLength);
            } else {
                // Otherwise, throw an exception
                throw new PWCInterfaceParseException("Response is too short - data has not been returned for all sensors");
            }

            faultySensorMatcher.reset();                    // Reset to allow reuse in the next loop
        }
    }

    public Node getNode(){

        return node;
    }
}
