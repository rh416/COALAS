package uk.ac.kent.coalas.pwc.gui.frames;

import controlP5.ControlEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceErrorPayload;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEventPayload;

/**
 * Created by rm538 on 06/08/2014.
 */
public class DiagnosticsFrame extends WheelchairGUIFrame {


    public DiagnosticsFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    public void setup() {

        super.setup();
    }

    @Override
    public void draw() {
        background(255);

    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {


    }

    public void controlEvent(ControlEvent e){

    }
}
