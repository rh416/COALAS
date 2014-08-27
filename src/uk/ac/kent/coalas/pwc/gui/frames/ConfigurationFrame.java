package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.*;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.Node;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.ui.RowPositionTracker;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneConfigRow;

import java.util.ArrayList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class ConfigurationFrame extends WheelchairGUIFrame {

    private Node node;

    private GButton btnConfigureChair;
    private GButton btnCancelConfig;
    private GLabel ConfigNodeTitle;

    private RowPositionTracker positionTracker = new RowPositionTracker(15, 60);

    private ArrayList<UIZoneConfigRow> uiZoneConfigRows = new ArrayList<UIZoneConfigRow>();


    public static void highlightChangedField(GAbstractControl field, Object originalValue, Object newValue){

        if(newValue.equals(originalValue)){
            field.setLocalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);
        } else {
            field.setLocalColorScheme(GConstants.GOLD_SCHEME);
        }
    }



    public ConfigurationFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public ConfigurationFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    @Override
    public void init() {
        super.init();

        btnConfigureChair = new GButton((PApplet)this, 60, 350, 200, 30, s("send_config_to_chair"));
        btnConfigureChair.addEventHandler(this, "handleButtonEvents");

        btnCancelConfig = new GButton((PApplet)this, 110, 400, 100, 30, s("cancel_config"));
        btnCancelConfig.addEventHandler(this, "handleButtonEvents");

        ConfigNodeTitle = new GLabel(this, 0, 10, width, 40);

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
                uiZoneConfigRows.add(new UIZoneConfigRow(this, zone, positionTracker));
            }
        }

        btnConfigureChair.moveTo(btnConfigureChair.getX(), positionTracker.getY());

        setConfigAppearance();
    }

    private void setConfigAppearance(){

        if(node != null){
            String windowTitle = String.format(s("title_config"), node.getId());
            getFrame().setTitle(windowTitle);
            ConfigNodeTitle.setText(windowTitle);
        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        log.debug("Button event");
    }

}
