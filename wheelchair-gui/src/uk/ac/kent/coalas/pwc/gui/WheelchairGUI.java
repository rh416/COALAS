package uk.ac.kent.coalas.pwc.gui;

import g4p_controls.*;
import jssc.*;
import org.apache.log4j.Logger;
import com.pi4j.wiringpi.Spi;

import uk.ac.kent.coalas.pwc.gui.frames.MainUIFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.LogFile;
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
    public static enum HeadlessMode {LIST_SERIAL_PORTS, MONITOR_JOYSTICK, LOG_START, LOG_END, LOG_LIST};


    private static int HEADLESS_BaudRate = DUE_BAUD_RATE;
    private static String HEADLESS_ComPort = null;
    private static HeadlessMode HEADLESS_Mode = null;

    private static PWCConsoleCommunicationProvider consoleCommsProvider = new PWCConsoleCommunicationProvider();
    private static PWCDueSerialCommunicationProvider serialCommsProvider = new PWCDueSerialCommunicationProvider();
    //private static PWCSPISerialCommunicationProvider spiCommsProvider = new PWCSPISerialCommunicationProvider();

    // Use this interface for debubgging using the console I/O
    //private static PWCInterface DueWheelchairInterface = new PWCInterface(consoleCommsProvider);
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    private static PWCInterface DueWheelchairInterface = new PWCInterface(serialCommsProvider);

    public static Logger log = null;



    public static void main(String args[]) {

        // Initialise log
        log = Logger.getLogger(WheelchairGUI.class);

        // If no command line arguments are given, launch the default GUI
        if(args.length == 0 || (args.length == 1 && args[0].trim() == "")) {
            LaunchGUI();
        } else if(args.length == 1 && (args[0].trim() == "-daemon" || args[0].trim() == "d")) {

            // TODO: Parse arguments properly

            if (HEADLESS_ComPort == null) {
                OutputHeadlessError(new Exception("Please specify the serial port that the chair is connected to"));
            }

            // Connect to the given com port
            //serialCommsProvider.connect(HEADLESS_ComPort, HEADLESS_BaudRate);

            LaunchHeadlessMode();
        }
    }

    public WheelchairGUI(){

        // Register this class to receive events from the wheelchair
        DueWheelchairInterface.registerListener(this);

        G4P.setGlobalColorScheme(DEFAULT_COLOUR_SCHEME);
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

    private static void LaunchHeadlessMode(){

        OutputHeadlessDataLn("Wheelchair UI - Headless Mode Started");
        OutputHeadlessDataLn("Waiting for commands...");

        BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

        // Loop forever, listening for commands
        while(true){

            // Check to see if we have any new commands
            try {
                if (SystemIn.ready()) {
                    String line = SystemIn.readLine();

                    // Parse any incoming commands to determine the mode to switch to
                    int argIndex = 0;

                    // Reset the mode
                    HEADLESS_Mode = null;

                    try {
                        //TODO: Parse incoming commands

                        /*
                        while (argIndex < args.length) {
                            String command = args[argIndex];

                            // Connect using the specified port
                            if ("-p".equals(command)) {
                                HEADLESS_ComPort = args[argIndex + 1];
                                argIndex++;
                            } else if ("-b".equals(command)) {
                                HEADLESS_BaudRate = Integer.parseInt(args[argIndex + 1]);
                                argIndex++;
                            } else if ("-m".equals(command)) {
                                try {
                                    HEADLESS_Mode = HeadlessMode.valueOf(args[argIndex + 1]);
                                } catch (IllegalArgumentException iex) {
                                    throw new Exception("The given mode is not valid. It must be one of: " + join(HeadlessMode.values(), ", "));
                                }
                                argIndex++;
                            }

                            argIndex++;
                        }
                        */

                        if (HEADLESS_Mode == null) {
                            throw new Exception("Please select which mode you would like to use. Choose from the following: " + join(HeadlessMode.values(), ", "));
                        }


                        if (HEADLESS_Mode == HeadlessMode.LIST_SERIAL_PORTS) {

                            String[] ports = SerialPortList.getPortNames();

                            for (String port : ports) {
                                OutputHeadlessDataLn(port);
                            }
                        } else if (HEADLESS_Mode == HeadlessMode.MONITOR_JOYSTICK) {
                        }
                    } catch (Exception e){
                            OutputHeadlessError(e);
                    }
                }
            } catch (Exception e) {
                OutputHeadlessError(e);
            }
        }
    }

    private static void OutputHeadlessData(String data){

        System.out.print(data);
    }

    private static void OutputHeadlessDataLn(String data){

        System.out.println(data);
    }

    private static void OutputHeadlessError(Exception e){

        OutputHeadlessDataLn("ERROR: " + e.getMessage());
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
                boolean isAvoidanceEnabled = joystickFeedback.isAvoidanceEnabled();

                // Output the Joystick data as 3 digit number from -100 to +100 for each one of
                OutputHeadlessDataLn(String.format("%03d%03d%03d%03d%1b", inTurn, inSpeed, outTurn, outSpeed, isAvoidanceEnabled));
            }
        } else if(HEADLESS_Mode == HeadlessMode.LOG_LIST || HEADLESS_Mode == HeadlessMode.LOG_END){
            if(e.getType() == PWCInterfaceEvent.EventType.LOG_LIST){
                PWCInterfacePayloadLogFileInfo logFileFeedback = (PWCInterfacePayloadLogFileInfo) e.getPayload();
                LogFile logFile = logFileFeedback.getLogFile();

                // Output the log file information
                OutputHeadlessDataLn(logFile.getFilename() + ":" + logFile.getSize());
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
        StringBuilder buffer = new StringBuilder();

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
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }

            pwcInterface.requestVersion();
        }

        public void serialEvent(SerialPortEvent event){

            // If the event is telling us that data is available...
            if(event.isRXCHAR()) {

                // Try reading it
                try {
                    // Send the input to the interface to be buffered, broken up by new line
                    byte[] byteBuffer = transportSerialPort.readBytes();

                    if(byteBuffer != null) {
                        for (byte b : byteBuffer) {
                            // Add the byte to the buffer
                            buffer.append((char) b);

                            // If this is the end of a line
                            if (b == '\r' || b == '\n') {
                                DueWheelchairInterface.buffer(buffer.toString());
                                buffer.setLength(0);
                            }
                        }
                    }
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
                pwcInterface.setConnected(false);
            }
        }
    }

    /*
    public static class PWCSPISerialCommunicationProvider implements PWCInterfaceCommunicationProvider{

        private PWCInterface pwcInterface = null;
        private int channel = Spi.CHANNEL_0;    // Default SPI channel
        private int speed = 123456;             // Default SPI clock speed
        private boolean available = false;

        @Override
        public boolean isAvailable() {

            return available;
        }

        @Override
        public void write(String command) {

            // TODO: Convert the string command into a byte command - maybe just add a stop character?

            Spi.wiringPiSPIDataRW(channel, command, command.length());

            // Get the response

        }

        @Override
        public void setPWCInterface(PWCInterface pwcInterface){

            this.pwcInterface = pwcInterface;
        }

        private boolean connect(){

            // Attempt to connect to the SPI
            if(Spi.wiringPiSPISetup(channel, speed) == -1){
                return false;
            } else {
                // Record the the SPI interface is now available
                available = true;
                return true;
            }
        }

        public void connect(int channel){

            this.channel = channel;

            this.connect();
        }

        public void connect(int channel, int speed){

            this.channel = channel;
            this.speed = speed;

            this.connect();
        }
    }
    */

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
