package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfacePayloadBusScan extends PWCInterfaceEventPayload {

    private int nodeId;
    private boolean present;

    public PWCInterfacePayloadBusScan(String response) throws Exception{

        super(response);

        System.out.println("Bus Scan Payload");

        // Valid response is exactly 4 characters
        if (response.length() != 4) {
            throw new PWCInterfaceParseException("Response is the wrong length");
        }

        nodeId = Integer.parseInt(response.substring(1, 2));

        String connected = response.substring(3);

        if("Y".equals(connected)){
            present = true;
        } else if ("N".equals(connected)){
            present = false;
        } else {
            throw new PWCInterfaceParseException("Invalid response");
        }

    }

    public int getNodeId(){

        return nodeId;
    }

    public boolean getPresent(){

        return present;
    }
}
