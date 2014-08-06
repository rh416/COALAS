package uk.ac.kent.coalas.pwc.gui.frames;

import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;

/**
 * Created by rm538 on 06/08/2014.
 */
public class DiagnosticsFrame extends WheelchairGUIFrame {


    public DiagnosticsFrame(WheelchairGUI parent, int width, int height, int xPos, int yPos){

        super(parent, width, height, xPos, yPos);
    }

    public void setup() {

        super.setup();

        cp5.addSlider("abc").setRange(0, 255).setPosition(10, 10);
        cp5.addSlider("def").plugTo(parent, "def").setRange(0, 255).setPosition(10, 30);
    }

    @Override
    public void draw() {
        //console(frameRate);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        String eType = e.getType().name();
        console("Got event: " + eType);

        switch(e.getType()){

            case BUS_SCAN:
                console("Lets see what we have");
                break;

            case CHAIR_MOVED:
                console("Uh oh!");
                break;

            case CHAIR_STOPPED:
                console("Phew!");
                break;
        }
    }
}
