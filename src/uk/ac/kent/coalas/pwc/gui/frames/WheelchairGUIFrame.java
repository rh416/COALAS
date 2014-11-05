package uk.ac.kent.coalas.pwc.gui.frames;

import g4p_controls.G4P;
import g4p_controls.GEditableTextControl;
import g4p_controls.GEvent;
import g4p_controls.GPanel;
import org.apache.log4j.Logger;
import processing.core.PApplet;
import uk.ac.kent.coalas.pwc.gui.pwcinterface.PWCInterfaceListener;
import uk.ac.kent.coalas.pwc.gui.WheelchairGUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class WheelchairGUIFrame extends PApplet implements PWCInterfaceListener {

    protected WheelchairGUI parent;
    private final Frame containingFrame;

    private int frameWidth, frameHeight;

    protected Logger log = Logger.getLogger(this.getClass());

    public boolean CLICK_EVENT_STOPPED;

    public WheelchairGUIFrame(){

        this(WheelchairGUI.WindowWidth, WheelchairGUI.WindowHeight, 0, 0);
    }

    public WheelchairGUIFrame(WheelchairGUIFrame copyFrame){

        this(copyFrame.getViewFrame().getWidth(), copyFrame.getViewFrame().getHeight(), copyFrame.getViewFrame().getX(), copyFrame.getViewFrame().getY());
    }

    public WheelchairGUIFrame(int theWidth, int theHeight, int xPos, int yPos) {
        frameWidth = theWidth;
        frameHeight = theHeight;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Max sure the window is visible on screen
        xPos = Math.max(xPos, 0);
        yPos = Math.max(yPos, 0);

        xPos = Math.min(xPos, (int) screenSize.getWidth() - theWidth);
        yPos = Math.min(yPos,  (int) screenSize.getHeight() - theHeight);


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
                // Let the main window know we're closing
                getMainApplication().childWindowClosing(WheelchairGUIFrame.this);
                // Release any objects held in memory
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

        getViewFrame().setTitle(title);
    }

    public Frame getViewFrame(){

        return containingFrame;
    }

    public String s(String stringName){

        return WheelchairGUI.Strings.getString(stringName);
    }

    public WheelchairGUI getMainApplication(){

        return WheelchairGUI.getInstance();
    }

    public void handleTextEvents(GEditableTextControl textControl, GEvent event){
        // Do nothing - this stops the G4P library sending messages to the console about missing event handlers
    }

    public void handlePanelEvents(GPanel panel, GEvent event){
        // Do nothing - this stops the G4P library sending messages to the console about missing event handlers
    }

    public abstract void draw();
}
