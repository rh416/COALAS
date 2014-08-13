package shapes;

import processing.core.PApplet;
import shapes.JShape;

import java.awt.geom.*;

/** Represents a triangle that can be drawn on screen and inform listeners that it 
  * has interacted with the mouse.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public class JTriangle extends JShape
{
  // ------------------ Object variables ------------------
  
  private GeneralPath myTriangle;
  
  // -------------------- Constructors --------------------
  
  /** Creates a triangle with the given dimensions.
    * @param x1 x-coordinate of the first triangle vertex.
    * @param y1 y-coordinate of the first triangle vertex.
    * @param x2 x-coordinate of the second triangle vertex.
    * @param y2 y-coordinate of the second triangle vertex.
    * @param x3 x-coordinate of the third triangle vertex.
    * @param y3 y-coordinate of the third triangle vertex.
    */
  public JTriangle(PApplet parent, float x1, float y1, float x2, float y2, float x3, float y3)
  {
    super(parent);
    
    myTriangle = new GeneralPath();
    myTriangle.moveTo(x1,y1);
    myTriangle.lineTo(x2,y2);
    myTriangle.lineTo(x3,y3);
    myTriangle.closePath();
    
    shape = myTriangle;
  }    
  
  // ---------------------- Methods -----------------------
  
  /** Draws the triangle.
    */
  public void draw()
  {    
    super.draw();
    float[] coords = new float[6];
    PathIterator pi = myTriangle.getPathIterator(null);
    
    parent.beginShape();
    while(!pi.isDone())
    {
      pi.currentSegment(coords);
      parent.vertex(coords[0],coords[1]);
      pi.next();
    }
    parent.endShape(parent.CLOSE);
  }
  
  /** Moves the triangle by the given offsets.
    * @param dx Amount to move ellipse in the x-direction.
    * @param dy Amount to move ellipse in the y-direction.
    */
  public void move(float dx, float dy)
  {
    myTriangle.transform(AffineTransform.getTranslateInstance(dx,dy));
  }
}
