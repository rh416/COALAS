package shapes;

import processing.core.PApplet;

import java.awt.geom.*;

/** Represents an ellipse that can be drawn on screen and inform listeners that it 
  * has interacted with the mouse.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public class JEllipse extends JShape
{
  // ------------------ Object variables ------------------
  
  private Ellipse2D.Float myEllipse;
  
  // -------------------- Constructors --------------------
  
  /** Creates an ellipse with the given dimensions.
    * @param x x-coordinate of the centre of the ellipse.
    * @param y y-coordinate of the centre of the ellipse.
    * @param w Width of ellipse.
    * @param h Height of ellipse.
    */
  public JEllipse(PApplet parent, float x, float y, float w, float h)
  {
    super(parent);
    // Because Java Ellipse2D stores x,y as top left corner, not centre
    // we need to offset centre.
    myEllipse = new Ellipse2D.Float(x-w/2,y-h/2,w,h);
    shape = myEllipse;
  }    
  
  // ---------------------- Methods -----------------------
  
  /** Draws the ellipse.
    */
  public void draw()
  {    
    super.draw();
    parent.ellipseMode(parent.CORNER);
    parent.ellipse(myEllipse.x,myEllipse.y, myEllipse.width,myEllipse.height);
  }
  
  /** Moves the ellipse by the given offsets.
    * @param dx Amount to move ellipse in the x-direction.
    * @param dy Amount to move ellipse in the y-direction.
    */
  public void move(float dx, float dy)
  {
    myEllipse.x +=dx;
    myEllipse.y +=dy;
  }

    /**
     * Move the ellipse to the given position
     * @param x Where to move the centre of the ellipse to in the x-direction
     * @param y Where to move the centre of the ellipse to in the y-direction
     */
    public void moveTo(float x, float y){

        // As the ellipse use top-left positioning, we must account for that
        myEllipse.x = x - myEllipse.width / 2;
        myEllipse.y = y - myEllipse.height / 2;
    }
}
