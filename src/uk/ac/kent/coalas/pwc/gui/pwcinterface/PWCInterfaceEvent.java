package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterfaceEvent {

    public static enum EventType {ERROR, UNKNOWN, FIRMWARE_INFO, BUS_SCAN,
        NODE_CURRENT_DATA, NODE_CONFIGURATION, NODE_DATA_FORMAT, ACK }

    private EventType type;
    private PWCInterfaceEventPayload payload;

    public PWCInterfaceEvent(EventType type, PWCInterfaceEventPayload payload){

        this.type = type;
        this.payload = payload;
    }

    public EventType getType(){

        return type;
    }

    public PWCInterfaceEventPayload getPayload(){

        return payload;
    }
}
