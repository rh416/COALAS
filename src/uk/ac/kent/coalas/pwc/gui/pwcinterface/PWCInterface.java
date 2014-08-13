package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import uk.ac.kent.coalas.pwc.gui.hardware.Node;

import java.util.ArrayList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterface {

    private PWCInterfaceListener listener;
    private PWCInterfaceCommunicationProvider commsProvider;
    private ArrayList<Node> nodes = new ArrayList<Node>(10);
    char EOL = '\n';

    public final Node ALL_NODES;

    public static enum UltrasoundMode{
        CONTINUOUS('C'), PULSED('P');

        private final char charCode;
        private UltrasoundMode(final char charCode){ this.charCode = charCode; }
        public char getCharCode(){ return this.charCode; }
    }

    public PWCInterface(PWCInterfaceListener listener, PWCInterfaceCommunicationProvider commsProvider){

        this.listener = listener;
        this.commsProvider = commsProvider;

        // Init nodes array - chair has a max of 9 so lets create them
        for(int i = 0; i < 9; i++){
            nodes.add(new Node(this, i + 1));
        }

        // Create a special node with an ID of 0 - this can be used to address all nodes
        ALL_NODES = new Node(this, 0);
    }

    public Node getNode(int nodeId){

        return nodes.get(nodeId - 1);
    }

    public void sendCommand(String command){

        command += EOL;

        if(commsProvider != null){
            commsProvider.write(command);
        } else {
            throw new PWCInterfaceException("No valid communication provider supplied");
        }
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

                // Result from requesting node configuration
                case 'C':
                    type = PWCInterfaceEvent.EventType.NODE_CONFIGURATION;
                    payload = new PWCInterfacePayloadNodeConfiguration(this, response);
                    break;

                // Result of scanning whether a node exists or not
                case 'S':
                    type = PWCInterfaceEvent.EventType.BUS_SCAN;
                    payload = new PWCInterfacePayloadBusScan(this, response);
                    break;

                case 'F':
                    type = PWCInterfaceEvent.EventType.NODE_DATA_FORMAT;
                    payload = new PWCInterfacePayloadNodeDataFormat(this, response);
                    break;

                // Diagnostic information from sensors
                case 'D':
                    type = PWCInterfaceEvent.EventType.NODE_CURRENT_DATA;
                    payload = new PWCInterfacePayloadNodeCurrentData(this, response);
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

    public String intTo12BitHex(int input){

        if(input >= 4096){
            throw new RuntimeException("Input is too large");
        }

        return String.format("%03x", input).toUpperCase();
    }
}
