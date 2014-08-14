package uk.ac.kent.coalas.pwc.gui;

import processing.core.PApplet;

import processing.serial.Serial;

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

    BufferedReader SystemIn = new BufferedReader(new InputStreamReader(System.in));

    private Serial DueSerialPort, DXBusSerialPort;

    // Use this interface for debubgging using the console I/O
    private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCConsoleCommunicationProvider());
    //
    // Use this interface for communication with a Due running Diagnostics / Config Firmware
    //private PWCInterface DueWheelchairInterface = new PWCInterface(this, new PWCDueSerialCommunicationProvider());

    public void setup() {
        size(800, 800);
        smooth();

        //addNewFrame(FrameId.USER, new DiagnosticsFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        addNewFrame(FrameId.OVERVIEW, new OverviewFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
        //addNewFrame(FrameId.DIAGNOSTICS, new DiagnosticsFrame(WindowWidth, WindowHeight, WindowXPosition, WindowYPosition));
        WindowXPosition += WindowWidth + WindowXOffset;
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
