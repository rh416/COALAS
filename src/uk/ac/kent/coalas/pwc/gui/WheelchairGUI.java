package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.*;
import jssc.*;
import org.apache.log4j.Logger;

import uk.ac.kent.coalas.pwc.gui.frames.MainUIFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by rm538 on 05/08/2014.
 */
public class WheelchairGUI implements PWCInterfaceListener {

    // Store a link to this instance
    public static WheelchairGUI instance = null;

    // Ensure that we refer to different frames consistently
    public static enum FrameId {MAIN, USER, OVERVIEW, CONFIG, DIAGNOSTICS, JOYSTICK}

    public static final int MAX_NODES = 9;

    public static final boolean ENABLE_LOGGING = true;

    public static final int DEFAULT_COLOUR_SCHEME = GConstants.BLUE_SCHEME;
    public static final int ERROR_COLOUR_SCHEME = GConstants.SCHEME_9;
    public static final int HIGHLIGHT_COLOUR_SCHEME = GConstants.GOLD_SCHEME;
    public static final int SUCCESS_COLOUR_SCHEME = GConstants.GREEN_SCHEME;

    public static final int PANEL_COLOUR_SCHEME = GConstants.SCHEME_10;
    public static final int CONSOLE_COLOUR_SCHEME = GConstants.SCHEME_8;

    // Store references to any frame we create
    private static EnumMap<FrameId, WheelchairGUIFrame> frames = new EnumMap<FrameId, WheelchairGUIFrame>(FrameId.class);

    public static int WindowWidth = 320;
    public static int WindowHeight = 480;

    public static Locale CurrentLocale = new Locale("en", "GB");
    //public static Locale CurrentLocale = new Locale("fr", "FR");
    public static ResourceBundle Strings = ResourceBundle.getBundle("uk.ac.kent.coalas.pwc.gui.locale/Strings", CurrentLocale);

    public static int DUE_BAUD_RATE = 115200;        // Must match the baud rate set in the DUE firmware.

    // Variables for use in headless mode. Set as many useful defaults as possible
    public static enum HeadlessMode {LIST_SERIAL_PORTS, MONITOR_JOYSTICK};


    private static int HEADLESS_BaudRate = DUE_BAUD_RATE;
    private static String HEADLESS_ComPort = null;
    private static HeadlessMode HEADLESS_Mode = null;

    private static PWCConsoleCommunicationProvider consoleCommsProvider = new PWCConsoleCommunicationProvider();
    private static PWCDueSerialCommunicationProvider serialCommsProvider = new PWCDueSerialCommunicationProvider();

    // Use this interface for debubgging using the console I/O
    private static PWCInterface DueWheelchairInterface = new PWCInterface(consoleCommsProvider);
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private static PWCInterface DueWheelchairInterface = new PWCInterface(serialCommsProvider);


    public static void main(String args[]) {

        WheelchairGUI.createInstance();

        // If no command line arguments are given, launch the default GUI
        if(args.length == 0 || (args.length == 1 && args[0].trim() == "")) {
            LaunchGUI();
        } else {
            // Parse the command line arguments
            int argIndex = 0;

            try {

                while (argIndex < args.length) {
                    String command = args[argIndex];

                    // Connect using the specified port
                    if ("-p".equals(command)){
                        HEADLESS_ComPort = args[argIndex + 1];
                        argIndex++;
                    } else if ("-b".equals(command)){
                        HEADLESS_BaudRate = Integer.parseInt(args[argIndex + 1]);
                        argIndex++;
                    } else if ("-m".equals(command)){
                        try {
                            HEADLESS_Mode = HeadlessMode.valueOf(args[argIndex + 1]);
                        } catch (IllegalArgumentException iex){
                            throw new Exception("The given mode is not valid. It must be one of: " + join(HeadlessMode.values(), ", "));
                        }
                        argIndex++;
                    }

                    argIndex++;
                }

                if(HEADLESS_Mode == null){
                    throw new Exception("Please select which mode you would like to use. Choose from the following: " + join(HeadlessMode.values(), ", "));
                }

                LaunchHeadlessMode();
            } catch (Exception ex){
                // For any errors, report the message back to the console / log file
                log.error(ex.getMessage());
                System.exit(1);
            }
        }
    }

    private static Logger log = Logger.getLogger(WheelchairGUI.class);

    public WheelchairGUI(){

        // Register this class to receive events from the wheelchair
        DueWheelchairInterface.registerListener(this);

        G4P.setGlobalColorScheme(DEFAULT_COLOUR_SCHEME);
    }

    public static void createInstance(){

        getInstance();
    }

    public static WheelchairGUI getInstance(){

        if(instance == null){
            instance = new WheelchairGUI();
        }

        return instance;
    }

    private static void LaunchGUI(){

        addNewFrame(FrameId.MAIN, new MainUIFrame());
    }

    private static void LaunchHeadlessMode() throws Exception{

        if(HEADLESS_Mode == HeadlessMode.LIST_SERIAL_PORTS){

            String[] ports = SerialPortList.getPortNames();

            for(String port : ports){
                OutputHeadlessDataLn(port);
            }

            System.exit(0);

        } else if(HEADLESS_Mode == HeadlessMode.MONITOR_JOYSTICK){
            if(HEADLESS_ComPort == null){
                throw new Exception("Please specify the serial port that the chair is connected to");
            }

            // Connect to the given com port
            serialCommsProvider.connect(HEADLESS_ComPort, HEADLESS_BaudRate);
        }

    }

    private static void OutputHeadlessData(String data){

        System.out.print(data);
    }

    private static void OutputHeadlessDataLn(String data){

        System.out.println(data);
    }

    public static WheelchairGUIFrame addNewFrame(FrameId id, WheelchairGUIFrame frame){

        // Register this window to receive events from the Wheelchair Interface
        getChairInterface().registerListener(frame);
        // Record that this frame is open
        frames.put(id, frame);
        // Record this frame's ID
        frame.setFrameId(id);
        return frame;
    }

    public static WheelchairGUIFrame getFrame(FrameId frameId){

        WheelchairGUIFrame frame = frames.get(frameId);

        if(frame != null && frame.finished){
            return null;
        } else {
            return frame;
        }
    }

    public void childWindowClosing(WheelchairGUIFrame frame){

        // Stop receiving events from the Wheelchair Interface
        getChairInterface().unregisterListener(frame);
        // Remove this frame from the list of open windows
        frames.remove(frame.getFrameId());

        // If this window was the last one or the main window, close the application
        if(frames.size() == 0 || frame.getFrameId() == FrameId.MAIN){
            System.exit(0);
        }

    }

    public static PWCInterface getChairInterface(){

        return DueWheelchairInterface;
    }

    public PWCDueSerialCommunicationProvider getPWCConnection(){

        return serialCommsProvider;
    }

    public void onPWCInterfaceEvent(PWCInterfaceEvent e){

        // These events are only used in headless mode (and only certain modes) - so check which mode we're in
        if(HEADLESS_Mode == HeadlessMode.MONITOR_JOYSTICK){
            if(e.getType() == PWCInterfaceEvent.EventType.JOYSTICK_FEEDBACK){
                PWCInterfacePayloadJoystickFeedback joystickFeedback = (PWCInterfacePayloadJoystickFeedback) e.getPayload();
                int inSpeed = joystickFeedback.getInputPosition().getSpeed();
                int inTurn = joystickFeedback.getInputPosition().getTurn();
                int outSpeed = joystickFeedback.getOutputPosition().getSpeed();
                int outTurn = joystickFeedback.getOutputPosition().getTurn();

                // Output the Joystick data as 3 digit number from -100 to +100 for each one of
                OutputHeadlessDataLn(String.format("%03d%03d%03d%03d", inSpeed, inTurn, inSpeed, inTurn));
            }
        }

    }

    public static String s(String stringName){

        return Strings.getString(stringName);
    }




















    public static class PWCConsoleCommunicationProvider implements PWCInterfaceCommunicationProvider{

        private PWCInterface pwcInterface = null;
        private BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));
        private Thread inputReadThread = new Thread(){

            public void run(){

                while(!Thread.currentThread().isInterrupted()){
                    // Check the console input for any new data
                    checkInput();
                    try{
                        // Sleep for 100ms
                        Thread.sleep(100);
                    } catch (Exception e){
                        log.error(e.getMessage());
                    }
                }

            }
        };

        public PWCConsoleCommunicationProvider(){

            // Start the console reading thread
            inputReadThread.start();
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void setPWCInterface(PWCInterface pwcInterface){

            this.pwcInterface = pwcInterface;
        }

        @Override
        public void write(String command){

            System.out.println(command);
        }

        public void checkInput(){

            try {
                if(SystemIn.ready()) {
                    String line = SystemIn.readLine();
                    pwcInterface.buffer(line + '\n');
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static class PWCDueSerialCommunicationProvider implements PWCInterfaceCommunicationProvider, SerialPortEventListener{

        private PWCInterface pwcInterface = null;
        private SerialPort transportSerialPort = null;

        @Override
        public boolean isAvailable() {

            return (transportSerialPort != null && transportSerialPort.isOpened());
        }

        @Override
        public void write(String command) {

            try {
                transportSerialPort.writeString(command + '\n');
            } catch (SerialPortException ex){
                log.error(ex.getMessage());
            }
        }

        @Override
        public void setPWCInterface(PWCInterface pwcInterface){

            this.pwcInterface = pwcInterface;
        }

        public void connect(String portName, int baudRate) throws SerialPortException{

            // Connect to the serial port
            transportSerialPort = new SerialPort(portName);

            transportSerialPort.openPort();
            transportSerialPort.setParams(baudRate, 8, 1, 0);

            // Determine which events we want to be alerted for
            int eventMask = SerialPort.MASK_RXCHAR + SerialPort.MASK_ERR;
            transportSerialPort.setEventsMask(eventMask);

            // Register as a listener
            transportSerialPort.addEventListener(this);

            log.info("Attempting to connect to PWC on Port: " + portName);

            // Pause for a moment to give the Serial port time to open fully
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }

            pwcInterface.requestVersion();
        }

        public void serialEvent(SerialPortEvent event){

            // If the event is telling us that data is available...
            if(event.isRXCHAR()) {

                // Try reading it
                try {
                    // Send the input to the interface to be buffered
                    DueWheelchairInterface.buffer(transportSerialPort.readString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(event.isERR()){
                log.error("There was an error with the serial port");
            }
        }

        public void disconnect(){

            // Close the serial port if it is already active
            if (isAvailable()) {
                try{
                    transportSerialPort.closePort();
                } catch (SerialPortException ex){
                    log.error(ex.getMessage());
                }
            }

            if(pwcInterface != null) {
                pwcInterface.disconnect();
            }
        }
    }

    public static String firstCharUpperCase(String inString){

        if(inString.length() > 0){
            return Character.toUpperCase(inString.charAt(0)) + inString.substring(1).toLowerCase();
        } else {
            return inString;
        }
    }

    public static String join(List<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

    public static String join(Object[] list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(Object o : list) {

            sb.append(loopDelim);
            sb.append(o.toString());

            loopDelim = delim;
        }

        return sb.toString();
    }
}
