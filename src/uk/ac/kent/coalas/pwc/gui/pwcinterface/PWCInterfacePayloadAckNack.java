package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 21/08/2014.
 *
 * A class representing the payload for an ACK / NACK event from the Interface
 *
 */
public class PWCInterfacePayloadAckNack extends PWCInterfaceEventPayload {

    public PWCInterfacePayloadAckNack(PWCInterface chairInterface, String response) {

        super(chairInterface, response);

        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);
    }
}
