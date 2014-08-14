package uk.ac.kent.coalas.pwc.gui.ui;

import uk.ac.kent.coalas.pwc.gui.hardware.Zone;

import java.awt.*;

/**
 * Created by rm538 on 13/08/2014.
 */
public abstract class UIObject {

    public abstract void draw();

    public static Point getCenterFromPosition(Zone.Position position){

        switch(position){

            case FRONT_LEFT_CORNER:
                return new Point(80, 80);

            case FRONT_CENTRE:
                return new Point(160, 80);

            case FRONT_RIGHT_CORNER:
                return new Point(240, 80);

            case RIGHT_CENTRE:
                return new Point(240, 160);

            case BACK_RIGHT_CORNER:
                return new Point(240, 240);

            case BACK_CENTRE:
                return new Point(160, 240);

            case BACK_LEFT_CORNER:
                return new Point(80, 240);

            case LEFT_CENTRE:
                return new Point(80, 160);
        }

        return new Point(0, 0);
    }
}
