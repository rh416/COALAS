package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.NodeVersion;

/**
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a firmware information event from the Interface
 *
 */
public class PWCInterfacePayloadNodeFirmwareInfo extends PWCInterfaceEventPayload {

    private NodeVersion nodeVersion;

    public PWCInterfacePayloadNodeFirmwareInfo(PWCInterface chairInterface, String response) throws Exception{

        super(chairInterface, response);

        // Get a reference to the node that this response refers to
        int nodeId = Integer.parseInt(response.substring(1, 2));
        Node node = getResponseNodeFromId(nodeId);

        // Parse the input stream as a hex string
        nodeVersion = NodeVersion.parseVersionString(response.substring(3));

        // Save the firmware version to the node
        node.setFirmwareVersion(nodeVersion);
    }

    public NodeVersion getVersion(){

        return nodeVersion;
    }
}
