package shapes;

import processing.core.PApplet;

import java.awt.geom.*;

/** Represents a closed polygon that can be drawn on screen and inform listeners
  * that it has interacted with the mouse.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public class JPolygon extends JShape
{
  // ------------------ Object variables ------------------
  
  private GeneralPath myPolygon;
  
  // -------------------- Constructors --------------------
  
  /** Creates a polygon with the given coordinates. Polygons are always
    * closed, so there is no need to make the first and last coordinates match.
    * @param x x-coordinates of the polygon.
    * @param y y-coordinates of the polygon
    */
  public JPolygon(PApplet parent, float[] x, float[] y)
  {
    super(parent);
    
    myPolygon = new GeneralPath();
    myPolygon.moveTo(x[0],y[0]);
    for (int i=1; i<x.length; i++)
    {
      myPolygon.lineTo(x[i],y[i]);
    }
    myPolygon.closePath();
    
    shape = myPolygon;
  }    
  
  // ---------------------- Methods -----------------------
  
  /** Draws the ellipse.
    */
  public void draw()
  {    
    super.draw();
    float[] coords = new float[6];
    PathIterator pi = myPolygon.getPathIterator(null);
    
    parent.beginShape();
    while(!pi.isDone())
    {
      pi.currentSegment(coords);
      parent.vertex(coords[0],coords[1]);
      pi.next();
    }
    parent.endShape(parent.CLOSE);
  }
  
  /** Moves the polygon by the given offsets.
    * @param dx Amount to move polygon in the x-direction.
    * @param dy Amount to move polygon in the y-direction.
    */
  public void move(float dx, float dy)
  {
    myPolygon.transform(AffineTransform.getTranslateInstance(dx,dy));
  }
}
