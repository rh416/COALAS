package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Sensor;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

/**
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a node data format event from the Interface
 *
 */
public class PWCInterfacePayloadNodeDataFormat extends PWCInterfaceEventPayload {

    public PWCInterfacePayloadNodeDataFormat(PWCInterface chairInterface, String response) throws Exception{

        super(chairInterface, response);

        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);

        // Get the response minus the first 3 characters
        String formatStr = response.substring(3);

        // Clear the sensor data order for this node
        node.clearSensorDataRequestList();

        Zone currentZone;
        Sensor currentSensor;
        Sensor.SensorDataFormat currentSensorFormat;
        Sensor.SensorDataInterpretation currentSensorInterpretation;
        int sensorFormatStrLength;

        // Step our way through the format string
        while(formatStr.length() > 0){
            int sensorId = (int)formatStr.charAt(0);  // We want to do some bitwise operations on this value, so store it as an int
            char dataType = formatStr.charAt(1);

            // If this sensor's data type is Logic, we don't need to ask how to interpret its response - it will be boolean
            if(dataType == 'L'){
                currentSensorFormat = Sensor.SensorDataFormat.LOGIC;
                currentSensorInterpretation = Sensor.SensorDataInterpretation.THRESHOLD;
                sensorFormatStrLength = 2;
            } else {
                char dataRepresentation = formatStr.charAt(2);
                currentSensorFormat = Sensor.getDataFormatFromCharacter(dataType);
                currentSensorInterpretation = Sensor.getDataInterpretationFromCharacter(dataRepresentation);
                sensorFormatStrLength = 3;
            }

            currentZone = node.getZone(sensorId);   // No need to apply bitmask here, as it is handled within the getter
            if(currentZone == null){
                throw new PWCInterfaceParseException(s("parse_exception_invalid_zone"));
            }

            currentSensor = currentZone.getSensorByTypeBitmask(sensorId);

            if (currentSensor != null) {
                currentSensor.setDataFormat(currentSensorFormat);
                currentSensor.setDataInterpretation(currentSensorInterpretation);
                node.addSensorToDataRequestList(sensorId);
            } else {
                throw new PWCInterfaceParseException(String.format(s("parse_exception_invalid_sensor"), formatStr.substring(0,sensorFormatStrLength)));
            }

            node.setDataFormatKnown(true);
            formatStr = formatStr.substring(sensorFormatStrLength);
        }
    }
}
