package shapes;

import shapes.JShape;
import shapes.JShapeListener;

/** Provides default behaviour for a ShapeListener. This version does nothing
  * for each of the five types of mouse events, but one or more of the methods
  * can be overridden to perform actions in response to a mouse event.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public class JShapeAdapter implements JShapeListener
{  
  /** Responds to the mouse entering the bounds of the shape.
    * @param shape Shape entered by the mouse.
    */
  public void shapeEntered(JShape shape)
  {
    // Does nothing unless overridden.
  }
  
  /** Responds to the mouse leaving the bounds of the shape.
    * @param shape Shape exited by the mouse.
    */
  public void shapeExited(JShape shape)
  {
    // Does nothing unless overridden.
  }
  
  /** Responds to the mouse button being pressed within the bounds of the shape.
    * @param shape Shape pressed by the mouse.
    */
  public void shapePressed(JShape shape)
  {
    // Does nothing unless overridden.
  }
  
  /** Responds to the mouse button being release within the bounds of the shape.
    * @param shape Shape released by the mouse.
    */
  public void shapeReleased(JShape shape)
  {
    // Does nothing unless overridden.
  }
  
  /** Responds to the mouse being dragged within the bounds of the shape.
    * @param shape Shape dragged by the mouse.
    */
  public void shapeDragged(JShape shape, float dx, float dy)
  {
    // Does nothing unless overridden.
  }
}
  
  
