package shapes;

import processing.core.PApplet;
import shapes.JShape;

import java.awt.geom.*;

/** Represents a rectangle that can be drawn on screen and inform listeners that it 
  * has interacted with the mouse.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public class JRectangle extends JShape
{
  // ------------------ Object variables ------------------
  
  private Rectangle2D.Float myRect;
  
  // -------------------- Constructors --------------------
  
  /** Creates a rectangle with the given dimensions.
    * @param x x-coordinate of the top-left corner of the rectangle.
    * @param y y-coordinate of the top-left corner of the rectangle.
    * @param w Width of rectangle.
    * @param h Height of rectangle.
    */
  public JRectangle(PApplet parent, float x, float y, float w, float h)
  {
    super(parent);
    myRect = new Rectangle2D.Float(x,y,w,h);
    shape = myRect;
  }    
  
  
  // ---------------------- Methods -----------------------
  
  /** Draws the rectangle.
    */
  public void draw()
  {    
    super.draw();
    parent.rect(myRect.x,myRect.y, myRect.width,myRect.height);
  }
  
  /** Moves the rectangle by the given offsets.
    * @param dx Amount to move rectange in the x-direction.
    * @param dy Amount to move rectange in the y-direction.
    */
  public void move(float dx, float dy)
  {
    myRect.x +=dx;
    myRect.y +=dy;
  }
}
