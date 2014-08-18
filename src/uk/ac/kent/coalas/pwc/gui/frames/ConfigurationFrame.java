package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.ui.RowPositionTracker;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneRow;

import java.util.ArrayList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class ConfigurationFrame extends WheelchairGUIFrame {

    private Node node;

    private GButton btnPress;
    private GLabel ConfigNodeTitle;

    private RowPositionTracker positionTracker = new RowPositionTracker(5, 80);

    private ArrayList<UIZoneRow> uiZoneRows = new ArrayList<UIZoneRow>();



    public ConfigurationFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public ConfigurationFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    public void setup() {
        super.setup();

        btnPress = new GButton((PApplet)this, 50, 400, 200, 20, "Send Configuration to Chair");
        btnPress.addEventHandler(this, "handleButtonEvents");

        ConfigNodeTitle = new GLabel(this, 25, 25, 270, 40);

        setConfigAppearance();
    }

    @Override
    public void draw() {

        background(255);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {


    }

    public void setConfigNode(Node node){

        this.node = node;

        for(int i = 0; i < Node.MAX_ZONES; i++){
            Zone zone = node.getZone(i + 1);
            if(zone.getPosition() != Zone.Position.UNKNOWN){
                uiZoneRows.add(new UIZoneRow(this, zone, positionTracker));
            }
        }

        setConfigAppearance();
    }

    private void setConfigAppearance(){

        if(node != null){
            if(ConfigNodeTitle != null){
                ConfigNodeTitle.setText("Configure Node #" + node.getId());
            }
        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        console("Event");
    }

}
