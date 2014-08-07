package uk.ac.kent.coalas.pwc.gui;

import controlP5.*;
import processing.core.PApplet;

import processing.core.PFont;
import processing.serial.Serial;

import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterface;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceEvent;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumMap;

/*

======= IMPORTANT ======

If you are trying to use Serial commands and you receive the following error:

Exception in thread "Animation Thread" java.lang.UnsatisfiedLinkError: jssc.SerialNativeInterface.getSerialPortNames()[Ljava/lang/String;

Make sure that the JVM has been given the location of the native files for your platform. This can be done by
    appending -Djava.library.path=/absolute-path/to/lib/serial/YOUR-PLATFORM/ to the JVM command line options in the IDE

    This value can be set using the Edit Configurations option in IntelliJ in "VM Options for Applet Viewer"

 */

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI extends PApplet implements PWCInterfaceListener, PWCInterfaceCommunicationProvider, ControlListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, CONFIG, DIAGNOSTICS}
    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private ControlP5 ControlsObject;

    PFont DefaultFont = createFont("Georgia", 20);
    PFont ErrorFont = createFont("Georgia", 15);

    private ListBox DueSerialList, DXBusSerialList;
    private Textfield DueBaudRate;
    private Textlabel DueError;
    private Button DueSerialConnect, DueSerialDisconnect;

    private static String CONTROL_ID_DROPDOWN_DUE = "ListDUESerialPorts";
    private static String CONTROL_ID_TEXTFIELD_DUE_BAUD = "TextfieldDUEBaudRate";
    private static String CONTROL_ID_TEXTLABEL_DUE_ERROR = "TextlabelDUEError";
    private static String CONTROL_ID_BUTTON_DUE_CONNECT = "ButtonDUESerialConnect";
    private static String CONTROL_ID_BUTTON_DUE_DISCONNECT = "ButtonDUESerialDisconnect";

    BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial SerialPort;

    private PWCInterface WheelchairInterface = new PWCInterface(this, this);

    public void setup() {
        size(400, 400);
        ControlsObject = new ControlP5(this);

        addNewFrame(FrameId.USER, new DiagnosticsFrame(this, 320, 480, 200, 200));
        addNewFrame(FrameId.CONFIG, new ConfigurationFrame(this, 320, 480, 525, 200));
        addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(this, 320, 480, 850, 200));


        ControlsObject.setFont(DefaultFont);

        DueSerialList = ControlsObject.addListBox(CONTROL_ID_DROPDOWN_DUE)
                .setPosition(50, 100)
                .addItems(Serial.list())
                .setLabel("Due Com Port:")
                .setSize(100, 40);

        DueBaudRate = ControlsObject.addTextfield(CONTROL_ID_TEXTFIELD_DUE_BAUD)
                .setPosition(170, 100)
                .setCaptionLabel("Due Baud Rate:")
                .setSize(50, 40);

        DueSerialConnect = ControlsObject.addButton(CONTROL_ID_BUTTON_DUE_CONNECT)
                .setPosition(240, 100)
                .setCaptionLabel("Connect")
                .setSize(100, 40);

        DueSerialConnect = ControlsObject.addButton(CONTROL_ID_BUTTON_DUE_DISCONNECT)
                .setPosition(360, 100)
                .setCaptionLabel("Disconnect")
                .setSize(100, 40)
                .lock();

        DueError = ControlsObject.addTextlabel(CONTROL_ID_TEXTLABEL_DUE_ERROR)
                .setPosition(50, 350)
                .setText("Blah blah blah")
                .setSize(200, 20)
                .setFont(ErrorFont);
    }

    public void draw() {

        try {
            if(SystemIn.ready()) {
                String line = SystemIn.readLine();
                WheelchairInterface.parse(line);
            }
        } catch (Exception e){
            println(e.getMessage());
        }

        if(frameCount % 500 == 0){
            WheelchairInterface.scanForNode(1);
        } else if (frameCount % 300 == 0){
            WheelchairInterface.scanForNode(3);
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

            default:
                break;
        }

        // When we get an event from the PWC Interface, forward it to all the other frames so that they can handle it
        for(WheelchairGUIFrame frame : frames.values()){
            frame.onPWCInterfaceEvent(e);
        }

    }

    public void serialEvent(Serial port){

        // Send the input to the interface to parsed
        WheelchairInterface.parse(port.readString());
    }

    public void controlEvent(ControlEvent event){

        if(event.getName() == CONTROL_ID_BUTTON_DUE_CONNECT){
            WheelchairInterface.scanForNode(2);
        }
    }

    public Object getCommunicationProvider(){

        return System.out; // For testing
        //return SerialPort // For production
    }
}
