package shapes;

import shapes.JShape;

/** Defines the minimum behaviour for any class that needs to listen to shape mouse events.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public interface JShapeListener
{
  /** Should provide code that responds to the mouse entering the shape from outside.
    * @param shape Shape that has been entered by the mouse.
    */
  public abstract void shapeEntered(JShape shape);
  
  /** Should provide code that responds to the mouse leaving the shape from inside.
    * @param shape Shape that has been exited by the mouse.
    */
  public abstract void shapeExited(JShape shape);
  
  /** Should provide code that responds to the mouse button being pressed while inside the shape.
    * @param shape Shape that has been pressed by the mouse.
    */
  public abstract void shapePressed(JShape shape);
  
  /** Should provide code that responds to the mouse button being released while inside the shape.
    * @param shape Shape that has been released by the mouse.
    */
  public abstract void shapeReleased(JShape shape);
  
  /** Should provide code that responds to the mouse button being dragged after being pressed inside the shape.
    * @param shape Shape that has been dragged by the mouse.
    * @param dx Amount mouse has been dragged in the x-direction.
    * @param dy Amount mouse has been dragged in the y-direction.
    */
  public abstract void shapeDragged(JShape shape,float dx, float dy);  
}
