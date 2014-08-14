package uk.ac.kent.coalas.pwc.gui.ui;

import processing.core.PApplet;
import shapes.JShape;
import shapes.JShapeAdapter;
import shapes.JTriangle;
import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.awt.*;

import static uk.ac.kent.coalas.pwc.gui.hardware.Zone.*;

/**
 * Created by rm538 on 13/08/2014.
 */
public class UIZone extends UIObject {

    public static int ZONE_COLOUR = 0x80F87217;
    public static int ZONE_COLOUR_HOVER = 0xFFF87217;
    public static int ZONE_INDICATOR_LENGTH = 60;
    public static int ZONE_INDICATOR_WIDTH = 60;

    private PApplet parent;
    private Orientation orientation;

    private JTriangle zoneIndicator;

    private int dx, dy;

    public UIZone(PApplet parent, Position position, Orientation orientation){

        this.parent = parent;
        this.orientation = orientation;

        Point basePosition = UIObject.getCenterFromPosition(position);
        double angleInRads = Math.toRadians(orientation.getAngle());

        dx = (int) (UINode.NODE_WIDTH / 2d) * (int) Math.round(Math.sin(angleInRads));
        dy = (-1) * (int) (UINode.NODE_HEIGHT / 2d) *(int) Math.round(Math.cos(angleInRads));

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
        zoneIndicator.addShapeListener(new ZoneShapeListener(this));
    }

    public void draw(){
        zoneIndicator.draw();
    }

    public class ZoneShapeListener extends JShapeAdapter{

        private UIZone parent;

        public ZoneShapeListener(UIZone parent){

            this.parent = parent;
        }

        @Override
        public void shapeEntered(JShape shape){

            shape.setFillColour(ZONE_COLOUR_HOVER);
        }

        @Override
        public void shapeExited(JShape shape){

            shape.setFillColour(ZONE_COLOUR);
        }
    }
}
