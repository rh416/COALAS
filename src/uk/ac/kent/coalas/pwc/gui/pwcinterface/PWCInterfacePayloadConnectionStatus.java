package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 02/09/2014.
 */
public class PWCInterfacePayloadConnectionStatus extends PWCInterfaceEventPayload {

    private boolean connected = false;

    public PWCInterfacePayloadConnectionStatus(PWCInterface chairInterface, boolean connected) {
        super(chairInterface, "");

        this.connected = connected;
    }

    public boolean isConnected(){

        return  connected;
    }
}
