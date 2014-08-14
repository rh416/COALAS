package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.GButton;
import g4p_controls.GConstants;
import g4p_controls.GEvent;
import g4p_controls.GTextArea;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.ui.UINode;

import java.awt.*;
import java.io.InputStream;
import java.util.EnumMap;

/**
 * Created by rm538 on 06/08/2014.
 */
public class OverviewFrame extends WheelchairGUIFrame {

    GButton btnScanBus;
    GTextArea txtScanResults;

    private EnumMap<Zone.Position, UINode> uiNodes = new EnumMap<Zone.Position, UINode>(Zone.Position.class);

    public OverviewFrame(int theWidth, int theHeight, int xPos, int yPos) {

        super(theWidth, theHeight, xPos, yPos);
        setTitle("Wheelchair Overview");

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

        btnScanBus = new GButton(this, 130, 130, 60, 60, "Scan\nBus");
        txtScanResults = new GTextArea(this, 20, 350, 280, 80, GConstants.SCROLLBARS_VERTICAL_ONLY);
        //txtScanResults.setTextEditEnabled(false);
        txtScanResults.setFont(logFont);
        txtScanResults.setLocalColorScheme(GConstants.SCHEME_8);
    }

    @Override
    public void draw() {

        background(255);

        for(UINode uiNode : uiNodes.values()){
            uiNode.draw();
        }

    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        PWCInterfaceEvent.EventType eventType = e.getType();
        PWCInterfaceEventPayload payload = e.getPayload();

        Node eventNode;

        String eType = eventType.name();

        console("Got event: " + eType);

        switch(e.getType()){

            case BUS_SCAN:
                PWCInterfacePayloadBusScan busPayload = (PWCInterfacePayloadBusScan)payload;
                eventNode = busPayload.getNode();

                // If the node is connected, request its configuration so that we can display its status
                if(eventNode.isConnectedToBus()){
                    eventNode.requestNodeConfiguration();
                } else {
                    if(txtScanResults != null) {
                        txtScanResults.appendText("Node " + eventNode.getId() + " is not connected to the bus\n");
                    }
                }
                break;

            case NODE_CONFIGURATION:
                PWCInterfacePayloadNodeConfiguration configPayload = (PWCInterfacePayloadNodeConfiguration)payload;
                eventNode = configPayload.getNode();

                // As nodes don't have a position themselves, we'll locate it in the same place as its first zone as an approximation.
                UINode uiNode = uiNodes.get(eventNode.getZone(1).getPosition());
                if(uiNode != null){
                    uiNode.setDataNode(eventNode);
                } else {
                    console("uiNode was null");
                }
        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnScanBus){
            // Scan the bus for any nodes
            for(int i = 1; i < 10; i++){
                parent.getChairInterface().getNode(i).checkExistsOnBus();
            }



            // Simulate commands from the chair
            parent.getChairInterface().parse("S1:Y");
            parent.getChairInterface().parse("S2:Y");
            parent.getChairInterface().parse("S4:Y");
            parent.getChairInterface().parse("S5:Y");

            parent.getChairInterface().parse("S3:N");
            parent.getChairInterface().parse("S6:N");
            parent.getChairInterface().parse("S7:N");
            parent.getChairInterface().parse("S8:N");
            parent.getChairInterface().parse("S9:N");

            parent.getChairInterface().parse("C1:1gIE,1hu,1aO.");
            parent.getChairInterface().parse("C2:3cI,3eF,3bG.");
            parent.getChairInterface().parse("C4:FaE,FbJ.");
            parent.getChairInterface().parse("C5:RbIE,RcJ,RdG.");
        }
    }
}
