package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.*;
import processing.core.PApplet;

import processing.core.PFont;
import processing.serial.Serial;

import uk.ac.kent.coalas.pwc.gui.frames.ConfigurationFrame;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.OverviewFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import javax.annotation.Resource;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI extends PApplet implements PWCInterfaceListener {

    // Ensure that we refer to different frames consistently
    public static enum FrameId {USER, OVERVIEW, CONFIG, DIAGNOSTICS}

    public static final int MAX_NODES = 9;

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

    public Font defaultLabelFont = new Font("Arial", Font.PLAIN, 12);

    private GDropList DueSerialPortList;
    private GButton DueSerialControlButton;
    private GLabel DueInfoLabel;

    private BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial DueSerialPort = null;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCDueSerialCommunicationProvider());

    public void setup() {
        size(300, 300);
        noSmooth();

        G4P.setGlobalColorScheme(DEFAULT_COLOUR_SCHEME);

        DueSerialPortList = new GDropList(this, 25, 25, 100, 150, 4);
        DueSerialPortList.setItems(Serial.list(), 0);

        DueSerialControlButton = new GButton(this, 175, 25, 100, 30, s("connect"));

        DueInfoLabel = new GLabel(this, 25, 100, 200, 30);
        DueInfoLabel.setFont(defaultLabelFont);

        //addNewFrame(FrameId.USER, new ConfigurationFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.OVERVIEW, new OverviewFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        //addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;


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
                DueInfoLabel.setText(String.format(s("firmware_response"), firmwareInfo.getVersion()));
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

    public String s(String stringName){

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
                    DueInfoLabel.setText(s("connection_check"));
                    DueWheelchairInterface.getVersion();
                } catch (RuntimeException e){
                    DueInfoLabel.setText("Error: " + e.getMessage());
                }
            } else {
                // Otherwise we are trying to disconnect, so don't recreate connection but we need to change the button label
                button.setText(s("connect"));
                DueInfoLabel.setText(s("connection_end"));
            }
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        // Reset the connect button to ease changing port
        DueSerialControlButton.setText(s("connect"));
    }


















    public class PWCConsoleCommunicationProvider implements PWCInterfaceCommunicationProvider{

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void write(String command){

            System.out.print(command);
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

            DueSerialPort.write(command);
        }
    }
}
