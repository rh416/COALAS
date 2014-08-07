package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfaceSensorPayload extends PWCInterfaceEventPayload {

    private int nodeId;
    private boolean present;

    public PWCInterfaceSensorPayload(String response){

        super(response);

    }

    public int getNodeId(){

        return nodeId;
    }

    public boolean getPresent(){

        return present;
    }
}
