package uk.ac.kent.coalas.pwc.gui.ui;

import shapes.JShape;
import shapes.JShapeAdapter;
import shapes.JTriangle;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;
import uk.ac.kent.coalas.pwc.gui.frames.DiagnosticsFrame;
import uk.ac.kent.coalas.pwc.gui.frames.WheelchairGUIFrame;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.awt.*;

import static uk.ac.kent.coalas.pwc.gui.hardware.Zone.*;

/**
 * Created by rm538 on 13/08/2014.
 */
public class UIZone extends UIObject {

    public static int ZONE_COLOUR = 0x80F87217;
    public static int ZONE_COLOUR_HIGHLIGHT = 0xFFF87217;
    public static int ZONE_INDICATOR_LENGTH = 60;
    public static int ZONE_INDICATOR_WIDTH = 60;

    private WheelchairGUIFrame parent;
    private Zone dataZone;

    private JTriangle zoneIndicator;

    private int dx, dy;

    public UIZone(WheelchairGUIFrame parent, Zone zone) {

        this.parent = parent;
        this.dataZone = zone;

        Position position = zone.getPosition();
        Orientation orientation = zone.getOrientation();

        Point basePosition = UIObject.getCenterFromPosition(position);
        double angleInRads = Math.toRadians(orientation.getAngle());

        dx = (int) (UINode.NODE_WIDTH / 2d) * (int) Math.round(Math.sin(angleInRads));
        dy = (-1) * (int) (UINode.NODE_HEIGHT / 2d) * (int) Math.round(Math.cos(angleInRads));

        dx = 0;
        dy = 0;

        int x1 = (int) basePosition.getX() + dx;
        int y1 = (int) basePosition.getY() + dy;

        double centralXOffset = ZONE_INDICATOR_LENGTH * Math.sin(angleInRads);
        double centralYOffset = ZONE_INDICATOR_LENGTH * Math.cos(angleInRads);

        int x2 = x1 + (int) (centralXOffset + ((ZONE_INDICATOR_WIDTH / 2d) * Math.cos(angleInRads)));
        int y2 = y1 - (int) (centralYOffset - ((ZONE_INDICATOR_WIDTH / 2d) * Math.sin(angleInRads)));

        int x3 = x1 + (int) (centralXOffset - ((ZONE_INDICATOR_WIDTH / 2d) * Math.cos(angleInRads)));
        int y3 = y1 - (int) (centralYOffset + ((ZONE_INDICATOR_WIDTH / 2d) * Math.sin(angleInRads)));

        zoneIndicator = new JTriangle(parent, x1, y1, x2, y2, x3, y3);
        zoneIndicator.setFillColour(ZONE_COLOUR);
        zoneIndicator.setBorderWidth(0);
        zoneIndicator.addShapeListener(new ZoneShapeListener());
    }

    public void draw() {

        if(parent.getMainApp().getFrame(WheelchairGUI.FrameId.DIAGNOSTICS) == null){
            zoneIndicator.setFillColour(ZONE_COLOUR);
        }

        zoneIndicator.draw();
    }

    public class ZoneShapeListener extends JShapeAdapter {

        @Override
        public void shapeEntered(JShape shape) {

            shape.setFillColour(ZONE_COLOUR_HIGHLIGHT);
        }

        @Override
        public void shapeExited(JShape shape) {

            DiagnosticsFrame diagnosticsFrame = (DiagnosticsFrame) parent.getMainApp().getFrame(WheelchairGUI.FrameId.DIAGNOSTICS);

            // If the diagnostics frame is not visible, the zone cannot be being monitored, so reset the zone colour
            if(diagnosticsFrame == null){
                shape.setFillColour(ZONE_COLOUR);

            // Otherwise, if the frame does exist, check whether or not this particular zone is being monitored. If not, reset the colour
            } else if(!diagnosticsFrame.isZoneBeingMonitored(dataZone)) {
                shape.setFillColour(ZONE_COLOUR);
            }
        }

        @Override
        public void shapePressed(JShape shape) {

            DiagnosticsFrame diagnosticsFrame = (DiagnosticsFrame) parent.getMainApp().getFrame(WheelchairGUI.FrameId.DIAGNOSTICS);

            if (diagnosticsFrame == null) {
                int w = parent.width;
                int h = parent.height;
                int x = parent.getFrame().getX() - w - 10;
                int y = parent.getFrame().getY();
                diagnosticsFrame = (DiagnosticsFrame) parent.getMainApp().addNewFrame(WheelchairGUI.FrameId.DIAGNOSTICS, new DiagnosticsFrame(w, h, x, y));
            }

            if(diagnosticsFrame.isZoneBeingMonitored(dataZone)){
                diagnosticsFrame.stopMonitoringZone(dataZone);
                shape.setFillColour(ZONE_COLOUR);
            } else{
                diagnosticsFrame.monitorZone(dataZone);
                shape.setFillColour(ZONE_COLOUR_HIGHLIGHT);
            }
        }
    }
}

