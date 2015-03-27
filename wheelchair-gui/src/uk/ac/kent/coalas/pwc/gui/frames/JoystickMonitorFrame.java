package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.GButton;
import g4p_controls.GConstants;
import shapes.JEllipse;
import shapes.JRectangle;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.hardware.JoystickPosition;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.*;

import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by rm538 on 06/08/2014.
 */
public class JoystickMonitorFrame extends WheelchairGUIFrame {



    private static final int MARKER_GRAPH_CENTRE_X = 160;
    private static final int MARKER_GRAPH_CENTER_Y = 180;
    private static final int MARKER_GRAPH_RADIUS = 120;
    private static final int MARKER_GRAPH_LINE_COLOUR = 0xFF666666;
    private static final int MARKER_GRAPH_LINES_OVERFLOW = 20;

    private static final int MARKER_INPUT_DIAMETER = 40;
    private static final int MARKER_INPUT_COLOUR = 0xFF7375D8;
    private static final int MARKER_OUTPUT_DIAMETER = 30;
    private static final int MARKER_OUTPUT_COLOUR = 0xFFC9CEFF;

    private static final int MARKER_MAX_AGE_SECS = 10;



    private ArrayList<JEllipse> markerGraphCircles;

    private JRectangle markerGraphVertical, markerGraphHorizontal;
    private JEllipse inputMarker;
    private JEllipse outputMarker;
    private GButton btnDriveAssistIndicator;

    JoystickPosition inputPosition;
    JoystickPosition outputPosition;

    boolean isDriveAssistEnabled = false;

    public JoystickMonitorFrame(WheelchairGUIFrame copyFrame){

        super(copyFrame);
    }

    public JoystickMonitorFrame(int width, int height, int xPos, int yPos){

        super(width, height, xPos, yPos);
    }

    @Override
    public void init() {
        super.init();

        setTitle(s("joystick_monitor"));

        int circleDiameter = MARKER_GRAPH_RADIUS * 2;
        float factor = 1;

        markerGraphCircles = new ArrayList<JEllipse>();

        // Create graph circles
        markerGraphCircles.add(new JEllipse(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, circleDiameter, circleDiameter));
        factor = 0.75f;
        markerGraphCircles.add(new JEllipse(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, circleDiameter * factor, circleDiameter * factor));
        factor = 0.5f;
        markerGraphCircles.add(new JEllipse(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, circleDiameter * factor, circleDiameter * factor));

        // Set the line and fill properties of each circle
        for(JEllipse circle : markerGraphCircles){
            circle.setBorderWidth(1);
            circle.setBorderColour(MARKER_GRAPH_LINE_COLOUR);
            circle.setFillColour(0xFFFFFFFF);
        }

        markerGraphHorizontal = new JRectangle(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, MARKER_GRAPH_RADIUS * 2 + MARKER_GRAPH_LINES_OVERFLOW, 1);
        markerGraphHorizontal.setFillColour(MARKER_GRAPH_LINE_COLOUR);
        markerGraphHorizontal.setBorderWidth(0);

        markerGraphVertical = new JRectangle(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, 1, MARKER_GRAPH_RADIUS * 2 + MARKER_GRAPH_LINES_OVERFLOW);
        markerGraphVertical.setFillColour(MARKER_GRAPH_LINE_COLOUR);
        markerGraphVertical.setBorderWidth(0);

        inputMarker = new JEllipse(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, MARKER_INPUT_DIAMETER, MARKER_INPUT_DIAMETER);
        inputMarker.setFillColour(MARKER_INPUT_COLOUR);
        inputMarker.setBorderWidth(0);

        outputMarker = new JEllipse(this, MARKER_GRAPH_CENTRE_X, MARKER_GRAPH_CENTER_Y, MARKER_OUTPUT_DIAMETER, MARKER_OUTPUT_DIAMETER);
        outputMarker.setFillColour(MARKER_OUTPUT_COLOUR);
        outputMarker.setBorderWidth(0);

        // Set the default position to the centre
        inputPosition = new JoystickPosition(0, 0);
        outputPosition = new JoystickPosition(0, 0);

        // Create the button that will indicate the state of the Drive Assist
        btnDriveAssistIndicator = new GButton(this, 40, 360, 240, 50);
        // Disable it as we're misusing it slightly as just an output
        btnDriveAssistIndicator.setEnabled(false);

        setMarkerPositions();
    }

    @Override
    public void draw() {
        background(255);

        // Draw the marker graph
        for(JEllipse circle : markerGraphCircles){
            circle.draw();
        }
        markerGraphHorizontal.draw();
        markerGraphVertical.draw();

        // Update each marker's transparency, depending on how old the joystick data is
        setMarkerTransparencies();

        // Draw each marker to the screen
        inputMarker.draw();
        outputMarker.draw();
    }

    @Override
    public void onPWCInterfaceEvent(PWCInterfaceEvent e) {

        PWCInterfaceEvent.EventType eventType = e.getType();
        PWCInterfaceEventPayload payload = e.getPayload();

        switch(eventType){

            case JOYSTICK_FEEDBACK:
                PWCInterfacePayloadJoystickFeedback joystickPayload = (PWCInterfacePayloadJoystickFeedback) payload;
                // Update each marker's position
                inputPosition = joystickPayload.getInputPosition();
                outputPosition = joystickPayload.getOutputPosition();
                isDriveAssistEnabled = joystickPayload.isAvoidanceEnabled();
                setMarkerPositions();
                break;

            case DISCONNECTED:
                // Close the window when the chair is disconnected
                getViewFrame().dispatchEvent(new WindowEvent(getViewFrame(), WindowEvent.WINDOW_CLOSING));
                break;
        }


    }

    private void setMarkerPositions(){

        setMarkerPosition(inputMarker, inputPosition);
        setMarkerPosition(outputMarker, outputPosition);

        // Check to see if Drive Assist is enabled
        if(isDriveAssistEnabled) {
            // Check to see if adjustments are being made, ie Drive Assist is active
            if (inputPosition.getSpeed() != outputPosition.getSpeed() || inputPosition.getTurn() != outputPosition.getTurn()) {
                // Drive Assist IS active
                btnDriveAssistIndicator.setLocalColorScheme(GConstants.GREEN_SCHEME);
                btnDriveAssistIndicator.setText(s("drive_assist") + '\n' + s("drive_assist_enabled") + ": " + s("drive_assist_active").toUpperCase());
            } else {
                // Drive Assist IS NOT active
                btnDriveAssistIndicator.setLocalColorScheme(GConstants.GOLD_SCHEME);
                btnDriveAssistIndicator.setText(s("drive_assist") + '\n' + s("drive_assist_enabled") + ": " + s("drive_assist_inactive").toUpperCase());
            }
        } else {
            // Drive assist is disabled
            btnDriveAssistIndicator.setLocalColorScheme(WheelchairGUI.ERROR_COLOUR_SCHEME);
            btnDriveAssistIndicator.setText(s("drive_assist") + '\n' + s("drive_assist_disabled").toUpperCase());
        }
    }

    private void setMarkerPosition(JEllipse marker, JoystickPosition position){

        // Get horizontal position;
        int xPos = MARKER_GRAPH_CENTRE_X + (int) (MARKER_GRAPH_RADIUS * ((double) position.getTurn() / 100));

        // Get vertical position
        int yPos = MARKER_GRAPH_CENTER_Y - (int) (MARKER_GRAPH_RADIUS * ((double) position.getSpeed() / 100));

        // Set marker position
        marker.moveTo(xPos, yPos);
    }

    private void setMarkerTransparencies(){

        float transparency = Math.min((float) inputPosition.getDataAge() / (float) (MARKER_MAX_AGE_SECS * 1000), 1);

        inputMarker.setFillColour(setColourTransparency(MARKER_INPUT_COLOUR, transparency));
        outputMarker.setFillColour(setColourTransparency(MARKER_OUTPUT_COLOUR, transparency));
    }

    private int setColourTransparency(int colour, float transparency){

        float opacity = 1 - transparency;                   // Invert the transparency into opacity, as that is what we need

        int colourNoTransparency = colour & 0x00FFFFFF;     // We use this bitmask as we want the last 6 characters representing the colour
        int newOpacity = (int) (opacity * 255) << 24;       // Convert the opacity proportion (0-1) to a value between 0 and 255, then
                                                                //  shift its bits so that we can add it to the original colour
        return colourNoTransparency + newOpacity;
    }
}
