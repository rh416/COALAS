package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import controlP5.ControlListener;
import processing.serial.Serial;
import uk.ac.kent.coalas.pwc.gui.PWCInterfaceCommunicationProvider;

import java.io.PrintStream;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterface {

    private PWCInterfaceListener listener;
    private PWCInterfaceCommunicationProvider commsProvider;
    char EOL = '\n';

    public PWCInterface(PWCInterfaceListener listener, PWCInterfaceCommunicationProvider commsProvider) throws PWCInterfaceException{

        this.listener = listener;
        this.commsProvider = commsProvider;
    }

    private void sendCommand(String command){

        Object thisCommsProvider = commsProvider.getCommunicationProvider();

        command += EOL;

        if(thisCommsProvider instanceof Serial){
            Serial comms = (Serial)thisCommsProvider;
            comms.write(command);
        } else if(thisCommsProvider instanceof PrintStream){
            PrintStream comms = (PrintStream)thisCommsProvider;
            comms.print(command);
        } else {
            throw new PWCInterfaceException("No valid communication provider supplied");
        }
    }

    public void scanForNode(int nodeId){

        sendCommand("&" + String.valueOf(nodeId) + "S");
    }

    public void parse(String line) {

        // Get first character to determine response type
        char firstChar = line.charAt(0);
        String response = line.substring(1);

        PWCInterfaceEvent.EventType type = PWCInterfaceEvent.EventType.UNKNOWN;
        PWCInterfaceEventPayload payload = null;

        try {

            switch (firstChar) {

                // Information from the joystick
                case 'j':

                    break;

                // Information from a scan of the bus
                case 'b':
                    type = PWCInterfaceEvent.EventType.BUS_SCAN;
                    payload = new PWCInterfaceBusScanPayload(response);
                    break;

                // Diagnostic information from sensors
                case 'd':
                    type = PWCInterfaceEvent.EventType.SENSOR_VALUES;

                    break;
            }
        }catch(Exception e){
            type = PWCInterfaceEvent.EventType.ERROR;
            payload= new PWCInterfaceErrorPayload(response, e);
        }

        if(listener != null) {
            listener.onPWCInterfaceEvent(new PWCInterfaceEvent(type, payload));
        }
    }
}
