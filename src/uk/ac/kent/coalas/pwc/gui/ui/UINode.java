package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import shapes.JEllipse;
import shapes.JRectangle;
import shapes.JShape;
import shapes.JShapeAdapter;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.OverviewFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by rm538 on 13/08/2014.
 */
public class UINode extends UIObject {

    public static int NODE_HEIGHT = 30;
    public static int NODE_WIDTH = 30;

    public static int NODE_ID_RADIUS = 18;
    public static int NODE_ID_COLOUR = 0xFFFFFFFF;

    public static int NODE_COLOUR_NOT_ON_BUS = 0xFFCCCCCC;
    public static int NODE_COLOUR_ON_BUS = 0xFF00FF00;
    public static int NODE_COLOUR_HIGHLIGHT = 0xFFF0FFF0;

    private WheelchairGUIFrame parent;
    private Zone.Position position;

    private float xCenterPos;
    private float yCenterPos;

    private Node dataNode;

    private boolean mouseOverNode = false;

    private JRectangle nodeIndicator;
    private JEllipse nodeIdentifier;
    private PFont nodeIdentifierFont;

    private ArrayList<UIZone> uiZones = new ArrayList<UIZone>(Node.MAX_ZONES);

    public UINode(WheelchairGUIFrame parent, Zone.Position position){

        this.parent = parent;
        this.position = position;

        Point shapeCenter = UIObject.getCenterFromPosition(position);
        xCenterPos = (float)shapeCenter.getX();
        yCenterPos = (float)shapeCenter.getY();

        nodeIndicator = new JRectangle(parent, xCenterPos, yCenterPos, NODE_WIDTH, NODE_HEIGHT);
        nodeIndicator.addShapeListener(new NodeShapeListener());

        nodeIdentifier = new JEllipse(parent, xCenterPos, yCenterPos, NODE_ID_RADIUS, NODE_ID_RADIUS);
        nodeIdentifier.setFillColour(NODE_ID_COLOUR);

        nodeIdentifierFont = parent.createFont("Arial", 10, true);
    }

    public void draw(){

        nodeIndicator.setFillColour(NODE_COLOUR_NOT_ON_BUS);

        if(dataNode != null) {
            if(dataNode.isConnectedToBus()) {
                if(mouseOverNode){
                    nodeIndicator.setFillColour(NODE_COLOUR_HIGHLIGHT);
                } else {
                    nodeIndicator.setFillColour(NODE_COLOUR_ON_BUS);
                }

                for (UIZone zone : uiZones) {
                    zone.draw();
                }
            }
        }

        // Draw the node indicator square
        nodeIndicator.draw();

        if(dataNode != null) {
            if(dataNode.isConnectedToBus()) {
                // Draw the circle which will contain the node identifier
                nodeIdentifier.draw();

                // Draw the node identifier
                parent.textFont(nodeIdentifierFont);
                parent.fill(0);                          // Make the text black
                parent.textAlign(PConstants.CENTER, PConstants.CENTER);
                parent.text(dataNode.getId(), xCenterPos, yCenterPos - 1);  // -1 used to position text more centrally vertically
            }
        }
    }

    public void setDataNode(Node node){

        this.dataNode = node;
        Zone thisZone;

        this.uiZones.clear();

        for(int i = 0; i < Node.MAX_ZONES; i++){
            thisZone = node.getZone(i + 1);
            this.uiZones.add(i, new UIZone(parent, thisZone));
        }
    }

    class NodeShapeListener extends JShapeAdapter{

        @Override
        public void shapeEntered(JShape shape){

            mouseOverNode = true;
        }

        @Override
        public void shapeExited(JShape shape){

            mouseOverNode = false;
        }

        @Override
        public void shapePressed(JShape shape){

            if(dataNode != null && dataNode.isConnectedToBus()){
                ConfigurationFrame configFrame = (ConfigurationFrame) parent.getMainApp().addNewFrame(WheelchairGUI.FrameId.CONFIG,
                        new ConfigurationFrame(parent));
                configFrame.setConfigNode(dataNode);
            }
            // Use this to stop the click event handle in UIZone from opening up a Diagnostics window
            parent.CLICK_EVENT_STOPPED = true;
        }
    }
}
