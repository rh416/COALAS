package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.*;
import jssc.SerialPortException;
import jssc.SerialPortList;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by rm538 on 20/10/2014.
 */
public class MainUIFrame extends WheelchairGUIFrame implements PWCInterfaceListener{

    private GDropList DueSerialPortList;
    private GButton btnDueSerialControlButton, btnJoystickMonitorLaunch;
    private GTextArea InterfaceLogArea;

    private String DueSerialInfo;

    private LinkedList<String> logQueue = new LinkedList<String>();

    private DateFormat timestampFormatShort = new SimpleDateFormat("HH:mm:ss");
    private DateFormat timestampFormatLong = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

    private int SerialListUpdateRateMs = 500;
    private long SerialListUpdateLastTime = 0;

    public MainUIFrame(){

        super();
    }

    public MainUIFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public MainUIFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    @Override
    public void setup(){

        //setTitle("COALAS Wheelchair Configuration UI");
        setTitle("CareTECH Wheelchair Demo");
        size(WheelchairGUI.WindowWidth, WheelchairGUI.WindowHeight);
        noSmooth();

        DueSerialPortList = new GDropList(this, 25, 25, 150, 150, 4);
        DueSerialPortList.setItems(new String[]{s("ports_loading")}, 0);
        updateSerialPortList(0);

        btnDueSerialControlButton = new GButton(this, 195, 25, 100, 30, s("connect"));

        Font logFont = new Font("Monospace", Font.PLAIN, 10);

        InterfaceLogArea = new GTextArea(this, 25, 120, 275, 290, GConstants.SCROLLBARS_VERTICAL_ONLY);
        //InterfaceLogArea.setTextEditEnabled(false);
        InterfaceLogArea.setFont(logFont);
        InterfaceLogArea.setLocalColorScheme(WheelchairGUI.CONSOLE_COLOUR_SCHEME);

        btnJoystickMonitorLaunch = new GButton(this, 25, 430, 275, 30, s("launch_joystick_monitor_no_connection"));

        setJoystickMonitorEnabled(false);
    }


    @Override
    public void draw() {

        // Check if there's any messages waiting in the log - if so write them to the screen
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

    private synchronized void logToScreen(String logString){

        logQueue.add(logString);
    }

    private void updateSerialPortList(int index){

        String[] portList = SerialPortList.getPortNames();

        // If no ports were found
        if(portList.length == 0){
            DueSerialPortList.setItems(new String[]{s("ports_none")}, 0);
        } else {
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
    }

    private void updateSerialPortList(){

        updateSerialPortList(DueSerialPortList.getSelectedIndex());
    }

    private void setJoystickMonitorEnabled(boolean enabled){

        btnJoystickMonitorLaunch.setEnabled(enabled);

        if(enabled){
            btnJoystickMonitorLaunch.setText(s("launch_joystick_monitor"));
        } else {
            btnJoystickMonitorLaunch.setText(s("launch_joystick_monitor_no_connection"));

        }
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        // Uncomment this line for detailed logging - will be a lot of data if Diagnostics window is used
        //logToScreen(timestamp() + " - " + e.getPayload().getResponse());

        // Handle any specific events we want to here
        switch(e.getType()){

            case ERROR:
                PWCInterfacePayloadError errorPayload = (PWCInterfacePayloadError)e.getPayload();
                Exception err = errorPayload.getException();

                logToScreen(timestamp() + " - " + err.getMessage());

                err.printStackTrace();

                break;

            case FIRMWARE_INFO:
                PWCInterfacePayloadFirmwareInfo firmwareInfo = (PWCInterfacePayloadFirmwareInfo) e.getPayload();
                DueSerialInfo = String.format(s("firmware_response"), firmwareInfo.getVersion());

                setJoystickMonitorEnabled(true);

                //Open Overview Window, if not already open
                if(getMainApplication().getFrame(WheelchairGUI.FrameId.OVERVIEW) == null) {
                    int xPos = getX() + getWidth() + 10;
                    int yPos = getY();
                    OverviewFrame overview = (OverviewFrame) getMainApplication().addNewFrame(WheelchairGUI.FrameId.OVERVIEW, new OverviewFrame(WheelchairGUI.WindowWidth, WheelchairGUI.WindowHeight, xPos, yPos));
                    overview.scanBus();
                }
                break;

            // Enable the Joystick monitor if we receive a Joystick Feedback event
            case JOYSTICK_FEEDBACK:

                setJoystickMonitorEnabled(true);

                break;

            case CONNECTED:
                // We have successfully connected - change the button text and re-enable the button
                btnDueSerialControlButton.setText(s("disconnect"));
                btnDueSerialControlButton.setEnabled(true);
                break;

            case DISCONNECTED:
                // We appear to have disconnected - change the button text and re-enable it
                btnDueSerialControlButton.setText(s("connect"));
                btnDueSerialControlButton.setEnabled(true);
                DueSerialInfo = s("connection_end");
                setJoystickMonitorEnabled(false);
                break;

            case TIMEOUT:
                PWCInterfacePayloadTimeout timeoutInfo = (PWCInterfacePayloadTimeout) e.getPayload();
                // Check if the timeout is related to getting the version information
                if(timeoutInfo.getRequest().getType() == PWCInterfaceEvent.EventType.FIRMWARE_INFO){
                    DueSerialInfo = String.format(s("firmware_timeout"), DueSerialPortList.getSelectedText());
                    // Reset the button text and re-enable it
                    btnDueSerialControlButton.setText(s("connect"));
                    btnDueSerialControlButton.setEnabled(true);
                }
                break;
        }

        // Choose which events we wanted to be shown in the on-screen log - all events are logged to file
        switch(e.getType()){

            case FIRMWARE_INFO:
            case BUS_SCAN:

                logToScreen(timestamp() + " - " + e.getType());
                break;

        }
    }

    public void handleButtonEvents(GButton button, GEvent event){

        if(button == btnDueSerialControlButton && event == GEvent.CLICKED) {
            String portName = DueSerialPortList.getSelectedText();
            String buttonText = button.getText();

            // Disconnect from the serial port
            getMainApplication().getPWCConnection().disconnect();

            // If we are trying to connect
            if(buttonText == s("connect")){

                try {
                    // Change button label to show we are trying to connect and disable it
                    button.setText(s("connecting"));
                    button.setEnabled(false);
                    DueSerialInfo = s("connection_check");
                    getMainApplication().getPWCConnection().connect(portName, WheelchairGUI.DUE_BAUD_RATE);
                } catch (SerialPortException se){
                    if("Port busy".equals(se.getExceptionType())) {
                        DueSerialInfo = s("port_busy");
                        button.setText(s("connect"));
                    } else if("Port not found".equals(se.getExceptionType())) {
                        DueSerialInfo = s("port_not_found");
                        button.setText(s("connect"));
                    } else {
                        se.printStackTrace();
                    }
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

                setJoystickMonitorEnabled(false);
            }
        } else if(button == btnJoystickMonitorLaunch){

            WheelchairGUIFrame joystickFrame = getMainApplication().getFrame(WheelchairGUI.FrameId.JOYSTICK);

            // If the joystick window already exists
            if(joystickFrame != null){
                // Select the window and bring it to the front
                joystickFrame.requestFocus();
            } else {
                // Otherwise create the window
                int xPos = getX() - getWidth() - 10;
                int yPos = getY();
                getMainApplication().addNewFrame(WheelchairGUI.FrameId.JOYSTICK, new JoystickMonitorFrame(WheelchairGUI.WindowWidth, WheelchairGUI.WindowHeight, xPos, yPos));
            }
        }
    }

    public void handleDropListEvents(GDropList list, GEvent event){

        // Reset the connect button to ease changing port
        btnDueSerialControlButton.setText(s("connect"));
    }
}
