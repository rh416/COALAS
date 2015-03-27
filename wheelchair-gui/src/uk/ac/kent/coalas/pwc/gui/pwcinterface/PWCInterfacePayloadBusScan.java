package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a scan of the RS-485 bus event from the Interface
 *
 */
public class PWCInterfacePayloadBusScan extends PWCInterfaceEventPayload {

    private int VALID_RESPONSE_LENGTH = 4;

    public PWCInterfacePayloadBusScan(PWCInterface chairInterface, String response) throws Exception{

        super(chairInterface, response);

        // Valid response is exactly 4 characters
        if (response.length() != VALID_RESPONSE_LENGTH) {
            throw new PWCInterfaceParseException(String.format(s("parse_exception_wrong_length"), VALID_RESPONSE_LENGTH));
        }

        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);

        String connected = response.substring(3);

        if("Y".equals(connected)){
            node.setConnectedToBus(true);
        } else if ("N".equals(connected)){
            node.setConnectedToBus(false);
        } else {
            throw new PWCInterfaceParseException(s("parse_exception_response_invalid"));
        }
    }
}
