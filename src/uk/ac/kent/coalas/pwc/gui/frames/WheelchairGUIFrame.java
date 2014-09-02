package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.G4P;
import g4p_controls.GConstants;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.MissingResourceException;

// the ControlFrame class extends PApplet, so we
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
public abstract class WheelchairGUIFrame extends PApplet implements PWCInterfaceListener {

    protected WheelchairGUI parent;
    private final Frame containingFrame;

    private int frameWidth, frameHeight;

    protected Logger log = Logger.getLogger(this.getClass());

    public boolean CLICK_EVENT_STOPPED;

    public WheelchairGUIFrame(WheelchairGUIFrame copyFrame){

        this(copyFrame.getFrame().getWidth(), copyFrame.getFrame().getHeight(), copyFrame.getFrame().getX(), copyFrame.getFrame().getY());
    }

    public WheelchairGUIFrame(int theWidth, int theHeight, int xPos, int yPos) {
        frameWidth = theWidth;
        frameHeight = theHeight;

        containingFrame = new Frame(this.getClass().getName());
        containingFrame.add(this);
        containingFrame.setTitle(this.getClass().getName());
        containingFrame.setSize(theWidth, theHeight);
        containingFrame.setLocation(xPos, yPos);
        //containingFrame.setUndecorated(true);
        containingFrame.setResizable(false);
        containingFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
                containingFrame.dispose();
            }
        });

        containingFrame.setVisible(true);

        this.init();
    }

    public void setup() {
        size(frameWidth, frameHeight);
        frameRate(25);
        smooth();

        G4P.setGlobalColorScheme(WheelchairGUI.DEFAULT_COLOUR_SCHEME);

    }

    public void setTitle(String title){

        getFrame().setTitle(title);
    }

    public void setParent(WheelchairGUI parent){

        this.parent = parent;
    }

    public Frame getFrame(){

        return containingFrame;
    }

    public WheelchairGUI getMainApp(){

        return parent;
    }

    public String s(String stringName){

        return WheelchairGUI.Strings.getString(stringName);
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
