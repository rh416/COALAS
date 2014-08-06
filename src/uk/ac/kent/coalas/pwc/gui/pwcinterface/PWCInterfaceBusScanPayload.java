package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfaceBusScanPayload extends PWCInterfaceEventPayload {

    private int nodeId;
    private boolean present;

    public PWCInterfaceBusScanPayload(String response){

        super(response);

        // Valid response is exactly 4 characters
        if(response.length() != 4){
            parseFailed("Response is the wrong length");
            return;
        }

        nodeId = Integer.parseInt(response.substring(2));
        present = (response.substring(-1).equals("Y"));
    }

    public int getNodeId(){

        return nodeId;
    }

    public boolean getPresent(){

        return present;
    }
}
