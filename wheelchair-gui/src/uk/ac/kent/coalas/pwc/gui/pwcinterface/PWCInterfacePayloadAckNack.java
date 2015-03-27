package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 21/08/2014.
 *
 * A class representing the payload for an ACK / NACK event from the Interface
 *
 */
public class PWCInterfacePayloadAckNack extends PWCInterfaceEventPayload {

    private PWCInterfaceRequest request;

    public PWCInterfacePayloadAckNack(PWCInterface chairInterface, String response) {

        super(chairInterface, response);

        // Get Node and Type information from ACK / NACK
        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);
        int requestType = Integer.parseInt(response.substring(3));

        // Create an instance of PWCInterfaceRequest that represents the request that was initially sent
        request = new PWCInterfaceRequest(PWCInterfaceEvent.EventType.values()[requestType], node);
    }

    public PWCInterfaceRequest getRequest(){

        return request;
    }
}
