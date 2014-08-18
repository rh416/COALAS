package uk.ac.kent.coalas.pwc.gui.ui;

import uk.ac.kent.coalas.pwc.gui.ui.UISensorRow;
import uk.ac.kent.coalas.pwc.gui.ui.UIZoneRow;

/**
 * Created by rm538 on 18/08/2014.
 */
public class RowPositionTracker {

    private int initialX, initialY;
    private int rowPositionX, rowPositionY;

    public RowPositionTracker(int initialX, int initialY){

        this.initialX = initialX;
        this.initialY = initialY;

        this.rowPositionX = initialX;
        this.rowPositionY = initialY;
    }

    public void incrementXPosition(int amount){

        this.rowPositionX += amount;
    }

    public void incrementYPosition(Class<?> clazz){

        if(UIZoneRow.class.equals(clazz)){
            rowPositionY += 10;
        } else if(UISensorRow.class.equals(clazz)){
            rowPositionY += 30;
        }
    }

    public int getX(){

        return rowPositionX;
    }

    public int getY(){

        return rowPositionY;
    }

    public void resetX(){

        this.rowPositionX = initialX;
    }

    public void resetY(){

        this.rowPositionY = initialY;
    }
}
