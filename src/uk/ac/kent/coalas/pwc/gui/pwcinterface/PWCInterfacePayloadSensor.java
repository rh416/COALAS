package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 *
 * TODO: Delete this class?
 *
 * Created by rm538 on 06/08/2014.
 *
 * A class representing the payload for a firmware information event from the Interface
 *
 */
public class PWCInterfacePayloadSensor extends PWCInterfaceEventPayload {

    private int nodeId;
    private boolean present;

    public PWCInterfacePayloadSensor(PWCInterface chairInterface, String response){

        super(chairInterface, response);

    }

    public int getNodeId(){

        return nodeId;
    }

    public boolean getPresent(){

        return present;
    }
}
