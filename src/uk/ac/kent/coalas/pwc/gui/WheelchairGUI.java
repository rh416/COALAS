package uk.ac.kent.coalas.pwc.gui;

import controlP5.ControlP5;
import processing.core.PApplet;
import processing.serial.Serial;
import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterface;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;

import java.util.EnumMap;

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI extends PApplet implements PWCInterfaceListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, CONFIG, DIAGNOSTICS};
    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private ControlP5 ControlsObject;

    private PWCInterface WheelchairInterface = new PWCInterface(this);

    public void setup() {
        size(400, 400);
        ControlsObject = new ControlP5(this);

        addNewFrame(FrameId.USER, new DiagnosticsFrame(this, 320, 480, 200, 200));
        addNewFrame(FrameId.CONFIG, new ConfigurationFrame(this, 320, 480, 525, 200));
        addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(this, 320, 480, 850, 200));
    }

    public void draw() {

        if(frameCount % 50 == 0){
            this.onPWCInterfaceEvent(new PWCInterfaceEvent(PWCInterfaceEvent.EventType.CHAIR_STOPPED, null));
        }
        if(frameCount % 200 == 0){
            getFrame(FrameId.DIAGNOSTICS).onPWCInterfaceEvent(new PWCInterfaceEvent(PWCInterfaceEvent.EventType.BUS_SCAN, null));
        }

    }

    private WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        frames.put(id, frame);
        return frame;
    }

    public WheelchairGUIFrame getFrame(FrameId frameId){

        return frames.get(frameId);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        // Handle any specific events we want to here
        switch(e.getType()){

        }



        // When we get an event from the PWC Interface, forward it to all the other frames
        for(WheelchairGUIFrame frame : frames.values()){
            frame.onPWCInterfaceEvent(e);
        }

    }

    public void serialEvent(Serial port){

        // Send the input to the interface to parsed
        WheelchairInterface.parse(port.readString());
    }
}
