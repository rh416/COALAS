package uk.ac.kent.coalas.pwc.gui.ui;

import g4p_controls.GAbstractControl;

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

    public void incrementXPosition(GAbstractControl control, int padding){

        incrementXPosition((int) control.getWidth() + padding);
    }

    public void incrementXPosition(XIncrementer incrementer){

        incrementXPosition(incrementer.incrementX());
    }

    public void incrementYPosition(int amount){

        this.rowPositionY += amount;
    }

    public void incrementYPosition(YIncrementer incrementer){

        incrementYPosition(incrementer.incrementY());
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

    public interface XIncrementer {
        public int incrementX();

    }

    public interface YIncrementer {
        public int incrementY();

    }
}
