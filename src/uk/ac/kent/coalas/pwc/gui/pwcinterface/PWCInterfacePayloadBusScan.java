package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfacePayloadBusScan extends PWCInterfaceEventPayload {

    private Node node;

    public PWCInterfacePayloadBusScan(PWCInterface chairInterface, String response) throws Exception{

        super(response);

        // Valid response is exactly 3 characters
        if (response.length() != 3) {
            throw new PWCInterfaceParseException("Response is the wrong length");
        }

        int nodeId = Integer.parseInt(response.substring(1, 1));
        node = chairInterface.getNode(nodeId);

        String connected = response.substring(2);

        if("Y".equals(connected)){
            node.setConnectedToBus(true);
        } else if ("N".equals(connected)){
            node.setConnectedToBus(false);
        } else {
            throw new PWCInterfaceParseException("Response is invalid");
        }

    }

    public Node getNode(){

        return node;
    }
}
