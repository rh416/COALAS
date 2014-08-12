package uk.ac.kent.coalas.pwc.gui;

import controlP5.*;
import processing.core.PApplet;

import processing.core.PFont;
import processing.serial.Serial;

import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
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
public class WheelchairGUI extends PApplet implements PWCInterfaceListener, ControlListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, CONFIG, DIAGNOSTICS}
    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private ControlP5 ControlsObject;

    public PFont HeadingFont = createFont("Georgia", 20);
    public PFont DefaultFont = createFont("Tahoma", 15);

    private ListBox DueSerialList, DXBusSerialList;
    private Textfield DueBaudRate;
    private Textlabel DueError;
    private Button DueSerialConnect, DueSerialDisconnect;
    private Textarea LogTextArea;
    private CheckBox LogCheckbox;

    private static String CONTROL_ID_DROPDOWN_DUE = "ListDUESerialPorts";
    private static String CONTROL_ID_TEXTFIELD_DUE_BAUD = "TextfieldDUEBaudRate";
    private static String CONTROL_ID_TEXTLABEL_DUE_ERROR = "TextlabelDUEError";
    private static String CONTROL_ID_BUTTON_DUE_CONNECT = "ButtonDUESerialConnect";
    private static String CONTROL_ID_BUTTON_DUE_DISCONNECT = "ButtonDUESerialDisconnect";
    private static String CONTROL_ID_TEXTAREA_LOG = "TextareaLog";
    private static String CONTROL_ID_CHECKBOX_LOG = "CheckboxLog";

    private int WindowWidth = 320;
    private int WindowHeight = 480;
    private int WindowXPosition = 500;
    private int WindowYPosition = 100;
    private int WindowXOffset = 10;

    BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial DueSerialPort, DXBusSerialPort;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCDueSerialCommunicationProvider());

    int cnt = 1;

    public void setup() {
        size(800, 800);
        smooth();

        ControlsObject = new ControlP5(this);

        addNewFrame(FrameId.USER, new DiagnosticsFrame(this, WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.CONFIG, new ConfigurationFrame(this, WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(this, WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;

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

        DueSerialDisconnect = ControlsObject.addButton(CONTROL_ID_BUTTON_DUE_DISCONNECT)
                .setPosition(360, 100)
                .setCaptionLabel("Disconnect")
                .setSize(100, 40)
                .lock();

        DueError = ControlsObject.addTextlabel(CONTROL_ID_TEXTLABEL_DUE_ERROR)
                .setPosition(50, 350)
                .setText("Blah blah blah")
                .setSize(200, 20)
                .setColor(0xffff0000);

        LogTextArea = ControlsObject.addTextarea(CONTROL_ID_TEXTAREA_LOG)
                .setPosition(100, 300)
                .setSize(400, 400)
                .setFont(createFont("Consolas", 12))
                .setColorBackground(0xff000000);

        LogCheckbox = ControlsObject.addCheckBox(CONTROL_ID_CHECKBOX_LOG)
                .setPosition(550, 300)
                .setCaptionLabel("Enable Logging")
                .setSize(30, 30);
    }

    public void draw() {

        try {
            if(SystemIn.ready()) {
                String line = SystemIn.readLine();
                DueWheelchairInterface.parse(line);
            }
        } catch (Exception e){
            println(e.getMessage());
        }

        if(frameCount % 500 == 0){
            //DueWheelchairInterface.getNode(1).checkExistsOnBus();
        } else if (frameCount % 300 == 0){
            //DueWheelchairInterface.getNode(3).checkExistsOnBus();
        }

    }

    private WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        frames.put(id, frame);
        return frame;
    }

    public WheelchairGUIFrame getFrame(FrameId frameId){

        return frames.get(frameId);
    }

    private String timestamp(){

        return new Timestamp(new Date().getTime()).toString();
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        // Handle any specific events we want to here
        switch(e.getType()){

            case ERROR:
                PWCInterfaceErrorPayload errorPayload = (PWCInterfaceErrorPayload)e.getPayload();
                Exception err = errorPayload.getException();
                println(errorPayload.getResponse() + ": " + err.getClass().getName() + ":" + err.getMessage());

                StringWriter sw = new StringWriter();
                err.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();

                println(exceptionAsString);
                break;

            default:
                println(e.getType().name());
                LogTextArea.setText(LogTextArea.getText() + timestamp() + " - " + e.getType().name() + "\n");
                break;
        }

        // When we get an event from the PWC Interface, forward it to all the other frames so that they can handle it
        for(WheelchairGUIFrame frame : frames.values()){
            frame.onPWCInterfaceEvent(e);
        }

    }

    public void serialEvent(Serial port){

        // Send the input to the interface to parsed
        DueWheelchairInterface.parse(port.readString());
    }

    public void controlEvent(ControlEvent event){

        if(event.isFrom(DueSerialConnect)){
//            for(int i = 1; i < 10; i++){
//                DueWheelchairInterface.getNode(i).checkExistsOnBus();
//            }
            DueWheelchairInterface.parse("C3:1gIE,1hu,1aM.");
            cnt++;
        }
    }

    public class PWCConsoleCommunicationProvider implements PWCInterfaceCommunicationProvider{

        @Override
        public void write(String command){

            System.out.print(command);
        }
    }

    public class PWCDueSerialCommunicationProvider implements PWCInterfaceCommunicationProvider {

        @Override
        public void write(String command) {

            DueSerialPort.write(command);
        }
    }
}
