package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.GButton;
import g4p_controls.GDropList;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import processing.core.PApplet;

import processing.serial.Serial;

import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.OverviewFrame;
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
public class WheelchairGUI extends PApplet implements PWCInterfaceListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, OVERVIEW, CONFIG, DIAGNOSTICS}
    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private int WindowWidth = 320;
    private int WindowHeight = 480;
    private int WindowXPosition = 1200;
    private int WindowYPosition = 100;
    private int WindowXOffset = 10;

    private static int DUE_BAUD_RATE = 9600;        // Must match the baud rate set in the DUE firmware.

    private static final String STRING_CONNECT = "Connect";
    private static final String STRING_DISCONNECT = "Disconnect";
    private static final String STRING_CHECKING_CONNECTION = "Connecting to Wheelchair, please wait.";
    private static final String STRING_FIRMWARE_RESPONSE = "Wheelchair connected! Running firmware version: ";
    private static final String STRING_SERIAL_DISCONNECTED = "Serial Disconnected";


    GDropList DueSerialPortList;
    GButton DueSerialControlButton;
    GLabel DueInfoLabel;

    BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial DueSerialPort, DXBusSerialPort;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCDueSerialCommunicationProvider());

    public void setup() {
        size(300, 300);
        smooth();

        DueSerialPortList = new GDropList(this, 25, 25, 100, 150, 4);
        DueSerialPortList.setItems(Serial.list(), 0);

        DueSerialControlButton = new GButton(this, 175, 25, 100, 30, STRING_CONNECT);

        DueInfoLabel = new GLabel(this, 25, 100, 200, 30);

        PWCInterface pwcI = getChairInterface();

        // Simulate commands from the chair
        pwcI.parse("S1:Y");
        pwcI.parse("S2:Y");
        pwcI.parse("S4:Y");
        pwcI.parse("S5:Y");

        pwcI.parse("S3:N");
        pwcI.parse("S6:N");
        pwcI.parse("S7:N");
        pwcI.parse("S8:N");
        pwcI.parse("S9:N");

        pwcI.parse("C1:1gIE,1hu,1aO.");
        pwcI.parse("C2:3cI,3eF,3bG.");
        pwcI.parse("C4:FaE,FbJ.");
        pwcI.parse("C5:RbIE,RcJ,RdG.");

        addNewFrame(FrameId.USER, new ConfigurationFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.OVERVIEW, new OverviewFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        //addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;

        ConfigurationFrame cfgFrame = (ConfigurationFrame)getFrame(FrameId.USER);
        cfgFrame.setConfigNode(getChairInterface().getNode(1));
    }

    public void draw() {

        background(255);    // Make sure this is here, otherwise controls won't work properly

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

    public WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        frame.setParent(this);
        frames.put(id, frame);
        return frame;
    }

    public WheelchairGUIFrame getFrame(FrameId frameId){

        return frames.get(frameId);
    }

    private String timestamp(){

        return new Timestamp(new Date().getTime()).toString();
    }

    public PWCInterface getChairInterface(){

        return DueWheelchairInterface;
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        // Handle any specific events we want to here
        switch(e.getType()){

            case ERROR:
                PWCInterfacePayloadError errorPayload = (PWCInterfacePayloadError)e.getPayload();
                Exception err = errorPayload.getException();
                println(errorPayload.getResponse() + ": " + err.getClass().getName() + ":" + err.getMessage());

                StringWriter sw = new StringWriter();
                err.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();

                println(exceptionAsString);
                break;

            case FIRMWARE_INFO:
                PWCInterfacePayloadFirmwareInfo firmwareInfo = (PWCInterfacePayloadFirmwareInfo) e.getPayload();
                DueInfoLabel.setText(STRING_FIRMWARE_RESPONSE + firmwareInfo.getVersion());
                break;

            default:
                println(e.getType().name());
                //LogTextArea.setText(LogTextArea.getText() + timestamp() + " - " + e.getType().name() + "\n");
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

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == DueSerialControlButton && event == GEvent.CLICKED) {
            String portName = DueSerialPortList.getSelectedText();

            // Close the serial port if it is already active
            if (DueSerialPort != null) {
                DueSerialPort.clear();
                DueSerialPort.stop();
            }

            // If we are trying to connect
            if(button.getText() == STRING_CONNECT){

                try {
                    // Connect to the serial port
                    DueSerialPort = new Serial(this, portName, DUE_BAUD_RATE);

                    // Change button label for disconnection
                    button.setText(STRING_DISCONNECT);
                    println("Serial connected on: " + portName);
                    println("Checking DUE Firmware version");
                    DueInfoLabel.setText(STRING_CHECKING_CONNECTION);
                    DueWheelchairInterface.getVersion();
                } catch (RuntimeException e){
                    DueInfoLabel.setText("Error: " + e.getMessage());
                }
            } else {
                // Otherwise we are trying to disconnect, so don't recreate connection but we need to change the button label
                button.setText(STRING_CONNECT);
                DueInfoLabel.setText(STRING_SERIAL_DISCONNECTED);
                println("Serial disconnected");
            }
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        // Reset the connect button to ease changing port
        DueSerialControlButton.setText(STRING_CONNECT);
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
