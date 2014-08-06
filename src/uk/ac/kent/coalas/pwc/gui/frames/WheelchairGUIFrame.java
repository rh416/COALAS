package uk.ac.kent.coalas.pwc.gui.frames;

import controlP5.ControlP5;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;

import java.awt.*;

/**
 * ControlP5 Controlframe
 * with controlP5 2.0 all java.awt dependencies have been removed
 * as a consequence the option to display controllers in a separate
 * window had to be removed as well.
 * this example shows you how to create a java.awt.frame and use controlP5
 *
 * by Andreas Schlegel, 2012
 * www.sojamo.de/libraries/controlp5
 *
 */




// the ControlFrame class extends PApplet, so we
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
public abstract class WheelchairGUIFrame extends PApplet implements PWCInterfaceListener {

    ControlP5 cp5;
    WheelchairGUI parent;
    Frame containingFrame;

    int frameWidth, frameHeight;

    public WheelchairGUIFrame(WheelchairGUI theParent, int theWidth, int theHeight, int xPos, int yPos) {
        parent = theParent;
        frameWidth = theWidth;
        frameHeight = theHeight;

        Frame f = new Frame(this.getClass().getName());
        f.add(this);
        this.init();
        f.setTitle(this.getClass().getName());
        f.setSize(theWidth, theHeight);
        f.setLocation(xPos, yPos);
        f.setUndecorated(true);
        f.setResizable(false);
        f.setVisible(true);

        this.containingFrame = f;
    }

    public void setup() {
        size(frameWidth, frameHeight);
        frameRate(25);

        cp5 = new ControlP5(this);
    }

    public Frame getFrame(){

        return containingFrame;
    }

    public void console(String s){

        println(this.getClass().getSimpleName() + " - " + s);
    }

    public void console(int val){
        console(String.valueOf(val));
    }

    public void console(float val){
        console(String.valueOf(val));
    }

    public void console(double val){
        console(String.valueOf(val));
    }

    public void console(char val){
        console(String.valueOf(val));
    }

    public void console(byte val){
        console(String.valueOf(val));
    }

    public void console(boolean val){
        console(String.valueOf(val));
    }

    public void console(Object val){
        console(String.valueOf(val));
    }

    public abstract void draw();
}
