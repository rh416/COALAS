package uk.ac.kent.coalas.pwc.gui.pwcinterface;

import org.apache.log4j.Logger;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;

import java.util.*;

/**
 * Created by rm538 on 06/08/2014.
 */
public class PWCInterface {

    private PWCInterfaceListener listener;
    private PWCInterfaceCommunicationProvider commsProvider;
    private ArrayList<Node> nodes = new ArrayList<Node>(10);

    private Map<PWCInterfaceRequestIdentifier, PWCInterfaceRequest> requests = new HashMap<PWCInterfaceRequestIdentifier, PWCInterfaceRequest>();

    private String buffer = "";

    private Thread TimeoutCheck = new Thread(){

        public void run(){

            while(!Thread.currentThread().isInterrupted()){
                checkForTimedoutRequests();
                try{
                    Thread.sleep(TIME_BETWEEN_TIMEOUT_CHECKS_MS);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    };

    public final Node ALL_NODES;

    private static Logger log = Logger.getLogger(PWCInterface.class);

    public final int DEFAULT_TIMEOUT_SECS = 5;
    public final int TIME_BETWEEN_TIMEOUT_CHECKS_MS = 500;


    /**
     * A method to look for requests which have not received an appropriate response.
     *
     * This method immediately returns void, but will initiate a PWCInterfaceEvent with the
     * EventType of TIMEOUT for any requests that have timed-out
     *
     * When used with no parameters, DEFAULT_TIMEOUT_SECS is used
     *
     */
    public synchronized void checkForTimedoutRequests(){

        checkForTimedoutRequests(DEFAULT_TIMEOUT_SECS);
    }

    /**
     * A method to look for requests which have not received an appropriate response.
     *
     * This method immediately returns void, but will initiate a PWCInterfaceEvent with the
     * EventType of TIMEOUT for any requests that have timed-out
     *
     * @param timeout   A Timeout in seconds - any requests initiated longer ago than this value will produce a
     *                  TIMEOUT event.
     *
     */
    public synchronized void checkForTimedoutRequests(int timeout){

        // Loop over all requests in the request list to find any that have gone over the timeout period
        // Need to use an iterator so that the list can be altered whilst being iterated over
        Iterator it = requests.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            PWCInterfaceRequest request = (PWCInterfaceRequest) entry.getValue();

            if(request.hasTimedout(timeout)){
                // Remove this request from the list, otherwise it'll keep triggering events
                it.remove();

                // Create a timeout event
                PWCInterfacePayloadTimeout timeoutPayload = new PWCInterfacePayloadTimeout(this, request);
                PWCInterfaceEvent timeoutEvent = new PWCInterfaceEvent(this, PWCInterfaceEvent.EventType.TIMEOUT, timeoutPayload);

                // Dispatch the event
                dispatchPWCInterfaceEvent(timeoutEvent);
            }
        }
    }

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
            nodes.add(new Node(i + 1));
        }

        // Create a special node with an ID of 0 - this can be used to address all nodes
        ALL_NODES = new Node(0);

        // Start the thread which will keep an eye on timeouts
        TimeoutCheck.start();
    }

    public Node getNode(int nodeId){

        if(nodeId > 0) {
            return nodes.get(nodeId - 1);
        } else {
            return null;
        }
    }


    /**
     * Request the current firmware version being used on the wheelchair.
     *
     * Immediately returns void, but will initiate a FIRMWARE_INFO event when the chair responds
     */
    public void getVersion(){

        sendCommand(PWCInterfaceEvent.EventType.FIRMWARE_INFO, 0, "V");
    }


    /**
     * Check whether a node exists on the RS-485 bus
     *
     * Immediately returns void, but will initiate a BUS_SCAN event when the chair responds
     *
     * @param node  The Node which is to have its presence on the bus checked
     */
    public void checkNodeExistsOnBus(Node node){

        checkNodeExistsOnBus(node.getId());
    }

    /**
     * Check whether a node exists on the RS-485 bus
     *
     * Immediately returns void, but will initiate a BUS_SCAN event when the chair responds
     *
     * @param nodeId  The ID of a Node which is to have its presence on the bus checked
     */
    public void checkNodeExistsOnBus(int nodeId){

        sendCommand(PWCInterfaceEvent.EventType.BUS_SCAN, nodeId, "%dS");
    }


    /**
     * Configure a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate an ACK or NACK event when the chair responds
     *
     * @param node          The Node which is to have its presence on the bus checked
     * @param configString  A string defining the Node's new configuration (see Documentation)
     */
    public void configureNodeSensors(Node node, String configString){

        configureNodeSensors(node.getId(), configString);
    }

    /**
     * Configure a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a TODO: NODE_CONFIGURATION event when the chair responds
     *
     * @param nodeId        The ID of a Node which is to have its presence on the bus checked
     * @param configString  A string defining the Node's new configuration (see Documentation)
     */
    public void configureNodeSensors(int nodeId, String configString){

        sendCommand(PWCInterfaceEvent.EventType.NODE_CONFIGURATION, nodeId, "%dC" + configString);
    }


    /**
     * Request the sensor configuration of a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_CONFIGURATION event when the chair responds
     *
     * @param node          The Node which is to have its presence on the bus checked
     */
    public void requestNodeConfiguration(Node node){

        requestNodeConfiguration(node.getId());
    }

    /**
     * Request the sensor configuration of a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_CONFIGURATION event when the chair responds
     *
     * @param nodeId          The ID of a Node which is to have its presence on the bus checked
     */
    public void requestNodeConfiguration(int nodeId){

        sendCommand(PWCInterfaceEvent.EventType.NODE_CONFIGURATION, nodeId, "%dR");
    }


    /**
     * Request the current sensor data from a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_CURRENT_DATA event when the chair responds
     *
     * @param node          The Node whose sensor data is to be returned
     */
    public void requestNodeCurrentData(Node node){

        requestNodeCurrentData(node.getId());
    }

    /**
     * Request the current sensor data from a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_CURRENT_DATA event when the chair responds
     *
     * @param nodeId          The ID of a Node whose sensor data is to be returned
     */
    public void requestNodeCurrentData(int nodeId){

        sendCommand(PWCInterfaceEvent.EventType.NODE_CURRENT_DATA, nodeId, "%dD");
    }


    /**
     * Request the sensor data format from a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_DATA_FORMAT event when the chair responds
     *
     * @param node          The Node whose sensor data format is to be returned
     */
    public void requestNodeDataFormat(Node node){

        requestNodeDataFormat(node.getId());
    }

    /**
     * Request the sensor data format from a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a NODE_DATA_FORMAT event when the chair responds
     *
     * @param nodeId          The ID of a Node whose sensor data format is to be returned
     */
    public void requestNodeDataFormat(int nodeId){

        sendCommand(PWCInterfaceEvent.EventType.NODE_DATA_FORMAT, nodeId, "%dF");
    }


    /**
     * Set the sensor thresholds for each Zone in a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a TODO: NODE_THRESHOLDS event when the chair responds
     *
     * @param node              The Node whose zone thresholds are to be set
     * @param zone1Threshold    The distance threshold for Zone 1
     * @param zone2Threshold    The distance threshold for Zone 2
     * @param zone3Threshold    The distance threshold for Zone 3
     */
    public void setNodeThresholds(Node node, int zone1Threshold, int zone2Threshold, int zone3Threshold){

        setNodeThresholds(node.getId(), zone1Threshold, zone2Threshold, zone3Threshold);
    }

    /**
     * Set the sensor thresholds for each Zone in a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a TODO: NODE_THRESHOLDS event when the chair responds
     *
     * @param nodeId            The ID of a Node whose zone thresholds are to be set
     * @param zone1Threshold    The distance threshold for Zone 1
     * @param zone2Threshold    The distance threshold for Zone 2
     * @param zone3Threshold    The distance threshold for Zone 3
     */
    public void setNodeThresholds(int nodeId, int zone1Threshold, int zone2Threshold, int zone3Threshold){

        sendCommand(PWCInterfaceEvent.EventType.NODE_THRESHOLDS,
                nodeId, "%dT" +
                        intTo12BitHex(zone1Threshold) +
                        intTo12BitHex(zone2Threshold) +
                        intTo12BitHex(zone3Threshold));
    }


    /**
     * Set the mode for the ultrasound sensors on a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a TODO: NODE_MODE event when the chair responds
     *
     * @param node    The Node whose ultrasound mode is to be set
     * @param mode    The ultrasound mode to be set
     */
    public void setNodeUltrasoundMode(Node node, UltrasoundMode mode){

        setNodeUltrasoundMode(node.getId(), mode);
    }


    /**
     * Set the mode for the ultrasound sensors on a node on the RS-485 bus
     *
     * Immediately returns void, but will initiate a TODO: NODE_MODE event when the chair responds
     *
     * @param nodeId  The ID of a Node whose ultrasound mode is to be set
     * @param mode    The ultrasound mode to be set
     */
    public void setNodeUltrasoundMode(int nodeId, UltrasoundMode mode){

        char modeStr = mode.getCharCode();
        sendCommand(PWCInterfaceEvent.EventType.NODE_MODE, nodeId, "%dM" + modeStr);
    }



    /**
     * Returns whether or not the interface is able to send / receive messages.
     *
     * @return True if the underlying communication provider is ready to send / receive messages
     */
    public boolean isAvailable(){

        return commsProvider.isAvailable();
    }



    /**
     * Internal method to send commands to the chair.
     *
     * @param type      The type of command that is being sent
     * @param nodeId    The ID of the Node that this command affects
     *
     * @param command   The command to be sent to the chair
     */
    private void sendCommand(PWCInterfaceEvent.EventType type, int nodeId, String command){

        if(commsProvider != null){
            if(commsProvider.isAvailable()) {
                // Record this request
                addRequestToRequestList(type, nodeId);
                /* Build the command to send the firmware - this includes:
                    &   - command indicator
                    #   - command type id (Enum Ordinal - allows us to recover which type of request was called.
                                            Should NOT be stored long term - ie could change at next compile)
                    &   - command indicator
                    #   - Node ID
                    A-Z - command
                    ... - any command parameters
                */
                String commandToSend = String.format("&%02d&", type.ordinal()) + String.format(command, nodeId);
                log.info("Command sent :" + commandToSend);
                commsProvider.write(commandToSend);
            } else{
                throw new PWCInterfaceException("Communication provider is not available");
            }
        } else {
            throw new PWCInterfaceException("No valid communication provider supplied");
        }
    }

    /**
     * Internal method used to record when a request was sent, so that requests can be checked for timeouts
     *
     * @param type      The type of command that is being sent
     * @param nodeId    The ID of the Node that this command affects
     */
    private void addRequestToRequestList(PWCInterfaceEvent.EventType type, int nodeId){

        // Check that a matching request doesn't already exist - we don't want to overwrite an older request,
        // as this could hide a timeout - ie if we're regularly asking for data from a node that doesn't response
        PWCInterfaceRequestIdentifier identifier = new PWCInterfaceRequestIdentifier(type, nodeId);
        if(requests.get(identifier) == null) {
            requests.put(identifier, new PWCInterfaceRequest(type, getNode(nodeId)));
        }
    }





    public void buffer(String inString){

        // Append the input to the current buffer
        buffer += inString;

        // If the last character is a newline or carriage return, parse the buffer
        char lastChar = buffer.charAt(buffer.length() - 1);
        if(lastChar == '\n' || lastChar == '\r'){
            // Trim any whitespace from the buffer string
            buffer = buffer.trim();
            // If the buffer is not empty, parse it
            if(!buffer.isEmpty()) {
                parse(buffer);
            }
            // Clear the buffer
            buffer = "";
        }
    }


    /**
     * Method used to parse a response from the chair and dispatch the appropriate event
     *
     * @param response    The response from the chair
     */
    public void parse(String response) {

        log.info("Response received: " + response);

        // Get first character to determine response type
        char firstChar = response.charAt(0);

        PWCInterfaceEvent.EventType type = PWCInterfaceEvent.EventType.UNKNOWN;
        PWCInterfaceEventPayload payload = null;

        try {
            switch (firstChar) {

                // Ignore lines that start with an underscore _
                case '_':
                    return;

                // Version information from the firmware
                case 'V':
                    type = PWCInterfaceEvent.EventType.FIRMWARE_INFO;
                    payload = new PWCInterfacePayloadFirmwareInfo(this, response);
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

                // Detect Acks from the node
                case 'A':
                    type = PWCInterfaceEvent.EventType.ACK;
                    payload = new PWCInterfacePayloadAckNack(this, response);
                    break;

                // Detect Nacks from the node
                case 'N':
                    type = PWCInterfaceEvent.EventType.NACK;
                    payload = new PWCInterfacePayloadAckNack(this, response);
                    break;

                // Detect Joystick position data
                case 'J':
                    type = PWCInterfaceEvent.EventType.JOYSTICK_FEEDBACK;
                    payload = new PWCInterfacePayloadJoystickFeedback(this, response);
                    break;

                /*
                case 'CUSTOM_CHARACTER':
                    type = PWCInterfaceEvent.EventType.CUSTOM_TYPE
                    payload = new PWCInterfacePayloadCUSTOMPAYLOAD extends PWCInterfaceEventPayload
                    break;

                    Then handle this type in onPWCInterfaceEvent of the window expecting data

                 */
            }
        }catch(Exception e){
            type = PWCInterfaceEvent.EventType.ERROR;
            payload= new PWCInterfacePayloadError(this, response, e);
        }

        if(payload == null){
            payload = new PWCInterfacePayloadUnknown(this, response);
        }

        dispatchPWCInterfaceEvent(new PWCInterfaceEvent(this, type, payload));
    }

    /**
     * Internal method used to dispatch PWCInterfaceEvents to the application via the InterfaceListener supplied
     * when setting up the Interface
     *
     * @param event    The PWCInterfaceEvent to be dispatched
     */
    private void dispatchPWCInterfaceEvent(PWCInterfaceEvent event){

        // Find the request in the request list that relates to this response
        PWCInterfaceEvent.EventType eventType = event.getType();
        PWCInterfaceEvent.EventType identifierType = eventType;

        // If the event is an ACK or NACK, we need to get the original request's type in order to identify it properly
        if(eventType == PWCInterfaceEvent.EventType.ACK || eventType == PWCInterfaceEvent.EventType.NACK){
            PWCInterfacePayloadAckNack payload = (PWCInterfacePayloadAckNack) event.getPayload();
            identifierType = payload.getRequest().getType();
        }

        Node identifierNode = event.getPayload().getNode();
        int identifierNodeId = 0;

        // Get the specific Node Id, if a node was given - otherwise use the default value of 0
        if(identifierNode != null){
            identifierNodeId = identifierNode.getId();
        }

        // Remove the past event using an identifier made up of the EventType and Node ID
        PWCInterfaceRequestIdentifier identifier = new PWCInterfaceRequestIdentifier(identifierType, identifierNodeId);
        requests.remove(identifier);

        log.info("Event Dispatched: " + event.getType());

        // Send the event out to the listener, if one has been specified
        if(listener != null) {
            listener.onPWCInterfaceEvent(event);
        }
    }

    /**
     * Return the 12-bit hex (3 character) representation of the input integer.
     *
     * @param input    Integer to be converted to hex. Has a max value of 4096.
     *
     * @return 12-bit hex representation of the input integer
     */
    public String intTo12BitHex(int input){

        if(input >= 4096){
            throw new RuntimeException("Input is too large");
        }

        return String.format("%03x", input).toUpperCase();
    }
}
