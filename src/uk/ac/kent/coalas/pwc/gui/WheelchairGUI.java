package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.*;
import org.apache.log4j.Logger;
import processing.core.PApplet;

import processing.serial.Serial;

import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.JoystickMonitorFrame;
import uk.ac.kent.coalas.pwc.gui.frames.OverviewFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI extends PApplet implements PWCInterfaceListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, OVERVIEW, CONFIG, DIAGNOSTICS, JOYSTICK}

    public static final int MAX_NODES = 9;

    public static final boolean ENABLE_LOGGING = true;

    public static final int DEFAULT_COLOUR_SCHEME = GConstants.BLUE_SCHEME;
    public static final int ERROR_COLOUR_SCHEME = GConstants.SCHEME_9;
    public static final int HIGHLIGHT_COLOUR_SCHEME = GConstants.GOLD_SCHEME;
    public static final int SUCCESS_COLOUR_SCHEME = GConstants.GREEN_SCHEME;

    public static final int PANEL_COLOUR_SCHEME = GConstants.SCHEME_10;
    public static final int CONSOLE_COLOUR_SCHEME = GConstants.SCHEME_8;

    // Store references to any frame we create
    private EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    private int WindowWidth = 320;
    private int WindowHeight = 480;

    private static int DUE_BAUD_RATE = 115200;        // Must match the baud rate set in the DUE firmware.

    public static Locale CurrentLocale = new Locale("en", "GB");
    //public static Locale CurrentLocale = new Locale("fr", "FR");
    public static ResourceBundle Strings = ResourceBundle.getBundle("uk.ac.kent.coalas.pwc.gui.locale/Strings", CurrentLocale);

    private GDropList DueSerialPortList;
    private GButton btnDueSerialControlButton, btnJoystickMonitorLaunch;
    private GTextArea InterfaceLogArea;

    private String DueSerialInfo;

    private LinkedList<String> logQueue = new LinkedList<String>();

    private DateFormat timestampFormatShort = new SimpleDateFormat("HH:mm:ss");
    private DateFormat timestampFormatLong = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

    private BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private int SerialListUpdateRateMs = 500;
    private long SerialListUpdateLastTime = 0;

    private Serial DueSerialPort = null;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(new PWCDueSerialCommunicationProvider());



    public static void main(String args[]) {
        PApplet.main("uk.ac.kent.coalas.pwc.gui.WheelchairGUI");
    }

    private static Logger log = Logger.getLogger(WheelchairGUI.class);

    public void setup(){
        frame.setTitle("COALAS Wheelchair Configuration UI");
        size(WindowWidth, WindowHeight);
        noSmooth();

        // Register this window to receive events from the wheelchair
        DueWheelchairInterface.registerListener(this);

        G4P.setGlobalColorScheme(DEFAULT_COLOUR_SCHEME);

        DueSerialPortList = new GDropList(this, 25, 25, 100, 150, 4);
        DueSerialPortList.setItems(new String[]{"placeholder"}, 0);
        updateSerialPortList(0);

        btnDueSerialControlButton = new GButton(this, 195, 25, 100, 30, s("connect"));

        Font logFont = new Font("Monospace", Font.PLAIN, 10);

        InterfaceLogArea = new GTextArea(this, 25, 120, 275, 290, GConstants.SCROLLBARS_VERTICAL_ONLY);
        //InterfaceLogArea.setTextEditEnabled(false);
        InterfaceLogArea.setFont(logFont);
        InterfaceLogArea.setLocalColorScheme(CONSOLE_COLOUR_SCHEME);

        btnJoystickMonitorLaunch = new GButton(this, 25, 430, 275, 30, s("launch_joystick_monitor"));
    }

    public void draw() {

        // Check if there's any mesages waiting in the log - if so write them to the screen
        while(logQueue.peekFirst() != null){
            InterfaceLogArea.appendText(logQueue.removeFirst() + "\n");
        }

        background(255);    // Make sure this is here, otherwise controls won't work properly

        // Write any information regarding the connection to the Due to the screen
        if(DueSerialInfo != null) {
            fill(100);
            textSize(11);
            text(DueSerialInfo, 25, 70, 295, 110);
        }

        // Only update the serial list if we're actively looking at this screen and the list hasn't been updated recently
        if(focused && System.currentTimeMillis() - SerialListUpdateLastTime > SerialListUpdateRateMs){
            updateSerialPortList();
            SerialListUpdateLastTime = System.currentTimeMillis();
        }

        try {
            if(SystemIn.ready()) {
                String line = SystemIn.readLine();
                DueWheelchairInterface.buffer(line + '\n');
            }
        } catch (Exception e){
            println(e.getMessage());
        }
    }

    public WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        // Create a reference back to this window
        frame.setParent(this);
        // Register this window to receive events from the Wheelchair Interface
        getChairInterface().registerListener(frame);
        // Record that this frame is open
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

    public void childWindowClosing(WheelchairGUIFrame frame){

        getChairInterface().unregisterListener(frame);
        frames.remove(frame);
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

    private void updateSerialPortList(int index){

        String[] portList = Serial.list();
        int validIndex = Math.min(index, portList.length - 1);

        String currentSelectedItem = DueSerialPortList.getSelectedText();

        // Check that the correct item is re-selected
        // If the indexes don't match, scan the list
        if (index >= portList.length || !portList[index].equals(currentSelectedItem)) {
            validIndex = 0;
            int loopIndex = 0;
            for (String item : portList) {
                if (item.equals(currentSelectedItem)) {
                    validIndex = loopIndex;
                    break;
                }
                loopIndex++;
            }
        }

        DueSerialPortList.setItems(portList, validIndex);

    }

    private void updateSerialPortList(){

        updateSerialPortList(DueSerialPortList.getSelectedIndex());
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

                //Open Overview Window
                int xPos = frame.getX() + frame.getWidth() + 10;
                int yPos = frame.getY();
                OverviewFrame overview = (OverviewFrame) addNewFrame(FrameId.OVERVIEW, new OverviewFrame(WindowWidth, WindowHeight, xPos, yPos));
                overview.scanBus();
                break;

            case TIMEOUT:
                PWCInterfacePayloadTimeout timeoutInfo = (PWCInterfacePayloadTimeout) e.getPayload();
                // Check if the timeout is related to getting the version information
                if(timeoutInfo.getRequest().getType() == PWCInterfaceEvent.EventType.FIRMWARE_INFO){
                    DueSerialInfo = String.format(s("firmware_timeout"), DueSerialPortList.getSelectedText());
                }
                break;

            default:
                logToScreen(timestamp() + " - " + e.getType());
        }
    }

    public static String s(String stringName){

        return Strings.getString(stringName);
    }

    public void serialEvent(Serial port){

        try {
            // Send the input to the interface to be buffered
            DueWheelchairInterface.buffer(port.readString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnDueSerialControlButton && event == GEvent.CLICKED) {
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
                    log.info("Attempting to connect to PWC on Port: " + portName);

                    // Change button label for disconnection
                    button.setText(s("disconnect"));
                    DueSerialInfo = s("connection_check");

                    // Pause for a moment to give the Serial port time to open fully
                    try{
                        Thread.sleep(250);
                    } catch (InterruptedException e){}

                    DueWheelchairInterface.getVersion();
                } catch (RuntimeException e){
                    e.printStackTrace();
                    DueSerialInfo = "Error: " + e.getMessage();
                } catch(Exception er){
                    er.printStackTrace();
                }
            } else {
                // Otherwise we are trying to disconnect, so don't recreate connection but we need to change the button label
                button.setText(s("connect"));
                DueSerialInfo = s("connection_end");
            }
        } else if(button == btnJoystickMonitorLaunch){
            int xPos = frame.getX() - frame.getWidth() - 10;
            int yPos = frame.getY();
            addNewFrame(FrameId.JOYSTICK, new JoystickMonitorFrame(WindowWidth, WindowHeight, xPos, yPos));
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        // Reset the connect button to ease changing port
        btnDueSerialControlButton.setText(s("connect"));
    }

    public void handleTextEvents(GEditableTextControl textControl, GEvent event){
        // Do nothing - this stops the G4P library sending messages to the console about missing event handlers
    }



    public void handlePanelEvents(GPanel panel, GEvent event){
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
