package uk.ac.kent.coalas.pwc.gui.pwcinterface;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterface {

    private PWCInterfaceListener listener;

    public PWCInterface(PWCInterfaceListener listener){

        this.listener = listener;
    }

    private void sendCommand(String command){

    }

    public void parse(String line) {

        // Get first character to determine response type
        char firstChar = line.charAt(0);
        String response = line.substring(1);

        PWCInterfaceEvent.EventType type = PWCInterfaceEvent.EventType.UNKNOWN;
        PWCInterfaceEventPayload payload;

        switch(firstChar){

            // Information from the joystick
            case 'j':

                break;

            // Information from a scan of the bus
            case 'b':
                type = PWCInterfaceEvent.EventType.BUS_SCAN;
                break;

            // Diagnostic information from sensors
            case 'd':
                type = PWCInterfaceEvent.EventType.SENSOR_VALUES;

                break;

        }

        // Check that the parsing was successful
        if(!payload.parseWasSuccessful()){
            payload = new Pa
        }

        if(listener != null) {
            listener.onPWCInterfaceEvent(new PWCInterfaceEvent(type, null));
        }
    }



}
