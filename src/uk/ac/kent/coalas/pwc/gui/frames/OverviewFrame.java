package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.GButton;
import g4p_controls.GConstants;
import g4p_controls.GEvent;
import g4p_controls.GTextArea;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;
import uk.ac.kent.coalas.pwc.gui.ui.UINode;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.LinkedList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class OverviewFrame extends WheelchairGUIFrame {

    private GButton btnScanBus;
    private GTextArea txtScanResults;

    private EnumMap<Zone.Position, UINode> uiNodes = new EnumMap<Zone.Position, UINode>(Zone.Position.class);

    private LinkedList<String> logQueue = new LinkedList<String>();

    public OverviewFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public OverviewFrame(int theWidth, int theHeight, int xPos, int yPos) {

        super(theWidth, theHeight, xPos, yPos);
        setTitle(s("title_overview"));

        // Create nodes for display
        for(Zone.Position position : Zone.Position.values()){
            // Don't create a node object for the position "Unknown"
            if(position != Zone.Position.UNKNOWN) {
                uiNodes.put(position, new UINode(this, position));
            }
        }
    }

    @Override
    public void setup() {
        super.setup();

        Font logFont = new Font("Monospace", Font.PLAIN, 10);

        btnScanBus = new GButton(this, 130, 130, 60, 60, s("scan_bus"));
        btnScanBus.addEventHandler(this, "handleButtonEvents");

        txtScanResults = new GTextArea(this, 20, 350, 280, 80, GConstants.SCROLLBARS_VERTICAL_ONLY);
        txtScanResults.setTextEditEnabled(false);
        txtScanResults.setFont(logFont);
        txtScanResults.setLocalColorScheme(WheelchairGUI.CONSOLE_COLOUR_SCHEME);
    }

    @Override
    public void draw() {

        CLICK_EVENT_STOPPED = false;

        while(logQueue.peekFirst() != null){
            txtScanResults.appendText(logQueue.removeFirst() + "\n");
        }

        background(255);

        for(UINode uiNode : uiNodes.values()){
            uiNode.draw();
        }

    }

    public synchronized void logToScreen(String logStr){

        logQueue.add(logStr);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        PWCInterface chairInterface = e.getChairInterface();
        PWCInterfaceEvent.EventType eventType = e.getType();
        PWCInterfaceEventPayload payload = e.getPayload();

        Node eventNode;

        String eType = eventType.name();

        switch(e.getType()){

            case BUS_SCAN:
                PWCInterfacePayloadBusScan busPayload = (PWCInterfacePayloadBusScan)payload;
                eventNode = busPayload.getNode();

                // If the node is connected, request its configuration so that we can display its status
                if(eventNode.isConnectedToBus()){
                    // Get the node's firmware information
                    //chairInterface.requestNodeFirmwareVersion(eventNode);

                    // Get the configuration so that it can be displayed to the user
                    chairInterface.requestNodeConfiguration(eventNode);

                    // Get the response format so that the interpretation of the sensors data is known
                    chairInterface.requestNodeDataFormat(eventNode);
                } else {
                    // Otherwise, print to the onscreen log that the node was not found
                    logToScreen(String.format(s("node_not_connected"), eventNode.getId()));
                }
                break;

            case NODE_CONFIGURATION:
                PWCInterfacePayloadNodeConfiguration configPayload = (PWCInterfacePayloadNodeConfiguration)payload;
                eventNode = configPayload.getNode();

                // As nodes don't have a position themselves, we'll locate it in the same place as its first zone as an approximation.
                UINode uiNode = uiNodes.get(eventNode.getZone(1).getPosition());
                if(uiNode != null){
                    uiNode.setDataNode(eventNode);
                }

                break;

            case ERROR:
                PWCInterfacePayloadError errorPayload = (PWCInterfacePayloadError)payload;
                logToScreen(s("error_occurred_check_log"));
                break;

            case DISCONNECTED:
                // Close the window when the chair is disconnected
                getViewFrame().dispatchEvent(new WindowEvent(getViewFrame(), WindowEvent.WINDOW_CLOSING));
                break;
        }
    }

    public void scanBus(){

        PWCInterface pwcI = parent.getChairInterface();

        if(pwcI.isAvailable()) {
            // Scan the bus for any nodes
            for (int i = 1; i < 10; i++) {
                pwcI.checkNodeExistsOnBus(i);
            }
        } else {
            logToScreen("Wheelchair Interface is not connected");
        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnScanBus){
            scanBus();
        }
    }
}
