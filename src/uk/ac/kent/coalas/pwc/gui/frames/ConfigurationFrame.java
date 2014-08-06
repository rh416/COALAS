package uk.ac.kent.coalas.pwc.gui.frames;

import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;

/**
 * Created by rm538 on 06/08/2014.
 */
public class ConfigurationFrame extends WheelchairGUIFrame {


    public ConfigurationFrame(WheelchairGUI theParent, int theWidth, int theHeight, int xPos, int yPos) {

        super(theParent, theWidth, theHeight, xPos, yPos);
    }

    @Override
    public void setup() {
        super.setup();

        // Setup controls here
    }

    @Override
    public void draw() {

    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        console("Got event!");
    }
}
