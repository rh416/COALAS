package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.*;
import org.apache.log4j.Logger;
import processing.core.PApplet;

import processing.serial.Serial;

import sun.security.krb5.Config;
import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.OverviewFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI extends PApplet implements PWCInterfaceListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, OVERVIEW, CONFIG, DIAGNOSTICS}

    public static final int MAX_NODES = 9;

    public static final boolean ENABLE_LOGGING = true;

    public static final int DEFAULT_COLOUR_SCHEME = GConstants.BLUE_SCHEME;

    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private int WindowWidth = 320;
    private int WindowHeight = 480;
    private int WindowXPosition = 1200;
    private int WindowYPosition = 100;
    private int WindowXOffset = 10;

    private static int DUE_BAUD_RATE = 9600;        // Must match the baud rate set in the DUE firmware.

    public static Locale CurrentLocale = new Locale("en", "GB");
    //public static Locale CurrentLocale = new Locale("fr", "FR");
    public static ResourceBundle Strings = ResourceBundle.getBundle("uk.ac.kent.coalas.pwc.gui.locale/Strings", CurrentLocale);

    private GDropList DueSerialPortList;
    private GButton DueSerialControlButton;
    private GTextArea InterfaceLogArea;

    private String DueSerialInfo;

    private LinkedList<String> logQueue = new LinkedList<String>();

    private DateFormat timestampFormatShort = new SimpleDateFormat("HH:mm:ss");
    private DateFormat timestampFormatLong = new SimpleDateFormat("YYYY/MM/dd - HH:mm:ss");

    private BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial DueSerialPort = null;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCDueSerialCommunicationProvider());


    public static void main(String args[]) {
        PApplet.main("uk.ac.kent.coalas.pwc.gui.WheelchairGUI");
    }

    private static Logger log = Logger.getLogger(WheelchairGUI.class);

    public void setup(){
        size(WindowWidth, WindowHeight);
        noSmooth();

        G4P.setGlobalColorScheme(DEFAULT_COLOUR_SCHEME);

        DueSerialPortList = new GDropList(this, 25, 25, 100, 150, 4);
        //DueSerialPortList.setItems(Serial.list(), 0);
        DueSerialPortList.setItems(new String[]{"one", "two", "three"}, 0);

        DueSerialControlButton = new GButton(this, 195, 25, 100, 30, s("connect"));

        Font logFont = new Font("Monospace", Font.PLAIN, 10);

        InterfaceLogArea = new GTextArea(this, 25, 120, 275, 340, GConstants.SCROLLBARS_VERTICAL_ONLY);
        //InterfaceLogArea.setTextEditEnabled(false);
        InterfaceLogArea.setFont(logFont);
        InterfaceLogArea.setLocalColorScheme(GConstants.SCHEME_8);

        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.OVERVIEW, new OverviewFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        addNewFrame(FrameId.CONFIG, new ConfigurationFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
    }

    public void draw() {

        if(frameCount == 1){
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


            ConfigurationFrame configFrame = (ConfigurationFrame)getFrame(FrameId.CONFIG);
            configFrame.setConfigNode(getChairInterface().getNode(1));
        }

        while(logQueue.peekFirst() != null){
            InterfaceLogArea.appendText(logQueue.removeFirst() + "\n");
        }

        background(255);    // Make sure this is here, otherwise controls won't work properly

        if(DueSerialInfo != null) {
            fill(100);
            textSize(11);
            text(DueSerialInfo, 25, 70, 295, 110);
        }

        try {
            if(SystemIn.ready()) {
                String line = SystemIn.readLine();
                DueWheelchairInterface.parse(line);
            }
        } catch (Exception e){
            println(e.getMessage());
        }
    }

    public WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        frame.setParent(this);
        frames.put(id, frame);
        return frame;
    }

    public WheelchairGUIFrame getFrame(FrameId frameId){

        WheelchairGUIFrame frame = frames.get(frameId);

        if(frame != null && frame.finished){
            return null;
        } else {
            return frame;
        }
    }

    private String timestamp(boolean longFormat){

        if(longFormat){
            return timestampFormatLong.format(new Date());
        } else {
            return timestampFormatShort.format(new Date());
        }
    }

    private String timestamp(){

        return timestamp(false);
    }

    public PWCInterface getChairInterface(){

        return DueWheelchairInterface;
    }

    private synchronized void logToScreen(String logString){

        logQueue.add(logString);
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        // Uncomment this line for detailed logging - will be a lot of data if Diagnostics window is used
        logToScreen(timestamp() + " - " + e.getPayload().getResponse());

        // Handle any specific events we want to here
        switch(e.getType()){

            case ERROR:
                PWCInterfacePayloadError errorPayload = (PWCInterfacePayloadError)e.getPayload();
                Exception err = errorPayload.getException();

                logToScreen(timestamp() + " - " + errorPayload.getErrorMessage());

                StringWriter sw = new StringWriter();
                err.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();

                println(exceptionAsString);
                break;

            case FIRMWARE_INFO:
                PWCInterfacePayloadFirmwareInfo firmwareInfo = (PWCInterfacePayloadFirmwareInfo) e.getPayload();
                DueSerialInfo = String.format(s("firmware_response"), firmwareInfo.getVersion());
                break;

            default:
                logToScreen(timestamp() + " - " + e.getType());
        }

        // When we get an event from the PWC Interface, forward it to all the other frames so that they can handle it
        for(WheelchairGUIFrame frame : frames.values()){
            frame.onPWCInterfaceEvent(e);
        }
    }

    public static String s(String stringName){

        return Strings.getString(stringName);
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
            if(button.getText() == s("connect")){

                try {
                    // Connect to the serial port
                    DueSerialPort = new Serial(this, portName, DUE_BAUD_RATE);

                    // Change button label for disconnection
                    button.setText(s("disconnect"));
                    DueSerialInfo = s("connection_check");
                    DueWheelchairInterface.getVersion();
                } catch (RuntimeException e){
                    DueSerialInfo = "Error: " + e.getMessage();
                }
            } else {
                // Otherwise we are trying to disconnect, so don't recreate connection but we need to change the button label
                button.setText(s("connect"));
                DueSerialInfo = s("connection_end");
            }
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        // Reset the connect button to ease changing port
        DueSerialControlButton.setText(s("connect"));
    }

    public void handleTextEvents(GEditableTextControl textControl, GEvent event){
        // Do nothing - this stops the G4P library sending messages to the console about missing event handlers
    }


















    public class PWCConsoleCommunicationProvider implements PWCInterfaceCommunicationProvider{

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void write(String command){

            //System.out.println(command);
        }
    }

    public class PWCDueSerialCommunicationProvider implements PWCInterfaceCommunicationProvider {

        @Override
        public boolean isAvailable() {

            if(DueSerialPort == null){
                return false;
            } else{
                return DueSerialPort.port.isOpened();
            }
        }

        @Override
        public void write(String command) {

            DueSerialPort.write(command + '\n');
        }
    }

    public static String firstCharUpperCase(String inString){

        if(inString.length() > 0){
            return Character.toUpperCase(inString.charAt(0)) + inString.substring(1).toLowerCase();
        } else {
            return inString;
        }
    }
}
